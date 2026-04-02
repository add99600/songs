package org.example.backendspring.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backendspring.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

/**
 * OAuth2 로그인 성공 핸들러. JWT를 발급하고 프론트엔드로 리다이렉트한다.
 */
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${oauth2.redirect-uri:http://localhost:5200}")
    private String redirectUri;

    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 인증 성공 시 JWT를 쿠키에 설정하고 콜백 URL로 리다이렉트한다.
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Object userIdAttr = oAuth2User.getAttribute("userId");
        if (userIdAttr == null) {
            response.sendRedirect(redirectUri + "/login?error=auth_failed");
            return;
        }

        Long userId = ((Number) userIdAttr).longValue();
        String token = jwtUtil.generateToken(userId);

        ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(redirectUri + "/oauth2/callback");
    }
}
