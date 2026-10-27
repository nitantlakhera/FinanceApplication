import { useState } from "react";
import { userService } from "../api/services";
import { useAppState } from "../hooks/useAppState";
import { ResponsePanel } from "../components/ResponsePanel";
import { ApiResponse, ServiceConfig, User } from "../types";

export function Users() {
  const { services, setCreatedUserId } = useAppState();
  const [email, setEmail] = useState("alice@example.com");
  const [firstName, setFirstName] = useState("Alice");
  const [lastName, setLastName] = useState("Smith");
  const [userId, setUserId] = useState("");
  const [response, setResponse] = useState<ApiResponse<User> | null>(null);
  const [loading, setLoading] = useState(false);

  const service = userService(services.user as ServiceConfig);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    const result = await service.create(email, firstName, lastName);
    setResponse(result);
    if (result.ok && result.data?.id) {
      setCreatedUserId(result.data.id);
    }
    setLoading(false);
  };

  const handleGet = async () => {
    if (!userId.trim()) return;
    setLoading(true);
    const result = await service.getById(userId.trim());
    setResponse(result);
    setLoading(false);
  };

  return (
    <div className="mx-auto max-w-3xl">
      <h2 className="mb-6 text-2xl font-bold text-[var(--text)]">Users</h2>

      <div className="rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-semibold text-[var(--text)]">
          Create User
        </h3>
        <form onSubmit={handleCreate} className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
            <div>
              <label className="mb-1 block text-sm font-medium text-[var(--text)]">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-[var(--text)]">
                First Name
              </label>
              <input
                type="text"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                required
                className="w-full rounded-md border border-[var(--border)] px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-[var(--text)]">
                Last Name
              </label>
              <input
                type="text"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
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
            {loading ? "Creating..." : "Create User"}
          </button>
        </form>
      </div>

      <div className="mt-6 rounded-lg border border-[var(--border)] bg-[var(--card)] p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-semibold text-[var(--text)]">
          Get User by ID
        </h3>
        <div className="flex gap-3">
          <input
            type="text"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            placeholder="User ID"
            className="flex-1 rounded-md border border-[var(--border)] px-3 py-2 text-sm"
          />
          <button
            onClick={handleGet}
            disabled={loading}
            className="rounded-md bg-[var(--primary)] px-4 py-2 text-sm font-medium text-white hover:bg-[var(--primary-dark)] disabled:opacity-50"
          >
            {loading ? "Loading..." : "Get User"}
          </button>
        </div>
      </div>

      <ResponsePanel response={response} />
    </div>
  );
}
