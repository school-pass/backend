package gbsw.plutter.project.PMS.controller;


import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;

    @PostMapping("/login")
    public ResponseEntity<MemberDTO> login(@RequestBody SignRequest sign) throws Exception {
        return new ResponseEntity<>(signService.login(sign), HttpStatus.OK);
    }
}
