package sia.trafficanalyser.repository.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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
}
