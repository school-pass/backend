package gbsw.plutter.project.PMS.service;

import gbsw.plutter.project.PMS.config.JwtProvider;
import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.model.Authority;
import gbsw.plutter.project.PMS.model.Member;
import gbsw.plutter.project.PMS.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class SignService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtProvider jwtProvider;

    public MemberDTO login(SignRequest request) throws Exception {
            Member member = memberRepository.findByAccount(request.getAccount()).orElseThrow(() ->
                    new BadCredentialsException("잘못된 계정정보입니다."));

            if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
                throw new BadCredentialsException("잘못된 계정정보입니다.");
            }

            return MemberDTO.builder()
                    .id(member.getId())
                    .account(member.getAccount())
                    .name(member.getName())
                    .grade(member.getGrade())
                    .classes(member.getClasses())
                    .number(member.getNumber())
                    .roles(member.getRoles())
                    .token(jwtProvider.createToken(member.getAccount(), member.getRoles()))
                    .build();
    }
    public boolean register(SignRequest request) throws Exception {
        try {
            Member member = Member.builder()
                    .account(request.getAccount())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .grade(request.getGrade())
                    .classes(request.getClasses())
                    .number(request.getNumber())
                    .build();

            if (request.getPermission().equals(0)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));
            } else if (request.getPermission().equals(1)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_TEACHER").build()));
            } else if (request.getPermission().equals(2)) {
                member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_STUDENT").build()));
            }

            memberRepository.save(member);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return true;
    }

}
