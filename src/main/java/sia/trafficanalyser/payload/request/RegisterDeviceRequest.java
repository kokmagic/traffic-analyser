package sia.trafficanalyser.payload.request;

import lombok.Getter;

@Getter
public class RegisterDeviceRequest {
    private String key;
    private String name;
}
