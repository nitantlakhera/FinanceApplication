export const DEFAULT_SERVICE_PORTS: Record<string, number> = {
  user: 8081,
  order: 8082,
  payment: 8083,
  inventory: 8084,
  notification: 8085,
  ai: 8086,
};

export interface ServiceConfig {
  host: string;
  port: number;
  useProxy?: boolean;
}

const isDev = import.meta.env.DEV;

export const DEFAULT_CONFIG: Record<string, ServiceConfig> = {
  user: { host: "localhost", port: DEFAULT_SERVICE_PORTS.user, useProxy: isDev },
  order: { host: "localhost", port: DEFAULT_SERVICE_PORTS.order, useProxy: isDev },
  payment: { host: "localhost", port: DEFAULT_SERVICE_PORTS.payment, useProxy: isDev },
  inventory: { host: "localhost", port: DEFAULT_SERVICE_PORTS.inventory, useProxy: isDev },
  notification: { host: "localhost", port: DEFAULT_SERVICE_PORTS.notification, useProxy: isDev },
  ai: { host: "localhost", port: DEFAULT_SERVICE_PORTS.ai, useProxy: isDev },
};
