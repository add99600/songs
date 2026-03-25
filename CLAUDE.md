# Songs Project - Claude Code 가이드

## 프로젝트 구조

- `front-end/` : React + TypeScript + Vite + shadcn ui + tailwind css 프론트엔드
- `back-end/` : Spring Boot 4.0.3 + JPA + PostgreSQL + MongoDB 백엔드 (Groovy)
- `song-crawling/` : Python 크롤링 스크립트

---

## 에이전트 팀 구성

팀 생성 시 아래 역할을 기반으로 구성하세요.

### frontend-dev
- **역할**: React/TypeScript 컴포넌트 개발 및 UI 구현
- **담당 경로**: `front-end/src/`
- **허용 작업**: 컴포넌트 생성/수정, 스타일링, 라우팅, 훅 작성
- **사용 도구**: Read, Edit, Write, Glob, Grep

### backend-dev
- **역할**: Spring Boot REST API 및 비즈니스 로직 구현
- **담당 경로**: `back-end/`
- **허용 작업**: Controller/Service/Repository 작성, JPA 엔티티, application.yml 설정
- **사용 도구**: Read, Edit, Write, Glob, Grep, Bash

### crawler-dev
- **역할**: Python 크롤링 스크립트 개발 및 유지보수
- **담당 경로**: `song-crawling/`
- **허용 작업**: 크롤러 로직 수정, 데이터 파싱, CSV 처리
- **사용 도구**: Read, Edit, Write, Glob, Grep, Bash

### code-reviewer
- **역할**: 코드 품질, 성능, 유지보수성 검토
- **담당 경로**: 전체 (읽기 전용)
- **허용 작업**: 파일 읽기, 분석, 피드백 제공 — **파일 직접 수정 금지**
- **검토 관점**: 가독성, 중복 코드, 성능 이슈, 에러 처리
- **사용 도구**: Read, Glob, Grep

### security-reviewer
- **역할**: 보안 취약점 탐지 및 위험도 평가
- **담당 경로**: 전체 (읽기 전용)
- **허용 작업**: 파일 읽기, 분석, 피드백 제공 — **파일 직접 수정 금지**
- **검토 관점**: OWASP Top 10, 인증/인가, 민감정보 노출, 입력값 검증
- **사용 도구**: Read, Glob, Grep

### dba
- **역할**: DB 설계 및 최적화 전문가
- **담당 경로**: `back-end/` (스키마, 마이그레이션 관련)
- **허용 작업**: ERD 설계, 테이블 스키마 작성, 인덱스 전략, 마이그레이션 스크립트
- **DB**: PostgreSQL (유저, 플레이리스트, 인증) + MongoDB (노래 데이터)
- **사용 도구**: Read, Edit, Write, Glob, Grep

---

## 협업 규칙

1. `frontend-dev`, `backend-dev`, `crawler-dev`는 서로의 담당 경로를 수정하지 않는다.
2. `code-reviewer`와 `security-reviewer`는 다른 팀원의 작업이 완료된 후 리뷰를 시작한다.
3. `dba`가 스키마를 확정한 후 `backend-dev`가 JPA 엔티티를 작성한다.
4. 팀원 간 의존 작업은 명시적으로 완료 신호를 주고받는다.

---

## 팀 생성 예시 프롬프트

```
Create an agent team with 6 teammates:
- "frontend-dev": CLAUDE.md의 frontend-dev 역할 수행
- "backend-dev": CLAUDE.md의 backend-dev 역할 수행
- "crawler-dev": CLAUDE.md의 crawler-dev 역할 수행
- "dba": CLAUDE.md의 dba 역할 수행
- "code-reviewer": CLAUDE.md의 code-reviewer 역할 수행
- "security-reviewer": CLAUDE.md의 security-reviewer 역할 수행

작업: 대시보드 페이지에 크롤링 데이터를 표시하는 기능 추가
```
