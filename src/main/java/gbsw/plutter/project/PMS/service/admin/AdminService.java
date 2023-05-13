package gbsw.plutter.project.PMS.service.admin;

import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.dto.PlaceDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.model.*;
import gbsw.plutter.project.PMS.repository.MemberRepository;
import gbsw.plutter.project.PMS.repository.PlaceRepository;
import gbsw.plutter.project.PMS.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    public boolean addUser(SignRequest request) throws Exception {
        try{
            Member member = Member.builder()
                    .account(request.getAccount())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .serialNumber(request.getSerialNum())
                    .build();

            if (request.getPermission().equals(0)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));
                memberRepository.save(member);
                Optional<Member> mId = memberRepository.findByAccount(request.getAccount());
                saveTeacherUser(mId, "MAINTEACHER");
            } else if (request.getPermission().equals(1)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_TEACHER").build()));
                memberRepository.save(member);
                Optional<Member> mId = memberRepository.findByAccount(request.getAccount());
                saveTeacherUser(mId, "SUBTEACHER");
            } else if (request.getPermission().equals(2)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_STUDENT").build()));
            }
            memberRepository.save(member);
        } catch (Exception e) {
            throw new Exception("사용자 추가 중 에러 발생 : "+e.getMessage());
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
    public Boolean editUser(MemberDTO md) throws Exception {
        try {
            Optional<Member> isMember = memberRepository.findBySerialNumber(md.getSerialNum());
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
                    .locationDetail(placeDTO.getLocationDetail())
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
