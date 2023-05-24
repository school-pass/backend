package gbsw.plutter.project.PMS.controller.pass;


import gbsw.plutter.project.PMS.dto.PassDTO;
import gbsw.plutter.project.PMS.model.*;
import gbsw.plutter.project.PMS.repository.*;
import gbsw.plutter.project.PMS.service.pass.PassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("pass")
public class PassController {
    private final PassService passService;
    private final PassRepository passRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolTimeRepository schoolTimeRepository;
    //
    @PostMapping("/add")
    public ResponseEntity<Boolean> addPass(@RequestBody PassDTO pd) throws Exception {
        try {
            Optional<Member> mId;
            Optional<Place> particular;
            Optional<Teacher> tId;
            mId = memberRepository.findById(pd.getUserId());
            if(mId.isEmpty()) {
                throw new Exception("ID : "+pd.getUserId()+"를 가진 유저가 존재하지 않습니다.");
            }
            particular = placeRepository.findByLocationDetail(pd.getDetail());
            if(particular.isEmpty()) {
                throw new Exception(pd.getDetail()+"인 장소가 존재하지 않습니다.");
            }
            //장소로 선생님 찾기
            tId = teacherRepository.findById(particular.get().getTeacher().getId());
            if(tId.isEmpty()) {
                throw new Exception("ID : "+pd.getTeacherId()+"를 가진 교사가 존재하지 않습니다.");
            }
            return new ResponseEntity<>(passService.addPass(pd, particular, mId, tId), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("패스 신청 중 에러가 발생했습니다. 원인 : " + e.getMessage());
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<Boolean> applyPass(@RequestBody PassDTO pd) throws Exception {
        try {
            Optional<Pass> isPass;
            isPass = passRepository.findById(pd.getPassId());
            if(isPass.isEmpty()) {
                throw new Exception("ID : "+pd.getPassId()+"를 가진 출입증이 존재하지 않습니다.");
            }
            return new ResponseEntity<>(passService.applyPass(pd), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("출입증 승인/반려 중 에러가 발생했습니다. 원인 : " + e.getMessage());
        }
    }

    @PostMapping("/use")
    public ResponseEntity<Boolean> validPass(@RequestBody PassDTO pd) throws Exception {
//        try {
            return new ResponseEntity<>(passService.validPass(pd), HttpStatus.OK);
//        } catch (Exception e) {
//            throw new Exception("출입증 검증 도중 에러가 발생했습니다. 원인 : "+e.getMessage());
//        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getAllPass() throws Exception {
        try {
            List<Pass> passList = passService.getAllPass();
            List<Map<String, Object>> modifiedPassList = new ArrayList<>();
            LocalDate today = LocalDate.now();
            SchoolTime schoolTimeStart;
            SchoolTime schoolTimeEnd;
            LocalTime startTime;
            LocalTime endTime;
            LocalDateTime passStart;
            LocalDateTime passEnd;
            for (Pass pass : passList) {
                if (pass.getStartPeriod().equals(pass.getEndPeriod())) {
                    schoolTimeStart = schoolTimeRepository.findByPeriod(pass.getStartPeriod());
                    startTime = schoolTimeStart.getStartTime();
                    endTime = schoolTimeStart.getEndTime();
                    passStart = LocalDateTime.of(today, startTime);
                    passEnd = LocalDateTime.of(today, endTime);
                } else {
                    schoolTimeStart = schoolTimeRepository.findByPeriod(pass.getStartPeriod());
                    schoolTimeEnd = schoolTimeRepository.findByPeriod(pass.getEndPeriod());
                    startTime = schoolTimeStart.getStartTime();
                    endTime = schoolTimeEnd.getEndTime();
                    passStart = LocalDateTime.of(today, startTime);
                    passEnd = LocalDateTime.of(today, endTime);
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Map<String, Object> modifiedPass = new HashMap<>();
                modifiedPass.put("id", pass.getId());
                modifiedPass.put("memberId", pass.getMember().getId());
                modifiedPass.put("teacherId", pass.getTeacher().getId());
                modifiedPass.put("startPeriod", passStart.format(formatter));
                modifiedPass.put("endPeriod", passEnd.format(formatter));
                if (pass.getCreatedAt() != null)
                    modifiedPass.put("createdAt", pass.getCreatedAt().format(formatter));
                if (pass.getUpdatedAt() != null)
                    modifiedPass.put("updatedAt", pass.getUpdatedAt().format(formatter));

                modifiedPass.put("reason", pass.getPassReason());
                modifiedPass.put("passStatus", pass.getPassStatus().toString());

                modifiedPassList.add(modifiedPass);
            }
            return new ResponseEntity<>(modifiedPassList, HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("출입증 전체 조회 중 에러가 발생했습니다. 원인: " + e.getMessage());
        }
    }
//    @PutMapping("/update")
//    public ResponseEntity<Boolean> updatePass(@RequestBody PassDTO pd) throws Exception {
//        try {
//            Optional<Pass> pId;
//            pId = passRepository.findById(pd.getPassId());
//            if(pId.isEmpty()) {
//                throw new Exception("ID : "+pd.getPassId()+"를 가진 출입증이 존재하지 않습니다.");
//            }
//            return new ResponseEntity<>(passService.updatePass(pd), HttpStatus.OK);
//        } catch (Exception e) {
//            throw new Exception();
//        }
//    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deletePass(@RequestBody PassDTO pd) throws Exception {
        try {
            Optional<Pass> pId;
            pId = passRepository.findById(pd.getPassId());
            if(pId.isEmpty()) {
                throw new Exception();
            }
            return new ResponseEntity<>(passService.deletePass(pd), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("");
        }
    }
}
