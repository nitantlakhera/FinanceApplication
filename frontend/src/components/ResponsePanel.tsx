import { ApiResponse } from "../types";

interface ResponsePanelProps {
  response: ApiResponse<unknown> | null;
  title?: string;
}

export function ResponsePanel({ response, title = "Response" }: ResponsePanelProps) {
  if (!response) return null;

  return (
    <div className="mt-6 rounded-lg border border-[var(--border)] bg-[var(--card)] p-4 shadow-sm">
      <div className="mb-3 flex items-center justify-between">
        <h3 className="text-lg font-semibold text-[var(--text)]">{title}</h3>
        <span
          className={`rounded-full px-3 py-1 text-sm font-medium ${
            response.ok
              ? "bg-green-100 text-green-800"
              : "bg-red-100 text-red-800"
          }`}
        >
          {response.ok ? `HTTP ${response.status}` : "Error"}
        </span>
      </div>
      {response.ok ? (
        <pre className="max-h-96 overflow-auto rounded-md bg-gray-50 p-3 text-sm">
          {JSON.stringify(response.data, null, 2)}
        </pre>
      ) : (
        <div className="rounded-md bg-red-50 p-3 text-sm text-red-800">
          {response.error || `Request failed with status ${response.status}`}
        </div>
      )}
    </div>
  );
}
