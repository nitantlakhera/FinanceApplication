import { useAppState } from "../hooks/useAppState";
import { ConfigPanel } from "../components/ConfigPanel";

export function Settings() {
  const { services, setServices, token, setToken } = useAppState();

  return (
    <div className="mx-auto max-w-3xl">
      <h2 className="mb-6 text-2xl font-bold text-[var(--text)]">Settings</h2>
      <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        <ConfigPanel
          config={services}
          onChange={setServices}
          token={token}
          onTokenChange={setToken}
        />
      </div>
    </div>
  );
}
