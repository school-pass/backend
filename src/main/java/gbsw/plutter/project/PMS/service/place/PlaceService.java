package gbsw.plutter.project.PMS.service.place;


import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.model.Teacher;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public List<Place> getLocationDetail(PlaceDTO pd) throws Exception {
        List<Place> places;
        try {
            places = placeRepository.findAllByLocation(pd.getLocation());
            if (places.isEmpty()) {
                throw new Exception("location not found");
            }
        } catch (Exception e) {
            throw new Exception("DB에서 값을 가져오는 중 오류 발생");
        }
        return places;
    }

    public List<Place> getAllPlace() throws Exception {
        List<Place> places;
        try {
            places = placeRepository.findAll();
            if (places.isEmpty()) {
                throw new Exception("장소를 추가해주세요");
            }
        } catch (Exception e) {
            throw new Exception("DB에서 값을 가져오는 중 오류 발생");
        }
        return places;
    }

    public Boolean editPlace(PlaceDTO pd) throws Exception {
        try {
            Place place;
            place = placeRepository.findPlaceByIpAddress(pd.getIpAddress());
            Teacher tid = teacherRepository.findTeacherById(pd.getTeacherId());
            if(place != null) {
                place.setTeacher(tid);
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new Exception("장소를 수정하는 중 오류 발생");
        }
        return true;
    }

    public Boolean deletePlace(PlaceDTO pd) throws Exception {
        try{
            Optional<Place> place = placeRepository.findById(pd.getId());
            if(place.isEmpty()) {
                return false;
            }
            placeRepository.deleteById(pd.getId());
            } catch (Exception e) {
                throw new Exception("오류 발생");
            }
            return true;
        }
    }
