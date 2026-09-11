# 4회차 — HTTP 와 REST API

## TCP 와 HTTP

### TCP

- 프로세스 사이의 연결을 맺고, 바이트를 순서대로, 유실 없이 전달한다
- 그 바이트 안에 뭐가 들었는지 전혀 모른다

### HTTP

- 바이트 스트림에 "이런 형식으로 쓰자"고 정해둔 약속, 규격

```
클라이언트가 8080에 TCP 연결
  → 약속된 형식의 텍스트를 바이트로 흘려보냄
  → 톰캣이 그 바이트를 HTTP 문법으로 파싱
  → 메서드 / 경로 / 헤더 / 바디로 쪼개짐
  → 스프링이 (메서드 + 경로) 를 보고 어느 자바 메서드를 부를지 결정
```

### `@GetMapping("/health")`

- 메서드와 경로를 자바 메서드에 연결하는 표지판

---

## 메시지 구조

### 요청

```
POST /posts HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 32

{"title":"hello","content":"hi"}
```

- **시작줄** — 메서드 경로 버전
- **헤더** — 이 요청 자체에 대한 설명
- **빈 줄 하나** — "헤더 끝"이라는 구분자, 형식상 필요하다
- **바디** — 실어 보내는 내용물

### 응답

```
HTTP/1.1 201 Created
Content-Type: application/json

{"id":1,"title":"hello"}
```

### `Content-Type`

- 바디의 해석 방식을 알려주는 헤더

### stateless

- 서버는 이전 요청을 기억하지 않는다
- 그래서 필요한 정보는 매 요청마다 전부 다시 실어 보낸다
- 로그인 토큰을 매번 헤더에 붙이는 이유

### curl 은 이 텍스트를 조립해주는 도구

| 옵션 | 만드는 부분 |
|---|---|
| `-X` | 시작줄 |
| `-H` | 헤더 |
| `-d` | 바디 |

- 옵션을 외우는 게 아니라, 메시지 구조를 알면 옵션이 따라온다

---

## 값을 어디에 싣는가

### 경로 → `@PathVariable`

- "무엇을 가리키는가"
- 자원의 주소
- `/posts/1` → 1번 게시물 지목, `1` 이 빠지면 다른 자원이 된다

### 쿼리스트링 → `@RequestParam`

- "어떻게 보여줄까"
- 자원을 다루는 조건
- `?page=2&size=20&sort=createdAt` 을 빼도 `/posts` 는 여전히 유효한 요청

### 판단 기준

실무에서 판단할 때, 이 값을 빼면 요청이 아예 성립하지 않는가, 아니면 그냥 조건이 하나 빠진 채로 성립하는가

- 성립 X → 경로 → `@PathVariable`
- 성립 O → 쿼리스트링 → `@RequestParam`

---

## REST

### 핵심 규칙

- 경로에는 명사(자원)만 쓰고, 동사는 HTTP 메서드가 담당한다
- 자원은 보통 복수형으로 사용 (`/posts`) → 관계가 경로 모양에 그대로 드러나기 때문

```
❌ POST /getPost?id=1        동사가 경로에 있음
❌ POST /deletePost?id=1
❌ POST /updatePost

✅ GET    /posts/1
✅ DELETE /posts/1
✅ PUT    /posts/1
✅ POST   /posts
```

---

## 스프링에서의 연결

- 이제 위 텍스트가 자바 메서드로 어떻게 꽂히는지

```java
@RestController
@RequestMapping("/posts")          // 이 컨트롤러의 공통 앞부분
class PostController {

    // GET /posts/1
    @GetMapping("/{id}")
    PostResponse findOne(@PathVariable Long id) { ... }

    // GET /posts?page=2&size=20
    @GetMapping
    List<PostResponse> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) { ... }

    // POST /posts   + 바디에 JSON
    @PostMapping
    PostResponse create(@RequestBody PostCreateRequest request) { ... }
}
```

| 어노테이션 | 메시지의 어디에서 |
|---|---|
| `@PathVariable` | 시작줄의 경로 중 `{}` 자리 |
| `@RequestParam` | 시작줄의 `?` 뒤 |
| `@RequestBody` | 빈 줄 아래 바디 전체 |

- 어노테이션은 메타데이터고 **읽는 주체는 스프링**이다. `@PathVariable` 이 스스로 값을 꺼내는 게 아니라, 스프링이 요청을 파싱해두고 그 표시를 보고 자리를 맞춰 넣는다
- `@RequestBody` 의 JSON → 자바 객체 변환은 **Jackson** 이 한다. 3회차 자동설정의 "JSON 변환기 등록"이 이 Bean이다

---

## 쿼리스트링이 아니라 바디에 넣는 이유

`@RequestParam`, `@RequestBody` 둘 다 "값을 실어 보낸다"는 점이 같다.
`POST /posts` 로 게시글을 만들 때 제목과 내용을 바디에 넣는 이유는?

### 노출

- 서버 / 프록시 접근 로그에 요청 라인이 통째로 남는다
- 브라우저 히스토리, 북마크
- `Referer` 헤더 — 그 페이지에서 다른 사이트로 넘어갈 때 URL이 따라간다

→ 바디는 로그에 안 남는 게 기본이다. 비밀번호·개인정보를 쿼리스트링에 넣지 말라는 규칙이 나온다

### 길이

- HTTP 명세 자체에 URL 길이 제한은 없다. 실제로는 브라우저, 웹서버, 프록시마다 제각각 제한을 둔다
- 넘으면 `414 URI Too Long` 이 난다

### 구조

- 쿼리스트링은 `key=value` 를 `&` 로 이어붙인 평평한 문자열, 중첩을 표현할 방법이 없다

---

## 메서드와 멱등성

- **안전(safe)** — 서버 상태를 안 바꾼다. 몇 번을 불러도 데이터가 그대로다
- **멱등(idempotent)** — 상태는 바뀌지만, 한 번 부르나 열 번 부르나 결과 상태가 같다

```
POST   /posts   {"title":"hello"}   5번 → 게시글 5개 생성        ❌ 멱등 아님
PUT    /posts/1 {"title":"hello"}   5번 → 1번 글의 제목이 hello   ⭕️ 멱등
DELETE /posts/1                     5번 → 1번 글이 없음           ⭕️ 멱등
```

- 멱등한 요청 → 그냥 다시 보내면 된다. 재시도가 안전하다
- 멱등하지 않은 요청 → 다시 보내면 결제가 두 번 되거나 글이 두 개 생긴다

### DELETE 주의

- 두 번째 호출부터 404가 나와도 멱등이다
- "1번 글이 없다"는 최종 상태는 첫 호출 후와 똑같기 때문
- **멱등은 응답이 아니라 최종 상태로 판단한다**

---

## 멱등 키 (Idempotency Key)

- 헤더에 넣음
- 이 요청 자체에 대한 메타정보 → `Content-Type`, `Content-Length` 와 같은 자리
- 키는 클라이언트가 만든다 → 재시도할 때 같은 키를 다시 보내야 중복으로 인식되기 때문

```
요청 도착 → 키를 저장소에서 조회
  ├ 없음 → 처리하고, 키 + 결과를 저장 → 201 반환
  └ 있음 → 처리하지 않고, 저장해둔 결과를 그대로 반환
```

- 두 번째 호출도 실패가 아니라 첫 번째와 같은 응답을 돌려준다
- 클라이언트 입장에서 "몇 번을 보내도 결과가 같다" → **POST를 멱등하게 만든 것**

---

## 상태 코드

- 앞자리로 책임 소재를 가르는 것이 핵심

| 대역 | 뜻 | 누구 책임 |
|---|---|---|
| 2xx | 성공 | — |
| 3xx | 리다이렉트 | — |
| 4xx | 클라이언트가 잘못 보냄 | 요청자 |
| 5xx | 서버가 처리하다 터짐 | 서버 |

### 자주 쓰는 것

| 코드 | 언제 |
|---|---|
| 200 OK | 조회·수정 성공 |
| 201 Created | 생성 성공 (POST) |
| 204 No Content | 성공했는데 돌려줄 바디가 없음 (DELETE) |
| 400 Bad Request | 형식·검증 실패 |
| 401 Unauthorized | 인증 안 됨 (누군지 모름) |
| 403 Forbidden | 인가 안 됨 (누군지는 아는데 권한 없음) |
| 404 Not Found | 자원 없음 |
| 409 Conflict | 상태 충돌 (중복 가입 등) |
| 500 Internal Server Error | 서버에서 예외 터짐 |

- **401 vs 403** — 토큰이 없거나 만료 → 401. 토큰은 멀쩡한데 남의 글을 지우려 함 → 403
- 201에는 보통 `Location` 헤더로 만들어진 자원의 주소(`/posts/1`)를 같이 준다
