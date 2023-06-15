package sia.trafficanalyser.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sia.trafficanalyser.payload.response.MessageResponse;
import sia.trafficanalyser.repository.DeviceRepository;
import sia.trafficanalyser.repository.EventRepository;
import sia.trafficanalyser.repository.models.Device;
import sia.trafficanalyser.repository.models.Event;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
public class XMLParserController {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    EventRepository eventRepository;

    @GetMapping("/api/parse-xml")
    public ResponseEntity<?> parseXML() {
        String liplate = null;
        String vehicleType = null;
        String event = null;
        String detectionTime = null;
        String radarSpeed = null;
        String name = null;
        String serial = null;
        String location = null;

        try {
            File xmlFile = new File("E://events/2002201_2020_09_10_12_48_29_91_00_01_info.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            Element rootElement = document.getDocumentElement();

            NodeList targetInfoNodes = rootElement.getElementsByTagName("target_info");
            if (targetInfoNodes.getLength() > 0) {
                Element targetInfoElement = (Element) targetInfoNodes.item(0);
                liplate = getTagValue(targetInfoElement, "liplate");
                vehicleType = getTagValue(targetInfoElement, "vehicle_type");
                event = getTagValue(targetInfoElement, "event");
                detectionTime = getTagValue(targetInfoElement, "detection_time");
                radarSpeed = getTagValue(targetInfoElement, "radar_speed");
            }

            NodeList configNodes = rootElement.getElementsByTagName("config");
            if (configNodes.getLength() > 0) {
                Element configElement = (Element) configNodes.item(0);
                name = getTagValue(configElement, "name");
                serial = getTagValue(configElement, "serial");
                location = getTagValue(configElement, "location");
            }
        } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException e) {
            e.printStackTrace();
        }

        String typeOfEvent = "";

        String deviceName = name + " " + serial;
        System.out.println(deviceName);
        Device device = deviceRepository.findByName(deviceName);
        if (device == null) return ResponseEntity
                .ok()
                .body(new MessageResponse("Device with this name not found"));
        LocalDateTime date = LocalDateTime.parse(detectionTime);
        Double speed = -Double.parseDouble(radarSpeed);
        switch (event) {
            case ("C1.1 - Нарушение установленного скоростного режима"):
                typeOfEvent = "1";
                break;
            case ("C15 - Невыполнение требования Правил дорожного движения уступить дорогу пешеходам"):
                typeOfEvent = "2";
                break;
            case ("C0"):
                typeOfEvent = "0";
                break;
        }
        Event event1 = new Event(liplate, speed, date, vehicleType, typeOfEvent);
        Set<Event> events = device.getEvents();
        events.add(event1);
        eventRepository.save(event1);

        return ResponseEntity
                .ok()
                .body(new MessageResponse("Events added successfully."));
    }

    private String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }
}
