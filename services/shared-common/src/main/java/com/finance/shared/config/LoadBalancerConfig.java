package com.finance.shared.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Exposes a {@link RestTemplate} that is intercepted by Spring Cloud LoadBalancer.
 * <p>
 * Services can inject this bean and call other services by logical service ID, e.g.:
 * <pre>restTemplate.getForObject("http://user-service/users/{id}", User.class, id);</pre>
 * The load balancer resolves {@code user-service} to one of the registered instances.
 * <p>
 * In Kubernetes with Istio, Envoy sidecars already load-balance traffic at the mesh layer.
 * This configuration is primarily useful for local development and for explicit client-side
 * load-balancing when calls leave the mesh.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "app.load-balancer.enabled", havingValue = "true", matchIfMissing = true)
public class LoadBalancerConfig {

    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }
}
