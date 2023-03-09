package sia.trafficanalyser.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.trafficanalyser.payload.request.DeviceParametersRequest;
import sia.trafficanalyser.payload.request.LinkRequest;
import sia.trafficanalyser.payload.request.RegisterDeviceRequest;
import sia.trafficanalyser.payload.request.ShowAllDevicesRequest;
import sia.trafficanalyser.payload.response.DeviceParametersResponse;
import sia.trafficanalyser.payload.response.MessageResponse;
import sia.trafficanalyser.repository.DeviceRepository;
import sia.trafficanalyser.repository.UserRepository;
import sia.trafficanalyser.repository.models.Device;
import sia.trafficanalyser.repository.models.Role;
import sia.trafficanalyser.repository.models.User;
import sia.trafficanalyser.security.services.UserDetailsServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static sia.trafficanalyser.repository.models.ERole.ROLE_ADMIN;

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

    //отправляешь json с id type value как в профиле
    @PostMapping("/change_parameters")
    public ResponseEntity<?> changeParameters(@RequestBody DeviceParametersRequest deviceParametersRequest) {
        Long id = deviceParametersRequest.getId();
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Device with this id not found!"));
        Device device1 = device.get();
        String value = deviceParametersRequest.getValue();
        if (deviceParametersRequest.getType() != null) {
            switch (deviceParametersRequest.getType()) {
                case ("fov"):
                    device1.setFov(value);
                    break;
                case ("focus"):
                    device1.setFocus(value);
                    break;
                case ("brightness"):
                    device1.setBrightness(value);
                    break;
                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Invalid type to change!"));
            }
            deviceRepository.save(device1);
            return ResponseEntity.ok(new DeviceParametersResponse(
                    device1.getFov(),
                    device1.getFocus(),
                    device1.getBrightness()));
        } else return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Invalid type to change!"));
    }

    @GetMapping("/show_parameters")
    public ResponseEntity<?> showParameters (@RequestParam Long id, String username) { //отправялешь параметром айди камеры id и юзернейм
        User user = userRepository.findByUsername(username);
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Device with this id not found!"));
        Device device1 = device.get();
        if (!user.getDevices().contains(device1) && !userDetailsService.isAdmin(user)) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: this device doesn't belong to this user!"));
        return ResponseEntity.ok(new DeviceParametersResponse(
                device1.getFov(),
                device1.getFocus(),
                device1.getBrightness()));
    }

    @GetMapping("/show_all")
    public ResponseEntity<?> showAllDevices (@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: User with this id not found!"));
        List<Device> devices = deviceRepository.findAll();
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
