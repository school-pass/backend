package gbsw.plutter.project.PMS.service.pass;

import gbsw.plutter.project.PMS.dto.PassDTO;
import gbsw.plutter.project.PMS.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PassService {
    private final PassRepository passRepository;

    public Boolean applyPass(PassDTO pd) {
        return null;
    }
}
