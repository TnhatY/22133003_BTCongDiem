package Security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import Security.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfiguration(
			JwtAuthenticationFilter jwtAuthenticationFilter,
			AuthenticationProvider authenticationProvider
			
			) {
		this.authenticationProvider= authenticationProvider;
	
		this.jwtAuthenticationFilter =jwtAuthenticationFilter;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
	    return httpSecurity
	            .csrf(csrf -> csrf.disable()) // Tắt CSRF
	            .authorizeHttpRequests(auth -> auth
	                    .requestMatchers("/auth/**").permitAll() // Cho phép truy cập không cần xác thực
	                    .requestMatchers("/login**").permitAll()
	                    .requestMatchers("/user/**").permitAll()
	                    .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
	                    .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
	                    .anyRequest().authenticated() // Các request khác cần xác thực
	            )
	            .sessionManagement(management -> management
	                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Cấu hình session kiểu stateless
	            )
	            .authenticationProvider(authenticationProvider) // Thiết lập authenticationProvider
	            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
	            .build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    
	    // Cấu hình CORS
	    configuration.setAllowedOrigins(List.of("http://localhost:8005")); // Cho phép nguồn
	    configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")); // Phương thức HTTP
	    configuration.setAllowCredentials(true); // Cho phép gửi thông tin xác thực (cookie, header)
	    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control")); // Header được phép

	    // Tạo nguồn cấu hình dựa trên đường dẫn URL
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);

	    return source;
	}

}
