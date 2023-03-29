package gbsw.plutter.project.outing.service;

import gbsw.plutter.project.outing.model.User;
import gbsw.plutter.project.outing.repository.SignRepository;
import gbsw.plutter.project.outing.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignService {

    private final SignRepository signRepository;

    public String login(String userName, String pw) {
        //authorize
        return JwtUtil.createJwt(userName, "jwt.secret");
    }

}
