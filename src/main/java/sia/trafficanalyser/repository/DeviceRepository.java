package sia.trafficanalyser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sia.trafficanalyser.repository.models.Device;

import java.util.Set;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findByKey(String key);
    Boolean existsByKey(String key);
    Boolean existsByName(String name);
}
