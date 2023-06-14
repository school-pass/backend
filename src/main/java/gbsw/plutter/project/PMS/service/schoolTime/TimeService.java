package gbsw.plutter.project.PMS.service.schoolTime;

import gbsw.plutter.project.PMS.dto.STDTO;
import gbsw.plutter.project.PMS.model.SchoolTime;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
import gbsw.plutter.project.PMS.repository.SchoolTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TimeService {
    private final SchoolTimeRepository schoolTimeRepository;
    public SchoolTime findTimeByPeriod(Integer period) {
            SchoolTime school;
            school = schoolTimeRepository.findByPeriod(period);
            if(school == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"해당하는 교시를 가진 시간표가 없습니다.");
            }
        try {
            return school;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"에러 발생");
        }
    }
}
