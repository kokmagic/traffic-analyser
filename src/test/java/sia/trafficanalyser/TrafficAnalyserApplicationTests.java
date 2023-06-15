package sia.trafficanalyser;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import sia.trafficanalyser.payload.request.ForgotPasswordRequest;
import sia.trafficanalyser.payload.request.LinkRequest;
import sia.trafficanalyser.payload.request.LoginRequest;
import sia.trafficanalyser.payload.request.SignupRequest;
import sia.trafficanalyser.payload.response.JwtResponse;
import sia.trafficanalyser.payload.response.UserProfileResponse;
import sia.trafficanalyser.repository.DeviceRepository;
import sia.trafficanalyser.repository.UserRepository;
import sia.trafficanalyser.repository.models.Device;
import sia.trafficanalyser.repository.models.Event;
import sia.trafficanalyser.repository.models.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrafficAnalyserApplicationTests {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;


    @Test
    public void testSignUpUser() {
        // Arrange
        String username = "glebushkin.suhorukovvvv@gmail.com";
        String password = "1234567890";
        String phoneNumber = "89186103680";

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(username);
        signupRequest.setPassword(password);
        signupRequest.setPhoneNumber(phoneNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> requestEntity = new HttpEntity<>(signupRequest, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/auth/signup", requestEntity, String.class);
        String messageResponse = responseEntity.getBody();

        assertNotNull(messageResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"User registered successfully!\"}", messageResponse);
        User user = userRepository.findByUsername(username);
        userRepository.delete(user);
    }

    @Test
    public void testSignInUser() {
        String username = "glebushkin.suhorukov+6@gmail.com";
        String password = "1234567890";
        String phoneNumber = "89186103680";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<JwtResponse> responseEntity = restTemplate.postForEntity("/api/auth/signin", requestEntity, JwtResponse.class);
        JwtResponse jwtResponse = responseEntity.getBody();

        assertNotNull(jwtResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(username, jwtResponse.getUsername());
        assertEquals(phoneNumber, jwtResponse.getPhoneNumber());
    }

    public String signIn() {
        String username = "glebushkin.suhorukovv@gmail.com";
        String password = "0123456789";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<JwtResponse> responseEntity = restTemplate.postForEntity("/api/auth/signin", requestEntity, JwtResponse.class);
        JwtResponse jwtResponse = responseEntity.getBody();

        assert jwtResponse != null;
        return jwtResponse.getToken();
    }

    @Test
    public void testDeviceLink() {
        String jwt = signIn();
        Long id = 15L;
        String key = "12342453";

        LinkRequest linkRequest = new LinkRequest();
        linkRequest.setId(id);
        linkRequest.setKey(key);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<LinkRequest> requestEntity = new HttpEntity<>(linkRequest, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/device/link", requestEntity, String.class);
        String messageResponse = responseEntity.getBody();

        assertNotNull(messageResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Device successfully linked!\"}", messageResponse);

        Device device = deviceRepository.findByKey(key);
        User user = userRepository.findByUsername("glebushkin.suhorukov+6@gmail.com");
        Hibernate.initialize(user.getDevices());
        Set<Device> devices = user.getDevices();

        assertTrue(devices.contains(device));
    }

    @Test
    public void testDeviceShow() {
        String jwt = signIn();
        Long id = 15L;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);

        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<Set<Device>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Set<Device>> responseEntity = restTemplate.exchange("/api/device/show?id={id}", HttpMethod.GET, requestEntity, responseType, id);
        Set<Device> devices = responseEntity.getBody();
        Device device = deviceRepository.findByKey("12342453");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(devices.contains(device));
        System.out.println(devices);
    }

    @Test
    @Transactional
    public void testEventShow() {
        String jwt = signIn();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);

        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<Set<Event>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Set<Event>> responseEntity = restTemplate.exchange("/api/event/show_events?id=1&typeOfFiltration=3", HttpMethod.GET, requestEntity, responseType);
        Set<Event> events = responseEntity.getBody();
        Device device = deviceRepository.findByKey("12342453");
        Set<Event> deviceEvents = device.getEvents();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        for (Event event : deviceEvents) {
            assertTrue(events.contains(event));
        }
    }

    @Test
    public void testEventAverageSpeed() {
        String jwt = signIn();
        Double[] answers = {86.07, 3.14, 96.14, 0.0, 0.08, 0.0, 22.52, 0.0, 0.0, 0.0, 0.0, 54.739999999999995, 0.0, 0.0, 0.0, 0.0, 0.0, 0.94, 0.0, 3.66, 0.0, 0.0, 0.0, 33.64};

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<List<Double>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<Double>> responseEntity = restTemplate.exchange("/api/event/average_speed?year=2023&month=04&day=10&id=1", HttpMethod.GET, requestEntity, responseType);
        List<Double> result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        for (int i = 0; i < result.size(); i++) {
            double speed = result.get(i);
            assertEquals(answers[i], speed);
        }
    }

    @Test
    public void testEventTypeOfCar() {
        String jwt = signIn();

        List<Map<String, Integer>> answers = new ArrayList<>();

        Map<String, Integer> map1 = new HashMap<>();
        map1.put("3", 1);
        answers.add(map1);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("2", 1);
        answers.add(map2);

        Map<String, Integer> map3 = new HashMap<>();
        map3.put("0", 1);
        answers.add(map3);

        answers.add(new HashMap<>());

        Map<String, Integer> map4 = new HashMap<>();
        map4.put("1", 1);
        answers.add(map4);

        answers.add(new HashMap<>());

        Map<String, Integer> map5 = new HashMap<>();
        map5.put("3", 1);
        answers.add(map5);
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        Map<String, Integer> map6 = new HashMap<>();
        map6.put("0", 1);
        map6.put("1", 1);
        answers.add(map6);
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        Map<String, Integer> map7 = new HashMap<>();
        map7.put("0", 1);
        answers.add(map7);
        answers.add(new HashMap<>());
        answers.add(map7);
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        answers.add(new HashMap<>());
        answers.add(map7);



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<List<Map<String, Integer>>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<Map<String, Integer>>> responseEntity = restTemplate.exchange("/api/event/type_of_car?year=2023&month=04&day=10&id=1", HttpMethod.GET, requestEntity, responseType);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Map<String, Integer>> result = responseEntity.getBody();
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), answers.get(i));
        }
    }

    @Test
    public void testEventTypeOfEvent() {
        String jwt = signIn();

        List<Map<String, Integer>> data = new ArrayList<>();

        Map<String, Integer> map1 = new HashMap<>();
        map1.put("0", 1);
        data.add(map1);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("0", 1);
        data.add(map2);

        Map<String, Integer> map3 = new HashMap<>();
        map3.put("0", 1);
        data.add(map3);

        data.add(new HashMap<>());

        Map<String, Integer> map4 = new HashMap<>();
        map4.put("0", 1);
        data.add(map4);

        data.add(new HashMap<>());

        Map<String, Integer> map5 = new HashMap<>();
        map5.put("0", 1);
        data.add(map5);

        data.add(new HashMap<>());
        data.add(new HashMap<>());
        data.add(new HashMap<>());
        data.add(new HashMap<>());

        Map<String, Integer> map6 = new HashMap<>();
        map6.put("0", 2);
        data.add(map6);

        data.add(new HashMap<>());
        data.add(new HashMap<>());
        data.add(new HashMap<>());
        data.add(new HashMap<>());
        data.add(new HashMap<>());

        Map<String, Integer> map7 = new HashMap<>();
        map7.put("0", 1);
        data.add(map7);

        data.add(new HashMap<>());

        Map<String, Integer> map8 = new HashMap<>();
        map8.put("0", 1);
        data.add(map8);

        data.add(new HashMap<>());
        data.add(new HashMap<>());
        data.add(new HashMap<>());

        Map<String, Integer> map9 = new HashMap<>();
        map9.put("1", 1);
        data.add(map9);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<List<Map<String, Integer>>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<Map<String, Integer>>> responseEntity = restTemplate.exchange("/api/event/type_of_event?year=2023&month=04&day=10&id=1", HttpMethod.GET, requestEntity, responseType);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Map<String, Integer>> result = responseEntity.getBody();
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), data.get(i));
        }
    }

    @Test
    public void testEventAverageSpeedPerDay() {
        String jwt = signIn();

        Double[] answers = {35.56699999999999, 44.277};

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<Double>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<Double>> responseEntity = restTemplate.exchange("/api/event/average_speed_per_day?yearFrom=2023&monthFrom=04&dayFrom=10&id=1&yearTo=2023&monthTo=04&dayTo=11",
                HttpMethod.GET, requestEntity, responseType);
        List<Double> result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        for (int i = 0; i < result.size(); i++) {
            double speed = result.get(i);
            assertEquals(answers[i], speed);
        }
    }

    @Test
    public void testEventTypeOfCarPerDay() {
        String jwt = signIn();
        List<Map<String, Integer>> answers = new ArrayList<>();

        Map<String, Integer> map1 = new HashMap<>();
        map1.put("0", 5);
        map1.put("1", 2);
        map1.put("2", 1);
        map1.put("3", 2);
        answers.add(map1);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("0", 9);
        map2.put("1", 1);
        answers.add(map2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<List<Map<String, Integer>>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<Map<String, Integer>>> responseEntity = restTemplate.exchange("/api/event/type_of_car_per_day?yearFrom=2023&monthFrom=04&dayFrom=10&id=1&yearTo=2023&monthTo=04&dayTo=11",
                HttpMethod.GET, requestEntity, responseType);
        List<Map<String, Integer>> result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), answers.get(i));
        }
    }

    @Test
    public void testEventTypeOfEventPerDay() {
        String jwt = signIn();

        List<Map<String, Integer>> answers = new ArrayList<>();

        Map<String, Integer> map1 = new HashMap<>();
        map1.put("0", 9);
        map1.put("1", 1);
        answers.add(map1);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("0", 9);
        map2.put("1", 1);
        answers.add(map2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<List<Map<String, Integer>>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<Map<String, Integer>>> responseEntity = restTemplate.exchange("/api/event/type_of_event_per_day?yearFrom=2023&monthFrom=04&dayFrom=10&id=1&yearTo=2023&monthTo=04&dayTo=11",
                HttpMethod.GET, requestEntity, responseType);
        List<Map<String, Integer>> result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), answers.get(i));
        }
    }

    @Test
    public void testEventAverageSpeedByTypeOfCar() {
        String jwt = signIn();
        Map<String, Double> answers = new HashMap<>();
        answers.put("0", 45.62285714285714);
        answers.put("1", 15.996666666666668);
        answers.put("2", 3.14);
        answers.put("3", 54.294999999999995);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<Map<String, Double>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Map<String, Double>> responseEntity = restTemplate.exchange("/api/event/average_speed_by_type_of_car?yearFrom=2023&monthFrom=04&dayFrom=10&id=1&yearTo=2023&monthTo=04&dayTo=11",
                HttpMethod.GET, requestEntity, responseType);
        Map<String, Double> result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(answers, result);
    }

    @Test
    public void testEventAverageSpeedByTypeOfEvent() {
        String jwt = signIn();
        Map<String, Double> answers = new HashMap<>();
        answers.put("0", 41.569444444444436);
        answers.put("1", 25.095);
        answers.put("2", 0.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<Map<String, Double>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Map<String, Double>> responseEntity = restTemplate.exchange("/api/event/average_speed_by_type_of_event?yearFrom=2023&monthFrom=04&dayFrom=10&id=1&yearTo=2023&monthTo=04&dayTo=11",
                HttpMethod.GET, requestEntity, responseType);
        Map<String, Double> result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(answers, result);
    }

    @Test
    public void testEventPeakHoursForDay() {
        String jwt = signIn();

        List<List<String>> answers = new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add("11");
        list.add("2");
        answers.add(list);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<List<String>>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<List<String>>> responseEntity = restTemplate.exchange("/api/event/peak_hours_for_day?year=2023&month=04&day=10&id=1",
                HttpMethod.GET, requestEntity, responseType);
        List<List<String>> result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(answers, result);
    }

    @Test
    public void testEventPeakHoursForPeriod() {
        String jwt = signIn();

        List<List<String>> answers = new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add("10");
        list.add("11");
        list.add("2");
        answers.add(list);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<List<String>>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<List<String>>> responseEntity = restTemplate.exchange("/api/event/peak_hours_for_period?yearFrom=2023&monthFrom=04&dayFrom=10&id=1&yearTo=2023&monthTo=04&dayTo=11",
                HttpMethod.GET, requestEntity, responseType);
        List<List<String>> result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(answers, result);
    }

    @Test
    public void testEventAverageSpeedForPeakHour() {
        String jwt = signIn();

        Double answer = 54.739999999999995;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Double> responseEntity = restTemplate.exchange("/api/event/average_speed_for_peak_hour?year=2023&month=04&day=10&id=1&hour=11",
                HttpMethod.GET, requestEntity, Double.class);
        Double result = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(answer, result);
    }

    @Test
    public void testResetPassword() {
        String jwt = signIn();

        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setUsername("glebushkin.suhorukov+6@gmail.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<ForgotPasswordRequest> requestEntity = new HttpEntity<>(forgotPasswordRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/password/forgot", requestEntity, String.class);
        String messageResponse = responseEntity.getBody();

        assertNotNull(messageResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"We have sent a reset password link to your email. Please check.\"}", messageResponse);
    }

    @Test
    public void testProfileGetInfo() {
        String jwt = signIn();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<UserProfileResponse> responseEntity = restTemplate.exchange("/api/profile/getinfo?username=glebushkin.suhorukov@gmail.com", HttpMethod.GET, requestEntity, UserProfileResponse.class);
        UserProfileResponse result = responseEntity.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("glebushkin.suhorukov@gmail.com", result.getUsername());
        assertEquals("89186103680", result.getPhoneNumber());
    }

    @Test
    public void testProfileChangeInfo() {
        String jwt = signIn();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<UserProfileResponse> responseEntity = restTemplate.exchange("/api/profile/changeprofile?username=glebushkin.suhorukov@gmail.com&type=fullname&value=Gleb Suhorukov",
                HttpMethod.POST, requestEntity, UserProfileResponse.class);
        UserProfileResponse result = responseEntity.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("glebushkin.suhorukov@gmail.com", result.getUsername());
        assertEquals("89186103680", result.getPhoneNumber());
        assertEquals("Gleb Suhorukov", result.getFullname());
    }

    @Test
    public void testXmlParser() {
        String jwt = signIn();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange("/api/parse-xml", HttpMethod.GET, requestEntity, String.class);
        String messageResponse = responseEntity.getBody();

        assertNotNull(messageResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Events added successfully.\"}", messageResponse);
    }

    @Test
    public void testAdminShowAll() {
        String jwt = signIn();
        List<Device> answers = new ArrayList<>();
        Device device = deviceRepository.findByKey("12342453");
        answers.add(device);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<List<Device>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<Device>> responseEntity = restTemplate.exchange("/api/device/show_all?username=glebushkin.suhorukovv@gmail.com", HttpMethod.GET, requestEntity, responseType);
        List<Device> result = responseEntity.getBody();
        assertEquals(answers, result);
    }

    @Test
    public void testAdminRegisterDevice() {
        String jwt = signIn();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange("/api/device/register_device?key=123452&name=СКАТ-ПП 20036", HttpMethod.POST, requestEntity, String.class);
        String messageResponse = responseEntity.getBody();

        assertNotNull(messageResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Device registered successfully!\"}", messageResponse);
        assertNotNull(deviceRepository.findByName("СКАТ-ПП 20036"));
        Device device = deviceRepository.findByName("СКАТ-ПП 20036");
        deviceRepository.delete(device);

    }

    @Test
    void contextLoads() {
    }

}
