import { createContext, Dispatch, SetStateAction } from "react";
import { ServiceConfig } from "../types";

export interface AppState {
  services: Record<string, ServiceConfig>;
  setServices: Dispatch<SetStateAction<Record<string, ServiceConfig>>>;
  token: string;
  setToken: Dispatch<SetStateAction<string>>;
  createdUserId: string | null;
  setCreatedUserId: (id: string | null) => void;
  createdOrderId: string | null;
  setCreatedOrderId: (id: string | null) => void;
  createdProductId: string | null;
  setCreatedProductId: (id: string | null) => void;
}

export const AppContext = createContext<AppState | undefined>(undefined);

