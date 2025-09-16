# 코드 컨벤션 규칙
---
description: 코드컨벤션규칙
alwaysApply: true
---
# 코드 컨벤션 규칙

## 기본 원칙
- Java 21, Spring Boot 3.x 기준.
- 가독성 우선: 짧고 명확한 메서드, 한 책임 원칙(SRP) 준수.
- 모듈 경계 유지: 앱 모듈(`saviing-bank`, `saviing-game`) → `saviing-common`만 의존.

## 형식(Formatting)
- 들여쓰기: 4 spaces, 탭 사용 금지.
- 줄 길이: 권장 120자 이내(필요 시 개행).
- 줄 길이 제한은 권장치이며 강제하지 않음(자동 줄바꿈 금지, 가독성 위주로 의미 있는 개행만 사용).
- 파일 인코딩: UTF-8, 한 파일 하나의 public 클래스.
- import: 와일드카드 금지, 불필요한 import 제거.

### 인덴테이션
- 인덴테이션은 스페이스 바 사용 (not tabs)
- 들여쓰기는 4개의 빈칸 단위
- 연속 줄 인덴트(continuation indent)도 4칸으로 통일
- 줄 끝 공백(trailing whitespaces)은 제거 (저장 시 자동 정리 권장)
- 블록: 여는 `{` 는 같은 줄, 닫는 `}` 는 단독 줄.
- 개행/연속줄: 줄바꿈 시 +4 spaces 추가(정렬보다 고정 인덴트 우선).
- 메서드 파라미터/체이닝 줄바꿈 예시:
```java
void createOrder(
    String userId,
    Product product,
    int quantity
) { /* ... */ }

repository
    .findById(userId)
    .map(this::convert)
    .ifPresent(this::handle);
```

### public 메서드
- 가시성: 최소 공개 원칙. 외부 API가 아닌 경우 `public` 대신 패키지/보호/비공개 고려.
- 명명: 동사+목적어(`createAccount`, `calculateInterest`)로 부작용/목적이 드러나게.
- 계약: 입력 검증(필수), `null` 반환 금지. 컬렉션은 비널/불변 반환, 선택 값은 `Optional` 반환.
- 예외: 도메인 오류는 런타임 예외(커스텀) 사용, 메시지와 오류 코드를 포함(공통 에러 모델 활용).
- 문서화: 공개 API는 간단한 Javadoc 작성(파라미터/반환/예외 설명).
- 트랜잭션: 비즈니스 경계는 서비스 `public` 메서드에 선언, 조회는 `@Transactional(readOnly = true)`.
- 파라미터: 4개 이하 권장. 많다면 DTO/파라미터 객체 사용.
- 길이: 한 메서드는 한 책임. 복잡하면 private 헬퍼로 분리.
- 로깅: 정상 흐름은 디버그, 예외는 경고/에러. 민감정보 로그 금지.

```java
/** 계좌를 생성하고 초기 입금액을 반영한다. */
@Transactional
public AccountId createAccount(CreateAccountRequest req) {
    requireNonNull(req);
    validate(req);
    var id = accountRepository.nextId();
    var account = Account.open(id, req.owner());
    ledgerService.applyInitialDeposit(account, req.initialDeposit());
    accountRepository.save(account);
    return id;
}

@Transactional(readOnly = true)
public Optional<Account> findById(AccountId id) {
    return accountRepository.findById(requireNonNull(id));
}
```

### private 메서드
- 목적: 공개 메서드의 가독성을 높이기 위한 세부 구현 캡슐화. 한 책임만 수행하도록 작게 유지.
- 네이밍: 구현 의도가 드러나게 구체적으로(`calculateDailyInterest`, `validateOwnerPermission`).
- 부수효과: 가능하면 순수 함수(입력→출력)로 작성. 상태 변경이 필요하면 최소 범위에서 수행하고 반환 타입으로 결과를 전달.
- 가시성: 외부 노출 금지. 재사용이 필요한 유틸은 별도 컴포넌트/모듈(`saviing-common`)로 추출.
- 정적화: 인스턴스 상태를 사용하지 않으면 `static` 고려.
- 파라미터: 과도한 인자(>3) 지양. 의미 있는 값 객체/DTO로 묶음.
- 널/옵셔널: 입력은 비널 전제. `Optional`을 파라미터로 받지 않음. 필요 시 오버로드 제공.
- 예외/검증: 사전조건 위반 시 빠르게 실패하고 상위에서 처리될 수 있도록 명확한 예외를 던짐.
- 로깅: 반복 호출되는 private 메서드에서는 과도한 로그 지양(상위 레벨에서 로그).

```java
private Money calculateDailyInterest(Money principal, Bps rate) {
    requireNonNull(principal);
    requireNonNull(rate);
    return principal.multiply(rate.toDecimal()).divide(365);
}

private void validateOwnerPermission(Account account, UserId userId) {
    if (!account.isOwner(userId)) {
        throw new PermissionDeniedException("not owner");
    }
}
```

### 어노테이션 순서
- 원칙: 의미·적용 범위가 큰 것 → 구체 설정 → 보일러플레이트 순으로 배치. 한 줄에 너무 길면 줄바꿈하여 각 어노테이션을 개별 줄에 둠.

- 클래스 레벨(예: 컨트롤러/서비스/엔티티)
  1) 스테레오타입: `@RestController`/`@Controller`/`@Service`/`@Component`
  2) 매핑/검증: `@RequestMapping`, `@Validated`
  3) 크로스컷팅: `@Transactional`, `@PreAuthorize` 등 보안/트랜잭션
  4) 퍼시스턴스: `@Entity`, `@Table`
  5) 보일러플레이트: 롬복(`@RequiredArgsConstructor`, `@Getter` 등)

- 메서드 레벨(예: 컨트롤러/서비스 메서드)
  1) 요청 매핑: `@GetMapping`/`@PostMapping`/`@RequestMapping`
  2) 보안/권한: `@PreAuthorize`/`@PermitAll`
  3) 트랜잭션/캐싱: `@Transactional`/`@Cacheable`/`@CacheEvict`
  4) 검증/문서: `@Validated`/OpenAPI `@Operation` 등
  5) 언어 레벨: `@Override`, `@Deprecated`

- 파라미터 레벨
  1) 널/검증: `@NotNull`/`@Valid`
  2) 바인딩: `@PathVariable`/`@RequestParam`/`@RequestBody`/`@RequestPart`
  예: `(@NotNull @PathVariable String id)`, `(@Valid @RequestBody CreateRequest req)`

- 필드 레벨
  1) 널/검증: `@NonNull`/`@NotNull`
  2) 직렬화: `@JsonProperty`/`@JsonIgnore`
  3) 퍼시스턴스: `@Id`, `@GeneratedValue`, `@Column`, 연관 매핑
  4) 주입/값: `@Value`(필드 주입은 지양)

```java
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Transactional(readOnly = true)
    public AccountResponse get(@PathVariable String id) {
        return accountService.find(id);
    }
}
```

### 변수선언
- 네이밍: `camelCase` 명사형, 축약/헝가리안 표기 금지. 불리언은 `is/has/can` 접두어.
- 스코프: 최소 범위에 선언(사용 지점 근처). 재사용을 위해 범위 확장 금지.
- 초기화: 선언 시 초기화 권장. 불필요한 `null` 초기화 지양, 컬렉션은 빈 컬렉션으로.
- 변경 가능성: 가능한 `final` 로컬 변수 사용(재할당 방지).
- 타입: 인터페이스 타입 사용(`List`, `Map`). 제네릭 명시. `var`는 타입이 명확할 때만 제한적으로.
- 매직 넘버/문자열: 상수로 추출.
- 선언당 하나의 변수(One variable per declaration): 필드/지역변수는 한 선언에 하나씩만 선언.
- 예외: `for` 문 헤더에서는 여러 변수를 함께 선언 가능.
- 필요할 때 선언(Declared when needed): 지역변수는 블록 시작부에 습관적으로 몰아 선언하지 않고, 최초 사용 지점에 가깝게 선언/초기화하여 유효 범위를 최소화.

```java
final int retryLimit = 3;
List<String> names = new ArrayList<>();
boolean isActive = user.hasPermission("READ");
Map<AccountId, Account> accounts = new HashMap<>();
```

### 객체생성방식
- 생성자: 필수 불변 필드만 받도록 설계. 무거운 작업은 금지.
- 정적 팩토리: 의미 있는 이름(`of`, `from`, `valueOf`, `parse`)으로 의도를 드러내고, 검증/정규화 로직을 캡슐화.
- 빌더: 선택 파라미터가 많거나 가독성이 떨어질 때 사용. 불변 객체/DTO에 적합. 엔티티에 과도한 빌더 사용 지양.
- DTO 등 사용자 정의 단순 데이터 클래스는 Lombok `@Builder` 사용을 권장(필요 시 `toBuilder = true`).
- DI: 스프링 빈은 `new` 대신 주입. 외부 의존이 필요한 객체 생성은 팩토리/프로바이더 컴포넌트에 위임.
- 컬렉션/배열: 방어적 복사 후 저장, 외부에는 불변 뷰 반환.
- 변환: 외부/내부 모델 간 변환은 `from(dto)`/`toDto()` 등 양방향 명명 일관성 유지.

```java
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(String amount, Currency currency) {
        return new Money(new BigDecimal(amount), currency);
    }
}

@Builder
public record CreateAccountRequest(String owner, String initialDeposit) {}
```

### 롬복
- 생성자: 의존성 주입에는 `@RequiredArgsConstructor` 권장(필드 `final`). 무인자 생성자는 필요한 경우에만 `@NoArgsConstructor(access = PROTECTED)`.
- 접근자: `@Getter`는 필요한 범위에만, `@Setter`는 DTO 등 불변이 아닐 때에 한정. 엔티티/서비스에는 과도 사용 지양.
- 데이터 클래스: DTO/응답/요청에는 `@Builder` 적극 사용. 불변 DTO에는 `@Value` + `@Builder` 조합 고려.
- 금지/주의: `@Data`는 엔티티/서비스에 사용 지양(양방향 연관/equals/hashCode 문제). JPA 엔티티에는 `@EqualsAndHashCode` 사용 시 식별자 기준으로 제한.
- 유틸: `@Builder(toBuilder = true)`로 부분 변경 패턴 지원 가능하나, 도메인 불변식이 깨지지 않도록 팩토리/검증과 함께 사용.

#### 빌더 인라인 스타일(@Builder)
- Lombok의 `@Builder`로 메서드 인자에서 객체를 인라인 생성할 때 규칙:
  1) 메서드 호출의 여는 괄호 `(` 다음 줄에서 `builder()`를 시작한다.
  2) 빌더 체이닝은 단계마다 한 줄씩 작성하고 들여쓴다.
  3) `.build()` 호출 후에는 줄바꿈하고, 닫는 괄호 `)`와 세미콜론 `;`을 한 줄에 배치한다.
  4) 최종 `);`는 마지막 줄에서 정렬하여 닫는다.

```java
service.createAccount(
    CreateAccountRequest.builder()
        .owner("alice")
        .initialDeposit("100000")
        .build()
);
```

### DTO 컨벤션 예시
- DTO는 불변을 선호: `record` 또는 `@Value`(+`@Builder`).
- 필드명은 `camelCase`, 직렬화명 변경 시 `@JsonProperty`.
- 컬렉션은 불변/비널 유지, 검증은 `@NotNull`/`@Size` 등으로 선언.

```java
@Builder
public record CreateAccountRequest(
    @NotBlank String owner,
    @Pattern(regexp = "\\d+") String initialDeposit
) {}

// 사용 예
controller.create(
    CreateAccountRequest.builder()
        .owner("alice")
        .initialDeposit("100000")
        .build()
);
```
### 필드 선언
- 가시성: 최소 권한 원칙. 기본은 `private`; 필드 공개를 지양하고 메서드/생성자/빌더로 노출.
- 불변성: 가능한 한 `final` 지정. DI 필드는 `final` + 생성자 주입 사용.
- 타입: 구현체 대신 인터페이스 사용 — `List`/`Map`/`Set` 형태로 선언.
- 초기화: 간단 값은 인라인, 그 외는 생성자에서. 무거운 정적 초기화는 피하고 정적 팩토리로 위임.
- 널 처리: 기본 비널. 널 허용 시 `@Nullable` 명시. 필드에는 `Optional` 사용 금지(반환 타입 전용).
- 컬렉션: 비널 유지, 기본 빈 컬렉션으로 초기화. 외부에 노출 시 불변 뷰 반환.
- 롬복: `@RequiredArgsConstructor`로 DI, `@Getter`/`@Setter`는 필요한 범위에만. `@Data`는 엔티티/서비스에 지양.
- 순서: 정적 필드 → 인스턴스 필드(가시성 순). 관련 필드는 함께 묶음.

```java
@Service
@RequiredArgsConstructor
public class AccountService {
    // Static fields
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    // Instance fields (DI are final)
    private final LedgerService ledgerService;
    private final RateService rateService;

    // Non-null collections initialized to empty
    private List<String> notices = new ArrayList<>();

    public void applyRate(Account account) {
        var rate = rateService.getDefaultRate();
        ledgerService.apply(account, rate);
    }
}
```
 
### 메서드가 없는 경우
- 가능하면 `record` 사용으로 불변 데이터 모델을 간결하게 표현.
- 클래스를 사용할 때는 Lombok `@Value` 또는 `@Getter` + `@Builder` 조합으로 메서드 없이 필드만 유지(불변 권장, `@Setter` 지양).
- 마커 클래스/완전 빈 클래스는 한 줄 빈 블록 허용: `final class Marker {}`.
- 필드만 있는 클래스는 불필요한 빈 줄 없이 필드/어노테이션만 배치, 닫는 `}` 는 단독 줄로 배치 후 줄바꿈.

```java
// record (권장)
public record UserView(String id, String name) {}

// Lombok 기반 불변 DTO
@Value
@Builder
public class AccountView {
    String id;
    String name;
}

// 마커 클래스(허용)
final class Marker {}
```

### 메서드가 있는 경우
- 위치: 클래스 내용 순서에 따름(생성자/정적 팩토리 아래에 public 메서드, 이후 private 헬퍼).
- 간격: 필드/생성자 블록과 첫 메서드 사이에 한 줄 공백. 메서드 간에도 한 줄 공백 유지.
- 길이/책임: 한 메서드는 한 책임. 복잡하면 private 헬퍼로 분리.
- 문서화: 공개 메서드는 간단한 Javadoc 작성(파라미터/반환/예외).
- record의 경우: 컴팩트 생성자에서 검증을 수행하고, 유틸 메서드는 생성자 아래 배치.

```java
public record UserDto(
    String name,
    String email,
    int age,
    String address
) {
    // Compact constructor for validation
    public UserDto {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(email, "email");
        if (age < 0) throw new IllegalArgumentException("age >= 0");
    }

    /** 이메일의 로컬파트를 마스킹한다. */
    public String maskedEmail() {
        int i = email.indexOf('@');
        if (i <= 1) return "***@" + email.substring(Math.max(i, 0) + 1);
        return email.charAt(0) + "***" + email.substring(i);
    }

    /** 전체 주소 문자열을 반환한다. */
    public String fullAddress() {
        return address == null || address.isBlank() ? "N/A" : address;
    }
}
```

### 생성자
- 목적: 불변 상태 초기화와 유효성 검증을 수행. 무거운 작업(I/O, DB, 원격 호출)은 금지.
- 의존성 주입: 생성자 주입 사용(필드는 `final`). `@Autowired` 필드 주입 지양, 롬복 `@RequiredArgsConstructor` 권장.
- 검증: `Objects.requireNonNull(...)` 및 인자 검증으로 불변식 보장. 위반 시 `IllegalArgumentException` 또는 도메인 예외 사용.
- 오버로딩: 하나의 정식(canonical) 생성자에 위임. 선택 인자는 `static of(...)`/빌더 등 대안 제공.
- 컬렉션/가변 인자: 방어적 복사(`List.copyOf`, `Set.copyOf`) 및 불변 유지. 가변 배열은 복사 저장.
- 프레임워크 호환: 직렬화/프레임워크가 요구하는 경우에만 무인자 생성자 제공하고 `protected`로 제한.

```java
public class Account {
    private final String id;
    private final List<String> roles;

    public Account(String id, List<String> roles) {
        this.id = Objects.requireNonNull(id, "id");
        this.roles = List.copyOf(Objects.requireNonNull(roles, "roles"));
    }

    public static Account of(String id) {
        return new Account(id, List.of());
    }

    // For serialization frameworks only
    protected Account() {
        this.id = "";
        this.roles = List.of();
    }
}
```

### 초기화 메서드 (`@PostConstruct` 등)
- 목적: DI 완료 후의 가벼운 초기화(캐시 프리로드, 구성 값 검증, 로그). 필수 초기화는 생성자에서 처리.
- 금지: 무거운 I/O/네트워크 호출, 블로킹 작업, 긴 리트라이 루프. 트랜잭션 경계 보장 안 됨(`@Transactional` 기대 금지).
- 의존성 사용: 다른 빈 호출이 필요하다면 생성자 대신 `@PostConstruct`에서 수행.
- 예외 처리: 실패 시 명확한 예외로 부팅 실패를 유도하거나, 재시도 전략을 별도 컴포넌트로 분리.
- 스레드 안전: 공유 상태 초기화는 단일 스레드에서 완료하고 가시성 보장(불변/`volatile`).
- 종료 훅: 리소스 해제는 `@PreDestroy` 또는 `DisposableBean` 대신 어노테이션 사용 권장.
- 대안: 애플리케이션 시작 작업은 `ApplicationRunner`/`CommandLineRunner`, 라이프사이클 제어는 `SmartLifecycle` 고려.

```java
@Service
@RequiredArgsConstructor
public class CacheService {
    private final RemoteClient client;
    private Map<String, String> cache = Map.of();

    @PostConstruct
    void init() {
        // lightweight preload & validate
        var data = client.fetchWarmup();
        this.cache = Map.copyOf(data);
    }

    @PreDestroy
    void shutdown() {
        // release resources if any
    }
}
```
- switch-case: `switch` +0, `case` +0, 본문 +4, `break;` 본문과 동일 인덴트.
- 람다: 한 줄 표현식은 중괄호 생략, 블록은 +4 인덴트.

-### 중괄호 스타일
- K&R 스타일(Kernighan and Ritchie style)을 따른다.
- 적용 구문: `if`, `else`, `for`, `do`, `while` — 몸체가 없거나 한 줄인 경우에도 중괄호를 사용한다.
- 여는 `{` 앞에는 줄바꿈이 없고(같은 줄, 앞 공백 1칸), 여는 `{` 다음에는 줄바꿈하여 본문을 다음 줄에서 시작한다.
- 닫는 `}` 전에는 줄바꿈하여 `}` 를 단독 줄로 배치(마지막 구문과 같은 줄 금지).
- 닫는 `}` 다음에는 줄바꿈: 문(statement) 또는 메서드/생성자/클래스가 끝나는 경우에만 적용.
- 예외: 이어지는 구문이 있는 경우는 줄바꿈하지 않음 — `} else {`, `} catch (...) {`, `} finally {`, `} while (cond);`, `},` 등.
- `else`/`catch`/`finally` 는 `}` 와 같은 줄에서 이어서 작성: `} else {`, `} catch (Ex e) {`.
- `do-while` 은 `}` 뒤 같은 줄에 `while (cond);` 배치.
- 빈 블록은 간결하게 표현 가능: `void noop() {}` 같은 한 줄 표기 허용.
-. 블록 내부에 문자가 없거나(또는 줄바꿈만 있는) 완전한 빈 블록은 여는 `{` 직후 닫을 수 있음. 단, 멀티 블록 구문에는 적용하지 않음.

```java
// 허용 (완전한 빈 블록은 한 줄로)
void noop() {}
void doNothing() {}
if (ready) {}
for (int i = 0; i < n; i++) {}

// 비허용/비권장 (멀티 블록 구문에서의 즉시 닫기)
if (a) {} else {}
try {} catch (Exception e) {}
try { doSomething(); } catch (Exception e) {}
try { doWork(); } finally {}

// 비허용: 빈 블록을 여러 줄로 작성
void doNothing() {
}

// 허용되지 않음: 멀티 블럭 구문에서는 간결한 빈 블록 사용 불가
```

### 수평 공백
- 원칙: 프로그래밍 언어나 다른 스타일 규칙에서 요구되는 경우를 제외하고, 리터럴·주석·Javadoc을 제외하면 ASCII 공백 문자는 지정된 위치에만 하나 사용한다.
- 허용 위치(부분 목록):
  1. 예약어와 뒤따르는 여는 괄호 사이: `if (`, `for (`, `while (`, `switch (`, `catch (` — 예: `if (condition)`
  2. 닫는 중괄호와 이어지는 예약어 사이: `} else {`, `} catch (` , `} finally {`, `} while (`(do-while)
  3. 여는 중괄호(`{`) 앞: 제어문/클래스/메서드 선언 뒤 공백 1칸 — 예: `if (ok) {`, `class Foo {`, `void run() {`
  4. 모든 이항 연산자 및 삼항 연산자 양쪽: `a + b`, `x == y`, `i & mask`, `left << 2`, `flag ? A : B`
     - 포함되는 "연산자와 유사한" 기호:
       - 타입 경계의 `&`: `<T extends Foo & Bar>` (양옆 공백)
       - 다중 예외의 `|`: `catch (FooException | BarException e)` (양옆 공백)
       - 향상된 for의 `:`: `for (Item item : items)` (`:` 양옆 공백)
       - 람다의 `->`: `(String s) -> s.length()` (`->` 양옆 공백)
       - switch 화살표 `->`: `case "FOO" -> bar();` (`->` 양옆 공백)
     - 제외(공백 없음): 메서드 참조 `::`(예: `Object::toString`), 점 구분자 `.`(예: `object.toString()`)
  5. 쉼표(,), 콜론(:), 세미콜론(;), 닫는 괄호 `)` 뒤: 다음 토큰 앞에 공백 1칸. 예: `foo(a, b)`, `for (i = 0; i < n; i++)`, `if (ok) {`, `return (String) object;`
  6. 주석을 시작하는 `//` 앞과 뒤: 인라인 주석은 코드와 `//` 사이에 최소 1칸, `//` 다음 1칸 이상. 예: `doWork();  // run task`, `int a = 0; // 설명`
  7. 변수 선언에서 타입과 변수명 사이: 공백 1칸. 예: `String name`, `List<String> list`, `int[] counts`
  8. (선택) 배열 초기화 블록의 중괄호 `{}` 안쪽: 기본은 패딩 공백 없이 작성 — `{1, 2, 3}`. 팀 선택으로 `{ 1, 2, 3 }` 형태를 허용할 수 있으나 반드시 일관성 유지.
     - 중첩 초기화도 동일 규칙 적용. 예외 규칙에 따라 여는 중괄호 사이 공백은 사용하지 않음 — `String[][] x = {{"foo"}};`
     - `new int[] {5, 6}` 및 `new int[] { 5, 6 }` 모두 허용하나, 전자(공백 없음)를 권장.
  9. 타입 애노테이션과 `[]`/`...` 사이: 애노테이션 뒤에는 공백 없이 `[]` 또는 `...`를 붙인다. 타입과 애노테이션 사이는 공백 1칸.
     - 예: `String @NonNull[] names`, `String @Validated... args`, `List<@NonNull String> items`, `@Nullable String[] arr`
  
  예외:
  - 애노테이션에서 배열 값을 사용할 때는 괄호와 중괄호 사이에 공백을 넣지 않음 — `@SomeAnnotation({a, b})` (단, 배열 내부 콤마 뒤 공백은 유지)
  - 중첩 배열 초기화에서는 여는 중괄호 사이 공백을 두지 않음 — `String[][] x = {{"foo"}};` (즉, `{{` 사이 공백 불필요)
- 키워드/괄호: 위 1번 규칙 준수. 메서드 호출은 공백 없이 `doWork()`.
- 연산자: 이항/대입/논리/삼항 연산자 양옆에 공백 1칸 (`a + b`, `x == y`, `flag ? A : B`). 단항 연산자에는 공백 없음(`i++`).
- 콤마/세미콜론: 콤마 앞 공백 금지, 뒤는 1칸(`a, b`). for 구문 세미콜론 주위 공백은 한 칸(`for (i = 0; i < n; i++)`).
- for-each 콜론: 양옆 공백(`for (Item i : items)`).
- 람다: `->` 양옆 공백(`x -> x + 1`).
- 제네릭/괄호 내부: 바깥 공백 유지, 내부 여백은 두지 않음(`List<String>`, `call(a, b)`). 캐스팅은 닫는 괄호 뒤 공백(`(List<String>) value`).
- 주석: `//` 뒤 공백 1칸. Javadoc는 표준 형식 유지.
- 연속 공백/탭: 금지. 줄 끝 공백(trailing whitespaces) 제거.

```java
if (isActive && count > 0) {
    process(item1, item2);
}

for (Order o : orders) {
    handle(o);
}

List<String> names = list.stream()
    .map(x -> x + "!")
    .toList();
```

### 임포트 구문
- 와일드카드(`*`) 임포트 금지: `import a.b.*;` 형태는 허용하지 않음. 예: `import jakarta.persistence.*;` 금지 — 필요한 것만 명시적으로 임포트.
- static 임포트는 테스트 코드에서만 허용(JUnit/AssertJ/Mockito 등). 프로덕션 코드에서는 사용 지양.
- 그룹/순서(위→아래):
  1) static imports
  2) 일반 imports: `java.*` → `javax.*` → 제3자(`org.*`, `com.*` 등) → 사내/프로젝트(`saviing.*`)
- 각 그룹 내에서 사전순 정렬, 그룹 간 빈 줄 1줄.
- 한 줄당 하나의 import, 사용하지 않는 import 제거.
- 충돌되는 이름이 있을 경우 코드에서 FQN(완전수식명) 사용을 고려.

```java
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import saviing.common.response.CommonResult;
```

### 클래스 내용 순서
- 상수 → 정적 필드 → 인스턴스 필드(각각 public → protected → package-private → private 순).
- 생성자(필요 시 검증 포함) → 정적 팩토리 → 공개 메서드 → 보호/패키지/비공개 메서드 → 보조(private) 메서드.
- 접근자: 비즈니스 메서드 다음에 getter/setter 배치(롬복 사용 시 클래스 상단 주석으로 노출 의도 명시).
- 재정의 메서드: `equals`/`hashCode`/`toString`은 클래스 말미에 함께 배치.
- 중첩 타입(내부 클래스/enum/interface)은 파일 하단에 배치.

```java
public class Example {
    // Constants
    public static final int DEFAULT_SIZE = 16;

    // Static fields
    private static final Logger log = LoggerFactory.getLogger(Example.class);

    // Instance fields
    private final Service service;

    // Constructors
    public Example(Service service) {
        this.service = Objects.requireNonNull(service);
    }

    // Static factory
    public static Example of(Service service) {
        return new Example(service);
    }

    // Public API
    public void run() {
        doWork();
    }

    // Private helpers
    private void doWork() {
        // ...
    }

    @Override public String toString() { return "Example"; }
}
```

### 상수 선언
- 네이밍: `UPPER_SNAKE_CASE`.
- 가시성: 기본은 `private static final`; 외부 공개가 필요한 경우에만 `public` 허용.
- 위치: 클래스 상단(필드들 중 최상단 그룹)에 배치, 관련 상수는 함께 묶음.
- 숫자 상수: 가독성을 위해 언더스코어 사용(`1_000_000`), `long`은 `L` 접미사. 금액/정밀도는 `BigDecimal` 문자열 생성자 사용(`new BigDecimal("0.1")`).
- 시간/기간: 밀리초 `long` 대신 `Duration`, `Period`, `Instant` 등 타입 사용.
- 컬렉션 상수: 불변으로 선언(`List.of(...)`, `Map.of(...)`, `Set.of(...)`) 또는 `Collections.unmodifiableList(...)`.
- 열거 값: 한정된 상수 집합은 `enum` 사용을 우선.
- 금지: 가변 `static` 필드는 상수가 아님(사용 자제). 매직 넘버/문자열은 상수로 추출.

```java
public class InterestPolicy {
    public static final int DEFAULT_RATE_BPS = 250;
    private static final long MAX_BALANCE_CENTS = 10_000_000_00L;
    public static final Duration DEFAULT_TERM = Duration.ofDays(30);
    public static final List<String> SUPPORTED_PRODUCTS = List.of("SAVING", "DEPOSIT");

    public enum Tier { BASIC, PREMIUM }
}
```
### 수직 공백 (줄바꿈)
- 가독성을 높이기 위해 필요 시 한 줄의 빈 줄을 사용할 수 있음. 여러 문장 사이에 빈 줄을 넣어 코드를 논리적 블록으로 구분.
- 여러 줄의 연속적인 빈 줄도 허용되지만, 필수는 아니며 권장되지 않음(최소화 권장).
- 예외 — Enum 클래스: 메소드와 문서가 없는 단순 enum은 배열 초기화처럼 한 줄로 표기 가능.

```java
private enum Suit { CLUBS, HEARTS, SPADES, DIAMONDS }
```
```java
class Example {
    void process(boolean flag) {
        if (flag) {
            doWork();
        } else {
            handle();
        }

        try {
            run();
        } catch (Exception e) {
            log.error("fail", e);
        } finally {
            cleanup();
        }

        int i = 0;
        do {
            i++;
        } while (i < 3);
    }

    void noop() {}
}
```

## 네이밍
- 패키지: 소문자, 약어 지양(`saviing.bank`, `saviing.game`).
- 클래스/인터페이스: PascalCase (`InterestService`).
- 메서드/필드/변수: camelCase (`calculateInterest`).
- 상수: UPPER_SNAKE_CASE (`DEFAULT_RATE_BPS`).
- 테스트 클래스: `<ClassName>Tests` (`BankApplicationTests`).

#### 요약 규칙
- API Endpoint: kebab-case (예: `/api/v1/bank-accounts/{id}`)
- Class: PascalCase
- 메서드/변수명: camelCase
- static 상수: UPPER_SNAKE_CASE
- DB 컬럼명: snake_case

### 네이밍 컨벤션
- 패키지 기준
  - 앱 계층: `saviing.bank`, `saviing.game`, `saviing.common` 유지.
- 계층별 접미사
  - Controller: `AccountController`, `GameController`
  - Service: `InterestService`, `LedgerService`
  - Repository(필요 시): `AccountRepository`
  - Config/AOP/Filter: `SwaggerConfig`, `ExecutionTimeAspect`, `TraceFilter`
  - 예외: `InsufficientBalanceException`, `BusinessException`
  - DTO: 요청 `...Request`, 응답 `...Response` (예: `CreateAccountRequest`, `AccountResponse`)
  - VO/ID/상태: `AccountId`, `TxnType`, `TxnStatus`
- 메서드/변수
  - boolean: `is/has/can` 접두어 사용 (`isActive`, `hasBalance`)
  - 이벤트/명령형: `create*`, `update*`, `calculate*` 등 동사+목적어
- 테스트
  - 클래스: `<ClassName>Tests`, 메서드: `given_when_then` 포맷 권장.
  - 예: `AccountServiceTests#givenValidRequest_whenCreate_thenPersisted`.

| 항목 | 명사동사 | 형식 | 예시 | 설명 |
| --- | --- | --- | --- | --- |
| 클래스명 | 명사 | `PascalCase` | `User`, `OrderService`, `UserController` | 실체/역할을 나타내는 이름 |
| 메서드명 | 동사(또는 동사+명사) | `camelCase` | `getUser()`, `updateOrder()` | 동작이나 기능 수행 |
| 변수명 | 명사 | `camelCase` | `user`, `productList` | 데이터를 담는 객체 이름 |
| Boolean 변수 | 동사+의미 | `is/has/can + PascalCase` | `isActive`, `hasPermission`, `canEdit` | 상태/가능 여부 표현 |
| 패키지명 | 명사(복수 지양) | `lowercase` | `user`, `order`, `payment` | 도메인/기능 단위 |
| 상수명 | 명사 | `UPPER_SNAKE_CASE` | `MAX_RETRY_COUNT`, `DEFAULT_ROLE` | 변경되지 않는 값 |

### 파일인코딩
- 소스/리소스 파일 인코딩: UTF-8 (BOM 없음) 권장 — UTF-8 방식 사용.
- IDE 프로젝트 인코딩을 UTF-8로 고정.
- Gradle 권장 설정: `gradle.properties`에 `org.gradle.jvmargs=-Dfile.encoding=UTF-8` 추가.

## 구조 & 스프링 관례
- 의존 주입: 생성자 주입 사용, `@Autowired` 필드 주입 지양.
- 계층: `controller` → `service` → `domain(model|vo|exception)` → `infrastructure`(필요 시).
- 설정: `config/`, AOP는 `aspect/`, 필터는 `filter/` 하위에 배치.
- 예외: 도메인 예외는 `RuntimeException` 상속, 공통 에러 코드는 `saviing-common` 활용.
- 로깅: `org.slf4j.Logger` 사용, 민감정보 로그 금지.

## 테스트
- 프레임워크: JUnit Platform (`spring-boot-starter-test`).
- 단위 테스트 선호, 컨텍스트 필요 시에만 `@SpringBootTest`.
- 메서드명: `given_when_then` 패턴 권장.
- 실행: `./gradlew test`, 모듈별 `./gradlew :saviing-bank:test`.

## 문서 & 주석
- 공개 API/구성요소는 간단한 Javadoc 작성.
- 복잡한 비즈니스 로직은 의도 중심의 주석 1~2줄.

## 예시
```java
public class InterestService {
    private static final int DEFAULT_RATE_BPS = 250;
    public Money calculateInterest(Money principal) { /* ... */ }
}
```
