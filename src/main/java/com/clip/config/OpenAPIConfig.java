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
        1️⃣ <code>/api/auth/login</code> API 호출 시 로그인 후 <code>accessToken</code> 쿠키가 발급됩니다.<br>
        2️⃣ 인증이 필요한 API 호출 시 브라우저에서 자동으로 쿠키를 전송하여 인증합니다.<br><br>

        <b>⚙️ Swagger에서도 쿠키 인증 지원</b><br>
        ✔️ <code>springdoc.swagger-ui.with-credentials=true</code> 설정으로 Ajax 요청 시 쿠키 자동 포함.<br>
        ✔️ 백엔드에서 CORS 및 allowCredentials 허용 처리.<br>
        ✔️ HTTPS 환경에서는 Secure 쿠키 설정 필요.<br><br>

        <hr/>
        <b>🚨 글로벌 예외 응답 코드</b><br>
        모든 API는 아래 예외를 공통적으로 반환할 수 있습니다.<br><br>

        <ul>
          <li><b>400 BAD_REQUEST</b>
            <ul>
              <li><code>VALIDATION_ERROR</code> - @Valid / 유효성 검사 실패</li>
              <li><code>CONSTRAINT_VIOLATION</code> - GET 파라미터 유효성 실패</li>
              <li><code>BIND_ERROR</code> - 요청 DTO 바인딩 실패</li>
              <li><code>MISSING_PARAMETER</code> - 필수 요청 파라미터 누락</li>
              <li><code>INVALID_JSON</code> - 잘못된 JSON 형식</li>
              <li><code>DATA_INTEGRITY_ERROR</code> - DB 무결성 제약 위반</li>
              <li><code>SQL_CONSTRAINT_ERROR</code> - DB FK/PK 제약 위반</li>
            </ul>
          </li>

          <li><b>401 UNAUTHORIZED</b>
            <ul>
              <li><code>UNAUTHORIZED</code> - 인증 필요 (로그인되지 않은 상태)</li>
            </ul>
          </li>

          <li><b>403 FORBIDDEN</b>
            <ul>
              <li><code>ACCESS_DENIED</code> - 접근 권한 없음</li>
            </ul>
          </li>

          <li><b>404 NOT_FOUND</b>
            <ul>
              <li><code>RESOURCE_NOT_FOUND</code> - 리소스를 찾을 수 없음</li>
            </ul>
          </li>

          <li><b>405 METHOD_NOT_ALLOWED</b>
            <ul>
              <li><code>METHOD_NOT_ALLOWED</code> - 지원하지 않는 HTTP 메서드</li>
            </ul>
          </li>

          <li><b>409 CONFLICT</b>
            <ul>
              <li><code>DUPLICATION_ID</code> - 아이디 중복</li>
              <li><code>DUPLICATION_NICKNAME</code> - 닉네임 중복</li>
              <li><code>DUPLICATE_KEY</code> - DB 중복 키</li>
            </ul>
          </li>

          <li><b>413 PAYLOAD_TOO_LARGE</b>
            <ul>
              <li><code>UPLOAD_SIZE_EXCEEDED</code> - 업로드 파일 크기 제한 초과</li>
            </ul>
          </li>

          <li><b>500 INTERNAL_SERVER_ERROR</b>
            <ul>
              <li><code>NullPointerException</code> - 개발 중 발생한 NPE</li>
              <li><code>INTERNAL_ERROR</code> - 예상치 못한 서버 오류</li>
            </ul>
          </li>
        </ul>

        <hr/>
        <b>📦 공통 응답 구조</b><br>
        모든 API는 아래 형식으로 응답합니다.<br><br>
        <code>{
          "status": "SUCCESS or ERROR",
          "errorCode": "에러 코드 (성공 시 null)",
          "errorMessage": "에러 상세 메시지 (성공 시 null)",
          "data": { ... 응답 데이터 ... }
        }</code>
        """
        ),
        servers = {
        @Server(url = "https://www.clipin.store", description = "Production Server")
}
)
public class OpenAPIConfig {

}

