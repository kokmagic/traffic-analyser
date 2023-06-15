package sia.trafficanalyser.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        File csvFile;
        try {
            csvFile = File.createTempFile("export", ".csv");
            try (FileWriter writer = new FileWriter(csvFile)) {
                csvExportService.writeEventsToCsv(writer, result);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Создайте ресурс для файла CSV
        FileSystemResource csvResource = new FileSystemResource(csvFile);

        // Установите заголовки и метаданные для ответа
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("data.csv").build());

        // Верните ответ с ресурсом CSV файла
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvResource);
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

    @GetMapping("/average_speed_by_type_of_car")
    public ResponseEntity<?> getAverageSpeedByTypeOfCar(@RequestParam int yearFrom, int monthFrom, int dayFrom, int yearTo,
                                                        int monthTo, int dayTo, long id){
        Map<String, Double> result = eventService.getAverageSpeedByTypeOfCar(LocalDate.of(yearFrom, monthFrom, dayFrom), id, LocalDate.of(yearTo, monthTo, dayTo));
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/average_speed_by_type_of_event")
    public ResponseEntity<?> getAverageSpeedByTypeOfEvent(@RequestParam int yearFrom, int monthFrom, int dayFrom, int yearTo,
                                                        int monthTo, int dayTo, long id){
        Map<String, Double> result = eventService.getAverageSpeedByTypeOfEvent(LocalDate.of(yearFrom, monthFrom, dayFrom), id, LocalDate.of(yearTo, monthTo, dayTo));
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/peak_hours_for_day")
    public ResponseEntity<?> getPeakHoursForDay(@RequestParam int year, int month, int day, long id) {
        List<String> result = eventService.getPeakHoursForDay(LocalDate.of(year, month, day), id);
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/peak_hours_for_period")
    public ResponseEntity<?> getPeakHoursForPeriod(@RequestParam int yearFrom, int monthFrom, int dayFrom, int yearTo,
                                                   int monthTo, int dayTo, long id) {
        List<Object[]> result = eventService.getPeakHoursForPeriod(LocalDate.of(yearFrom, monthFrom, dayFrom), LocalDate.of(yearTo, monthTo, dayTo), id);
        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/average_speed_for_peak_hour")
    public ResponseEntity<?> getAverageSpeedInPeakHour(@RequestParam int year, int month, int day, int hour, long id) {
        LocalDateTime date = LocalDateTime.of(year, month, day, hour, 0);
        Double result = eventService.getAverageSpeedInPeak(date, id);
        return ResponseEntity
                .ok()
                .body(result);
    }
}
