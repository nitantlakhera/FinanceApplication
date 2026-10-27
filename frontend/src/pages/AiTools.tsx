import { useState } from "react";
import { aiService } from "../api/services";
import { useAppState } from "../hooks/useAppState";
import { ResponsePanel } from "../components/ResponsePanel";
import {
  ApiResponse,
  ChatResponse,
  FraudCheckResponse,
  ServiceConfig,
  SummarizeResponse,
} from "../types";

export function AiTools() {
  const { services, createdOrderId, createdUserId } = useAppState();
  const [activeTab, setActiveTab] = useState<"fraud" | "summarize" | "ask" | "ingest" | "provider">("fraud");
  const [response, setResponse] = useState<ApiResponse<unknown> | null>(null);
  const [loading, setLoading] = useState(false);

  const [amount, setAmount] = useState(199.99);
  const [currency, setCurrency] = useState("USD");
  const [paymentMethod, setPaymentMethod] = useState("CARD");
  const [userCountry, setUserCountry] = useState("US");
  const [merchantCountry, setMerchantCountry] = useState("US");
  const [userOrderCount, setUserOrderCount] = useState(2);

  const [orderStatus, setOrderStatus] = useState("PLACED");
  const [paymentStatus, setPaymentStatus] = useState("SUCCESS");
  const [inventoryStatus, setInventoryStatus] = useState("RESERVED");

  const [question, setQuestion] = useState("What are the refund policies?");
  const [context, setContext] = useState("Finance application terms");

  const [documentId, setDocumentId] = useState("doc-1");
  const [content, setContent] = useState("Refunds are processed within 5 business days.");
  const [metadata, setMetadata] = useState("finance,refund");

  const service = aiService(services.ai as ServiceConfig);

  const handleFraudCheck = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const result = await service.fraudCheck({
      orderId: createdOrderId || undefined,
      userId: createdUserId || undefined,
      amount,
      currency,
      paymentMethod,
      userCountry,
      merchantCountry,
      userOrderCount,
    });
    setResponse(result as ApiResponse<FraudCheckResponse>);
    setLoading(false);
  };

  const handleSummarize = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const result = await service.summarize({
      orderId: createdOrderId || undefined,
      orderStatus,
      paymentStatus,
      inventoryStatus,
    });
    setResponse(result as ApiResponse<SummarizeResponse>);
    setLoading(false);
  };

  const handleAsk = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const result = await service.ask({ question, context });
    setResponse(result as ApiResponse<ChatResponse>);
    setLoading(false);
  };

  const handleIngest = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const result = await service.ingest({ documentId, content, metadata });
    setResponse(result);
    setLoading(false);
  };

  const handleProvider = async () => {
    setLoading(true);
    const result = await service.provider();
    setResponse(result);
    setLoading(false);
  };

  const tabs = [
    { key: "fraud", label: "Fraud Check" },
    { key: "summarize", label: "Summarize" },
    { key: "ask", label: "Ask RAG" },
    { key: "ingest", label: "Ingest Doc" },
    { key: "provider", label: "Provider" },
  ] as const;

  return (
    <div className="mx-auto max-w-3xl">
      <h2 className="mb-6 text-2xl font-bold text-[var(--text)]">AI Tools</h2>

      <div className="mb-6 flex flex-wrap gap-2">
        {tabs.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`rounded-md px-4 py-2 text-sm font-medium transition-colors ${
              activeTab === tab.key
                ? "bg-[var(--primary)] text-white"
                : "border border-[var(--border)] bg-white text-[var(--text)] hover:bg-gray-50"
            }`}
          >
            {tab.label}
          </button>
        ))}
        {activeTab === "fraud" && (
          <form onSubmit={handleFraudCheck} className="space-y-4">
            <h3 className="text-lg font-semibold text-[var(--text)]">Fraud Risk Check</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="mb-1 block text-sm font-medium">Amount</label>
                <input type="number" step="0.01" value={amount}
                  onChange={(e) => setAmount(parseFloat(e.target.value))}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium">Currency</label>
                <input type="text" value={currency}
                  onChange={(e) => setCurrency(e.target.value)}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium">Payment Method</label>
                <input type="text" value={paymentMethod}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium">User Order Count</label>
                <input type="number" value={userOrderCount}
                  onChange={(e) => setUserOrderCount(parseInt(e.target.value, 10))}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium">User Country</label>
                <input type="text" value={userCountry}
                  onChange={(e) => setUserCountry(e.target.value)}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium">Merchant Country</label>
                <input type="text" value={merchantCountry}
                  onChange={(e) => setMerchantCountry(e.target.value)}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
            </div>
            <button type="submit" disabled={loading}
              className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50">
              {loading ? "Checking..." : "Check Fraud"}
            </button>
          </form>
        )}

        {activeTab === "summarize" && (
          <form onSubmit={handleSummarize} className="space-y-4">
            <h3 className="text-lg font-semibold text-[var(--text)]">Summarize Order</h3>
            <div className="grid grid-cols-3 gap-4">
              <div>
                <label className="mb-1 block text-sm font-medium">Order Status</label>
                <input type="text" value={orderStatus}
                  onChange={(e) => setOrderStatus(e.target.value)}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium">Payment Status</label>
                <input type="text" value={paymentStatus}
                  onChange={(e) => setPaymentStatus(e.target.value)}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium">Inventory Status</label>
                <input type="text" value={inventoryStatus}
                  onChange={(e) => setInventoryStatus(e.target.value)}
                  className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
              </div>
            </div>
            <button type="submit" disabled={loading}
              className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50">
              {loading ? "Summarizing..." : "Summarize"}
            </button>
          </form>
        )}

      </div>

      <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        {activeTab === "ask" && (
          <form onSubmit={handleAsk} className="space-y-4">
            <h3 className="text-lg font-semibold text-[var(--text)]">RAG Q&A</h3>
            <div>
              <label className="mb-1 block text-sm font-medium">Question</label>
              <input type="text" value={question}
                onChange={(e) => setQuestion(e.target.value)}
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">Context</label>
              <input type="text" value={context}
                onChange={(e) => setContext(e.target.value)}
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
            </div>
            <button type="submit" disabled={loading}
              className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50">
              {loading ? "Asking..." : "Ask"}
            </button>
          </form>
        )}

        {activeTab === "ingest" && (
          <form onSubmit={handleIngest} className="space-y-4">
            <h3 className="text-lg font-semibold text-[var(--text)]">Ingest Document</h3>
            <div>
              <label className="mb-1 block text-sm font-medium">Document ID</label>
              <input type="text" value={documentId}
                onChange={(e) => setDocumentId(e.target.value)}
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">Content</label>
              <textarea value={content} onChange={(e) => setContent(e.target.value)} rows={4}
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">Metadata</label>
              <input type="text" value={metadata}
                onChange={(e) => setMetadata(e.target.value)}
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm" />
            </div>
            <button type="submit" disabled={loading}
              className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50">
              {loading ? "Ingesting..." : "Ingest"}
            </button>
          </form>
        )}

        {activeTab === "provider" && (
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-[var(--text)]">AI Provider</h3>
            <button onClick={handleProvider} disabled={loading}
              className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50">
              {loading ? "Loading..." : "Show Provider"}
            </button>
          </div>
        )}
      </div>

      <ResponsePanel response={response} />
    </div>
  );
}

