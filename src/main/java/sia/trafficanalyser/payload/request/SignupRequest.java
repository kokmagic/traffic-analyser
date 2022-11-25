package sia.trafficanalyser.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    @NotBlank
    @Size(min = 10, max = 50)
    private String username;

    private Set<String> role;

    @NotBlank
    @Size(min = 8, max = 40)
    private String password;
    private String fullname;
    private String organisation;
    @NotBlank
    @Size(max = 20)
    private String phoneNumber;
}
