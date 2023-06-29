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

import java.util.ArrayList;
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

    public List<Place> findPlaceByTeacherId(Long id) {
        Teacher teacher;
        Optional<Teacher> isTeacher = teacherRepository.findById(id);
        if(isTeacher.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID : "+id+"를 가진 교사를 찾을 수 없습니다.");
        }
        teacher = isTeacher.get();
        List<Place> place = new ArrayList<>();
        Optional<List<Place>> isPlace = placeRepository.findAllByTeacher(teacher);
        if(isPlace.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID : "+id+"를 가진 교사가 담당하는 구역이 없습니다.");
        }
        try {
            place = isPlace.get();
            return place;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DB에서 값을 불러오는 도중 에러 발생");
        }
    }
    public Boolean editPlace(PlaceDTO pd) {
        Optional<Teacher> isTeacher = Optional.ofNullable(teacherRepository.findTeacherByMember_Id(pd.getTeacherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 ID를 가진 교사를 찾을 수 없습니다.")));
        Teacher teacher = isTeacher.get();
        Optional<Place> isPlace = Optional.ofNullable(placeRepository.findById(pd.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 ID를 가진 장소를 찾을 수 없습니다.")));
        Place isIp = placeRepository.findPlaceByIpAddress(pd.getIpAddress());

        if (isIp != null && !isIp.getId().equals(pd.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "다른 장소에서 이미 같은 IP 주소를 사용하고 있습니다.");
        }

        try {
            Place place = isPlace.get();
            place.setTeacher(teacher);
            place.setFloor(pd.getFloor());
            place.setLocation(pd.getLocation());
            place.setLocationDetail(pd.getDetail());
            place.setMaxCapacity(pd.getMaxCapacity());
            place.setIpAddress(pd.getIpAddress());

            placeRepository.save(place);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DB에 값을 저장하는 도중 에러가 발생했습니다.");
        }
        return true;
    }

    public Boolean deletePlace(PlaceDTO pd) {
        try {
            Optional<Place> place = placeRepository.findById(pd.getId());
            if (place.isEmpty()) {
                return false;
            }
            placeRepository.delete(place.get());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
