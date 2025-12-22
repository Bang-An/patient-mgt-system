<img width="1556" height="973" alt="image" src="https://github.com/user-attachments/assets/8941ae37-27c0-483a-827d-d89a13ed67a7" />

  
  
  ### Usage Example

  When a user pays (single record type), the processor writes one NDJSON line to s3://wallet-lake/raw/payments/dt=.../
  with payment_id, merchant_id, user_id, amount_cents, currency, status (APPROVED/DECLINED/REFUNDED/etc.), payment_method,
  risk_score, authorized_at, completed_at, created_at, source_system. A Glue crawler catalogs it; a Glue Spark job cleans
  and enriches it, writes curated Parquet partitioned by dt and merchant_id, and also publishes the curated record to
  Kafka payments.curated (key = payment_id). A Kafka Connect JDBC sink upserts into Postgres for fast API reads; Athena
  queries the Parquet for analytics. A Spring service serves reports off Postgres (and Athena for heavy/ad hoc).

  ### Data Flow & Event Types (single record)

  - Event: one payment_event record per transaction with fields above.
  - Landing: NDJSON to s3://wallet-lake/raw/payments/dt=YYYY/MM/DD/HH/.
  - Catalog: Glue Crawler → raw_payments (JSON SerDe).
  - Transform (Glue Spark): enforce schema, filter bad rows, dedupe by payment_id, derive dt, hour,
    authorized_to_complete_ms, flags (is_approved, is_refunded), add processed_at, curated_version; write Parquet to s3://
    wallet-lake/curated/payments/dt=YYYY-MM-DD/merchant_id=.../ partitioned by dt, merchant_id; publish curated DF to
    Kafka payments.curated (key payment_id).
  - Curated catalog: Athena/Glue table curated_payments with partition projection on dt, merchant_id.
  - DB loader: Kafka Connect JDBC sink upserts payments.curated into Postgres curated_payments (PK payment_id), batched,
    idempotent.

  ### Functional Requirements

  - Reliable ingest to S3; schema enforcement and dedup in Glue; derived fields.
  - Expose curated via Parquet/Athena, Kafka, and Postgres.
  - Idempotent writes (avoid dupes), partitioning for query efficiency, observability (job/sink/API errors), security
    (KMS, IAM, ACLs, secrets).

  ### API Design (Spring)

  - GET /reports/daily-recon?date=YYYY-MM-DD&merchant=... (totals/counts by status)
  - GET /reports/refund-rate?from=...&to=...&merchant=... (refund/capture ratios using status)
  - GET /reports/latency?from=...&to=...&payment_method=... (authorized→completed latency)
  - GET /reports/declines?from=...&to=...&merchant=... (decline counts by reason if present)

  ### Schemas

  - Raw Glue raw_payments (JSON): payment_id STRING, merchant_id STRING, user_id STRING, amount_cents BIGINT, currency
    STRING, status STRING, payment_method STRING, risk_score DOUBLE, authorized_at TIMESTAMP, completed_at TIMESTAMP,
    created_at TIMESTAMP, source_system STRING.
  - Curated Parquet curated_payments: raw fields + dt DATE, hour INT, authorized_to_complete_ms BIGINT, is_approved
    BOOLEAN, is_refunded BOOLEAN, processed_at TIMESTAMP, curated_version INT; partitions: dt, merchant_id.
  - Kafka payments.curated: key payment_id; value mirrors curated fields.
  - Postgres curated_payments: PK payment_id; columns matching curated; indexes on (merchant_id, dt) and dt. Optional
    aggregated views/materialized tables for daily recon, refund rates, latency stats.

  ### Project Implementation Plan (detailed, step-by-step)

  1. S3 Landing & Glue Crawler

  - Create S3 bucket/folder: s3://wallet-lake/raw/payments/ with subfolders by date/hour (dt=YYYY/MM/DD/HH/). Enable KMS
    encryption.
  - Prepare sample NDJSON files matching the raw schema; upload to a test dt/hour path.
  - Create Glue Data Catalog database (e.g., wallet_db).
  - Create Glue Crawler:
      - Data store: S3 path raw/payments/
      - IAM role with read access to the bucket.
      - Target database: wallet_db
      - Schedule: hourly (or on-demand while testing)
  - Run crawler; verify table wallet_db.raw_payments appears with expected columns/types. Test Athena SELECT * FROM
    raw_payments LIMIT 10;.

  2. Glue Transform Job (raw → curated Parquet)

  - Author a Glue Spark job (PySpark) with:
      - Input: raw_payments table.
      - Steps: apply schema; drop/route bad rows; dedupe by payment_id (keep latest created_at); derive dt, hour,
        authorized_to_complete_ms, is_approved (status == APPROVED), is_refunded (status == REFUNDED); add processed_at
        (now), curated_version (int).
      - Output: write Parquet to s3://wallet-lake/curated/payments/ partitioned by dt (date) and merchant_id.
  - Job config: IAM role with S3 read raw/write curated; DPUs per volume; enable job bookmarks if you later do
    incremental. Schedule nightly/hourly after raw arrives.
  - Test: run on sample raw data; inspect Parquet schema/partitions; ensure dedup and derived fields correct. Query via
    Athena to validate.

  3. Curated Catalog & Athena

  - Define curated_payments table (DDL in Athena or Glue) over s3://wallet-lake/curated/payments/:
      - Columns = curated schema; partitions = dt (string/date), merchant_id.
      - Optionally enable partition projection on dt/merchant_id to avoid manual partition adds.
  - Test: if not using projection, MSCK REPAIR TABLE or ALTER TABLE ADD PARTITION for test partitions. Run Athena queries
    filtering on dt/merchant_id to confirm partition pruning.

  4. Glue → Kafka (curated topic)

  - Create Kafka topic payments.curated (replication 3, N partitions) and register schema (Avro/JSON) with key =
    payment_id.
  - Update Glue job to also write curated DataFrame to Kafka:
      - Use Spark Kafka sink with broker bootstrap servers, topic payments.curated, key column payment_id, value
        serialized with your schema.
      - IAM/security: if MSK, configure VPC/subnet/security groups; if auth enabled, supply creds/SASL config.
  - Test: run job; consume from payments.curated with a simple consumer; verify keys, payload schema, no duplicate keys
    per run.

  5. Kafka → Postgres Loader (Connect)

  - Provision Postgres (RDS/local) with DB/schema; create table curated_payments with PK payment_id and indexes on
    (merchant_id, dt).
  - Configure Kafka Connect JDBC sink:
      - Source topic: payments.curated
      - Connection: JDBC URL + credentials (from secrets manager)
      - Insert mode: upsert; PK: payment_id; delete support off.
      - Batch size and max.in.flight.requests=1 (or single task) to preserve order per key; reasonable poll interval.
  - Deploy connector; monitor logs for errors/retries.
  - Test: produce sample curated records; verify rows in Postgres; replay same records to confirm idempotent upsert (no
    duplicates).

  6. Spring API Layer

  - Set up Spring Boot project with Postgres datasource.
  - Define read-only endpoints:
      - GET /reports/daily-recon?date=...&merchant=...
      - GET /reports/refund-rate?from=...&to=...&merchant=...
      - GET /reports/latency?from=...&to=...&payment_method=...
      - GET /reports/declines?from=...&to=...&merchant=... (if decline_reason present)
  - Back endpoints with SQL queries/views/materialized views on Postgres; optionally fall back to Athena for heavy/ad hoc.
  - Tests: unit tests for query builders; integration tests with seeded Postgres; smoke tests hitting endpoints.

  7. Observability & Security

  - Metrics/alerts: Glue job failure alarms; Kafka Connect task failure/restart alarms; API error rate/latency monitors;
    Postgres health.
  - Logging: structured logs from Glue, Connect, Spring; dead-letter path for bad records in Glue or Connect if needed.
  - Security: KMS on S3; IAM least privilege for Glue/Crawler/Job; Kafka ACLs; DB creds in Secrets Manager; VPC/security
    groups restricting access.
