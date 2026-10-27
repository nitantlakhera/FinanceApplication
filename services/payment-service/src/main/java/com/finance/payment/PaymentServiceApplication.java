package com.finance.payment;

import com.finance.shared.client.ServiceClient;
import com.finance.shared.config.CacheConfig;
import com.finance.shared.config.LoadBalancerConfig;
import com.finance.shared.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SecurityConfig.class, CacheConfig.class, LoadBalancerConfig.class, ServiceClient.class})
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
