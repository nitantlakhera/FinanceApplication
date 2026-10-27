# Finance Application Frontend

A React + TypeScript + Vite frontend for the FinanceApplication microservices.

## Tech Stack

- **React 19** with TypeScript
- **Vite 8** for bundling and dev server
- **React Router** for navigation
- **Tailwind CSS v4** for styling
- **Axios** for HTTP requests

## Features

- Dashboard with created entity IDs (user, order, product)
- Per-service pages:
  - **Users**: create and fetch users
  - **Inventory**: add/list/get stock
  - **Orders**: place orders and look them up
  - **Payments**: list and fetch payments
  - **Notifications**: list and fetch notifications
  - **AI Tools**: fraud check, summarize, RAG Q&A, document ingestion, provider info
- Settings panel with configurable service hosts/ports
- Optional JWT token for secured backends
- Vite dev-server proxy to avoid CORS during local development

## Local Development

```bash
cd frontend
npm install
npm run dev
```

The app will be available at `http://localhost:5173`.

By default, the frontend uses Vite's proxy (`/api/{service}`) to reach the backend services running on `localhost:8081-8086`. Toggle **Use Vite proxy (dev)** in Settings if you prefer direct cross-origin requests (requires CORS on the backend).

## Service Ports

| Service      | Default Port |
|--------------|--------------|
| user         | 8081         |
| order        | 8082         |
| payment      | 8083         |
| inventory    | 8084         |
| notification | 8085         |
| ai           | 8086         |

## Build

```bash
npm run build
```

Static output is written to `dist/`.

## Docker

```bash
# From the repository root
docker-compose up --build frontend
```

The frontend is then served on `http://localhost:3000`.

