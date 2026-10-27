import { useState, ReactNode, useEffect } from "react";
import { AppContext } from "./AppContext";
import { DEFAULT_CONFIG } from "../config";
import { ServiceConfig } from "../types";
import { setAuthToken } from "../api/client";

interface AppProviderProps {
  children: ReactNode;
}

export function AppProvider({ children }: AppProviderProps) {
  const [services, setServices] = useState<Record<string, ServiceConfig>>(DEFAULT_CONFIG);
  const [token, setToken] = useState("");
  const [createdUserId, setCreatedUserId] = useState<string | null>(null);
  const [createdOrderId, setCreatedOrderId] = useState<string | null>(null);
  const [createdProductId, setCreatedProductId] = useState<string | null>(null);

  useEffect(() => {
    setAuthToken(token);
  }, [token]);

  return (
    <AppContext.Provider
      value={{
        services,
        setServices,
        token,
        setToken,
        createdUserId,
        setCreatedUserId,
        createdOrderId,
        setCreatedOrderId,
        createdProductId,
        setCreatedProductId,
      }}
    >
      {children}
    </AppContext.Provider>
  );
}
