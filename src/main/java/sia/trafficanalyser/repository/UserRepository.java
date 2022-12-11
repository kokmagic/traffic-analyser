package sia.trafficanalyser.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import sia.trafficanalyser.models.User;

import java.util.Optional;


public interface UserRepository  extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Boolean existsByUsername(String username);
    User findByResetPasswordToken(String token);
}
