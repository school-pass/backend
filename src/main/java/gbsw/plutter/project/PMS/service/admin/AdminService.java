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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public boolean addUser(SignRequest request) throws Exception {
        try {
            Member member = Member.builder()
                    .account(request.getAccount())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .serialNumber(request.getSerialNum())
                    .build();

            if (request.getPermission() == 0) {
                member.setRoles(new ArrayList<>(Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build())));
                memberRepository.save(member);
                Optional<Member> mId = memberRepository.findByAccount(request.getAccount());
                saveTeacherUser(mId, "MAINTEACHER");
            } else if (request.getPermission() == 1) {
                member.setRoles(new ArrayList<>(Collections.singletonList(Authority.builder().name("ROLE_TEACHER").build())));
                memberRepository.save(member);
                Optional<Member> mId = memberRepository.findByAccount(request.getAccount());
                saveTeacherUser(mId, "SUBTEACHER");
            } else if (request.getPermission() == 2) {
                member.setRoles(new ArrayList<>(Collections.singletonList(Authority.builder().name("ROLE_STUDENT").build())));
            }
            memberRepository.save(member);
        } catch (Exception e) {
            throw new Exception("사용자 추가 중 에러 발생: " + e.getMessage());
        }
        return true;
    }

    public List<SchoolTime> getAllTime() throws Exception {
        List<SchoolTime> ls;
        try {
            ls = schoolRepository.findAll();
            if(ls.isEmpty()) {
                throw new Exception("schoolTime isn't exist");
            }
        } catch (Exception e) {
            throw new Exception("시간표 조회 중 오류 발생");
        }
        return ls;
    }

    public static LocalTime convertToLocalTime(String timeString, String pattern) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(timeString, formatter);
    }
    public Boolean addSchoolTime(STDTO stdto) throws Exception {
        try {
            SchoolTime isTime = schoolRepository.findByPeriod(stdto.getPeriod());
            if(isTime != null) {
                throw new Exception("이미 해당하는 교시의 시작과 종료 시간이 있습니다.");
            }
            SchoolTime school = SchoolTime.builder()
                    .period(stdto.getPeriod())
                    .startTime(convertToLocalTime(stdto.getStartTime(), "HH:mm"))
                    .endTime(convertToLocalTime(stdto.getEndTime(), "HH:mm"))
                    .build();
            schoolRepository.save(school);
        } catch (Exception e) {
            throw new Exception("데이터베이스에 값을 저장할 수 없습니다.");
        }
        return true;
    }
    public Optional<Member> getMemberById(Long id) throws Exception {
        Optional<Member> member;
        try {
             member = memberRepository.findById(id);
        } catch (Exception e) {
            throw new Exception("Can't get user by Id");
        }
        return member;
    }
    public List<Member> getUserList() throws Exception {
        List<Member> members;
        try {
            members = memberRepository.findAll();
            if(members.isEmpty()) {
                throw new Exception("user isn't exist");
            }
        } catch (Exception e) {
            throw new Exception("Can not get users");
        }
        return members;
    }
    public Boolean editSchoolTime(STDTO stdto) throws Exception {
        try {
            SchoolTime isPeriod;
            isPeriod = schoolRepository.findByPeriod(stdto.getPeriod());
            if (isPeriod != null) {
                isPeriod.setStartTime(convertToLocalTime(stdto.getStartTime(), "HH:mm"));
                isPeriod.setEndTime(convertToLocalTime(stdto.getEndTime(), "HH:mm"));

                schoolRepository.save(isPeriod);
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new Exception("404 Bad Request");
        }
    }

    public Boolean deleteSchoolTime(STDTO stdto) throws Exception {
        try {
            Optional<SchoolTime> isTime = schoolRepository.findById(stdto.getId());
            if(isTime.isEmpty()) {
                return false;
            }
            schoolRepository.deleteById(isTime.get().getId());
            return true;
        } catch (Exception e) {
            throw new Exception("404 Bad Request");
        }
    }

    public Boolean editUser(MemberDTO md) throws Exception {
        try {
            Optional<Member> isMember = memberRepository.findByAccount(md.getAccount());
            if(isMember.isPresent()) {
                throw new Exception("Found Same SerialNum : " + md.getSerialNum());
            }
            Member member = Member.builder()
                    .name(md.getName())
                    .serialNumber(md.getSerialNum())
                    .build();
            if (md.getPermission().equals(0)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));
                memberRepository.save(member);
                Optional<Member> mId = memberRepository.findByAccount(md.getAccount());
                saveTeacherUser(mId, "MAINTEACHER");
            } else if (md.getPermission().equals(1)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_TEACHER").build()));
                memberRepository.save(member);
                Optional<Member> mId = memberRepository.findByAccount(md.getAccount());
                saveTeacherUser(mId, "SUBTEACHER");
            } else if (md.getPermission().equals(2)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_STUDENT").build()));
            }
        } catch (Exception e) {
            throw new Exception("404 Bad Request");
        }
        return true;
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
    public void saveTeacherUser(Optional<Member> mId, String permission) {
        try {
            Teacher teacher = Teacher.builder()
                    .member(mId.get())
                    .name(mId.get().getName())
                    .serialNum(mId.get().getSerialNumber())
                    .tpermission(Tpermission.valueOf(permission))
                    .build();
            teacherRepository.save(teacher);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
