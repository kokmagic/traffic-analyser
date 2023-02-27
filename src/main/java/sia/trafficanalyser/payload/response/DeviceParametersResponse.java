package sia.trafficanalyser.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceParametersResponse {
    private String fov;
    private String focus;
    private String brightness;

    public DeviceParametersResponse(String fov, String focus, String brightness) {
        this.fov = fov;
        this.focus = focus;
        this.brightness = brightness;
    }
}
