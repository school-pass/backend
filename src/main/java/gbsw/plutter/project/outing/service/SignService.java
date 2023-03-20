package gbsw.plutter.project.outing.service;

import gbsw.plutter.project.outing.model.User;
import gbsw.plutter.project.outing.repository.SignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignService {

    private final SignRepository signRepository;

    public HashMap<String, Object> findUserByPassword(String password) {
        return signRepository.findByPasswordLike(password);
    }

}
