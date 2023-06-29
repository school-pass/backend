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
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/pass")
public class PassController {
    private final PassService passService;
    private final PassRepository passRepository;
    private final SchoolTimeRepository schoolTimeRepository;

    public LocalDateTime[] conversionPeriod(Integer startPeriod, Integer endPeriod) {
        LocalDate today = LocalDate.now();
        SchoolTime schoolTimeStart;
        SchoolTime schoolTimeEnd;
        LocalTime startTime;
        LocalTime endTime;
        LocalDateTime passStart;
        LocalDateTime passEnd;
        schoolTimeStart = schoolTimeRepository.findByPeriod(startPeriod);
        schoolTimeEnd = schoolTimeRepository.findByPeriod(endPeriod);
        startTime = schoolTimeStart.getStartTime();
        endTime = schoolTimeEnd.getEndTime();
        passStart = LocalDateTime.of(today, startTime);
        passEnd = LocalDateTime.of(today, endTime);

        LocalDateTime[] passStartAndEnd = { passStart, passEnd };
        return passStartAndEnd;
    }
    @PostMapping("/add")
    public ResponseEntity<Boolean> addPass(@RequestBody PassDTO pd) {
        return new ResponseEntity<>(passService.addPass(pd), HttpStatus.OK);
    }

    @PostMapping("/apply")
    public ResponseEntity<Boolean> applyPass(@RequestBody PassDTO pd) {
        Optional<Pass> isPass;
        isPass = passRepository.findById(pd.getPassId());
        if(isPass.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID : "+pd.getPassId()+"를 가진 출입증이 존재하지 않습니다.");
        }
        return new ResponseEntity<>(passService.applyPass(pd), HttpStatus.OK);
    }

    @PostMapping("/use")
    public ResponseEntity<Boolean> validPass(@RequestBody PassDTO pd) throws Exception {
        try {
            return new ResponseEntity<>(passService.validPass(pd), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("출입증 검증 도중 에러가 발생했습니다. 원인 : "+e.getMessage());
        }
    }

    @PostMapping("/phone")
    public ResponseEntity<List<Map<String, Object>>> findByUserId(@RequestBody PassDTO pd) throws Exception {
        List<Map<String, Object>> modifiedPassList = new ArrayList<>();
        try {
            List<Pass> passes = passService.findByUserId(pd);
            for(Pass pass : passes) {
                Map<String, Object> modfiedPass = new HashMap<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime[] passStartAndEnd = conversionPeriod(pass.getStartPeriod(), pass.getEndPeriod());
                LocalDateTime passStart = passStartAndEnd[0];
                LocalDateTime passEnd = passStartAndEnd[1];
                modfiedPass.put("passId", pass.getId());
                modfiedPass.put("memberId", pass.getMember().getId());
                modfiedPass.put("teacherId", pass.getTeacher().getId());
                modfiedPass.put("placeId", pass.getPlace().getId());
                modfiedPass.put("passReason", pass.getPassReason());
                modfiedPass.put("startPeriod", passStart.format(formatter));
                modfiedPass.put("endPeriod", passEnd.format(formatter));
                modfiedPass.put("passStatus", pass.getPassStatus());
                modfiedPass.put("createdAt", pass.getCreatedAt().format(formatter));
                if (pass.getUpdatedAt() != null) {
                    modfiedPass.put("updatedAt", pass.getUpdatedAt());
                }
                modifiedPassList.add(modfiedPass);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "출입증 검색 도중 에러가 발생했습니다. 원인 : "+e.getMessage());
        }
        return new ResponseEntity<>(modifiedPassList, HttpStatus.OK);
    }

//    @GetMapping("/teacherList")
//    public ResponseEntity<List<Pass>> findAllPassStatusEqualsRequested() {
//        try {
//            List<Pass> result = passService.findAllPassStatusEqualsRequested();
//            return new ResponseEntity<>(result, HttpStatus.OK);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
//        }
//    }

    //5분마다 여기로 요청을 보내면 userId
    @PostMapping("/check")
    public void checkPassesValid() {
        passService.checkPass();
    }

    @GetMapping("/studentList")
    public ResponseEntity<List<Map<String, Object>>> findAllPassStatusEqualsRequested() {
        List<Pass> passes = passService.getStudentList();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Pass pass : passes) {
            Map<String, Object> passMap = new HashMap<>();

            // Member 정보
            Member member = pass.getMember();
            passMap.put("id", pass.getId());
            passMap.put("memberId", member.getId());
            passMap.put("grade", member.getGrade());
            passMap.put("classes", member.getClasses());
            passMap.put("number", member.getNumber());
            passMap.put("studentName", member.getName());
            passMap.put("studentSerialNumber", member.getSerialNumber());
            passMap.put("account", member.getAccount());

            // Student Role
            List<String> studentRoles = member.getAuthorities()
                    .stream()
                    .map(authority -> authority.getName())
                    .collect(Collectors.toList());
            passMap.put("studentRole", studentRoles.get(0));

            // Teacher 정보
            Teacher teacher = pass.getTeacher();
            passMap.put("teacher", teacher.getId());
            passMap.put("TeacherSerialNumber", teacher.getSerialNum());
            passMap.put("teacherName", teacher.getName());
            passMap.put("teacherRole", teacher.getTpermission());

            // Place 정보
            Place place = pass.getPlace();
            passMap.put("place", place.getId());
            passMap.put("placeTeacherId", place.getTeacher().getId());
            passMap.put("ipAddress", place.getIpAddress());
            passMap.put("capacityNow", place.getCapacity());
            passMap.put("maxCapacity", place.getMaxCapacity());
            passMap.put("location", place.getLocation());
            passMap.put("floor", place.getFloor());
            passMap.put("locationDetail", place.getLocationDetail());

            // 나머지 필드
            passMap.put("passReason", pass.getPassReason());
            passMap.put("startPeriod", pass.getStartPeriod());
            passMap.put("endPeriod", pass.getEndPeriod());
            passMap.put("passStatus", pass.getPassStatus());
            passMap.put("serialNumber", pass.getSerialNumber());
            passMap.put("createdAt", pass.getCreatedAt());
            passMap.put("updatedAt", pass.getUpdatedAt());

            response.add(passMap);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getAllPass() throws Exception {
        try {
            List<Pass> passList = passService.getAllPass();
            List<Map<String, Object>> modifiedPassList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Pass pass : passList) {
                LocalDateTime[] passStartAndEnd = conversionPeriod(pass.getStartPeriod(), pass.getEndPeriod());
                LocalDateTime passStart = passStartAndEnd[0];
                LocalDateTime passEnd = passStartAndEnd[1];

                Map<String, Object> modifiedPass = new HashMap<>();
                modifiedPass.put("passId", pass.getId());
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
