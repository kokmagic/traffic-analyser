package sia.trafficanalyser.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String fullname;
    private String organisation;
    private String phoneNumber;

    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username,
                       String fullname, String organisation,
                       String phoneNumber, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.organisation = organisation;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }
}
