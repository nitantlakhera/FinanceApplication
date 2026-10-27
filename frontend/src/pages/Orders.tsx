import { useState } from "react";
import { orderService } from "../api/services";
import { useAppState } from "../hooks/useAppState";
import { ResponsePanel } from "../components/ResponsePanel";
import { ApiResponse, Order, ServiceConfig } from "../types";

export function Orders() {
  const { services, createdUserId, setCreatedOrderId } = useAppState();
  const [userId, setUserId] = useState(createdUserId || "");
  const [productId, setProductId] = useState("PROD-1");
  const [quantity, setQuantity] = useState(2);
  const [totalAmount, setTotalAmount] = useState(199.99);
  const [lookupId, setLookupId] = useState("");
  const [lookupUserId, setLookupUserId] = useState("");
  const [response, setResponse] = useState<ApiResponse<Order | Order[]> | null>(null);
  const [loading, setLoading] = useState(false);

  const service = orderService(services.order as ServiceConfig);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const result = await service.create(userId, [{ productId, quantity }], totalAmount);
    setResponse(result);
    if (result.ok && !Array.isArray(result.data) && result.data?.id) {
      setCreatedOrderId(result.data.id);
    }
    setLoading(false);
  };

  const handleGet = async () => {
    if (!lookupId.trim()) return;
    setLoading(true);
    const result = await service.getById(lookupId.trim());
    setResponse(result);
    setLoading(false);
  };

  const handleGetByUser = async () => {
    if (!lookupUserId.trim()) return;
    setLoading(true);
    const result = await service.getByUser(lookupUserId.trim());
    setResponse(result);
    setLoading(false);
  };

  return (
    <div className="mx-auto max-w-3xl">
      <h2 className="mb-6 text-2xl font-bold text-[var(--text)]">Orders</h2>

      <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-semibold text-[var(--text)]">
          Place Order
        </h3>
        <form onSubmit={handleCreate} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-[var(--text)]">
              User ID
            </label>
            <input
              type="text"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              required
              className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm"
            />
          </div>
          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-[var(--text)]">
                Product ID
              </label>
              <input
                type="text"
                value={productId}
                onChange={(e) => setProductId(e.target.value)}
                required
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-[var(--text)]">
                Quantity
              </label>
              <input
                type="number"
                value={quantity}
                onChange={(e) => setQuantity(parseInt(e.target.value, 10))}
                required
                min={1}
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-[var(--text)]">
                Total Amount
              </label>
              <input
                type="number"
                step="0.01"
                value={totalAmount}
                onChange={(e) => setTotalAmount(parseFloat(e.target.value))}
                required
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm"
              />
            </div>
          </div>
          <button
            type="submit"
            disabled={loading}
            className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50"
          >
            {loading ? "Placing..." : "Place Order"}
          </button>
        </form>
      </div>

      <div className="mt-6 rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-semibold text-[var(--text)]">
          Lookup Orders
        </h3>
        <div className="space-y-3">
          <div className="flex gap-3">
            <input
              type="text"
              value={lookupId}
              onChange={(e) => setLookupId(e.target.value)}
              placeholder="Order ID"
              className="flex-1 rounded-md border border-[var(--border)] px-3 py-2 text-sm"
            />
            <button
              onClick={handleGet}
              disabled={loading}
              className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50"
            >
              Get Order
            </button>
          </div>
          <div className="flex gap-3">
            <input
              type="text"
              value={lookupUserId}
              onChange={(e) => setLookupUserId(e.target.value)}
              placeholder="User ID"
              className="flex-1 rounded-md border border-[var(--border)] px-3 py-2 text-sm"
            />
            <button
              onClick={handleGetByUser}
              disabled={loading}
              className="rounded-md border border-[var(--border)] bg-white px-4 py-2 text-sm font-medium text-[var(--text)] hover:bg-gray-50 disabled:opacity-50"
            >
              List by User
            </button>
          </div>
        </div>
      </div>

      <ResponsePanel response={response} />
    </div>
  );
}
