package sia.trafficanalyser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sia.trafficanalyser.repository.models.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findByKey(String key);
}
