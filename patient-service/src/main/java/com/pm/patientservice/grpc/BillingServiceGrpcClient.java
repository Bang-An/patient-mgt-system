package com.pm.patientservice.grpc;


import billing.BillingAccountResponse;
import billing.BillingServiceGrpc;
import billing.CreateBillingAccountRequest;
import billing.UpdateBillingAccountPlanRequest;
import billing.CancelBillingAccountRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {

  private static final Logger log = LoggerFactory.getLogger(
      BillingServiceGrpcClient.class);
  private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

  public BillingServiceGrpcClient(
      @Value("${billing.service.address:localhost}") String serverAddress,
      @Value("${billing.service.grpc.port:9001}") int serverPort) {

    log.info("Connecting to Billing Service GRPC service at {}:{}",
        serverAddress, serverPort);

    ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress,
        serverPort).usePlaintext().build();

    blockingStub = BillingServiceGrpc.newBlockingStub(channel);
  }

  public BillingAccountResponse createBillingAccount(String patientId, String planCode, String discountCode,
                                                     String cadence,
                                                     String currency) {

    CreateBillingAccountRequest request = CreateBillingAccountRequest.newBuilder()
            .setPatientId(patientId)
            .setPlanCode(planCode)
            .setDiscountCode(discountCode)
            .setCadence(cadence)
            .setCurrency(currency)
            .build();

    BillingAccountResponse response = blockingStub.createBillingAccount(request);
    log.info("Received response from billing service via GRPC: {}", response);
    return response;
  }

  public BillingAccountResponse updateBillingAccountPlan(String billingAccountId,
                                                         String newPlanCode,
                                                         String discountCode,
                                                         String effectiveDate) {
    UpdateBillingAccountPlanRequest.Builder builder = UpdateBillingAccountPlanRequest.newBuilder()
        .setBillingAccountId(billingAccountId)
        .setNewPlanCode(newPlanCode);

    if (discountCode != null) {
      builder.setDiscountCode(discountCode);
    }
    if (effectiveDate != null) {
      builder.setEffectiveDate(effectiveDate);
    }

    BillingAccountResponse response = blockingStub.updateBillingAccountPlan(builder.build());
    log.info("Received response from billing service via GRPC (update plan): {}", response);
    return response;
  }

  public BillingAccountResponse cancelBillingAccount(String billingAccountId, String effectiveDate) {
    CancelBillingAccountRequest.Builder builder = CancelBillingAccountRequest.newBuilder()
        .setBillingAccountId(billingAccountId);
    if (effectiveDate != null) {
      builder.setEffectiveDate(effectiveDate);
    }
    BillingAccountResponse response = blockingStub.cancelBillingAccount(builder.build());
    log.info("Received response from billing service via GRPC (cancel): {}", response);
    return response;
  }

//  public BillingResponse createBillingAccount(String patientId, String name,
//      String email) {
//
//    BillingRequest request = BillingRequest.newBuilder().setPatientId(patientId)
//        .setName(name).setEmail(email).build();
//
//    BillingResponse response = blockingStub.createBillingAccount(request);
//    log.info("Received response from billing service via GRPC: {}", response);
//    return response;
//  }
}
