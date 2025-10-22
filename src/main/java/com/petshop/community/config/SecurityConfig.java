package com.petshop.community.config;

// --- 필요한 import 추가 ---
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
// -----------------------

// ... existing imports ...
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import lombok.RequiredArgsConstructor; 

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    // private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/**", "/board/api/**") // API 경로는 CSRF 무시
            )
            .headers(headers -> headers
                 .frameOptions(options -> options.deny()) // frameOptions() 수정
                 // .contentTypeOptions().and() // .and() 제거
                 .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                     .maxAgeInSeconds(31536000)
                     .includeSubDomains(true)
                     .preload(true))
                 .referrerPolicy(policy -> policy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)) // referrerPolicy() 수정
             )
             .sessionManagement(session -> session
                  .maximumSessions(3)
                  .maxSessionsPreventsLogin(false)
                  .sessionRegistry(sessionRegistry()) // sessionRegistry() Bean 필요
              )
            .authorizeHttpRequests(authz -> authz
                 // 정적 리소스 허용
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/favicon.ico").permitAll()
                 // 공개 페이지 허용
                .requestMatchers("/", "/home", "/login", "/signup").permitAll()
                .requestMatchers("/board/*/list", "/board/*/view/*").permitAll()
                .requestMatchers("/petshop/list", "/petshop/view/*").permitAll()
                .requestMatchers("/test/**", "/check/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                 // 인증 필요 페이지
                .requestMatchers("/member/**", "/board/*/write", "/board/*/edit/*", "/board/*/delete/*").authenticated()
                .requestMatchers("/petshop/review/**").authenticated()
                .requestMatchers("/api/**", "/board/api/**").authenticated() // 인증 필요한 API
                 // 관리자 전용 URL
                .requestMatchers("/admin/**").hasRole("ADMIN")
                 // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
             )

            // --- exceptionHandling 설정 ---
            .exceptionHandling(exceptions -> exceptions
                // /api/** 또는 /board/api/** 경로로 온 인증되지 않은 요청은 401 응답
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new OrRequestMatcher(
                        new AntPathRequestMatcher("/api/**"),
                        new AntPathRequestMatcher("/board/api/**")
                    )
                )
                // 그 외 모든 인증되지 않은 요청은 /login 페이지로 리다이렉트
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    AnyRequestMatcher.INSTANCE // 모든 요청에 해당
                )
                .accessDeniedPage("/error/403") // 접근 거부 시 에러 페이지
            )
            // ---------------------------------------------

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(rememberMe -> rememberMe
                .key("uniqueAndSecret") // 실제 운영 시에는 외부 설정 파일로 분리
                .tokenValiditySeconds(86400 * 14) // 14 days
                .userDetailsService(userDetailsService)
                .rememberMeParameter("remember-me")
            );

        return http.build();
    }

    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
        return new org.springframework.security.core.session.SessionRegistryImpl();
    }
}
