package com.clip.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Clip API",
                description = "자동 요약 클립 공유 플랫폼",
                version = "v1.0"
        )
)
@Configuration
public class OpenAPIConfig {
}
