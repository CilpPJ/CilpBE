package com.clip.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Clip API",
                version = "v1.0",
                description = "**JWT 인증 사용법** <br><br>"
                        + "1️⃣ 먼저 <code>/api/auth/login</code> API를 호출해 JWT 토큰을 받습니다. <br>"
                        + "2️⃣ 우측 상단의 <b>Authorize</b> 버튼을 클릭합니다. <br>"
                        + "3️⃣ <code>Bearer &lt;토큰값&gt;</code> 형식으로 입력 후 인증합니다. <br>"
                        + "4️⃣ 이후 인증이 필요한 API를 호출하면 자동으로 토큰이 포함됩니다. <br>"
        )
)
@Configuration
public class OpenAPIConfig {
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .components(new Components().addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                        ));
        }
}
