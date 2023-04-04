package gbsw.plutter.project.PMS.controller;

import gbsw.plutter.project.PMS.dto.ResultDto;
import gbsw.plutter.project.PMS.dto.UserDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SignController {

    @PostMapping("/login")
    public ResultDto login(UserDTO user) {
        return new ResultDto(true, "test", null);
    }

}
