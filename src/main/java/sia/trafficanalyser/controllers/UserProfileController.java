package sia.trafficanalyser.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.trafficanalyser.payload.request.UserProfileRequest;
import sia.trafficanalyser.repository.models.User;
import sia.trafficanalyser.payload.response.MessageResponse;
import sia.trafficanalyser.payload.response.UserProfileResponse;
import sia.trafficanalyser.repository.UserRepository;
import sia.trafficanalyser.security.jwt.AuthTokenFilter;
import sia.trafficanalyser.security.jwt.JwtUtils;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthTokenFilter authTokenFilter;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/getinfo")
    public ResponseEntity<?> getUserProfile(@RequestParam String username ) {
        User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: User with this id not found!"));
        } else {
                return ResponseEntity.ok(new UserProfileResponse(
                        user.getUsername(),
                        user.getFullname(),
                        user.getOrganisation(),
                        user.getPhoneNumber()));
            }
    }

    @PostMapping("/changeprofile")
    public ResponseEntity<?> changeUserProfile(@RequestParam String type, @RequestParam String value,
                                               @RequestParam String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User with this id not found!"));
        }
        if (type != null) {
            switch (type) {
                case ("fullname"):
                    user.setFullname(value);
                    break;
                case ("organisation"):
                    user.setOrganisation(value);
                    break;
                case ("phoneNumber"):
                    user.setPhoneNumber(value);
                    break;
                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Invalid type to change!"));
            }
            userRepository.save(user);
            return ResponseEntity.ok(new UserProfileResponse(
                    user.getUsername(),
                    user.getFullname(),
                    user.getOrganisation(),
                    user.getPhoneNumber()));
        } else return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Invalid type to change!"));
    }
}
