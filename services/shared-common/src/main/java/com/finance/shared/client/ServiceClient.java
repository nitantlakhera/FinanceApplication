package com.finance.shared.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Convenience client for making load-balanced HTTP calls between microservices.
 * <p>
 * Example usage:
 * <pre>
 *   UserDto user = serviceClient.get("user-service", "/users/" + id, UserDto.class);
 * </pre>
 */
@Service
public class ServiceClient {

    private final RestTemplate restTemplate;

    public ServiceClient(@LoadBalanced RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T get(String serviceId, String path, Class<T> responseType, Object... uriVariables) {
        return restTemplate.getForObject(buildUri(serviceId, path), responseType, uriVariables);
    }

    public <T> T get(String serviceId, String path, ParameterizedTypeReference<T> responseType) {
        ResponseEntity<T> response = restTemplate.exchange(buildUri(serviceId, path), HttpMethod.GET, null, responseType);
        return response.getBody();
    }

    public <T> T post(String serviceId, String path, Object request, Class<T> responseType) {
        return restTemplate.postForObject(buildUri(serviceId, path), request, responseType);
    }

    public <T> ResponseEntity<T> exchange(String serviceId, String path, HttpMethod method,
                                           HttpEntity<?> requestEntity, Class<T> responseType) {
        return restTemplate.exchange(buildUri(serviceId, path), method, requestEntity, responseType);
    }

    private String buildUri(String serviceId, String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return "http://" + serviceId + normalizedPath;
    }
}
