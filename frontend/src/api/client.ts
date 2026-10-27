import axios, { AxiosError, AxiosRequestConfig } from "axios";
import { ApiResponse, ServiceConfig } from "../types";

let authToken = "";

export function setAuthToken(token: string) {
  authToken = token;
}

export function buildUrl(
  serviceKey: string,
  config: ServiceConfig,
  path: string
): string {
  const normalized = path.startsWith("/") ? path : `/${path}`;
  if (config.useProxy) {
    return `/api/${serviceKey}${normalized}`;
  }
  return `http://${config.host}:${config.port}${normalized}`;
}

export async function apiCall<T = unknown>(
  method: "get" | "post" | "put" | "patch" | "delete",
  url: string,
  payload?: unknown,
  timeout = 10000
): Promise<ApiResponse<T>> {
  const config: AxiosRequestConfig = {
    method,
    url,
    timeout,
    headers: {
      "Content-Type": "application/json",
    },
  };

  if (authToken.trim()) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${authToken.trim()}`,
    };
  }

  if (payload !== undefined) {
    config.data = payload;
  }

  try {
    const response = await axios(config);
    return {
      ok: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    const axiosError = error as AxiosError;
    return {
      ok: false,
      status: axiosError.response?.status ?? 0,
      error:
        (axiosError.response?.data as string | undefined) ||
        axiosError.message ||
        "Unknown error",
    };
  }
}
