package Security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import Security.entity.User;
import Security.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	public List<User> allUser(){
		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);
		return users;
	}
}
