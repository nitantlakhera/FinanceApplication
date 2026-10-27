import { useState } from "react";
import { notificationService } from "../api/services";
import { useAppState } from "../hooks/useAppState";
import { ResponsePanel } from "../components/ResponsePanel";
import { ApiResponse, Notification, ServiceConfig } from "../types";

export function Notifications() {
  const { services, createdOrderId } = useAppState();
  const [orderId, setOrderId] = useState(createdOrderId || "");
  const [response, setResponse] = useState<ApiResponse<Notification | Notification[]> | null>(null);
  const [loading, setLoading] = useState(false);

  const service = notificationService(services.notification as ServiceConfig);

  const handleList = async () => {
    setLoading(true);
    const result = await service.list();
    setResponse(result);
    setLoading(false);
  };

  const handleGetByOrder = async () => {
    if (!orderId.trim()) return;
    setLoading(true);
    const result = await service.getByOrderId(orderId.trim());
    setResponse(result);
    setLoading(false);
  };

  return (
    <div className="mx-auto max-w-3xl">
      <h2 className="mb-6 text-2xl font-bold text-[var(--text)]">
        Notifications
      </h2>

      <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-semibold text-[var(--text)]">
          Lookup Notifications
        </h3>
        <div className="flex gap-3">
          <input
            type="text"
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
            placeholder="Order ID"
            className="flex-1 rounded-md border border-[var(--border)] px-3 py-2 text-sm"
          />
          <button
            onClick={handleGetByOrder}
            disabled={loading}
            className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50"
          >
            {loading ? "Loading..." : "Get by Order"}
          </button>
          <button
            onClick={handleList}
            disabled={loading}
            className="rounded-md border border-[var(--border)] bg-white px-4 py-2 text-sm font-medium text-[var(--text)] hover:bg-gray-50 disabled:opacity-50"
          >
            List All
          </button>
        </div>
      </div>

      <ResponsePanel response={response} />
    </div>
  );
}
