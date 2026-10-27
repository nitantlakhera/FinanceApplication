package com.finance.ai.web;

import com.finance.ai.dto.ChatRequest;
import com.finance.ai.dto.ChatResponse;
import com.finance.ai.dto.FraudCheckRequest;
import com.finance.ai.dto.FraudCheckResponse;
import com.finance.ai.dto.IngestRequest;
import com.finance.ai.dto.SummarizeRequest;
import com.finance.ai.dto.SummarizeResponse;
import com.finance.ai.service.AiModelRouter;
import com.finance.ai.service.DocumentIngestionService;
import com.finance.ai.service.FraudCheckService;
import com.finance.ai.service.NaturalLanguageQueryService;
import com.finance.ai.service.OrderSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final FraudCheckService fraudCheckService;
    private final OrderSummaryService orderSummaryService;
    private final NaturalLanguageQueryService naturalLanguageQueryService;
    private final DocumentIngestionService documentIngestionService;
    private final AiModelRouter modelRouter;

    public AiController(FraudCheckService fraudCheckService,
                        OrderSummaryService orderSummaryService,
                        NaturalLanguageQueryService naturalLanguageQueryService,
                        DocumentIngestionService documentIngestionService,
                        AiModelRouter modelRouter) {
        this.fraudCheckService = fraudCheckService;
        this.orderSummaryService = orderSummaryService;
        this.naturalLanguageQueryService = naturalLanguageQueryService;
        this.documentIngestionService = documentIngestionService;
        this.modelRouter = modelRouter;
    }

    @PostMapping("/fraud-check")
    public ResponseEntity<FraudCheckResponse> fraudCheck(@RequestBody FraudCheckRequest request) {
        return ResponseEntity.ok(this.fraudCheckService.checkFraud(request));
    }

    @PostMapping("/summarize-order")
    public ResponseEntity<SummarizeResponse> summarizeOrder(@RequestBody SummarizeRequest request) {
        return ResponseEntity.ok(this.orderSummaryService.summarize(request));
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(this.naturalLanguageQueryService.ask(request));
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingest(@RequestBody IngestRequest request) {
        this.documentIngestionService.ingest(request);
        return ResponseEntity.ok("Document ingested");
    }

    @GetMapping("/provider")
    public ResponseEntity<String> provider() {
        return ResponseEntity.ok(this.modelRouter.getProvider());
    }
}
