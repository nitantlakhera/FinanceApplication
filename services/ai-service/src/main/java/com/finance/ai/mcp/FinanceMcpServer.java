package com.finance.ai.mcp;

import com.finance.ai.dto.FraudCheckRequest;
import com.finance.ai.dto.FraudCheckResponse;
import com.finance.ai.service.FraudCheckService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class FinanceMcpServer {

    private final FraudCheckService fraudCheckService;

    public FinanceMcpServer(FraudCheckService fraudCheckService) {
        this.fraudCheckService = fraudCheckService;
    }

    @Tool(name = "check_fraud_risk", description = "Evaluate fraud risk for a payment/order transaction.")
    public FraudCheckResponse checkFraudRisk(
            @ToolParam(description = "Order identifier") UUID orderId,
            @ToolParam(description = "User identifier") UUID userId,
            @ToolParam(description = "Transaction amount") BigDecimal amount,
            @ToolParam(description = "Currency code, e.g. USD") String currency,
            @ToolParam(description = "Payment method, e.g. CREDIT_CARD") String paymentMethod,
            @ToolParam(description = "User country ISO code") String userCountry,
            @ToolParam(description = "Merchant country ISO code") String merchantCountry,
            @ToolParam(description = "Number of previous orders by the user") int userOrderCount) {

        FraudCheckRequest request = new FraudCheckRequest(
                orderId, userId, amount, currency, paymentMethod, userCountry, merchantCountry, userOrderCount
        );
        return this.fraudCheckService.checkFraud(request);
    }
}
