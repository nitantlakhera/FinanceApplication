import { useState } from "react";
import { inventoryService } from "../api/services";
import { useAppState } from "../hooks/useAppState";
import { ResponsePanel } from "../components/ResponsePanel";
import { ApiResponse, InventoryItem, ServiceConfig } from "../types";

export function Inventory() {
  const { services, setCreatedProductId } = useAppState();
  const [productId, setProductId] = useState("PROD-1");
  const [quantity, setQuantity] = useState(100);
  const [lookupId, setLookupId] = useState("");
  const [response, setResponse] = useState<ApiResponse<InventoryItem | InventoryItem[]> | null>(null);
  const [loading, setLoading] = useState(false);

  const service = inventoryService(services.inventory as ServiceConfig);

  const handleAdd = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const result = await service.addStock(productId, quantity);
    setResponse(result);
    if (result.ok && result.data && !Array.isArray(result.data)) {
      setCreatedProductId(result.data.productId);
    }
    setLoading(false);
  };

  const handleList = async () => {
    setLoading(true);
    const result = await service.list();
    setResponse(result);
    setLoading(false);
  };

  const handleGet = async () => {
    if (!lookupId.trim()) return;
    setLoading(true);
    const result = await service.getById(lookupId.trim());
    setResponse(result);
    setLoading(false);
  };

  return (
    <div className="mx-auto max-w-3xl">
      <h2 className="mb-6 text-2xl font-bold text-[var(--text)]">Inventory</h2>

      <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-semibold text-[var(--text)]">
          Add Stock
        </h3>
        <form onSubmit={handleAdd} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
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
                min={0}
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm"
              />
            </div>
          </div>
          <button
            type="submit"
            disabled={loading}
            className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50"
          >
            {loading ? "Adding..." : "Add Stock"}
          </button>
        </form>
      </div>

      <div className="mt-6 rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-semibold text-[var(--text)]">
          Lookup Inventory
        </h3>
        <div className="flex gap-3">
          <input
            type="text"
            value={lookupId}
            onChange={(e) => setLookupId(e.target.value)}
            placeholder="Product ID"
            className="flex-1 rounded-md border border-[var(--border)] px-3 py-2 text-sm"
          />
          <button
            onClick={handleGet}
            disabled={loading}
            className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50"
          >
            {loading ? "Loading..." : "Get Item"}
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
