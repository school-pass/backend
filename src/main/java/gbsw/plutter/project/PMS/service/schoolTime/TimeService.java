package gbsw.plutter.project.PMS.service.schoolTime;

import gbsw.plutter.project.PMS.dto.STDTO;
import gbsw.plutter.project.PMS.model.SchoolTime;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
import gbsw.plutter.project.PMS.repository.SchoolTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TimeService {
    private final SchoolTimeRepository schoolTimeRepository;
    public SchoolTime findTimeByPeriod(STDTO stdto) throws Exception {
        try {
            SchoolTime school;
            school = schoolTimeRepository.findByPeriod(stdto.getPeriod());
            if(school == null) {
                throw new Exception("해당하는 교시를 가진 시간표가 없습니다.");
            }
            return school;
        } catch (Exception e) {
            throw new Exception("에러 발생");
        }
    }
}
