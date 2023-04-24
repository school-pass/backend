package gbsw.plutter.project.PMS.controller.place;

import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.service.Place.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping()
    public List<Place> getParticular (@RequestBody PlaceDTO pd) throws Exception {
        return new ArrayList<>(placeService.getParticular(pd));
    }
}
