package gbsw.plutter.project.PMS.service.student;

import gbsw.plutter.project.PMS.dto.PassDTO;
import gbsw.plutter.project.PMS.model.Pass;
import gbsw.plutter.project.PMS.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {
    private final PassRepository passRepository;

//    public boolean reqPass(PassDTO passDTO) {
//        try {
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException();
//        }
//        return true;
//    }
}
