package com.finance.shared.loadbalancer;

import com.finance.shared.config.LoadBalancerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that Spring Cloud LoadBalancer distributes requests across the configured
 * service instances. Static service instances are used here so the test runs without
 * a live discovery backend such as Eureka or Kubernetes.
 */
@SpringBootTest(
        classes = LoadBalancerClientTest.TestConfig.class,
        properties = {
                "spring.cloud.discovery.client.simple.instances.user-service[0].uri=http://localhost:8100",
                "spring.cloud.discovery.client.simple.instances.user-service[1].uri=http://localhost:8101"
        }
)
class LoadBalancerClientTest {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Test
    void shouldDistributeRequestsAcrossMultipleUserServiceInstances() {
        Set<String> chosen = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            ServiceInstance instance = loadBalancerClient.choose("user-service");
            assertThat(instance).isNotNull();
            chosen.add(instance.getUri().toString());
        }

        assertThat(chosen).containsExactlyInAnyOrder(
                "http://localhost:8100",
                "http://localhost:8101"
        );
    }

    @EnableAutoConfiguration
    @Import(LoadBalancerConfig.class)
    static class TestConfig {
    }
}
