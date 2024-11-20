package Service;

import Entity.UserInfo;
import Repository.UserInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public record UserService(UserInfoRepository  repository,
                          PasswordEncoder passwordEncoder) {
    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "Them user thanh cong!";
    }
}
