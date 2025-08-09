package com.clip.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.examples.Example;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger / OpenAPI 설정 (프론트 친화)
 * - 쿠키 기반 JWT 인증(cookieAuth)
 * - 공통 응답 스키마(Response_Success / Response_Error)
 * - 전 API 공통 에러 응답 자동 부착(400/401/403/404/409/413/429/500) + 상태코드별 예시
 */
@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {

                String socialLoginGuide = """
                        ## 📌 소셜 로그인 테스트 방법
                        Swagger에서 직접 실행(`Try it out`)은 OAuth2 리다이렉트 흐름 특성상 동작하지 않습니다.  
                        대신 **브라우저 주소창**에서 아래 URL로 이동하세요:
                        
                        - 로컬 Google 로그인: [http://localhost:8080/oauth2/authorization/google](http://localhost:8080/oauth2/authorization/google)  
                        - 로컬 Kakao 로그인: [http://localhost:8080/oauth2/authorization/kakao](http://localhost:8080/oauth2/authorization/kakao)  
                        - 운영 Google 로그인: [https://www.clipin.store/oauth2/authorization/google](https://www.clipin.store/oauth2/authorization/google)  
                        - 운영 Kakao 로그인: [https://www.clipin.store/oauth2/authorization/kakao](https://www.clipin.store/oauth2/authorization/kakao)  
                        
                        로그인 성공 시 JWT Access/Refresh Token이 **쿠키**로 저장되고,  
                        벡엔드 환경변수 `app.frontend.redirect.*`에 지정된 프론트 주소로 리다이렉트됩니다.(로컬에선 로컬 프론트로, 배포에선 배포 프론트로)
                        
                         ## 📌 예외 코드 처리
                        각 api를 열어보면 Responses Code마다 예시가 하나씩 들어있습니다
                        
                        ## 📌 스웨거 테스트시 유의점
                        바로 밑에 보이는 Servers에서 로컬환경이면 로컬주소를 선택해서, 배포환경이면 배포주소를 선택하고 테스트해주세요.
                        """;

                // 쿠키 기반 인증 스키마
                SecurityScheme cookieScheme = new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("accessToken")
                        .description("JWT Access Token (Cookie)");

                return new OpenAPI()
                        .info(new Info()
                                .title("Clip API")
                                .version("v1.0")
                                .description(socialLoginGuide))
                        .servers(List.of(
                                new Server().url("https://www.clipin.store").description("Production Server (HTTPS)"),
                                new Server().url("http://localhost:8080").description("Local Development Server (HTTP)")
                        ))
                        .components(new Components()
                                .addSecuritySchemes("cookieAuth", cookieScheme));
        }

        /**
         * 공통 스키마 존재 보장 + 전 API 공통 에러 응답 일괄 추가
         * (그룹 문서/빈 초기화 순서 이슈 대응 + 상태코드별 예시 부착)
         */
        @Bean
        public OpenApiCustomizer globalErrorResponses() {
                return openApi -> {
                        // --- 스키마 존재 보장 ---
                        if (openApi.getComponents() == null) {
                                openApi.setComponents(new Components());
                        }
                        var components = openApi.getComponents();
                        if (components.getSchemas() == null) {
                                components.setSchemas(new LinkedHashMap<>());
                        }
                        var schemas = components.getSchemas();

                        // 성공 응답 포맷 (설명용)
                        schemas.putIfAbsent("Response_Success",
                                new Schema<>().type("object")
                                        .addProperties("status", new StringSchema().example("SUCCESS"))
                                        .addProperties("serverDateTime", new StringSchema().example("2025-08-09T12:34:56.789"))
                                        .addProperties("errorCode", new StringSchema().nullable(true).example(null))
                                        .addProperties("errorMessage", new StringSchema().nullable(true).example(null))
                                        .addProperties("data", new ObjectSchema().description("응답 데이터 페이로드"))
                        );

                        // 에러 응답 포맷 (설명용)
                        schemas.putIfAbsent("Response_Error",
                                new Schema<>().type("object")
                                        .addProperties("status", new StringSchema().example("ERROR"))
                                        .addProperties("serverDateTime", new StringSchema().example("2025-08-09T12:34:56.789"))
                                        .addProperties("errorCode", new StringSchema().example("에러코드(대문자 문자열)"))
                                        .addProperties("errorMessage", new StringSchema().example("에러메시지"))
                                        .addProperties("data", new ObjectSchema().nullable(true).example(null))
                        );

                        // --- 공통 에러 응답 부착 (상태코드별 예시) ---
                        if (openApi.getPaths() == null) return;

                        openApi.getPaths().values().forEach(pathItem ->
                                pathItem.readOperations().forEach(op -> {
                                        op.getResponses().addApiResponse("400",
                                                errorResponse("잘못된 요청(Validation/Binding)", "REQ.VALIDATION", "입력값을 확인해주세요."));
                                        op.getResponses().addApiResponse("401",
                                                errorResponse("인증 필요", "AUTH.UNAUTHORIZED", "로그인이 필요합니다."));
                                        op.getResponses().addApiResponse("403",
                                                errorResponse("권한 없음", "AUTH.FORBIDDEN", "접근 권한이 없습니다."));
                                        op.getResponses().addApiResponse("404",
                                                errorResponse("리소스 없음", "DATA.NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."));
                                        op.getResponses().addApiResponse("409",
                                                errorResponse("중복/제약 위반", "DATA.DUPLICATE", "이미 존재하는 데이터입니다."));
                                        op.getResponses().addApiResponse("413",
                                                errorResponse("업로드 용량 초과", "UPLOAD.TOO_LARGE", "파일 용량 제한을 초과했습니다."));
                                        op.getResponses().addApiResponse("500",
                                                errorResponse("백엔드 서버 내부 오류", "SYS.INTERNAL", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
                                })
                        );
                };
        }

        // --- util: 상태코드/설명/에러코드/메시지를 받아 예시 포함 ApiResponse 생성 ---
        private ApiResponse errorResponse(String description, String code, String message) {
                boolean is500 = "SYS.INTERNAL".equals(code); // 또는 description/코드로 판별
                Map<String, Object> exampleMap = new LinkedHashMap<>();
                exampleMap.put("status", is500 ? "ERROR" : "FAIL");  // 4xx=FAIL, 5xx=ERROR
                exampleMap.put("serverDateTime", "2025-08-09T12:34:56.789");
                exampleMap.put("errorCode", code);
                exampleMap.put("errorMessage", message);
                exampleMap.put("data", null);

                Example example = new Example()
                        .summary("예시")
                        .value(exampleMap);

                MediaType mediaType = new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/Response_Error"))
                        .addExamples("default", example);

                Content content = new Content().addMediaType("application/json", mediaType);

                return new ApiResponse().description(description).content(content);
        }
}
