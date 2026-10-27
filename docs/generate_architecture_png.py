import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch
import os

out_dir = r'C:/Users/nila0425/Desktop/Microservice_Architecture/FinanceApplication/docs'
os.makedirs(out_dir, exist_ok=True)
dpi = 120
fig, ax = plt.subplots(figsize=(2200/dpi, 1300/dpi), dpi=dpi)
ax.set_xlim(0, 100); ax.set_ylim(0, 100); ax.axis('off')

c = {
    'client':'#E0F7FA', 'gw':'#FFF3E0', 'mesh':'#E8F5E9',
    'svc':'#E3F2FD', 'db':'#F3E5F5', 'cache':'#FFEBEE',
    'msg':'#E0E0E0', 'sec':'#FFF8E1', 'obs':'#FBE9E7', 'ext':'#E1F5FE'
}

def box(x,y,w,h,t,color,fs=9,bold=False):
    ax.add_patch(FancyBboxPatch((x-w/2,y-h/2),w,h,boxstyle="round,pad=0.02,rounding_size=0.8",facecolor=color,edgecolor='#555555',linewidth=1.2))
    ax.text(x,y,t,ha='center',va='center',fontsize=fs,weight='bold' if bold else 'normal')

def arr(x1,y1,x2,y2):
    ax.annotate('',xy=(x2,y2),xytext=(x1,y1),arrowprops=dict(arrowstyle='->',color='#555555',lw=1.2,connectionstyle='arc3,rad=0.05'))

box(50,94,16,5,'Client / Browser / Mobile',c['client'],bold=True)
box(50,86,18,5,'External L4/L7 Load Balancer',c['client'],bold=True)
box(50,78,18,5,'API Gateway\nIstio Gateway / Kong',c['gw'],bold=True)
box(12,78,16,5,'Keycloak\nOIDC / JWT',c['sec'],bold=True)

ax.add_patch(FancyBboxPatch((5,30),80,38,boxstyle="round,pad=0.01,rounding_size=1.2",facecolor=c['mesh'],edgecolor='#2E7D32',linewidth=2,linestyle='--'))
ax.text(45,65.5,'Istio Service Mesh (Envoy sidecars, mTLS, L7 policies)',ha='center',va='center',fontsize=11,weight='bold',color='#2E7D32')

box(17,58,14,6,'user-service\n(PostgreSQL, Redis)',c['svc'])
box(38,58,14,6,'order-service\n(MongoDB, Redis, Kafka)',c['svc'])
box(59,58,14,6,'payment-service\n(PostgreSQL, Redis, Kafka)',c['svc'])
box(80,58,14,6,'inventory-service\n(MongoDB, Redis, Kafka)',c['svc'])
box(38,48,14,6,'notification-service\n(PostgreSQL, Redis, Kafka)',c['svc'])

box(17,35,14,5,'PostgreSQL\n16-alpine',c['db'])
box(38,35,12,5,'MongoDB\n7.0',c['db'])
box(59,35,11,5,'Redis\n7-alpine',c['cache'])
box(80,35,13,5,'Apache Kafka\n3.7.0',c['msg'])

box(92,58,14,5,'Prometheus\nGrafana',c['obs'],fs=8.5)
box(92,48,11,5,'Jaeger',c['obs'],fs=8.5)
box(92,38,11,5,'Loki',c['obs'],fs=8.5)

box(22,27,16,5,'Payment Processor\n(e.g. Stripe)',c['ext'],fs=8.5)
box(55,27,16,5,'Notification Provider\n(Email/SMS/Push)',c['ext'],fs=8.5)

box(17,12,16,4,'HPA 3-10 replicas',c['mesh'],fs=8)
box(38,12,16,4,'PDB minAvailable=2',c['mesh'],fs=8)
box(59,12,18,4,'DestinationRule + VirtualService',c['mesh'],fs=8)
box(82,12,12,4,'Kubernetes\n1.28+',c['mesh'],fs=8)

arr(50,91.5,50,88.5); arr(50,83.5,50,80.5); arr(50,75.5,50,68)
arr(20,78,41,78); arr(20,75.5,20,61)
# services to primary data/messaging
arr(17,55,17,37.5); arr(38,55,38,37.5); arr(59,55,59,37.5); arr(80,55,80,37.5)
arr(38,45,17,37.5)  # notification-service -> PostgreSQL
# services publish events to Kafka
arr(38,55,80,40); arr(59,55,80,40); arr(80,55,80,40); arr(38,45,80,40)
# observability collects from mesh
arr(92,58,86,58); arr(92,48,86,48); arr(92,38,86,38)
# external integrations
arr(59,55,22,29.5); arr(38,45,55,29.5)
# platform policies support services
arr(25,14,25,45); arr(46,14,46,45); arr(68,14,68,45)

ax.text(50,98.5,'Finance Application — Architecture',ha='center',va='center',fontsize=18,weight='bold')

diag_path = os.path.join(out_dir, 'tmp_architecture_diagram.png')
plt.tight_layout(pad=0)
plt.savefig(diag_path, dpi=dpi, bbox_inches='tight', pad_inches=0.2, facecolor='white')
plt.close()
print('Diagram saved')

from PIL import Image, ImageDraw, ImageFont
import textwrap

descriptions = [
    ("Client / Browser / Mobile", "The end-user application that sends HTTP requests into the system."),
    ("External L4/L7 Load Balancer", "Distributes incoming traffic across gateway replicas and terminates TLS at the edge (e.g. AWS ALB/NLB, Azure App Gateway, MetalLB)."),
    ("API Gateway — Istio Gateway / Kong", "Single entry point for all clients. Routes requests to the correct microservice and can enforce rate limiting, authentication, and TLS."),
    ("Keycloak", "OIDC/JWT identity provider. Issues access tokens and validates user identity."),
    ("Istio Service Mesh", "A layer of Envoy sidecars that intercept all intra-cluster traffic to provide mTLS, traffic management, retries, timeouts, and telemetry."),
    ("Envoy sidecars", "Istio-injected proxies running next to each app container. They receive dynamic endpoint lists from Istio Pilot via xDS/EDS."),
    ("user-service", "Manages users. Stores data in PostgreSQL and caches reads in Redis."),
    ("order-service", "Creates and tracks orders. Uses MongoDB, Redis cache, and publishes/consumes Kafka events."),
    ("payment-service", "Processes payments. Uses PostgreSQL, Redis, and Kafka; calls external payment processors."),
    ("inventory-service", "Reserves product stock. Uses MongoDB, Redis, and Kafka."),
    ("notification-service", "Listens to events and records notifications. Uses PostgreSQL and Redis."),
    ("PostgreSQL", "Relational database for user, payment, and notification data."),
    ("MongoDB", "Document database for order and inventory data."),
    ("Redis", "In-memory cache used by all services through Spring Cache."),
    ("Apache Kafka", "Event broker that decouples services via topics such as orders.placed, payments.processed, and inventory.reserved."),
    ("Prometheus + Grafana", "Metrics collection and dashboards; Prometheus scrapes each service's /actuator/prometheus endpoint."),
    ("Jaeger", "Distributed tracing backend that collects traces from Micrometer Tracing + Brave."),
    ("Loki", "Centralized log aggregation backend for application logs."),
    ("Payment Processor", "External third-party service (e.g. Stripe, PayPal) invoked by payment-service."),
    ("Notification Provider", "External email/SMS/push provider invoked by notification-service."),
    ("Horizontal Pod Autoscaler (HPA)", "Scales each service Deployment between 3 and 10 replicas based on CPU and memory utilization."),
    ("Pod Disruption Budget (PDB)", "Ensures at least two pods remain available during voluntary disruptions such as node drains or upgrades."),
    ("DestinationRule", "Istio policy defining L7 load balancing (LEAST_CONN), connection pools, locality-aware routing, and outlier detection."),
    ("VirtualService", "Istio policy that configures retries, timeouts, and routing rules for ingress traffic."),
    ("mTLS", "Mutual TLS encrypts and authenticates service-to-service traffic inside the Istio mesh."),
    ("Kubernetes Service / Endpoints", "Kubernetes-native service discovery. Provides stable DNS names and keeps the list of healthy pod endpoints up to date."),
]

panel_width = 2200
margin = 50
line_height = 30
try:
    title_font = ImageFont.truetype("arial.ttf", 22)
    heading_font = ImageFont.truetype("arial.ttf", 18)
    body_font = ImageFont.truetype("arial.ttf", 16)
except Exception:
    title_font = heading_font = body_font = ImageFont.load_default()

def size(draw, text, font):
    return draw.textbbox((0,0), text, font=font)[2:4]

img_temp = Image.new('RGB', (panel_width, 2000), 'white')
draw_temp = ImageDraw.Draw(img_temp)
y = 40
_, th = size(draw_temp, "Component Descriptions", title_font)
y += th + 20

wrapped = []
for title, body in descriptions:
    t = textwrap.fill(title + ":", width=130)
    b = textwrap.fill(body, width=130)
    wrapped.append(("t", t)); wrapped.append(("b", b)); wrapped.append(("s", ""))
    y += (t.count('\n')+1)*line_height + (b.count('\n')+1)*line_height + line_height

panel_height = y + 40

desc_img = Image.new('RGB', (panel_width, panel_height), 'white')
draw = ImageDraw.Draw(desc_img)
y = 40
draw.text((margin, y), "Component Descriptions", fill='black', font=title_font)
y += th + 20

for kind, text in wrapped:
    if kind == "t":
        draw.text((margin, y), text, fill='#1A237E', font=heading_font)
        y += (text.count('\n')+1)*line_height
    elif kind == "b":
        draw.text((margin, y), text, fill='black', font=body_font)
        y += (text.count('\n')+1)*line_height
    else:
        y += line_height//2

final_path = os.path.join(out_dir, 'FinanceApplication-Architecture.png')
diag = Image.open(diag_path)
ratio = panel_width / diag.width
new_h = int(diag.height * ratio)
diag = diag.resize((panel_width, new_h), Image.LANCZOS)

combined = Image.new('RGB', (panel_width, new_h + panel_height), 'white')
combined.paste(diag, (0,0))
combined.paste(desc_img, (0, new_h))
combined.save(final_path, 'PNG')
os.remove(diag_path)
print('Final PNG saved:', final_path)

