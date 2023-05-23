package sia.trafficanalyser.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sia.trafficanalyser.repository.EventRepository;
import sia.trafficanalyser.repository.models.Event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    public Set<Event> filterByBoth (String typeOfCar, String typeOfEvent, Set<Event> events) {
        Set<Event> result = new HashSet<>();
        for (Event event: events) {
            if (Objects.equals(event.getTypeOfEvent(), typeOfEvent) && Objects.equals(event.getTypeOfCar(), typeOfCar))
                result.add(event);
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

    public Map<String, Double> getAverageSpeedByTypeOfCar(LocalDate dayFrom, Long deviceId, LocalDate dayTo) {
        Map<String, Double> result = new HashMap<>();
        LocalDateTime start = LocalDateTime.of(dayFrom, LocalTime.of(0, 0));
        LocalDateTime end = LocalDateTime.of(dayTo, LocalTime.of(23, 59));
        for (int i = 0; i <= 3; i++) {
            TypedQuery<Double> query = entityManager.createQuery(
                    "SELECT COALESCE(AVG(e.speed), 0.0) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :start AND :end AND e.typeOfCar = :typeOfCar", Double.class);
            query.setParameter("deviceId", deviceId);
            query.setParameter("start", start);
            query.setParameter("end", end);
            query.setParameter("typeOfCar", Integer.toString(i));
            Double averageSpeed = query.getSingleResult();
            result.put(Integer.toString(i), averageSpeed);
        }
        return result;
    }

    public Map<String, Double> getAverageSpeedByTypeOfEvent(LocalDate dayFrom, Long deviceId, LocalDate dayTo) {
        Map<String, Double> result = new HashMap<>();
        LocalDateTime start = LocalDateTime.of(dayFrom, LocalTime.of(0, 0));
        LocalDateTime end = LocalDateTime.of(dayTo, LocalTime.of(23, 59));
        for (int i = 0; i <= 2; i++) {
            TypedQuery<Double> query = entityManager.createQuery(
                    "SELECT COALESCE(AVG(e.speed), 0.0) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :start AND :end AND e.typeOfEvent = :typeOfEvent", Double.class);
            query.setParameter("deviceId", deviceId);
            query.setParameter("start", start);
            query.setParameter("end", end);
            query.setParameter("typeOfEvent", Integer.toString(i));
            Double averageSpeed = query.getSingleResult();
            result.put(Integer.toString(i), averageSpeed);
        }
        return result;
    }

    public List<String> getPeakHoursForDay(LocalDate date, long id) {
        String jpql = "SELECT FUNCTION('HOUR', e.time), COUNT(e) " +
                "FROM Device d JOIN d.events e WHERE d.id = :deviceId " +
                "AND e.time >= :startDate AND e.time <= :endDate " +
                "GROUP BY FUNCTION('HOUR', e.time) " +
                "ORDER BY COUNT(e) DESC";

        LocalDateTime startDate = LocalDateTime.of(date, LocalTime.of(0, 0));
        LocalDateTime endDate = LocalDateTime.of(date, LocalTime.of(23, 59));
        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("deviceId", id);
        query.setMaxResults(1);

        return query.getResultList();
    }

    public List<Object[]> getPeakHoursForPeriod(LocalDate startDate, LocalDate endDate, long id) {
        String jpql = "SELECT FUNCTION('DAY', e.time), FUNCTION('HOUR', e.time), COUNT(e) " +
                "FROM Device d JOIN d.events e WHERE d.id = :deviceId " +
                "AND e.time >= :startDate AND e.time < :endDate " +
                "GROUP BY FUNCTION('DAY', e.time), FUNCTION('HOUR', e.time) " +
                "ORDER BY COUNT(e) DESC";

        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.of(0, 0));
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.of(0, 0));
        LocalDateTime endDateAdjusted = end.plusDays(1);
        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", start);
        query.setParameter("endDate", endDateAdjusted);
        query.setParameter("deviceId", id);
        query.setMaxResults(1);
        return query.getResultList();
    }

    public Double getAverageSpeedInPeak(LocalDateTime date, long id) {
        LocalDateTime endDate = date.plusHours(1);
        TypedQuery<Double> query = entityManager.createQuery(
                "SELECT COALESCE(AVG(e.speed), 0.0) FROM Device d JOIN d.events e WHERE d.id = :deviceId AND e.time BETWEEN :date AND :endDate", Double.class);
        query.setParameter("deviceId", id);
        query.setParameter("date", date);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }
}
