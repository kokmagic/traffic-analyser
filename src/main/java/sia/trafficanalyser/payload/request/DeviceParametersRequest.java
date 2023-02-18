package sia.trafficanalyser.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class DeviceParametersRequest {
    @NotBlank
    private Long id;
    @NotBlank
    private String type;
    private String value;
}
