package Config;

import Entity.UserInfo;
import Repository.UserInfoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserInfoService implements UserDetailsService {
    UserInfoRepository repository;

    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.repository = userInfoRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = repository.findByName(username);
        return userInfo.map(UserInfoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }
}
