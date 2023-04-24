package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByLocation(String location);
}