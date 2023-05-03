package sia.trafficanalyser.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.trafficanalyser.payload.response.MessageResponse;
import sia.trafficanalyser.repository.DeviceRepository;
import sia.trafficanalyser.repository.EventRepository;
import sia.trafficanalyser.repository.UserRepository;
import sia.trafficanalyser.repository.models.Device;
import sia.trafficanalyser.repository.models.Event;
import sia.trafficanalyser.security.services.EventService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/api/event")
public class EventController {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventService eventService;

    @GetMapping("/show_events")
    public ResponseEntity<?> showEvents(@RequestParam String key) {
        Device device = deviceRepository.findByKey(key);
        if (device == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Device with this key not found!"));
        Set<Event> events = device.getEvents();
        return ResponseEntity
                .ok()
                .body(events);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterEvents(@RequestParam int filterType, String type, String key) {
        Device device = deviceRepository.findByKey(key);
        if (device == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Device with this key not found!"));
        Set<Event> events = device.getEvents();
        Set<Event> result;
        if (filterType == 0) result = eventService.filterByEvent(type, events);
        else result = eventService.filterByCar(type, events);
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/average_speed")
    public ResponseEntity<?> getAverageSpeed(@RequestParam int year, int month, int day) {
        List<Double> result = eventService.getAverageSpeedPerHour(LocalDate.of(year, month, day));
        return ResponseEntity
                .ok()
                .body(result);
    }
}
