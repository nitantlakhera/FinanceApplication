package com.finance.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiModelRouter {

    private static final Logger log = LoggerFactory.getLogger(AiModelRouter.class);

    private final String provider;

    public AiModelRouter(@Value("${ai.model.provider:kimi}") String provider) {
        this.provider = provider;
        log.info("AI model provider active: {}", provider);
    }

    public String getProvider() {
        return this.provider;
    }
}
