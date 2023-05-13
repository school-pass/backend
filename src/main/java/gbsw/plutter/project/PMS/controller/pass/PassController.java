package gbsw.plutter.project.PMS.controller.pass;


import gbsw.plutter.project.PMS.dto.PassDTO;
import gbsw.plutter.project.PMS.model.Member;
import gbsw.plutter.project.PMS.model.Pass;
import gbsw.plutter.project.PMS.model.Place;
import gbsw.plutter.project.PMS.model.Teacher;
import gbsw.plutter.project.PMS.repository.MemberRepository;
import gbsw.plutter.project.PMS.repository.PassRepository;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import gbsw.plutter.project.PMS.service.pass.PassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PostMapping("use")
    public ResponseEntity<Boolean> validPass(@RequestBody PassDTO pd) throws Exception {
        try {
            return new ResponseEntity<>(passService.validPass(pd), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("출입증 검증 도중 에러가 발생했습니다. 원인 : "+e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Pass>> getAllPass() throws Exception {
        try {
            return new ResponseEntity<>(passService.getAllPass(), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception("출입증 전체 조회 중 에러가 발생했습니다. 원인 : " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Boolean> updatePass(@RequestBody PassDTO pd) throws Exception {
        try {
            Optional<Pass> pId;
            pId = passRepository.findById(pd.getPassId());
            if(pId.isEmpty()) {
                throw new Exception("ID : "+pd.getPassId()+"를 가진 출입증이 존재하지 않습니다.");
            }
            return new ResponseEntity<>(passService.updatePass(pd), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception();
        }
    }

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
