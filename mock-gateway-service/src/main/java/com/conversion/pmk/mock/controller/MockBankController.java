package com.conversion.pmk.mock.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.mock.config.FailureSimulator;
import com.conversion.pmk.mock.dto.bank.CardChargeRequest;
import com.conversion.pmk.mock.dto.bank.CardChargeResponse;
import com.conversion.pmk.mock.dto.bank.NetsConfirmRequest;
import com.conversion.pmk.mock.dto.bank.NetsConfirmResponse;
import com.conversion.pmk.mock.dto.bank.NetsInitRequest;
import com.conversion.pmk.mock.dto.bank.NetsInitResponse;
import com.conversion.pmk.mock.dto.bank.TxnStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

// Simulates the bank payment gateway (NETS debit and card)
@Slf4j
@RestController
@RequestMapping("/mock/bank")
@RequiredArgsConstructor
public class MockBankController {

    private final FailureSimulator failureSimulator;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Initiates a NETS debit transaction at the terminal
    @PostMapping("/nets/initiate")
    public ApiResponse<NetsInitResponse> netsInitiate(@RequestBody NetsInitRequest request) {
        log.debug("NETS initiate for sessionRef={} amount={}", request.getSessionRef(), request.getAmount());

        if (failureSimulator.shouldFail()) {
            log.warn("NETS initiate simulated decline");
            NetsInitResponse declined = NetsInitResponse.builder()
                    .status("DECLINED")
                    .message("Insufficient funds")
                    .build();
            return ApiResponse.ok(declined);
        }

        String txnRef = "NETS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        NetsInitResponse response = NetsInitResponse.builder()
                .txnRef(txnRef)
                .status("APPROVED")
                .message("NETS transaction approved")
                .build();

        return ApiResponse.ok(response);
    }

    // Confirms a previously initiated NETS transaction
    @PostMapping("/nets/confirm")
    public ApiResponse<NetsConfirmResponse> netsConfirm(@RequestBody NetsConfirmRequest request) {
        log.debug("NETS confirm for txnRef={}", request.getTxnRef());

        // Only references issued by this mock carry the NETS- prefix
        boolean valid = request.getTxnRef() != null && request.getTxnRef().startsWith("NETS-");
        NetsConfirmResponse response = NetsConfirmResponse.builder()
                .txnRef(request.getTxnRef())
                .status(valid ? "CONFIRMED" : "FAILED")
                .build();

        return ApiResponse.ok(response);
    }

    // Charges a credit or debit card via the terminal
    @PostMapping("/card/charge")
    public ApiResponse<CardChargeResponse> cardCharge(@RequestBody CardChargeRequest request) {
        log.debug("Card charge for sessionRef={} network={}", request.getSessionRef(), request.getCardNetwork());

        if (failureSimulator.shouldFail()) {
            log.warn("Card charge simulated decline");
            CardChargeResponse declined = CardChargeResponse.builder()
                    .cardNetwork(request.getCardNetwork())
                    .status("DECLINED")
                    .message("Card declined")
                    .build();
            return ApiResponse.ok(declined);
        }

        String approvalRef = "CRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        CardChargeResponse response = CardChargeResponse.builder()
                .approvalRef(approvalRef)
                .cardNetwork(request.getCardNetwork())
                .status("APPROVED")
                .message("Card approved")
                .build();

        return ApiResponse.ok(response);
    }

    // Returns the settlement status of a transaction by reference
    @GetMapping("/transaction/{txnRef}")
    public ApiResponse<TxnStatusResponse> getTransactionStatus(@PathVariable("txnRef") String txnRef) {
        log.debug("Transaction status query for txnRef={}", txnRef);

        boolean known = txnRef.startsWith("NETS-") || txnRef.startsWith("CRD-");
        TxnStatusResponse response = TxnStatusResponse.builder()
                .txnRef(txnRef)
                .status(known ? "SETTLED" : "NOT_FOUND")
                .updatedAt(LocalDateTime.now().format(DT_FMT))
                .build();

        return ApiResponse.ok(response);
    }
}
