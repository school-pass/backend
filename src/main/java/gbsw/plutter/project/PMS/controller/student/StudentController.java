package gbsw.plutter.project.PMS.controller.student;

import gbsw.plutter.project.PMS.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
//    @PostMapping("reqPass")
//    public ResponseEntity<Boolean> reqPass(PassDTO passDTO) {
//        return new ResponseEntity<>(studentService.reqPass(passDTO), HttpStatus.OK);
//    }
}
