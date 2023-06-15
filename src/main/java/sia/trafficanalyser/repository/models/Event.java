package sia.trafficanalyser.repository.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String carId;

    private Double speed;

    private LocalDateTime time;

    private String typeOfCar;

    private String typeOfEvent;

    public Event() {

    }

    public Event(String carId, Double speed, LocalDateTime time, String typeOfCar, String typeOfEvent) {
        this.carId = carId;
        this.speed = speed;
        this.time = time;
        this.typeOfCar = typeOfCar;
        this.typeOfEvent = typeOfEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
