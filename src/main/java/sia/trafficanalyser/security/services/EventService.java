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
import java.time.temporal.ChronoUnit;
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

    public List<Double> getAverageSpeedPerHour(LocalDate day, Long deviceId) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime from = LocalDateTime.of(day, LocalTime.of(i, 0));
            LocalDateTime to = from.plusHours(1);

            TypedQuery<Double> query = entityManager.createQuery(
                    "SELECT COALESCE(AVG(e.speed), 0.0) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :from AND :to", Double.class);
            query.setParameter("deviceId", deviceId);
            query.setParameter("from", from);
            query.setParameter("to", to);

            Double averageSpeed = query.getResultList().stream().findFirst().orElse(0.0);
            result.add(averageSpeed);
        }
        return result;
    }

    public List<Map<String, Integer>> getCarTypeCountsPerHour(LocalDate day, Long deviceId) {
        List<Map<String, Integer>> result = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime from = LocalDateTime.of(day, LocalTime.of(i, 0));
            LocalDateTime to = from.plusHours(1);

            TypedQuery<Object[]> query = entityManager.createQuery(
                    "SELECT e.typeOfCar, COUNT(e) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :from AND :to GROUP BY e.typeOfCar", Object[].class);
            query.setParameter("deviceId", deviceId);
            query.setParameter("from", from);
            query.setParameter("to", to);

            Map<String, Integer> carTypeCounts = new HashMap<>();
            for (Object[] row : query.getResultList()) {
                carTypeCounts.put((String) row[0], ((Number) row[1]).intValue());
            }
            result.add(carTypeCounts);
        }
        return result;
    }

    public List<Map<String, Integer>> getEventTypeCountsPerHour(LocalDate day, Long deviceId) {
        List<Map<String, Integer>> result = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime from = LocalDateTime.of(day, LocalTime.of(i, 0));
            LocalDateTime to = from.plusHours(1);

            TypedQuery<Object[]> query = entityManager.createQuery(
                    "SELECT e.typeOfEvent, COUNT(e) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :from AND :to GROUP BY e.typeOfEvent", Object[].class);
            query.setParameter("deviceId", deviceId);
            query.setParameter("from", from);
            query.setParameter("to", to);

            Map<String, Integer> eventTypeCounts = new HashMap<>();
            for (Object[] row : query.getResultList()) {
                eventTypeCounts.put((String) row[0], ((Number) row[1]).intValue());
            }
            result.add(eventTypeCounts);
        }
        return result;
    }

    public List<Map<String, Integer>> getCarTypeCountsPerDay(LocalDate dayFrom, Long deviceId, LocalDate dayTo) {
        List<Map<String, Integer>> result = new ArrayList<>();
        LocalDateTime from = LocalDateTime.of(dayFrom, LocalTime.of(0,0));
        LocalDateTime to = LocalDateTime.of(dayTo, LocalTime.of(23,59));
        long compare = ChronoUnit.DAYS.between(from, to);
        for (int i = 0; i <= compare; i++) {
            LocalDateTime start = from.plusDays(i);
            LocalDateTime end = start.plusDays(1);
            TypedQuery<Object[]> query = entityManager.createQuery(
                    "SELECT e.typeOfCar, COUNT(e) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :start AND :end GROUP BY e.typeOfCar", Object[].class);
            query.setParameter("deviceId", deviceId);
            query.setParameter("start", start);
            query.setParameter("end", end);

            Map<String, Integer> carTypeCounts = new HashMap<>();
            for (Object[] row : query.getResultList()) {
                carTypeCounts.put((String) row[0], ((Number) row[1]).intValue());
            }
            result.add(carTypeCounts);
        }
        return result;
    }

    public List<Double> getAverageSpeedPerDay(LocalDate dayFrom, Long deviceId, LocalDate dayTo) {
        List<Double> result = new ArrayList<>();
        LocalDateTime from = LocalDateTime.of(dayFrom, LocalTime.of(0,0));
        LocalDateTime to = LocalDateTime.of(dayTo, LocalTime.of(23,59));
        long compare = ChronoUnit.DAYS.between(from, to);
        for (int i = 0; i <= compare; i++) {
            LocalDateTime start = from.plusDays(i);
            LocalDateTime end = start.plusDays(1);
            TypedQuery<Double> query = entityManager.createQuery(
                    "SELECT COALESCE(AVG(e.speed), 0.0) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :start AND :end", Double.class);
            query.setParameter("deviceId", deviceId);
            query.setParameter("start", start);
            query.setParameter("end", end);

            Double averageSpeed = query.getResultList().stream().findFirst().orElse(0.0);
            result.add(averageSpeed);
        }
        return result;
    }

    public List<Map<String, Integer>> getEventTypeCountsPerDay(LocalDate dayFrom, Long deviceId, LocalDate dayTo) {
        List<Map<String, Integer>> result = new ArrayList<>();
        LocalDateTime from = LocalDateTime.of(dayFrom, LocalTime.of(0,0));
        LocalDateTime to = LocalDateTime.of(dayTo, LocalTime.of(23,59));
        long compare = ChronoUnit.DAYS.between(from, to);
        for (int i = 0; i <= compare; i++) {
            LocalDateTime start = from.plusDays(i);
            LocalDateTime end = start.plusDays(1);
            TypedQuery<Object[]> query = entityManager.createQuery(
                    "SELECT e.typeOfEvent, COUNT(e) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :start AND :end GROUP BY e.typeOfEvent", Object[].class);
            query.setParameter("deviceId", deviceId);
            query.setParameter("start", start);
            query.setParameter("end", end);

            Map<String, Integer> eventTypeCounts = new HashMap<>();
            for (Object[] row : query.getResultList()) {
                eventTypeCounts.put((String) row[0], ((Number) row[1]).intValue());
            }
            result.add(eventTypeCounts);
        }
        return result;
    }

}
