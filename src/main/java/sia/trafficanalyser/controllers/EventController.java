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
}
