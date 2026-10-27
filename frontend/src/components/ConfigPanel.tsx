import { DEFAULT_SERVICE_PORTS } from "../config";
import { ServiceConfig } from "../types";

interface ConfigPanelProps {
  config: Record<string, ServiceConfig>;
  onChange: (config: Record<string, ServiceConfig>) => void;
  token: string;
  onTokenChange: (token: string) => void;
}

const SERVICES = [
  { key: "user", label: "User Service" },
  { key: "order", label: "Order Service" },
  { key: "payment", label: "Payment Service" },
  { key: "inventory", label: "Inventory Service" },
  { key: "notification", label: "Notification Service" },
  { key: "ai", label: "AI Service" },
];

export function ConfigPanel({
  config,
  onChange,
  token,
  onTokenChange,
}: ConfigPanelProps) {
  const updateService = (key: string, field: keyof ServiceConfig, value: string | number) => {
    onChange({
      ...config,
      [key]: { ...config[key], [field]: value },
    });
  };

  const resetDefaults = () => {
    const defaults: Record<string, ServiceConfig> = {};
    SERVICES.forEach(({ key }) => {
      defaults[key] = { host: "localhost", port: DEFAULT_SERVICE_PORTS[key] };
    });
    onChange(defaults);
  };

  return (
    <div className="space-y-6">
      <div>
        <label className="mb-1 block text-sm font-medium text-[var(--text)]">
          JWT Token
        </label>
        <input
          type="password"
          value={token}
          onChange={(e) => onTokenChange(e.target.value)}
          placeholder="Leave empty when security is disabled"
          className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm focus:border-[var(--primary)] focus:outline-none focus:ring-1 focus:ring-[var(--primary)]"
        />
        <p className="mt-1 text-xs text-[var(--muted)]">
          Used when app.security.enabled=true
        </p>
      </div>

      <div>
        <div className="mb-2 flex items-center justify-between">
          <h3 className="font-semibold text-[var(--text)]">Service Endpoints</h3>
          <div className="flex items-center gap-3">
            <label className="flex items-center gap-2 text-xs text-[var(--muted)]">
              <input
                type="checkbox"
                checked={!!config[SERVICES[0].key].useProxy}
                onChange={(e) => {
                  const next: Record<string, ServiceConfig> = {};
                  SERVICES.forEach(({ key }) => {
                    next[key] = { ...config[key], useProxy: e.target.checked };
                  });
                  onChange(next);
                }}
                className="h-4 w-4 rounded border-gray-300 text-[var(--primary)]"
              />
              Use Vite proxy (dev)
            </label>
            <button
              type="button"
              onClick={resetDefaults}
              className="text-xs text-[var(--primary)] hover:underline"
            >
              Reset defaults
            </button>
          </div>
        </div>
        <div className="space-y-3">
          {SERVICES.map(({ key, label }) => (
            <div key={key} className="grid grid-cols-3 gap-3">
              <div className="col-span-1 flex items-center text-sm font-medium text-[var(--text)]">
                {label}
              </div>
              <input
                type="text"
                value={config[key].host}
                onChange={(e) => updateService(key, "host", e.target.value)}
                placeholder="host"
                className="rounded-md border border-[var(--border)] px-3 py-2 text-sm focus:border-[var(--primary)] focus:outline-none focus:ring-1 focus:ring-[var(--primary)]"
              />
              <input
                type="number"
                value={config[key].port}
                onChange={(e) =>
                  updateService(key, "port", parseInt(e.target.value, 10))
                }
                placeholder="port"
                min={1}
                max={65535}
                className="rounded-md border border-[var(--border)] px-3 py-2 text-sm focus:border-[var(--primary)] focus:outline-none focus:ring-1 focus:ring-[var(--primary)]"
              />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
