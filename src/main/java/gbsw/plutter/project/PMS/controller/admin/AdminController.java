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
    private final PlaceService placeService;
    private final TeacherRepository teacherRepository;

    // ...

    @PostMapping("/addUser")
    public ResponseEntity<Boolean> addUser(@RequestBody SignRequest req) {
        Optional<Member> serNum = memberRepository.findBySerialNumber(req.getSerialNum());
        if (serNum.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists for serialNum: " + req.getSerialNum());
        }
        try {
            return new ResponseEntity<>(adminService.addUser(req), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while adding user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add user");
        }
    }

    @PostMapping("/addTime")
    public ResponseEntity<Boolean> addTime(@RequestBody STDTO stdto) {
        try {
            return new ResponseEntity<>(adminService.addSchoolTime(stdto), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while adding school time", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add school time");
        }
    }

    @PutMapping("/editTime")
    public ResponseEntity<Boolean> editTime(@RequestBody STDTO stdto) {
        try {
            return new ResponseEntity<>(adminService.editSchoolTime(stdto), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while editing school time", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to edit school time");
        }
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
            log.error("Error occurred while deleting school time", e);
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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found For SerialNum: " + md.getSerialNum());
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
            log.error("Error occurred while editing user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to edit user");
        }
    }

    //pass
    @DeleteMapping("/deleteUser")
    public ResponseEntity<Boolean> deleteUser(@RequestBody MemberDTO md) {
        try {
            Optional<Member> serNum = memberRepository.findById(md.getId());
            if (serNum.isPresent()) {
                return new ResponseEntity<>(adminService.deleteUser(md), HttpStatus.OK);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found ID : " + md.getId());
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete user");
        }
    }

    @PostMapping("/addPlace")
    public ResponseEntity<Boolean> addPlace(@RequestBody PlaceDTO req, HttpServletRequest httpReq) {
        try {
            Optional<Teacher> tId;
            if (req.getTeacherId() != null) {
                try {
                    tId = teacherRepository.findTeacherByMember_Id(Long.parseLong(req.getTeacherId().toString()));
                } catch (NumberFormatException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid teacher ID format");
                }
                if (tId.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found");
                }
            } else {
                String token = jwtProvider.resolveToken(httpReq);
                String teacherId = jwtProvider.getUserId(token.replace("Bearer", ""));
                Optional<Member> acc = memberRepository.findByAccount(teacherId);
                tId = teacherRepository.findTeacherByMember_Id(acc.get().getId());
            }
            return new ResponseEntity<>(adminService.addPlace(req, tId), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while adding place", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add place");
        }
    }

    @PutMapping("/editPlace")
    public ResponseEntity<Boolean> ediBPlace(@RequestBody PlaceDTO pd) {
        try {
            return new ResponseEntity<>(placeService.editPlace(pd), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while editing place", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to edit place");
        }
    }

    @DeleteMapping("/deletePlace")
    public ResponseEntity<Boolean> deletePlace(@RequestBody PlaceDTO pd) {
        try {
            return new ResponseEntity<>(placeService.deletePlace(pd), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while deleting place", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete place");
        }
    }
}

