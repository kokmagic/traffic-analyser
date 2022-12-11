package sia.trafficanalyser.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank
    public String newPassword;
    @NotBlank
    public String token;
}
