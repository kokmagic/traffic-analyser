package sia.trafficanalyser.repository.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String key;

    private String name;

    private String mode;

    private String signal;

    private String view;

    private String focus;

    private String fov;

    private String brightness;

    public Device(){

    }

    public Device (String key, String name, String mode, String signal, String view){
        this.key = key;
        this.name = name;
        this.mode = mode;
        this.signal = signal;
        this.view = view;
        this.brightness = null;
        this.fov = null;
        this.focus = null;
    }
}
