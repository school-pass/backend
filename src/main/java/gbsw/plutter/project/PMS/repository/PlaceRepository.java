package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByLocation(String location);

    Optional<Place> findByLocationDetail(String detail);

    Optional<Place> findPlaceByLocationAndLocationDetail(String location, String detail);

    Place findPlaceByIpAddress(String ipAddress);

    Optional<List<Place>> findAllByTeacher(Teacher teacher);

    List<Place> findPlacesByTeacher(Teacher teacher);
}