package sia.trafficanalyser.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.trafficanalyser.payload.request.DeviceParametersRequest;
import sia.trafficanalyser.payload.request.LinkRequest;
import sia.trafficanalyser.payload.response.DeviceParametersResponse;
import sia.trafficanalyser.payload.response.MessageResponse;
import sia.trafficanalyser.repository.DeviceProjection;
import sia.trafficanalyser.repository.DeviceRepository;
import sia.trafficanalyser.repository.UserRepository;
import sia.trafficanalyser.repository.models.Device;
import sia.trafficanalyser.repository.models.User;
import sia.trafficanalyser.security.services.UserDetailsServiceImpl;

import java.util.List;
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
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/link")
    public ResponseEntity<?> linkDevice(@RequestBody LinkRequest linkRequest) {
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
        Set<Device> devices = user1.getDevices();
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

    @GetMapping("/show_all")
    public ResponseEntity<?> showAllDevices (@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: User with this id not found!"));
        List<DeviceProjection> devices = deviceRepository.findAllBy();
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/register_device")
    public ResponseEntity<?> registerDevice (@RequestParam String key, String name) {
        if (key == null || name == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: name and key of device must not be null!"));
        Device device = new Device();
        device.setKey(key);
        device.setName(name);
        if (deviceRepository.existsByKey(key) || deviceRepository.existsByName(name)) return
                ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: device with this key or name already exists!"));
        deviceRepository.save(device);
        return ResponseEntity.ok(new MessageResponse("Device registered successfully!"));
    }
}
