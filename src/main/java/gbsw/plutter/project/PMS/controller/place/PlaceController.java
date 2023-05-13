package gbsw.plutter.project.PMS.controller.place;

import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.service.place.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;
    //모든 장소 조회(미완)
    //건물에 따라 조회(완료)
    //층 수에 따라 조회(미완)
    //장소 인원 조회(미완) ex : 현재원/총원
    @PostMapping()
    public List<Place> getLocationDetail (@RequestBody PlaceDTO pd) throws Exception {
        return placeService.getLocationDetail(pd);
    }
}
