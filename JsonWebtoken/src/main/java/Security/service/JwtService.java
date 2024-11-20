package Security.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;



@Service
public class JwtService{
		@Value("$(security.jwt.secret-key)")
		private String secretkey;
		@Value("$(security.jwt.expiration-time}")
		private long jwtExpiration;

		public String extractUsername(String token) {
			return extractClaim(token, Claims::getSubject);
		}

		public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
			final Claims claims =extractAllClaims(token);
			return claimsResolver.apply(claims);
		}

		

		public String generateToken (UserDetails userDetails) { 
			return generateToken (new HashMap<>(), userDetails);
				
		}
		public String generateToken (Map<String, Object> extraclaims, UserDetails userDetails) { 
			return buildToken (extraclaims, userDetails, jwtExpiration);
		}
		public long getExpirationTime() {
			return jwtExpiration;
		}
		private String buildToken(Map<String, Object> extraclaims, UserDetails userDetails, long expiration) {
			return Jwts.builder()
					.issuedAt(new Date(System.currentTimeMillis())) // the token will be expired in 30 hours
					.claims (extraclaims) 
					.subject (userDetails.getUsername()) 
					.expiration(new Date(System.currentTimeMillis() + 1000* 60 * 60 *30)) 
					.signWith(getSignInKey(), Jwts.SIG.HS256)
					.compact();
		}
		public boolean isTokenValid(String token, UserDetails userDetails) {
			final String username = extractUsername(token);
			return (username.equals(userDetails.getUsername())) && ! isTokenExpired(token);
		}
		
		private boolean isTokenExpired (String token) {
			return extractExpiration (token).before(new Date()); 
		}
		private Date extractExpiration (String token) {
			return extractClaim (token, Claims::getExpiration); 
		}
		private Claims extractAllClaims (String token) {
			return Jwts
					.parser()
					.verifyWith(getSignInKey())
					.build()
					.parseSignedClaims (token)
					.getPayload();
		}
		private SecretKey getSignInKey() {
			CharSequence secretKey;
			byte[] keyBytes = Decoders.BASE64.decode(secretkey); return Keys.hmacShaKeyFor(keyBytes); 
			}
		
}

