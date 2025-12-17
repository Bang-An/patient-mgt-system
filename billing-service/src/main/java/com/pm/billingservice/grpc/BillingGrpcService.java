package com.pm.billingservice.grpc;

import billing.BillingAccountResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import billing.CreateBillingAccountRequest;
import billing.UpdateBillingAccountPlanRequest;
import billing.CancelBillingAccountRequest;
import com.pm.billingservice.exception.*;
import com.pm.billingservice.model.BillingAccount;
import com.pm.billingservice.model.DiscountCode;
import com.pm.billingservice.model.PlanCode;
import com.pm.billingservice.service.BillingAccountService;
import com.pm.billingservice.service.PlanCatalogService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.UUID;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    private final PlanCatalogService planCatalogService;
    private final BillingAccountService billingAccountService;

    public BillingGrpcService(PlanCatalogService planCatalogService, BillingAccountService billingAccountService) {
        this.planCatalogService = planCatalogService;
        this.billingAccountService = billingAccountService;
    }

    private static final Logger log = LoggerFactory.getLogger(
            BillingGrpcService.class);

    @Override
    public void createBillingAccount(CreateBillingAccountRequest createBillingAccountRequest,
                                     StreamObserver<BillingAccountResponse> responseObserver) {

        log.info("createBillingAccount request received {}", createBillingAccountRequest.toString());

        // validate patient does not have existing billing account
        if (planCatalogService.existsByPatientId(createBillingAccountRequest.getPatientId())) {
            responseObserver.onError(Status.ALREADY_EXISTS.withDescription("Billing account already exists for patient")
                    .asRuntimeException());
            return;
        }
        // validate plan and discount code is not empty
        if (createBillingAccountRequest.getPlanCode().isBlank() || createBillingAccountRequest.getDiscountCode().isBlank()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Plan/Discount code is required").asRuntimeException());
            return;
        }

        // validate discount code and plan code resolve to enums
        PlanCode planCode;
        DiscountCode discountCode;
        try {
            planCode = PlanCode.valueOf(createBillingAccountRequest.getPlanCode().toUpperCase());
            discountCode = DiscountCode.valueOf(createBillingAccountRequest.getDiscountCode().toUpperCase());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Invalid plan/discount code").asRuntimeException());
            return;
        }

        try {
            planCatalogService.validatePlanAndDiscount(planCode, discountCode);
            BillingAccount account = billingAccountService.createBillingAccount(
                    createBillingAccountRequest.getPatientId(), planCode, discountCode,
                    createBillingAccountRequest.getCadence(), createBillingAccountRequest.getCurrency());

            BillingAccountResponse response = toResponse(account);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (PlanNotFoundException | DiscountNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (PlanNotDiscountableException | DiscountNotAllowedException | PlanInactiveException |
                 DiscountInactiveException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }


    }

    @Override
    public void updateBillingAccountPlan(UpdateBillingAccountPlanRequest request,
                                         StreamObserver<BillingAccountResponse> responseObserver) {
        log.info("updateBillingAccountPlan request received {}", request);
        PlanCode planCode;
        DiscountCode discountCode = null;
        UUID billingAccountId;
        try {
            planCode = PlanCode.valueOf(request.getNewPlanCode().toUpperCase());
            if (!request.getDiscountCode().isBlank()) {
                discountCode = DiscountCode.valueOf(request.getDiscountCode().toUpperCase());
            }
            billingAccountId = UUID.fromString(request.getBillingAccountId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid plan/discount code or billing account id").asRuntimeException());
            return;
        }

        LocalDate effectiveDate = request.getEffectiveDate().isBlank() ? null : LocalDate.parse(request.getEffectiveDate());

        try {
            BillingAccount account = billingAccountService.changePlan(
                    billingAccountId,
                    planCode,
                    discountCode,
                    effectiveDate
            );
            responseObserver.onNext(toResponse(account));
            responseObserver.onCompleted();
        } catch (AccountNotFoundException | PlanNotFoundException | DiscountNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (PlanNotDiscountableException | DiscountNotAllowedException | PlanInactiveException |
                 DiscountInactiveException | IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void cancelBillingAccount(CancelBillingAccountRequest request,
                                     StreamObserver<BillingAccountResponse> responseObserver) {
        log.info("cancelBillingAccount request received {}", request);
        LocalDate effectiveDate = request.getEffectiveDate().isBlank() ? null : LocalDate.parse(request.getEffectiveDate());
        UUID billingAccountId;
        try {
            billingAccountId = UUID.fromString(request.getBillingAccountId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid billing account id").asRuntimeException());
            return;
        }
        try {
            BillingAccount account = billingAccountService.cancelPlan(
                    billingAccountId,
                    effectiveDate
            );
            responseObserver.onNext(toResponse(account));
            responseObserver.onCompleted();
        } catch (AccountNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    private BillingAccountResponse toResponse(BillingAccount account) {
        return BillingAccountResponse.newBuilder()
                .setBillingAccountId(account.getId().toString())
                .setPatientId(account.getPatientId())
                .setPlanCode(account.getPlan().getPlanCode().name())
                .setDiscountCode(account.getDiscount() == null ? "" : account.getDiscount().getDiscountCode().name())
                .setAccountStatus(account.getAccountStatus().name())
                .setCadence(account.getBillingCadence().name())
                .setCycleAnchor(account.getCycleAnchor().toString())
                .setActivatedAt(account.getActivatedAt().toString())
                .setCanceledAt(account.getCanceledAt() == null ? "" : account.getCanceledAt().toString())
                .setLastInvoicedEnd(account.getLastInvoicedEnd() == null ? "" : account.getLastInvoicedEnd().toString())
                .setCurrency(account.getCurrency())
                .build();
    }
}
