package sia.trafficanalyser.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.trafficanalyser.payload.request.LinkRequest;
import sia.trafficanalyser.payload.response.MessageResponse;
import sia.trafficanalyser.repository.DeviceRepository;
import sia.trafficanalyser.repository.UserRepository;
import sia.trafficanalyser.repository.models.Device;
import sia.trafficanalyser.repository.models.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/api/device")
public class DeviceController {

    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/link")
    public ResponseEntity<?> linkDevice(@RequestBody LinkRequest linkRequest ) {
        String key = linkRequest.getKey();
        Device device = deviceRepository.findByKey(key);
        Optional<User> user = userRepository.findById(linkRequest.getId());
        if (user.isEmpty()) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User with this id not found!"));
        if (device == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Device with this key not found!"));
        User user1 = user.get();
        Set<Device> devices = new HashSet<>();
        devices.add(device);
        user1.setDevices(devices);
        userRepository.save(user1);
        return ResponseEntity
                .ok()
                .body(new MessageResponse("Device successfully linked!"));
    }
    @GetMapping("/show")
    public ResponseEntity<?> showDevices(@RequestParam Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: User with this id not found!"));
        User user1 = user.get();
        Set<Device> devices = user1.getDevices();
        return ResponseEntity
                .ok()
                .body(devices);
    }
}
