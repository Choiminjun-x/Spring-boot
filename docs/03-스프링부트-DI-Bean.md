# 3회차 — 스프링 부트 개념 · DI / Bean

## 스프링 컨테이너

- 객체를 대신 만들고 보관하고 필요한 곳에 꽂아주는 역할자 하나 두는 것
- 컨테이너가 관리하는 객체를 Bean → "내가 new 하지 않고 컨테이너가 만들어서 들고 있는 객체"

## IoC(제어의 역전)

- 내 코드가 흐름과 객체를 결정하던 권한을, 밖(프레임워크, 컨테이너)에 넘긴 것
- IoC는 넓은 개념, DI는 그중 객체 의존성에 한정한 좁은 이름

```
프레임워크 (일반)
 └ DI 컨테이너를 가진 프레임워크
    └ 스프링 프레임워크
       └ 스프링 컨테이너  ← Bean을 만들고 관리하는 부분
```

## 컨테이너를 왜 사용하는지?

1. 의존성 추가 시 조립 순서를 다시 짜야 한다 → 컨테이너가 그래프를 계산해 순서를 정한다
2. 클래스 하나 고치면 main도 같이 고쳐야 한다 → 조립 코드 자체가 없어진다
3. 컨테이너는 객체를 하나만 만들어 공유한다(싱글톤) → Bean에는 변하는 상태를 두지 않는다

## Bean — 컨테이너는 무엇을 Bean으로 만들지 어떻게 아는가?

- **방법 A** — 클래스에 표시를 붙인다 (컴포넌트 스캔) → `@Component`가 붙은 클래스를 컨테이너가 찾아서 Bean으로 만든다

```java
@Component
class EmailSender implements NotificationSender { }
```


| 어노테이션                             | 붙이는 자리    |
| --------------------------------- | --------- |
| `@Component`                      | 일반        |
| `@Service`                        | 비즈니스 로직   |
| `@Repository`                     | DB 접근     |
| `@Controller` / `@RestController` | 요청을 받는 자리 |


- **방법 B** — 만드는 방법을 직접 써준다 → 메서드가 반환한 객체를 Bean으로 등록한다

```java
@Configuration
class AppConfig {
    @Bean
    NotificationSender notificationSender() {
        return new EmailSender();
    }
}
```

## 어노테이션이란 무엇인가?

- 코드에 붙이는 메타데이터, 그 표시를 읽고 뭔가를 하는 건 언제나 다른 누군가
  - 컴파일러가 읽는 것 — `@Override`
  - 라이브러리가 읽는 것 — `@Test`는 JUnit이 읽는다
  - 컨테이너가 읽는 것 — `@Component`는 스프링이 읽는다
- 스프링을 뺀 채로 `@Component` 붙은 클래스를 실행하면 아무 일도 안 일어난다

## @SpringBootApplication

- 세 어노테이션을 합쳐놓은 것. 컴포넌트 스캔 범위를 정하고, 자동 설정을 켜고, 이 클래스 자체를 설정 클래스로 쓴다

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

- `@ComponentScan` — 어디를 뒤질지 정한다
  - 이 어노테이션이 붙은 클래스가 있는 패키지와, 그 하위 패키지 전부가 범위
  - 위 이유 때문에, 실무에서 메인 클래스를 최상위 패키지에 둔다
- `@EnableAutoConfiguration` — 설정을 알아서 채운다
  - 스프링이 자기 Bean을 알아서 채워넣는 것
  - `spring-boot-starter-web` → 웹 애플리케이션 → 내장 톰캣 띄우고, JSON 변환기 등록, 요청을 나눠줄 Bean들을 만든다
  - DB 드라이버와 접속 정보가 있다 → DataSource Bean을 만든다
  - 아무것도 없다 → 안 만든다
- `@Configuration` — 이 클래스 자체도 설정 클래스로 쓴다

## 내장 톰캣

- 톰캣이 라이브러리로 jar 안에 들어 있다
- `SpringApplication.run()`이 그걸 켜서 포트를 잡는다

## &gt;&gt;&gt;&gt; 스프링에서 설정값은 어디서 설정하는가?

- application.yaml - 스프링 부트가 기동할 때 자동으로 읽는 설정 파일. 읽을 때는 @Value로 주입 받는다
- 설정값은 파일 말고도 환경변수, 명령행 인자로 줄 수 있고, 우선순위는 명령행 &gt; 환경변수 &gt; yaml &gt; 기본값
- 같은 jar를 환경마다 다르게 띄우기 위한 구조. 비밀값은 yaml에 적지 않고 환경변수로 넘긴다

## 지금까지의 흐름(서버가 뜰 때 일어나는 일)

```
java -jar app.jar
  → main 실행
  → SpringApplication.run()
      → 컨테이너 생성
      → 컴포넌트 스캔 (내가 붙인 어노테이션 확인)
      → 자동설정 (클래스패스 보고 알아서 채우기)
      → 의존성 보고 순서 정해서 Bean 생성, 주입
      → 내장 톰캣 기동, 8080 bind + listen
  → 요청 대기
```

## Bean이 실제로 어떻게 들어가는지?

생성자 주입

```java
@Service
class OrderService {
    private final NotificationSender sender;

    OrderService(NotificationSender sender) {
        this.sender = sender;
    }
}
```

## 생성자 주입 방식을 사용하는 이유?

- **final을 붙일 수 있다**
  - final 필드는 선언할 때 또는 생성자 안에서, 둘 중 한 번만 값을 넣을 수 있다. 그 뒤로는 못 바꾼다 → 객체 내용을 바꾸는 것은 가능, 가리키는 대상을 바꾸는건 불가능

```java
private final Box box;

void doSomething() {
    box.value = 99;      // OK   — 객체 내용을 바꾸는 것
    box = new Box();     // 에러 — 가리키는 대상을 바꾸는 것
}
```

- **의존성이 빠지면 객체가 아예 안 만들어진다**
  - 생성자 주입은 인자 없이는 객체를 만들 수 없다 → 기동 시점에 실패
- **생성자가 설계 냄새를 알려준다**
  - 인자가 너무 많아지면 "이 클래스가 하는 일이 너무 많은 것 아닌가"를 생각하게 된다 → 불편함이 경고 역할을 하게 됨

## @RequiredArgsConstructor

- final 필드를 전부 인자로 받는 생성자를 컴파일 시점에 만들어준다

```java
@Service
@RequiredArgsConstructor
class OrderService {
    private final NotificationSender sender;
    private final OrderRepository repository;
}
```

## 컨테이너가 주입하는 방법

- 주입은 타입으로 찾는다. 타입이 유일하면 그걸 넣고, 없으면 실패하고, 여러 개면 모호해서 실패한다. 여러개일 때는 `@Qualifier`(지목) 또는 `@Primary`(기본값)로 근거를 준다

```java
// 같은 타입이 둘 → NoUniqueBeanDefinitionException
@Component
class EmailSender implements NotificationSender { }

@Component
class SmsSender implements NotificationSender { }
```

```java
// @Qualifier — 이름으로 지목한다
@Component("email")
class EmailSender implements NotificationSender { }

OrderService(@Qualifier("email") NotificationSender sender) { ... }
```

```java
// @Primary — 기본값을 정한다
@Component
@Primary
class EmailSender implements NotificationSender { }
```

