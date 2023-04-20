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
        try {
            Member member = memberRepository.findByAccount(request.getAccount()).orElseThrow(() ->
                    new BadCredentialsException("잘못된 계정정보입니다."));

            if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
                throw new BadCredentialsException("잘못된 계정정보입니다.");
            }

            return MemberDTO.builder()
                    .id(member.getId())
                    .account(member.getAccount())
                    .name(member.getName())
                    .serialNum(member.getSerialNum())
                    .roles(member.getRoles())
                    .token(jwtProvider.createToken(member.getId().toString(), member.getAccount(), member.getRoles()))
                    .build();
        } catch (Exception e) {
          e.printStackTrace();
          throw new Exception();
        }
    }

}
