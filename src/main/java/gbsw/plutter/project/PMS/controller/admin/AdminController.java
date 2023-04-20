package gbsw.plutter.project.PMS.controller.admin;

import gbsw.plutter.project.PMS.config.JwtProvider;
import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.model.Member;
import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.model.Teacher;
import gbsw.plutter.project.PMS.repository.MemberRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import gbsw.plutter.project.PMS.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    private final TeacherRepository teacherRepository;

    @PostMapping("/addUser")
    public ResponseEntity<Boolean> addUser(@RequestBody SignRequest req) throws Exception {
        Optional<Member> serNum = memberRepository.findBySerialNum(req.getSerialNum());
        if(serNum.isPresent()){

        }
        return new ResponseEntity<>(adminService.addUser(req), HttpStatus.OK);
    }

    @PostMapping("/addPlace")
    public ResponseEntity<Place> addPlace(@RequestBody PlaceDTO req, HttpServletRequest httpReq) throws Exception {
        Optional<Teacher> tId;
        // 사용자가 별도로 요청하는 teacher가 있는 경우
        if (req.getTeacherId() != null) {
            try {
                tId = teacherRepository.findTeacherByMember_Id(Long.parseLong(req.getTeacherId().toString()));
            } catch (NumberFormatException e) {
                throw new Exception("Invalid teacher ID format");
            }
            if (!tId.isPresent()) {
                throw new Exception("Teacher not found");
            }
        } else {
            // 사용자가 별도로 요청하는 teacher가 없는 경우
            String token = jwtProvider.resolveToken(httpReq);
            String teacherId = jwtProvider.getUserId(token.replace("Bearer", ""));
            Optional<Member> acc = memberRepository.findByAccount(teacherId);
            tId = teacherRepository.findTeacherByMember_Id(acc.get().getId());
        }
        return new ResponseEntity<>(adminService.addPlace(req, tId), HttpStatus.OK);
    }
}
