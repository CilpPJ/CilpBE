package com.clip.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
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
                description = """
        이 API는 <b>JWT + Cookie 기반 인증</b>을 사용합니다.<br><br>

        <b>🔐 인증 절차:</b><br>
        1️⃣ <code>/api/auth/login</code> API를 호출해 로그인합니다.<br>
        &nbsp;&nbsp;→ 성공 시, <code>accessToken</code>이 쿠키로 발급됩니다.<br><br>

        2️⃣ 이후 인증이 필요한 API를 호출하면,<br>
        &nbsp;&nbsp;→ 브라우저에서 자동으로 쿠키를 전송하여 인증됩니다.<br><br>

        <b>⚙️ Swagger에서도 쿠키 인증이 작동하는 이유</b><br>
        ✔️ <code>springdoc.swagger-ui.with-credentials=true</code> 설정으로,<br>
        &nbsp;&nbsp;&nbsp;&nbsp;Swagger가 Ajax 요청 시 쿠키를 자동 포함합니다.<br>
        ✔️ <code>allowedOrigins</code> 및 <code>allowCredentials</code> 설정으로,<br>
        &nbsp;&nbsp;&nbsp;&nbsp;CORS 허용 + 쿠키 허용을 Spring에서 처리합니다.<br>
        ✔️ Nginx에서 <code>Origin</code> 등의 헤더를 백엔드로 정확히 전달합니다.<br><br>

        이 조합을 통해, <b>Swagger UI에서도 쿠키 기반 인증이 완전히 지원</b>됩니다.<br>
        <i>(도메인과 포트가 일치해야 하며, HTTPS 환경에서는 Secure 설정도 필요합니다)</i>
        """
        ),
        servers = {
//        @Server(url = "https://www.clipin.store", description = "Production Server")
}
)
public class OpenAPIConfig {

}

