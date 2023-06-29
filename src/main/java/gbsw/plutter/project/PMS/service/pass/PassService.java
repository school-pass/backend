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


    public void checkPass() {
        LocalDate today = LocalDate.now();
        SchoolTime schoolTimeStart;
        SchoolTime schoolTimeEnd;
        LocalTime startTime;
        LocalTime endTime;
        LocalDateTime passStart;
        LocalDateTime passEnd;
        Place place = null;
        List<Pass> passList = passRepository.findAllByPassStatus(PassStatus.APPROVED);
        for(Pass pass : passList) {
            Optional<Place> places = placeRepository.findPlaceByLocationAndLocationDetail(pass.getPlace().getLocation(), pass.getPlace().getLocationDetail());
            place = places.get();
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
            if(!b) {
                place.setCapacity(place.getCapacity() - 1);
                pass.setPassStatus(PassStatus.EXPIRED);
                placeRepository.save(place);
                passRepository.save(pass);
            }
        }
    }

    public List<Pass> getStudentList() {
        List<Pass> passes = passRepository.findAllByPassStatus(PassStatus.REQUESTED);
        if(passes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "승인 대기중인 출입증이 없습니다.");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();

        List<Pass> expiredPasses = new ArrayList<>();
        try {
            for (Pass pass : passes) {
                LocalDateTime passUpdatedAt = pass.getUpdatedAt();
                if (passUpdatedAt != null) {
                    SchoolTime schoolTime = schoolTimeRepository.findByPeriod(pass.getEndPeriod());

                    if (schoolTime != null) {
                        LocalDateTime endTime = LocalDateTime.of(passUpdatedAt.toLocalDate(), schoolTime.getEndTime());
                        if (currentDateTime.isAfter(endTime)) {
                            pass.setPassStatus(PassStatus.EXPIRED);
                            expiredPasses.add(pass);
                        }
                    }
                }
            }
            if (!expiredPasses.isEmpty()) {
                passRepository.saveAll(expiredPasses);
            }
        }catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "출입증 조회 도중 오류 발생");
        }
        return passes;
    }

    public Boolean addPass(PassDTO pd) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        LocalDateTime limitTime;

        Optional<Place> place = placeRepository.findByLocationDetail(pd.getDetail());
        if (place.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, pd.getDetail() + "을 가진 장소를 찾을 수 없습니다.");
        }

        Optional<Member> member = memberRepository.findById(pd.getUserId());
        if (member.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID : " + pd.getUserId() + "를 가진 유저를 찾을 수 없습니다.");
        }

        if (dayOfWeek == DayOfWeek.FRIDAY) {
            limitTime = now.withHour(16).withMinute(30).withSecond(0).withNano(0);
        } else {
            limitTime = now.withHour(20).withMinute(0).withSecond(0).withNano(0);
        }

        if (now.isAfter(limitTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 만료된 출입증입니다.");
        }

        // 같은 멤버와 같은 장소에 이미 REQUESTED 또는 APPROVED인 데이터가 있는지 확인
        List<PassStatus> allowedStatuses = Arrays.asList(PassStatus.REQUESTED, PassStatus.APPROVED);
        List<Pass> existingPasses = passRepository.findByMemberAndPlaceAndPassStatusIn(member.get(), place.get(), allowedStatuses);
        if (!existingPasses.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 해당 장소에 신청/승인된 출입증이 존재합니다.");
        }

        try {
            Pass pass = Pass.builder()
                    .place(place.get())
                    .member(member.get())
                    .teacher(place.get().getTeacher())
                    .serialNumber(member.get().getSerialNumber())
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

    public Boolean applyPass(PassDTO pd) {
        Teacher teacher;
        Optional<Teacher> tId = teacherRepository.findTeacherByMember_Id(pd.getUserId());
        if (tId.isPresent()) {
            teacher = tId.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID : "+pd.getUserId()+"인 교사를 찾을 수 없습니다.");
        }
        Optional<Pass> passes = passRepository.findById(pd.getPassId());
        Pass pass = passes.get();
        if (pd.getConfirm().equals(1)) {
            pass.setPassStatus(PassStatus.APPROVED);
        } else {
            pass.setPassStatus(PassStatus.REJECTED);
        }

        pass.setTeacher(teacher);
        passRepository.save(pass);
        return true;
    }

    public List<Pass> getAllPass() {
        List<Pass> passlist;
        passlist = passRepository.findAll();
        if(passlist.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "출입증이 존재하지 않습니다.");
        }
        try {
            return passlist;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생하였습니다. 다시 시도해주세요");
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

//    public List<Pass> findAllPassStatusEqualsRequested() {
//        List<Pass> passes = passRepository.findAll();
//        List<Pass> filteredPasses = new ArrayList<>();
//        LocalDateTime currentDateTime = LocalDateTime.now();
//
//        for (Pass pass : passes) {
//            SchoolTime scTime = schoolTimeRepository.findByPeriod(pass.getEndPeriod());
//            if (pass.getPassStatus().equals(PassStatus.REQUESTED) && scTime.getEndTime().isAfter(LocalTime.from(currentDateTime))) {
//                filteredPasses.add(pass);
//            }
//        }
//
//        return filteredPasses;
//    }


    public Boolean validPass(PassDTO pd) throws Exception {
        try {
            Place place = placeRepository.findPlaceByIpAddress(pd.getPlaceIp());
            Member member = memberRepository.findMemberById(pd.getUserId());
            Pass pass = passRepository.findPassByMember_SerialNumberAndPlace(member.getSerialNumber(), place);
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
            if(b) {
                place.setCapacity(place.getCapacity()+1);
                placeRepository.save(place);
                return true;
            } else {
                pass.setPassStatus(PassStatus.EXPIRED);
                passRepository.save(pass);
                return false;
            }
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
