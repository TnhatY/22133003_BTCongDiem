package Security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Security.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
	Optional<User> findByEmail(String email);
}
