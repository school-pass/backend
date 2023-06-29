package gbsw.plutter.project.PMS.service.pass;

import gbsw.plutter.project.PMS.dto.PassDTO;
import gbsw.plutter.project.PMS.model.*;
import gbsw.plutter.project.PMS.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
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
    private final MemberRepository memberRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolTimeRepository schoolTimeRepository;

    public Boolean addPass(PassDTO pd, Optional<Place> particular, Optional<Member> mId, Optional<Teacher> tId) {
            LocalDateTime now = LocalDateTime.now();
            DayOfWeek dayOfWeek = now.getDayOfWeek();
            LocalDateTime limitTime;

            if (dayOfWeek == DayOfWeek.FRIDAY) {
                limitTime = now.withHour(16).withMinute(30).withSecond(0).withNano(0);
            } else {
                limitTime = now.withHour(20).withMinute(0).withSecond(0).withNano(0);
            }

            if (now.isAfter(limitTime)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 만료된 출입증입니다.");
            }
        try {
            Pass pass = Pass.builder()
                    .place(particular.get())
                    .member(mId.get())
                    .teacher(tId.get())
                    .passReason(pd.getPassReason())
                    .startPeriod(pd.getStartPeriod())
                    .endPeriod(pd.getEndPeriod())
                    .passStatus(PassStatus.REQUESTED)
                    .createdAt(now)
                    .build();
            passRepository.save(pass);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "출입증 신청 도중 오류 발생");
        }
        return true;
    }
    public static boolean isBetween(LocalDateTime now, LocalDateTime a, LocalDateTime b) {
        return !now.isBefore(a) && !now.isAfter(b);
    }

    public Boolean applyPass(PassDTO pd) throws Exception {
        try {
            Teacher teacher;
            Optional<Teacher> tId = teacherRepository.findTeacherByMember_Id(pd.getUserId());
            if (tId.isPresent()) {
                teacher = tId.get();
            } else {
                throw new Exception("해당 사용자 ID로 선생님을 찾을 수 없습니다: " + pd.getUserId());
            }
            Pass pass = passRepository.findById(pd.getPassId())
                    .orElseThrow(() -> new Exception("해당 ID로 패스를 찾을 수 없습니다: " + pd.getPassId()));
            if (pd.getConfirm().equals(1)) {
                pass.setPassStatus(PassStatus.APPROVED);
            } else {
                pass.setPassStatus(PassStatus.REJECTED);
            }

            pass.setTeacher(teacher);
            passRepository.save(pass);
            return true;
        } catch (Exception e) {
            throw new Exception("데이터베이스에 값을 저장하는 중에 오류가 발생했습니다");
        }
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

    public List<Pass> findByUserId(PassDTO pd) {
        List<Pass> passes = passRepository.findAll();
        List<Pass> filteredPasses = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();
        Member member = memberRepository.findMemberById(pd.getUserId());
        if(member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 ID를 가진 유저를 찾지 못 했습니다.");
        }
        Pass isPass = passRepository.findPassByMember(member);
        if (isPass == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 IMEI를 가진 출입증을 찾지 못 했습니다.");
        }
        try {
            for (Pass pass : passes) {
                SchoolTime scTime = schoolTimeRepository.findByPeriod(pass.getEndPeriod());
                if (pass.getPassStatus().equals(PassStatus.REQUESTED) && scTime.getEndTime().isAfter(LocalTime.from(currentDateTime))) {
                    filteredPasses.add(pass);
                }
            }

            return filteredPasses;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생하였습니다. 다시 시도해주세요");
        }
    }

    public List<Pass> findAllPassStatusEqualsRequested() {
        List<Pass> passes = passRepository.findAll();
        List<Pass> filteredPasses = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (Pass pass : passes) {
            SchoolTime scTime = schoolTimeRepository.findByPeriod(pass.getEndPeriod());
            if (pass.getPassStatus().equals(PassStatus.REQUESTED) && scTime.getEndTime().isAfter(LocalTime.from(currentDateTime))) {
                filteredPasses.add(pass);
            }
        }

        return filteredPasses;
    }


    public Boolean validPass(PassDTO pd) throws Exception {
        try {
            Place place = placeRepository.findPlaceByIpAddress(pd.getPlaceIp());
            Pass pass = passRepository.findPassByIMEIAndPlace(pd.getIMEI(), place);
            LocalDate today = LocalDate.now();
            SchoolTime schoolTimeStart;
            SchoolTime schoolTimeEnd;
            LocalTime startTime;
            LocalTime endTime;
            LocalDateTime passStart;
            LocalDateTime passEnd;
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
            boolean b = pass.getPassStatus().equals(PassStatus.APPROVED) && isBetween(LocalDateTime.now(), passStart, passEnd);
            return b;
        } catch (Exception e) {
            throw new Exception("출입증 검증 도중 에러가 발생했습니다. 다시 시도해주십시오");
        }
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
