package gbsw.plutter.project.PMS.service;

import gbsw.plutter.project.PMS.config.JwtProvider;
import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.model.Authority;
import gbsw.plutter.project.PMS.model.Member;
import gbsw.plutter.project.PMS.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class SignService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public MemberDTO login(SignRequest request) {
        Member member = memberRepository.findByAccount(request.getAccount()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }
        try {
            return MemberDTO.builder()
                    .id(member.getId())
                    .account(member.getAccount())
                    .name(member.getName())
                    .serialNum(member.getSerialNumber())
                    .roles(Collections.singletonList((Authority) member.getAuthorities()))
                    .token(jwtProvider.createToken(member.getId().toString(), member.getAccount(), member.getAuthorities()))
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "로그인에 실패했습니다.");
        }
    }
}
