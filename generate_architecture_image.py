import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch

C_CLIENT = "#E8F5E9"; C_GATEWAY = "#C8E6C9"; C_IDENTITY = "#FFECB3"
C_SERVICE = "#BBDEFB"; C_DB = "#FFCCBC"; C_CACHE = "#E1BEE7"
C_MQ = "#D1C4E9"; C_OBS = "#B2DFDB"; C_TEXT = "#212121"

fig, ax = plt.subplots(figsize=(18, 13), dpi=150)
ax.set_xlim(0, 18); ax.set_ylim(0, 13); ax.axis("off")

def box(x, y, w, h, label, color, fs=9):
    b = FancyBboxPatch((x-w/2, y-h/2), w, h, boxstyle="round,pad=0.02,rounding_size=0.2",
                       facecolor=color, edgecolor=C_TEXT, linewidth=1.5)
    ax.add_patch(b)
    ax.text(x, y, label, ha="center", va="center", fontsize=fs, color=C_TEXT, weight="bold")

def arrow(start, end, color="#757575", rad=0):
    ax.annotate("", xy=end, xytext=start,
                arrowprops=dict(arrowstyle="-|>", color=color, lw=1.5, connectionstyle=f"arc3,rad={rad}"))

# boundaries
ax.add_patch(FancyBboxPatch((0.4, 0.4), 17.2, 12.2, boxstyle="round,pad=0.02,rounding_size=0.3",
                            facecolor="none", edgecolor="#424242", linewidth=2, linestyle="--"))
ax.text(1.0, 12.2, "Kubernetes Cluster (finance namespace)", fontsize=11, color="#424242", weight="bold")
ax.add_patch(FancyBboxPatch((0.8, 4.0), 16.4, 6.4, boxstyle="round,pad=0.02,rounding_size=0.3",
                            facecolor="#ECEFF1", edgecolor="#607D8B", linewidth=1.5, alpha=0.4))
ax.text(1.1, 10.1, "Istio Service Mesh (mTLS)", fontsize=10, color="#607D8B", weight="bold")

# top row
client=(3.0,11.5); gateway=(9.0,11.5); keycloak=(15.0,11.5)
box(*client, 2.2, 0.9, "Client / Mobile App", C_CLIENT, 10)
box(*gateway, 3.0, 0.9, "API Gateway\n(Kong / Istio Ingress)", C_GATEWAY, 9)
box(*keycloak, 2.4, 0.9, "Keycloak\nOIDC / JWT", C_IDENTITY, 9)
arrow((client[0]+1.1, client[1]), (gateway[0]-1.5, gateway[1]))
arrow((keycloak[0]-1.2, keycloak[1]), (gateway[0]+1.5, gateway[1]))

# services
services={"user-service":(2.5,8.5), "order-service":(5.5,8.5), "payment-service":(8.5,8.5),
          "inventory-service":(11.5,8.5), "notification-service":(14.5,8.5)}
for n,(x,y) in services.items(): box(x,y,2.4,1.1,n.replace("-","\n"),C_SERVICE,9)

# gateway to services
arrow((gateway[0],gateway[1]-0.45),(gateway[0],9.2))
for x,y in services.values(): arrow((gateway[0],9.2),(x,y+0.55),rad=0.05)

# data layer
dbs={"PostgreSQL":(3.5,5.0),"MongoDB":(7.0,5.0),"Redis Cache":(10.5,5.0),"Apache Kafka":(14.5,5.0)}
for n,(x,y) in dbs.items():
    c=C_CACHE if "Redis" in n else C_MQ if "Kafka" in n else C_DB
    box(x,y,2.6,1.0,n,c,10)

# service -> db
arrow(services["user-service"],dbs["PostgreSQL"],rad=0.1)
arrow(services["payment-service"],dbs["PostgreSQL"],rad=-0.05)
arrow(services["notification-service"],(dbs["PostgreSQL"][0]+0.9,dbs["PostgreSQL"][1]+0.5),rad=-0.15)
arrow(services["order-service"],dbs["MongoDB"],rad=0.05)
arrow(services["inventory-service"],dbs["MongoDB"],rad=-0.05)

# all services -> redis
for x,y in services.values(): arrow((x,y-0.55),(dbs["Redis Cache"][0],dbs["Redis Cache"][1]+0.5),"#8E24AA",0.08)

# kafka
for svc in ["order-service","payment-service","inventory-service"]:
    sx,sy=services[svc]
    arrow((sx+0.8,sy-0.55),(dbs["Apache Kafka"][0]-1.0,dbs["Apache Kafka"][1]),"#5E35B1",0.05)
for svc in ["payment-service","inventory-service","notification-service","order-service"]:
    sx,sy=services[svc]
    arrow((dbs["Apache Kafka"][0]+1.0,dbs["Apache Kafka"][1]+0.4),(sx+0.8,sy-0.55),"#5E35B1",0.05)

# observability
obs={"Prometheus +\nGrafana":(3.5,1.8),"Jaeger":(8.5,1.8),"Loki":(13.5,1.8)}
for n,(x,y) in obs.items(): box(x,y,2.6,1.0,n,C_OBS,10)
for x,y in services.values():
    arrow((x-0.8,y-0.55),(obs["Prometheus +\nGrafana"][0],obs["Prometheus +\nGrafana"][1]+0.5),"#00695C",-0.1)
    arrow((x,y-0.55),(obs["Jaeger"][0],obs["Jaeger"][1]+0.5),"#00695C",0.05)
    arrow((x+0.8,y-0.55),(obs["Loki"][0],obs["Loki"][1]+0.5),"#00695C",0.1)

# title & legend
ax.text(9,12.7,"Finance Application — Microservices Architecture",fontsize=16,ha="center",weight="bold",color=C_TEXT)
legend=[("Client / Mobile App",C_CLIENT),("API Gateway",C_GATEWAY),("Identity",C_IDENTITY),
        ("Spring Boot Service",C_SERVICE),("Database",C_DB),("Cache",C_CACHE),("Message Bus",C_MQ),("Observability",C_OBS)]
for i,(l,c) in enumerate(legend):
    lx=1.2+(i%4)*4.0; ly=0.85-(i//4)*0.5
    ax.add_patch(mpatches.Rectangle((lx,ly),0.35,0.25,facecolor=c,edgecolor=C_TEXT,linewidth=1))
    ax.text(lx+0.45,ly+0.12,l,fontsize=8,va="center",color=C_TEXT)

plt.tight_layout()
out="C:/Users/nila0425/Desktop/Microservice_Architecture/FinanceApplication/HighEndMicroservicedesign.png"
plt.savefig(out,bbox_inches="tight",pad_inches=0.2,facecolor="white")
print(f"Saved: {out}")
