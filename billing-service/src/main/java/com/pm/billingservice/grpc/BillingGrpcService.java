package com.pm.billingservice.grpc;

import billing.BillingAccountResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import billing.CreateBillingAccountRequest;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

  private static final Logger log = LoggerFactory.getLogger(
      BillingGrpcService.class);

  @Override
  public void createBillingAccount(CreateBillingAccountRequest createBillingAccountRequest,
                                   StreamObserver<BillingAccountResponse> responseObserver) {

      log.info("createBillingAccount request received {}", createBillingAccountRequest.toString());

      // Business logic - e.g save to database, perform calculates etc

      BillingAccountResponse response = BillingAccountResponse.newBuilder()
              .setBillingAccountId("test-billing-account-id-123")
              .setPatientId("tesst-patient-id-456")
              .setPlanCode("test-plan-code")
              .setDiscountCode("test-discount-code")
              .setAccountStatus("ACTIVE")
              .setCadence("MONTHLY")
              .setCycleAnchor("2024-06-01T00:00:00Z")
              .setActivatedAt("2024-06-01T00:00:00Z")
              .setCanceledAt("")
              .setLastInvoicedEnd("2024-06-30T23:59:59Z")
              .setCurrency("USD")
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
  }
}
