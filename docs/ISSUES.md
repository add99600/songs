# Songs 프로젝트 이슈 트래킹

---

## 보안 (Security)

| # | 항목 | 심각도 | 상태 |
|---|------|--------|------|
| S1 | `application.properties` Git 미제외 | 높음 | 완료 |
| S2 | JWT를 URL 쿼리스트링으로 전달 (브라우저 히스토리 노출) | 높음 | 완료 |
| S3 | JWT를 localStorage에 저장 (XSS 취약점) | 높음 | 완료 |
| S4 | 서버 측 로그아웃 없음 (토큰 무효화 불가) | 중간 | 미완료 - Redis 블랙리스트 미구축 (인프라 필요) |
| S5 | OAuth2 state 파라미터 프론트 미검증 | 높음 | 완료 - Spring Security가 백엔드에서 자동 검증 |
| S6 | JWT 만료 7일 (과도하게 김) | 중간 | 완료 |
| S7 | `/api/**` 전체 인증 우회 + JwtAuthFilter 미존재 | 중간 | 완료 |
| S8 | CORS `allowedHeaders = ["*"]` 와일드카드 | 중간 | 완료 |
| S9 | OAuth2 리다이렉트 URI 하드코딩 | 중간 | 완료 |
| S10 | JWT 시크릿 키 엔트로피 부족 | 낮음 | 미완료 - 운영 배포 시 재발급 필요 |
| S11 | 클라이언트 토큰 만료 미검증 | 낮음 | 완료 |

---

## 코드 품질 (Code Quality)

| # | 항목 | 심각도 | 상태 |
|---|------|--------|------|
| C1 | `SharedPage` Button import 누락 | 높음 | 완료 |
| C2 | `AppRoutes.tsx`, `DashBoard.tsx` 데드 코드 | 높음 | 완료 |
| C3 | API URL / userId 하드코딩 | 높음 | 완료 |
| C4 | localStorage 인증 상태 분산 관리 | 높음 | 완료 |
| C5 | `SecurityConfig` `@CompileStatic` 누락 | 높음 | 완료 |
| C6 | `PlaylistController` 입력값 검증 없음 | 높음 | 완료 |
| C7 | axios 인스턴스 미생성 | 중간 | 완료 |
| C8 | `DashBoard.tsx` 디자인 시스템 불일치 | 중간 | 완료 (파일 삭제) |
| C9 | `SearchPage` 검색 기능 미연결 | 중간 | 미완료 - API 연동 필요 |
| C10 | `OAuth2SuccessHandler` 리다이렉트 URL 하드코딩 | 중간 | 완료 |
| C11 | `Song.groovy` MongoDB 어노테이션 누락 | 중간 | 미완료 - Atlas 연결 시 작업 예정 |
| C12 | `PlaylistService` 불필요한 `isEmpty()` 중복 | 중간 | 미완료 |
| C13 | 토큰 유효성(JWT 포맷) 검증 없음 | 낮음 | 완료 (HttpOnly 쿠키 전환으로 불필요) |
| C14 | 네이밍 컨벤션 불일치 (`dashBoard/`) | 낮음 | 완료 (파일 삭제) |

---

## 기능 미완성 (Feature)

| # | 항목 | 상태 |
|---|------|------|
| F1 | `SearchPage` API 연동 | 미완료 |
| F2 | `MyFoldersPage` userId JWT 기반으로 교체 | 미완료 |
| F3 | 플레이리스트 생성/삭제 API | 미완료 |
| F4 | 곡 추가/삭제 API | 미완료 |
| F5 | 플레이리스트 공유 기능 | 미완료 |
| F6 | 가사/메모 입력 기능 | 미완료 |
| F7 | MongoDB Atlas 연결 및 노래 검색 API | 미완료 |

---

## 미완료 항목 상세 설명

### S4 - 서버 측 로그아웃 (토큰 무효화)
- **이유**: JWT 무효화를 위해 Redis 토큰 블랙리스트가 필요하나 현재 Redis 미구축
- **현재 상태**: HttpOnly 쿠키 방식으로 전환하여 쿠키 삭제로 대응 (브라우저 단 무효화)
- **완전한 해결**: Redis 도입 후 블랙리스트 또는 Refresh Token 서버 관리 방식으로 전환 필요

### S10 - JWT 시크릿 키 엔트로피
- **이유**: 현재 개발 환경용 키 사용 중
- **해결 방법**: 운영 배포 전 `openssl rand -base64 64`로 생성한 키로 교체

### C9 / F1 - SearchPage 검색 기능
- **이유**: MongoDB Atlas 미연결로 노래 데이터 API 미구현
- **선행 조건**: Atlas 연결 → Song API 구현 → SearchPage 연동
