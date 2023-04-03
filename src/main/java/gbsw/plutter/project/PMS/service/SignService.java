package gbsw.plutter.project.outing.service;

import gbsw.plutter.project.outing.repository.SignRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignService {

    private final SignRepository signRepository;



}
