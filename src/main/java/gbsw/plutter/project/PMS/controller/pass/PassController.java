package gbsw.plutter.project.PMS.controller.pass;


import gbsw.plutter.project.PMS.dto.PassDTO;
import gbsw.plutter.project.PMS.service.pass.PassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("Pass")
public class PassController {
    private final PassService passService;
    @PostMapping()
    public ResponseEntity<Boolean> applyPass(PassDTO pd) {
        return new ResponseEntity<>(passService.applyPass(pd), HttpStatus.OK);
    }
}
