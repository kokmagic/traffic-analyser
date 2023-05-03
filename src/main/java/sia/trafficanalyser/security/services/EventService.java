package sia.trafficanalyser.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sia.trafficanalyser.repository.EventRepository;
import sia.trafficanalyser.repository.models.Event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
public class EventService {
    @Autowired
    EventRepository eventRepository;

    public Set<Event> filterByEvent (String type, Set<Event> events) {
        Set<Event> result = new HashSet<>();
        for (Event event: events) {
            if (Objects.equals(event.getTypeOfEvent(), type)) result.add(event);
        }
        return result;
    }

    public Set<Event> filterByCar (String type, Set<Event> events) {
        Set<Event> result = new HashSet<>();
        for (Event event: events) {
            if (Objects.equals(event.getTypeOfCar(), type)) result.add(event);
        }
        return result;
    }

    @PersistenceContext
    private EntityManager entityManager;

    public List<Double> getAverageSpeedPerHour(LocalDate day) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime from = LocalDateTime.of(day, LocalTime.of(i, 0));
            LocalDateTime to = from.plusHours(1);

            TypedQuery<Double> query = entityManager.createQuery(
                    "SELECT AVG(e.speed) FROM Event e WHERE e.time BETWEEN :from AND :to", Double.class);
            query.setParameter("from", from);
            query.setParameter("to", to);

            Double averageSpeed = query.getSingleResult();
            result.add(averageSpeed != null ? averageSpeed : 0.0);
        }
        return result;
    }

}
