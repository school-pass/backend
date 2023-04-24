package gbsw.plutter.project.PMS.service.place;


import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
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

    public List<Place> getParticular(PlaceDTO pd) throws Exception {
        try {
            List<Place> places;
            log.info(pd.getLocation());
            places = placeRepository.findAllByLocation(pd.getLocation());
            if (places.isEmpty()) {
                throw new Exception("location not found");
            }
            return places;
        } catch (Exception e) {
            throw new Exception("DB에서 값을 가져오는 중 오류 발생");
        }
    }

}
