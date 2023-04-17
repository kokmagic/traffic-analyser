package sia.trafficanalyser.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sia.trafficanalyser.repository.EventRepository;
import sia.trafficanalyser.repository.models.Event;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
}
