package gbsw.plutter.project.PMS.service.admin;

import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.dto.STDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.model.*;
import gbsw.plutter.project.PMS.repository.MemberRepository;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
import gbsw.plutter.project.PMS.repository.SchoolTimeRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final PlaceRepository placeRepository;
    private final TeacherRepository teacherRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolTimeRepository schoolRepository;

    //todo
    public boolean addUser(SignRequest request) {
        Optional<Member> isMember = memberRepository.findBySerialNumber(request.getSerialNum());
        if (isMember.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당하는 시리얼넘버를 가진 유저가 이미 존재합니다.\n serialNum : " + request.getSerialNum());
        }
        try {
            Member member = Member.builder()
                    .account(request.getAccount())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .serialNumber(request.getSerialNum())
                    .build();

            if (request.getPermission().equals(0)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));
            } else if (request.getPermission().equals(1)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_TEACHER").build()));
            } else if (request.getPermission().equals(2)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_STUDENT").build()));
            }

            memberRepository.save(member);
            Member mId = memberRepository.findMemberByAccount(request.getAccount());
            saveTeacherUser(mId.getId(), request.getPermission());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DB에 값을 저장하는 도중 에러 발생");
        }
        return true;
    }

    public List<SchoolTime> getAllTime() {
        List<SchoolTime> ls;
        try {
            ls = schoolRepository.findAll();
            if (ls.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "schoolTime isn't exist");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "시간표 조회 중 오류 발생");
        }
        return ls;
    }

    protected static LocalTime convertToLocalTime(String timeString, String pattern) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(timeString, formatter);
    }

    public Boolean addSchoolTime(STDTO stdto) {
        try {
            SchoolTime isTime = schoolRepository.findByPeriod(stdto.getPeriod());
            if (isTime != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflicted SchoolTime");
            }
            SchoolTime school = SchoolTime.builder()
                    .period(stdto.getPeriod())
                    .startTime(convertToLocalTime(stdto.getStartTime(), "HH:mm"))
                    .endTime(convertToLocalTime(stdto.getEndTime(), "HH:mm"))
                    .build();
            schoolRepository.save(school);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스에 값을 저장할 수 없습니다.");
        }
        return true;
    }

    public Member getMemberById(Long id) {
        Member resMember;
        Optional<Member> member;
        try {
            member = memberRepository.findById(id);
            if (member.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
            }
            resMember = member.get(); // Assigning the value from Optional<Member> to Member
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DB에서 값을 찾아오는 도중 에러가 발생하였습니다.");
        }
        return resMember;
    }

    public List<Member> findAllUsers() {
        List<Member> members;
        try {
            members = memberRepository.findAll();
            if (members.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user isn't exist");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can not get users");
        }
        return members;
    }

    //pass
    public boolean updateUser(MemberDTO md) {
        Optional<Member> isMember = memberRepository.findBySerialNumber(md.getSerialNum());
        if (isMember.isPresent() && !isMember.get().getId().equals(md.getId())) {
            return false;
        }
        Member member = memberRepository.findMemberById(md.getId());
        try {
            member.setName(md.getName());
            member.setSerialNumber(md.getSerialNum());
            List<Authority> roles = new ArrayList<>();
            if (md.getPermission() == 0) {
                roles.add(Authority.builder().name("ROLE_ADMIN").build());
            } else if (md.getPermission() == 1) {
                roles.add(Authority.builder().name("ROLE_TEACHER").build());
            } else if (md.getPermission() == 2) {
                roles.add(Authority.builder().name("ROLE_STUDENT").build());
            }
            member.setRoles(roles);

            memberRepository.save(member);
        } catch (DataAccessException e) {
            return false;
        }
        if (md.getPermission() < 2) {
            boolean saveTeacherUserResult = saveTeacherUser(member.getId(), md.getPermission());

            if (!saveTeacherUserResult) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        }
        return true;
    }

    public Boolean editSchoolTime(STDTO stdto) {
        SchoolTime isPeriod = schoolRepository.findByPeriod(stdto.getPeriod());

        if (isPeriod != null) {
            LocalTime startTime = parseLocalTime(stdto.getStartTime());
            LocalTime endTime = parseLocalTime(stdto.getEndTime());

            if (startTime != null && endTime != null) {
                SchoolTime conflictingPeriod = schoolRepository.findByStartTimeAndEndTime(
                        startTime,
                        endTime
                );

                if (conflictingPeriod != null && !conflictingPeriod.getPeriod().equals(stdto.getPeriod())) {
                    return false;
                } else {
                    try {
                        isPeriod.setStartTime(startTime);
                        isPeriod.setEndTime(endTime);

                        schoolRepository.save(isPeriod);
                        return true;
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DB에 값을 저장하는 도중 에러 발생");
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected LocalTime parseLocalTime(String timeString) {
        try {
            return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public Boolean deleteSchoolTime(STDTO stdto)     {
        try {
            Optional<SchoolTime> isTime = schoolRepository.findById(stdto.getId());
            if(isTime.isEmpty()) {
                return false;
            }
            schoolRepository.deleteById(isTime.get().getId());
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR ,"DB에서 값을 삭제하는 도중 에러 발생");
        }
    }
    public Boolean deleteUser(MemberDTO md) throws Exception {
        try{
            memberRepository.deleteById(md.getId());
        } catch (Exception e) {
            throw new Exception("500 Server Error"); 
        }
        return true;
    }
    public Boolean addPlace(PlaceDTO placeDTO, Optional<Teacher> tId) throws Exception {
        try {
        Place place = Place.builder()
                    .teacher(tId.get())
                    .floor(placeDTO.getFloor())
                    .ipAddress(placeDTO.getIpAddress())
                    .capacity(placeDTO.getCapacity())
                    .maxCapacity(placeDTO.getMaxCapacity())
                    .location(placeDTO.getLocation())
                    .locationDetail(placeDTO.getDetail())
                    .build();
        placeRepository.save(place);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return true;
    }
    public boolean saveTeacherUser(Long id, Integer permission) {
        String T_Permission = null;
        if(permission.equals(0)) {
            T_Permission = "MAINTEACHER";
        } else if(permission.equals(1)) {
            T_Permission = "SUBTEACHER";
        }
        Optional<Member> isMember = memberRepository.findById(id);
        if(isMember.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        try {
            Teacher teacher = Teacher.builder()
                    .member(isMember.get())
                    .name(isMember.get().getName())
                    .serialNum(isMember.get().getSerialNumber())
                    .tpermission(Tpermission.valueOf(T_Permission))
                    .build();
            teacherRepository.save(teacher);
        } catch (Exception e){
            return false;
        }
        return true;
    }
}
