package Security.filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails. UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import Security.service.JwtService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { private final HandlerExceptionResolver handlerExceptionResolver;
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	
	public JwtAuthenticationFilter(
			JwtService jwtService, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver)
	{
		this.jwtService= jwtService;
		this.userDetailsService =userDetailsService;
		this.handlerExceptionResolver= handlerExceptionResolver;
	}
	
	


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
	    final String authHeader = request.getHeader("Authorization");

	    // Kiểm tra xem Authorization header có tồn tại và bắt đầu bằng "Bearer "
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        filterChain.doFilter(request, response);
	        return;
	    }

	    try {
	        final String jwt = authHeader.substring(7); // Loại bỏ "Bearer " để lấy JWT
	        final String userEmail = jwtService.extractUsername(jwt); // Lấy email từ JWT

	        // Lấy Authentication hiện tại từ SecurityContext
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	        if (userEmail != null && authentication == null) {
	            // Load thông tin User từ UserDetailsService
	            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

	            // Kiểm tra tính hợp lệ của token
	            if (jwtService.isTokenValid(jwt, userDetails)) {
	                // Tạo UsernamePasswordAuthenticationToken
	                UsernamePasswordAuthenticationToken authToken =
	                        new UsernamePasswordAuthenticationToken(
	                                userDetails, 
	                                null, 
	                                userDetails.getAuthorities()
	                        );

	                // Thiết lập chi tiết cho authToken
	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	                // Đặt Authentication vào SecurityContext
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }

	        // Tiếp tục xử lý filter chain
	        filterChain.doFilter(request, response);
	    } catch (Exception exception) {
	        // Xử lý ngoại lệ
	        handlerExceptionResolver.resolveException(request, response, null, exception);
	    }
	}

}