import { NavLink, Outlet } from "react-router-dom";

interface NavItem {
  to: string;
  label: string;
  icon: React.ReactNode;
}

const iconClass = "h-4.5 w-4.5";

const navItems: NavItem[] = [
  { to: "/", label: "Dashboard", icon: <svg className={iconClass} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/></svg> },
  { to: "/users", label: "Users", icon: <svg className={iconClass} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg> },
  { to: "/inventory", label: "Inventory", icon: <svg className={iconClass} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="m7.5 4.27 9 5.15"/><path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/></svg> },
  { to: "/orders", label: "Orders", icon: <svg className={iconClass} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="8" cy="21" r="1"/><circle cx="19" cy="21" r="1"/><path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"/></svg> },
  { to: "/payments", label: "Payments", icon: <svg className={iconClass} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect width="20" height="14" x="2" y="5" rx="2"/><line x1="2" x2="22" y1="10" y2="10"/></svg> },
  { to: "/notifications", label: "Notifications", icon: <svg className={iconClass} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></svg> },
  { to: "/ai", label: "AI Tools", icon: <svg className={iconClass} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 8V4H8"/><rect width="16" height="12" x="4" y="8" rx="2"/><path d="M2 14h2"/><path d="M20 14h2"/><path d="M15 13v2"/><path d="M9 13v2"/></svg> },
  { to: "/settings", label: "Settings", icon: <svg className={iconClass} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1.43 1.93 2 2 0 0 1-2.2-.37l-.27-.27a2 2 0 0 0-2.83 0l-.66.66a2 2 0 0 0 0 2.83l.27.27a2 2 0 0 1 .37 2.2 2 2 0 0 1-1.93 1.43H3a2 2 0 0 0-2 2v.44a2 2 0 0 0 2 2h.18a2 2 0 0 1 1.93 1.43 2 2 0 0 1-.37 2.2l-.27.27a2 2 0 0 0 0 2.83l.66.66a2 2 0 0 0 2.83 0l.27-.27a2 2 0 0 1 2.2-.37 2 2 0 0 1 1.43 1.93V21a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1.43-1.93 2 2 0 0 1 2.2.37l.27.27a2 2 0 0 0 2.83 0l.66-.66a2 2 0 0 0 0-2.83l-.27-.27a2 2 0 0 1-.37-2.2 2 2 0 0 1 1.93-1.43H21a2 2 0 0 0 2-2v-.44a2 2 0 0 0-2-2h-.18a2 2 0 0 1-1.93-1.43 2 2 0 0 1 .37-2.2l.27-.27a2 2 0 0 0 0-2.83l-.66-.66a2 2 0 0 0-2.83 0l-.27.27a2 2 0 0 1-2.2.37 2 2 0 0 1-1.43-1.93V3a2 2 0 0 0-2-2Z"/><circle cx="12" cy="12" r="3"/></svg> },
];

export function Layout() {
  return (
    <div className="flex h-screen bg-[var(--bg)]">
      <aside className="w-64 flex-shrink-0 border-r border-[var(--border)] bg-[var(--card)]">
        <div className="border-b border-[var(--border)] px-6 py-5">
          <h1 className="text-xl font-bold text-[var(--primary)]">
            ?? Finance App
          </h1>
          <p className="mt-1 text-xs text-[var(--muted)]">Microservices UI</p>
        </div>
        <nav className="p-4">
          <ul className="space-y-1">
            {navItems.map((item) => (
              <li key={item.to}>
                <NavLink
                  to={item.to}
                  end={item.to === "/"}
                  className={({ isActive }) =>
                    `flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors ${
                      isActive
                        ? "bg-blue-50 text-[var(--primary)]"
                        : "text-[var(--muted)] hover:bg-gray-50 hover:text-[var(--text)]"
                    }`
                  }
                >
                  {item.icon}
                  {item.label}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>
      </aside>
      <main className="flex flex-1 flex-col overflow-hidden">
        <div className="flex-1 overflow-auto p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

