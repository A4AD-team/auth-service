# AGENTS.md

This file contains guidelines and commands for agentic coding agents working in this Java Spring Boot authentication service repository.

## Build & Development Commands

### Maven Commands
```bash
# Clean and compile
mvn clean compile

# Run tests (all)
mvn test

# Run single test class
mvn test -Dtest=ClassNameTest

# Run single test method
mvn test -Dtest=ClassNameTest#methodName

# Run with specific profile
mvn clean spring-boot:run -Dspring.profiles.active=local

# Build JAR
mvn clean package

# Skip tests during build
mvn clean package -DskipTests

# Generate dependency tree
mvn dependency:tree

# Check for dependency updates
mvn versions:display-dependency-updates
```

### Docker Commands
```bash
# Build Docker image
docker build -t auth-service .

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f auth-service
```

## Code Style Guidelines

### Java Conventions
- Use Java 21 features (records, pattern matching, sealed classes where appropriate)
- Follow Spring Boot 3.3+ conventions
- Package structure: `com.company.auth.{layer}.{feature}`
  - Example: `com.company.auth.controller.auth`, `com.company.auth.service.user`, `com.company.auth.repository.role`

### Import Organization
```java
// 1. Java standard library (java.*)
import java.util.List;
import java.util.Optional;

// 2. Third-party libraries (org.*, com.* but not your company)
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

// 3. Your company packages (com.company.*)
import com.company.auth.entity.User;
import com.company.auth.repository.UserRepository;
```

### Naming Conventions
- **Classes**: PascalCase (e.g., `AuthenticationService`, `UserRepository`)
- **Methods**: camelCase, descriptive verbs (e.g., `authenticateUser`, `findByEmail`)
- **Variables**: camelCase, meaningful names (e.g., `userEntity`, `jwtToken`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `DEFAULT_ROLE_NAME`, `JWT_EXPIRATION_MS`)
- **Packages**: lowercase, dot-separated (e.g., `controller.auth`, `service.user`)

### Error Handling
```java
// Use Spring's @ControllerAdvice for global exception handling
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
    }
}

// Custom exceptions should extend RuntimeException
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
```

### Service Layer Pattern
```java
@Service
@Transactional
@RequiredArgsConstructor // Lombok for constructor injection
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Implementation
    }
}
```

### Repository Pattern
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId")
    List<User> findByDepartment(@Param("departmentId") Long departmentId);
}
```

### Controller Pattern
```java
@RestController
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/sign_in")
    public ResponseEntity<AuthenticationResponse> signIn(
            @Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
```

### Entity Pattern
```java
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
```

### Configuration
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Security configuration
    }
}
```

## Testing Guidelines

### Test Structure
```java
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthenticationService authenticationService;
    
    @Test
    @DisplayName("Should authenticate user with valid credentials")
    void shouldAuthenticateUserWithValidCredentials() {
        // Test implementation
    }
}
```

### Test Naming
- Use `@DisplayName` for descriptive test names
- Pattern: `should[ExpectedBehavior]when[StateUnderTest]`
- Arrange-Act-Assert structure

## Git Flow & Commit Messages

Follow the Git Flow rules defined in `GIT_FLOW.md`:

### Commit Message Format
```
<type>[optional scope]: <description>

Examples:
feat(auth): add JWT token validation
fix: resolve memory leak in rate limiter
docs(api): update authentication endpoints
test(auth): add unit tests for user service
```

### Branch Naming
- `feature/<description>` - New features
- `bugfix/<description>` - Bug fixes  
- `hotfix/<description>` - Production hotfixes

## Environment Profiles

- `local` - Local development
- `dev` - Staging environment
- `prod` - Production (secrets via Vault/AWS SSM)

## Security Best Practices

- Never commit secrets or API keys
- Use environment variables for sensitive configuration
- Validate all input with `@Valid` and JSR-303 annotations
- Use Spring Security for authentication/authorization
- Hash passwords with BCrypt encoder
- Implement rate limiting for authentication endpoints

## Database

- Use Liquibase or Flyway for migrations
- Store migrations in `src/main/resources/db/migration/`
- Use PostgreSQL 16+ with JSONB for custom claims
- Follow JPA/Hibernate best practices

## Observability

- Use OpenTelemetry for distributed tracing
- Expose metrics at `/actuator/prometheus`
- Health checks at `/actuator/health`
- Use structured logging with correlation IDs