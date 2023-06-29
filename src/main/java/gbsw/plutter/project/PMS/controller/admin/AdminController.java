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
import gbsw.plutter.project.PMS.repository.SchoolTimeRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import gbsw.plutter.project.PMS.service.admin.AdminService;
import gbsw.plutter.project.PMS.service.place.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final SchoolTimeRepository schoolTimeRepository;
    private final PlaceService placeService;
    private final TeacherRepository teacherRepository;

    @PostMapping("/addUser")
    public ResponseEntity<Map<String, List<String>>> addUser(@RequestBody List<MemberDTO> reqs) {
        List<String> successAccount = new ArrayList<>();
        List<String> failedAccount = new ArrayList<>();

        for(MemberDTO req : reqs ){
            Optional<Member> isUser = memberRepository.findBySerialNumber(req.getSerialNum());
            if(isUser.isEmpty()) {
                boolean isSuccess = adminService.addUser(req);
                if(isSuccess) {
                    successAccount.add(req.getAccount());
                } else {
                    failedAccount.add(req.getAccount());
                }
            } else {
                failedAccount.add(req.getAccount());
            }
        }
        try {
            Map<String, List<String>> response = new HashMap<>();
            if(!successAccount.isEmpty()) {
                response.put("successAccounts", successAccount);
            }
            if(!failedAccount.isEmpty()) {
                response.put("failedAccounts", failedAccount);
            }
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자를 추가하지 못 했습니다.");
        }
    }

    @PostMapping("/addTime")
    public ResponseEntity<Map<String, List<Integer>>> addTime(@RequestBody List<STDTO> stdtos) {
        List<Integer> successIds = new ArrayList<>();
        List<Integer> failedIds = new ArrayList<>();
        try {
            for(STDTO stdto : stdtos) {
                boolean isSuccess = adminService.addSchoolTime(stdto);
                if(isSuccess) {
                    successIds.add(stdto.getPeriod());
                } else {
                    failedIds.add(stdto.getPeriod());
                }
            }
            Map<String, List<Integer>> res = new HashMap<>();
            if(!successIds.isEmpty()) {
                res.put("successIds", successIds);
            }
            if(!failedIds.isEmpty()) {
                res.put("failedIds", failedIds);
            }
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시간표 추가에 실패했습니다.");
        }
    }

    @PutMapping("/editTime")
    public ResponseEntity<Map<String, List<Integer>>> editTime(@RequestBody List<STDTO> stdtos) {
        List<Integer> successIds = new ArrayList<>();
        List<Integer> failedIds = new ArrayList<>();

        for (STDTO stdto : stdtos) {
            Optional<SchoolTime> isTime = Optional.ofNullable(schoolTimeRepository.findByPeriod(stdto.getPeriod()));
            if (isTime.isPresent()) {
                boolean isSuccess = adminService.editSchoolTime(stdto);
                if (isSuccess) {
                    successIds.add(stdto.getPeriod());
                } else {
                    failedIds.add(stdto.getPeriod());
                }
            } else {
                failedIds.add(stdto.getPeriod());
            }
        }

        Map<String, List<Integer>> response = new HashMap<>();
        if (!successIds.isEmpty()) {
            response.put("successPeriods", successIds);
        }
        if (!failedIds.isEmpty()) {
            response.put("failedPeriods", failedIds);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/deleteTime")
    public ResponseEntity<Map<String, List<Long>>> deleteTime(@RequestBody List<STDTO> stdtoList) {
        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        try {
            for (STDTO stdto : stdtoList) {
                boolean isSuccess = adminService.deleteSchoolTime(stdto);
                if (isSuccess) {
                    successIds.add(stdto.getId());
                } else {
                    failedIds.add(stdto.getId());
                }
            }
            Map<String, List<Long>> response = new HashMap<>();
            if(!successIds.isEmpty()) {
                response.put("successIds", successIds);
            }
            if(!failedIds.isEmpty()) {
                response.put("failedIds", failedIds);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to delete school time");
        }
    }

    //pass
    @PutMapping("/editUser")
    public ResponseEntity<Map<String, List<Long>>> editUser(@RequestBody List<MemberDTO> memberDTOs) {
        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        for (MemberDTO md : memberDTOs) {
            Optional<Member> isUser = memberRepository.findById(md.getId());
            if (isUser.isPresent()) {
                boolean isSuccess = adminService.updateUser(md);
                if (isSuccess) {
                    successIds.add(md.getId());
                } else {
                    failedIds.add(md.getId());
                }
            } else {
                failedIds.add(md.getId());
            }
        }
        try {
            Map<String, List<Long>> response = new HashMap<>();
            if(!successIds.isEmpty()) {
                response.put("successIds", successIds);
            }
            if(!failedIds.isEmpty()) {
                response.put("failedIds", failedIds);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to edit user");
        }
    }

    //pass
    @DeleteMapping("/deleteUser")
    public ResponseEntity<Boolean> deleteUser(@RequestBody MemberDTO md) {
        return new ResponseEntity<>(adminService.deleteUser(md), HttpStatus.OK);
    }

    @PostMapping("/addPlace")
    public ResponseEntity<Boolean> addPlace(@RequestBody PlaceDTO req, HttpServletRequest httpReq) {
            Optional<Teacher> tId;
            if (req.getTeacherId() != null) {
                tId = teacherRepository.findTeacherByMember_Id(Long.parseLong(req.getTeacherId().toString()));
                if (tId.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 Id를 가진 선생님을 찾을 수 없습니다.");
                }
            } else {
                String token = jwtProvider.resolveToken(httpReq);
                Long teacherId = Long.valueOf(jwtProvider.getUserId(token.replace("Bearer", "")));
                Optional<Member> acc = memberRepository.findById(teacherId);
                tId = teacherRepository.findTeacherByMember_Id(acc.get().getId());
            }
        return new ResponseEntity<>(adminService.addPlace(req, tId), HttpStatus.CREATED);
    }

    @PutMapping("/editPlace")
    public ResponseEntity<Boolean> ediBPlace(@RequestBody PlaceDTO pd) {
        return new ResponseEntity<>(placeService.editPlace(pd), HttpStatus.OK);
    }

    @DeleteMapping("/deletePlace")
    public ResponseEntity<Map<String, List<Long>>> deletePlace(@RequestBody List<PlaceDTO> pds) {
        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        try {
            for(PlaceDTO pd : pds) {
                boolean isSuccess = placeService.deletePlace(pd);
                if(isSuccess) {
                    successIds.add(pd.getId());
                } else {
                    failedIds.add(pd.getId());
                }
            }
            Map<String, List<Long>> res = new HashMap<>();
            if(!successIds.isEmpty()) {
                res.put("successIds", successIds);
            }
            if(!failedIds.isEmpty()) {
                res.put("failedIds", failedIds);
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"장소 삭제에 실패했습니다.");
        }
    }
}
