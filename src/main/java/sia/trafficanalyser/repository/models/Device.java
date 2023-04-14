package sia.trafficanalyser.repository.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "device_events",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events = new HashSet<>();

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

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }
}
