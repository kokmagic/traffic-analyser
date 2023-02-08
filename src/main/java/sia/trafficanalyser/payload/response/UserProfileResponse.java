package sia.trafficanalyser.payload.response;

import lombok.Getter;

@Getter
public class UserProfileResponse {
    private final String username;
    private final String fullname;
    private final String organisation;
    private final String phoneNumber;

    public UserProfileResponse (String username, String fullname, String organisation, String phoneNumber) {
        this.username = username;
        this.fullname = fullname;
        this.organisation = organisation;
        this.phoneNumber = phoneNumber;
    }
}
