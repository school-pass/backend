package gbsw.plutter.project.PMS.controller.admin;

import gbsw.plutter.project.PMS.config.JwtProvider;
import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.dto.STDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.model.Member;
import gbsw.plutter.project.PMS.model.SchoolTime;
import gbsw.plutter.project.PMS.model.Teacher;
import gbsw.plutter.project.PMS.repository.MemberRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import gbsw.plutter.project.PMS.service.admin.AdminService;
import gbsw.plutter.project.PMS.service.place.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final PlaceService placeService;
    private final TeacherRepository teacherRepository;
    //유저생성(완료), 유저수정(완료), 유저삭제(완료), 유저조회(완료)
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

    @PostMapping("/addTime")
    public ResponseEntity<Boolean> addTime(@RequestBody STDTO stdto) throws Exception {
        try {
            return new ResponseEntity<>(adminService.addSchoolTime(stdto), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("올바른 값을 입력해주세요");
        }
    }

    @PutMapping("/editTime")
    public ResponseEntity<Boolean> editTime(@RequestBody STDTO stdto) throws Exception {
        try {
            return new ResponseEntity<>(adminService.editSchoolTime(stdto), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("올바른 값을 입력해주세요");
        }
    }

    @DeleteMapping("/deleteTime")
    public ResponseEntity<Boolean> deleteTime(@RequestBody STDTO stdto) throws Exception {
        try {
            return new ResponseEntity<>(adminService.deleteSchoolTime(stdto), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("올바른 값을 입력해주세요");
        }
    }
    @PutMapping("/editUser")
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

    @PutMapping("/editPlace")
    public ResponseEntity<Boolean> editPlace(@RequestBody PlaceDTO pd) throws Exception {
        try {
            return new ResponseEntity<>(placeService.editPlace(pd), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("오류 발생");
        }
    }

    @DeleteMapping("/deletePlace")
    public ResponseEntity<Boolean> deletePlace(@RequestBody PlaceDTO pd) throws Exception {
        try {
            return new ResponseEntity<>(placeService.deletePlace(pd), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("오류 발생");
        }
    }
}
