package gbsw.plutter.project.PMS.service.admin;

import gbsw.plutter.project.PMS.config.JwtProvider;
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
            Member member = Member.builder()
                    .account(request.getAccount())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .serialNum(request.getSerialNum())
                    .build();

            if (request.getPermission().equals(0)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));
                memberRepository.save(member);
                Optional<Member> mId = memberRepository.findByAccount(request.getAccount());
                saveTeacherUser(mId, "MAINTEACHER");
                return true;
            } else if (request.getPermission().equals(1)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_TEACHER").build()));
                memberRepository.save(member);
                Optional<Member> mId = memberRepository.findByAccount(request.getAccount());
                saveTeacherUser(mId, "SUBTEACHER");
                return true;
            } else if (request.getPermission().equals(2)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_STUDENT").build()));
            }
            memberRepository.save(member);
        return true;
    }
    public Place addPlace(PlaceDTO placeDTO, Optional<Teacher> tId) throws Exception {
        try {
        Place place = Place.builder()
                    .teacher(tId.get())
                    .floor(placeDTO.getFloor())
                    .capacity(placeDTO.getCapacity())
                    .maxCapacity(placeDTO.getMaxCapacity())
                    .location(placeDTO.getLocation())
                    .particular(placeDTO.getParticular())
                    .build();
        placeRepository.save(place);
        return place;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }
    public boolean saveTeacherUser(Optional<Member> mId, String permission) {
        try {
            Teacher teacher = Teacher.builder()
                    .member(mId.get())
                    .name(mId.get().getName())
                    .tpermission(Tpermission.valueOf(permission))
                    .build();
            teacherRepository.save(teacher);
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
