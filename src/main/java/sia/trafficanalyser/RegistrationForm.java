package sia.trafficanalyser;

import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Data;
import sia.trafficanalyser.User;

@Data
public class RegistrationForm {
    private String username;
    private String password;
    private String fullname;
    private String organisation;
    private String phone;
    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username, passwordEncoder.encode(password),
                fullname, organisation, phone);
    }
}
