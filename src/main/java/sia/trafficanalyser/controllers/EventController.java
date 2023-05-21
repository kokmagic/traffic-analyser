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
import sia.trafficanalyser.security.services.CsvExportService;
import sia.trafficanalyser.security.services.EventService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

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

    @Autowired
    CsvExportService csvExportService;

    @GetMapping("/show_events")
    public ResponseEntity<?> showEvents(@RequestParam Long id, String typeOfFiltration, @RequestParam(value = "typeOfCar", required = false) String typeOfCar,
                                        @RequestParam(value = "typeOfEvent", required = false) String typeOfEvent) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Device with this id not found!"));
        Device device1 = device.get();
        Set<Event> events = device1.getEvents();
        Set<Event> result = new HashSet<>();
        switch (typeOfFiltration) {
            case ("0"):
                result = eventService.filterByEvent(typeOfEvent, events);
                break;
            case ("1"):
                result = eventService.filterByCar(typeOfCar, events);
                break;
            case ("2"):
                result = eventService.filterByBoth(typeOfCar, typeOfEvent, events);
                break;
            case ("3"):
                result = events;
        }
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportToCsv(@RequestParam Long id, String typeOfFiltration, @RequestParam(value = "typeOfCar", required = false) String typeOfCar,
                                         @RequestParam(value = "typeOfEvent", required = false) String typeOfEvent, HttpServletResponse servletResponse) throws IOException {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Device with this id not found!"));
        Device device1 = device.get();
        Set<Event> events = device1.getEvents();
        Set<Event> result = new HashSet<>();
        switch (typeOfFiltration) {
            case ("0"):
                result = eventService.filterByEvent(typeOfEvent, events);
                break;
            case ("1"):
                result = eventService.filterByCar(typeOfCar, events);
                break;
            case ("2"):
                result = eventService.filterByBoth(typeOfCar, typeOfEvent, events);
                break;
            case ("3"):
                result = events;
                break;
        }
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"events.csv\"");
        csvExportService.writeEventsToCsv(servletResponse.getWriter(), result);
        return ResponseEntity.ok(new MessageResponse("Events exported successfully!"));
    }

    @GetMapping("/average_speed")
    public ResponseEntity<?> getAverageSpeed(@RequestParam int year, int month, int day, long id) {
        List<Double> result = eventService.getAverageSpeedPerHour(LocalDate.of(year, month, day), id);
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/type_of_car")
    public ResponseEntity<?> getTypeOfCar(@RequestParam int year, int month, int day, long id) {
        List<Map<String, Integer>> result = eventService.getCarTypeCountsPerHour(LocalDate.of(year, month, day), id);
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/type_of_event")
    public ResponseEntity<?> getTypeOfEvent(@RequestParam int year, int month, int day, long id) {
        List<Map<String, Integer>> result = eventService.getEventTypeCountsPerHour(LocalDate.of(year, month, day), id);
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/average_speed_per_day")
    public ResponseEntity<?> getAverageSpeedPerDay(@RequestParam int yearFrom, int monthFrom, int dayFrom, int yearTo,
                                                   int monthTo, int dayTo, long id) {
        List<Double> result = eventService.getAverageSpeedPerDay(LocalDate.of(yearFrom, monthFrom, dayFrom), id, LocalDate.of(yearTo, monthTo, dayTo));
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/type_of_car_per_day")
    public ResponseEntity<?> getTypeOfCarPerDay(@RequestParam int yearFrom, int monthFrom, int dayFrom, int yearTo,
                                                   int monthTo, int dayTo, long id) {
        List<Map<String, Integer>> result = eventService.getCarTypeCountsPerDay(LocalDate.of(yearFrom, monthFrom, dayFrom), id, LocalDate.of(yearTo, monthTo, dayTo));
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/type_of_event_per_day")
    public ResponseEntity<?> getTypeOfEventPerDay(@RequestParam int yearFrom, int monthFrom, int dayFrom, int yearTo,
                                                int monthTo, int dayTo, long id) {
        List<Map<String, Integer>> result = eventService.getEventTypeCountsPerDay(LocalDate.of(yearFrom, monthFrom, dayFrom), id, LocalDate.of(yearTo, monthTo, dayTo));
        return ResponseEntity
                .ok()
                .body(result);
    }
}
