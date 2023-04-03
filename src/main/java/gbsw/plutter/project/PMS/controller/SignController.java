package gbsw.plutter.project.outing.controller;

import gbsw.plutter.project.outing.dto.UserDTO;
import gbsw.plutter.project.outing.service.SignService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sign")
public class SignController {
    private SignService signService;

    @PostMapping("/in")
    public UserDTO writeReview(UserDTO userDTO) {
        if(userDTO.getRole().equals("Admin") || userDTO.getRole().equals("Teacher")) {

        }else{
            return this.signService.loginStudents();
        }
    }
}
