import { useAppState } from "../hooks/useAppState";

export function Dashboard() {
  const { createdUserId, createdOrderId, createdProductId } = useAppState();

  return (
    <div className="mx-auto max-w-4xl">
      <h2 className="mb-2 text-2xl font-bold text-[var(--text)]">Dashboard</h2>
      <p className="mb-6 text-[var(--muted)]">
        Use the sidebar to navigate between microservice sections.
      </p>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-5 shadow-sm">
          <p className="text-sm text-[var(--muted)]">Created User ID</p>
          <p className="mt-1 break-all font-mono text-sm text-[var(--text)]">
            {createdUserId || "—"}
          </p>
        </div>
        <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-5 shadow-sm">
          <p className="text-sm text-[var(--muted)]">Created Order ID</p>
          <p className="mt-1 break-all font-mono text-sm text-[var(--text)]">
            {createdOrderId || "—"}
          </p>
        </div>
        <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-5 shadow-sm">
          <p className="text-sm text-[var(--muted)]">Created Product ID</p>
          <p className="mt-1 break-all font-mono text-sm text-[var(--text)]">
            {createdProductId || "—"}
          </p>
        </div>
      </div>

      <div className="mt-6 rounded-lg border border-blue-100 bg-blue-50 p-4 text-sm text-blue-800">
        <strong>Tip:</strong> Start with <strong>Users</strong> →{" "}
        <strong>Inventory</strong> → <strong>Orders</strong> to exercise the
        full order flow.
      </div>
    </div>
  );
}
