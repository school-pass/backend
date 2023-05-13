package gbsw.plutter.project.PMS.controller.admin;

import gbsw.plutter.project.PMS.config.JwtProvider;
import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.model.Member;
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
import java.util.List;
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
    //유저생성(완료), 유저수정(미완), 유저삭제(미완), 유저조회(미완)
    //장소생성(완료), 장소수정(미완), 장소삭제(미완), 장소조회(미완)
    @PostMapping("/addUser")
    public ResponseEntity<Boolean> addUser(@RequestBody SignRequest req) throws Exception {
        //serialNum 으로 유저 검색
        Optional<Member> serNum = memberRepository.findBySerialNumber(req.getSerialNum());
        if (serNum.isPresent()) {
            throw new Exception("user found for serialNum: " + req.getSerialNum());
        } else {
            return new ResponseEntity<>(adminService.addUser(req), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public Optional<Member> getMemberById(@PathVariable("id") Long id) throws Exception {
        return adminService.getMemberById(id);
    }
    @PostMapping("userList")
    public List<Member> userList(@RequestBody MemberDTO md) throws Exception {
        return adminService.getUserList();
    }

    @PutMapping("editUser")
    public ResponseEntity<Boolean> editUser(@RequestBody MemberDTO md) throws Exception {
        Optional<Member> isUser = memberRepository.findByAccount(md.getAccount());
        if (isUser.isPresent()) {
            return new ResponseEntity<>(adminService.editUser(md), HttpStatus.OK);
        } else {
            throw new Exception("User Not Found For SerialNum : " + md.getSerialNum());
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Boolean> deleteUser(@RequestBody MemberDTO md) throws  Exception {
        Optional<Member> serNum = memberRepository.findById(md.getId());
        if(serNum.isPresent()) {
            return new ResponseEntity<>(adminService.deleteUser(md), HttpStatus.OK);
        } else {
            throw new Exception("User Not Found ID : "+md.getId());
        }
    }

    @PostMapping("/addPlace")
    public ResponseEntity<Boolean> addPlace(@RequestBody PlaceDTO req, HttpServletRequest httpReq) throws Exception {
        Optional<Teacher> tId;
        // 사용자가 별도로 요청하는 teacher가 있는 경우
        if (req.getTeacherId() != null) {
            try {
                tId = teacherRepository.findTeacherByMember_Id(Long.parseLong(req.getTeacherId().toString()));
            } catch (NumberFormatException e) {
                throw new Exception("Invalid teacher ID format");
            }
            if (tId.isEmpty()) {
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
