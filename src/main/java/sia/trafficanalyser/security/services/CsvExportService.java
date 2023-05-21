package sia.trafficanalyser.security.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import sia.trafficanalyser.repository.models.Event;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class CsvExportService {
    private static final Logger log = getLogger(CsvExportService.class);

    public void writeEventsToCsv(Writer writer, Set<Event> events) {
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("ID", "Speed", "Date","Type of car","Type of event");
            for (Event event : events) {
                csvPrinter.printRecord(event.getId(), event.getSpeed(), event.getTime(), event.getTypeOfCar(), event.getTypeOfEvent());
            }
        } catch (IOException e) {
            log.error("Error While writing CSV ", e);
        }
    }
}
