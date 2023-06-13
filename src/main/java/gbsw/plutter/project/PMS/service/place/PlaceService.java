package gbsw.plutter.project.PMS.service.place;

import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.model.Teacher;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final TeacherRepository teacherRepository;

    public List<Place> getPlaceByLocation(String pd) {
        List<Place> places;
        try {
            places = placeRepository.findAllByLocation(pd);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while retrieving location detail", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve location detail", e);
        }
        return places;
    }

    public List<Place> getAllPlace() {
        List<Place> places = placeRepository.findAll();
        if (places.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No places found");
        } else {
            return places;
        }
    }

    public Boolean editPlace(PlaceDTO pd) {
        try {
            Place place = placeRepository.findPlaceByIpAddress(pd.getIpAddress());
            if (place != null) {
                Teacher teacher = teacherRepository.findTeacherById(pd.getTeacherId());
                if (teacher != null) {
                    place.setTeacher(teacher);
                    place.setFloor(pd.getFloor());
                    place.setLocation(pd.getLocation());
                    place.setLocationDetail(pd.getDetail());
                    place.setIpAddress(pd.getIpAddress());

                    placeRepository.save(place);

                    return true;
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found");
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while editing place", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to edit place");
        }
    }

    public Boolean deletePlace(PlaceDTO pd) {
        try {
            Optional<Place> place = placeRepository.findById(pd.getId());
            if (place.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found");
            }
            placeRepository.deleteById(pd.getId());
            return true;
        } catch (Exception e) {
            log.error("Error occurred while deleting place", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete place");
        }
    }
}
