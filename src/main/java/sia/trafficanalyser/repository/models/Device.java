package sia.trafficanalyser.repository.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
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

    private String address;


    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "device_events",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events = new HashSet<>();

    public Device(){

    }

    public Device (String key, String name, String address){
        this.key = key;
        this.name = name;
        this.address = address;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id) && Objects.equals(name, device.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
