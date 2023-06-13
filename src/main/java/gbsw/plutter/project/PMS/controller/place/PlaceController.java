package gbsw.plutter.project.PMS.controller.place;

import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.service.place.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;
    //모든 장소 조회(완료)
    //건물에 따라 조회(완료)
    //층 수에 따라 조회(미완)
    //장소 인원 조회(미완) ex : 현재원/총원
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getAllPlace() {
        List<Place> places = placeService.getAllPlace();
        if(places.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "location isn't exist");
        }
        try {
            List<Map<String, Object>> placeList = new ArrayList<>();
            for(Place place : places) {
                Map<String, Object> modList = new HashMap<>();

                modList.put("id", place.getId());
                modList.put("location", place.getLocation());
                modList.put("locationDetail", place.getLocationDetail());
                modList.put("floor", place.getFloor());
                modList.put("capacity", place.getCapacity());
                modList.put("maxCapacity", place.getMaxCapacity());
                modList.put("teacherId", place.getTeacher().getId());
                modList.put("teacherName", place.getTeacher().getName());
                modList.put("teacherRoles", place.getTeacher().getTpermission());
                modList.put("teacherSerialNum", place.getTeacher().getSerialNum());

                placeList.add(modList);
            }
            return new ResponseEntity<>(placeList, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"장소 전체 조회 중 오류 발생");
        }
    }

//    @PostMapping("/") {
//
//    }

    @PostMapping("/detail")
    public ResponseEntity<List<Map<String, Object>>> getPlaceByLocation(@RequestBody PlaceDTO pd) {
        List<Place> places = placeService.getPlaceByLocation(pd.getLocation());
        if(places.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can not find place by location");
        }
        try {
            List<Map<String, Object>> placeList = new ArrayList<>();
            for (Place place: places) {
                Map<String, Object> modList = new HashMap<>();

                modList.put("id", place.getId());
                modList.put("location", place.getLocation());
                modList.put("locationDetail", place.getLocationDetail());
                modList.put("floor", place.getFloor());
                modList.put("capacity", place.getCapacity());
                modList.put("maxCapacity", place.getMaxCapacity());
                modList.put("teacherId", place.getTeacher().getId());
                modList.put("teacherName", place.getTeacher().getName());
                modList.put("teacherRoles", place.getTeacher().getTpermission());
                modList.put("teacherSerialNum", place.getTeacher().getSerialNum());

                placeList.add(modList);
            }
            return new ResponseEntity<>(placeList, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "장소 상세 정보 조회 중 오류 발생");
        }
    }
}
