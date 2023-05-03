package sia.trafficanalyser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sia.trafficanalyser.repository.models.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {



}
