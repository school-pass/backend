package gbsw.plutter.project.PMS.service.pass;

import gbsw.plutter.project.PMS.dto.PassDTO;
import gbsw.plutter.project.PMS.model.*;
import gbsw.plutter.project.PMS.repository.PassRepository;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PassService {
    private final PassRepository passRepository;
    private final PlaceRepository placeRepository;
    private final TeacherRepository teacherRepository;

    public Boolean addPass(PassDTO pd, Optional<Place> particular, Optional<Member> mId, Optional<Teacher> tId) throws Exception {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime result = now.plusMinutes(pd.getPassExpiration());
            Pass pass = Pass.builder()
                    .place(particular.get())
                    .member(mId.get())
                    .teacher(tId.get())
                    .uid(pd.getUID())
                    .passStart(LocalDateTime.now())
                    .validPass(result)
                    .passReason(pd.getPassReason())
                    .passStatus(PassStatus.valueOf("REQUESTED"))
                    .createdAt(LocalDateTime.now())
                    .build();
            passRepository.save(pass);
        } catch (Exception e) {
            throw new Exception("출입증 신청 도중 오류 발생");
        }
        return true;
    }

    public List<Pass> getAllPass() throws Exception {
        try {
            List<Pass> passlist;
            passlist = passRepository.findAll();
            if(passlist.isEmpty()) {
                throw new Exception("출입증이 존재하지 않습니다.");
            }
            return passlist;
        } catch (Exception e) {
            throw new Exception("알 수 없는 오류가 발생하였습니다. 다시 시도해주세요");
        }
    }

    public Boolean applyPass(PassDTO pd) throws Exception {
        try {
            Teacher teacher;
            Optional<Teacher> tId = teacherRepository.findTeacherByMember_Id(pd.getUserId());
            if (tId.isPresent()) {
                teacher = tId.get();
            } else {
                throw new Exception("Teacher not found with user id " + pd.getUserId());
            }
            if (pd.getConfirm().equals(1)) {
                Pass pass = passRepository.findById(pd.getPassId())
                        .orElseThrow(() -> new Exception("Pass not found with id " + pd.getPassId()));
                pass.setPassStatus(PassStatus.APPROVED);
                pass.setTeacher(teacher);
                passRepository.save(pass);
            } else {
                Pass pass = passRepository.findById(pd.getPassId())
                        .orElseThrow(() -> new Exception("Pass not found with id " + pd.getPassId()));
                pass.setPassStatus(PassStatus.REJECTED);
                pass.setTeacher(teacher);
                passRepository.save(pass);
            }
            return true;
        } catch (Exception e) {
            throw new Exception("DB에 값을 저장하는 도중 에러 발생");
        }
    }


    public Boolean validPass(PassDTO pd) throws Exception {
        try {
            Place place = placeRepository.findPlaceByIpAddress(pd.getPlaceIp());
            Pass pass = passRepository.findPassByUidAndPlace(pd.getUID(), place);
            if (pass != null) {
                if (pass.getPassStatus().equals(PassStatus.APPROVED)) {
                    return true;
                } else if (pass.getPassStatus().equals(PassStatus.REJECTED)) {
                    return false;
                }
            }
        } catch (Exception e) {
            throw new Exception("출입증 검증 도중 에러가 발생했습니다. 다시 시도해주십시오");
        }
        return false;
    }


    public Boolean updatePass(PassDTO pd) throws Exception {
        try {
            Optional<Place> ipAddr;
            ipAddr = placeRepository.findById(pd.getPassId());
            LocalDateTime now = LocalDateTime.now();
        } catch (Exception e) {
            throw new Exception("");
        }
        return true;
    }
    public Boolean deletePass(PassDTO pd) throws Exception {
        try {
            passRepository.deleteById(pd.getPassId());
        } catch (Exception e) {
            throw new Exception("");
        }
        return true;
    }
}
