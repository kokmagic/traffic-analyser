package sia.trafficanalyser.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ForgotPasswordRequest {
    @NotBlank
    public String username;
}
