package Security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Security.entity.User;
import Security.model.LoginResponse;
import Security.model.LoginUserModel;
import Security.model.RegisterUserModel;
import Security.service.AuthenticationService;
import Security.service.JwtService;
import jakarta.transaction.Transactional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService; // Sửa cú pháp gán giá trị
        this.authenticationService = authenticationService; // Sửa cú pháp gán giá trị
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<User> register(@RequestBody RegisterUserModel registerUser) {
        User registeredUser = authenticationService.signup(registerUser); // Thêm dấu "="
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping(path = "/login")
    @Transactional
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserModel loginUser) {
        User authenticatedUser = authenticationService.authenticate(loginUser); // Thêm dấu "="
        String jwtToken = jwtService.generateToken(authenticatedUser); // Thêm dấu "="
        
        // Tạo đối tượng LoginResponse
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
