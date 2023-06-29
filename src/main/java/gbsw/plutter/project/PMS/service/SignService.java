package gbsw.plutter.project.PMS.service;

import gbsw.plutter.project.PMS.config.JwtProvider;
import gbsw.plutter.project.PMS.dto.MemberDTO;
import gbsw.plutter.project.PMS.dto.SignRequest;
import gbsw.plutter.project.PMS.dto.SignResponse;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SignService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public SignResponse login(SignRequest request) {
        Member member = memberRepository.findByAccount(request.getAccount()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }
        try {
            List<Authority> authorities = new ArrayList<>(member.getAuthorities());
            String token = jwtProvider.createToken(member.getId(), member.getName(), member.getSerialNumber(), member.getAccount(), authorities);
            return SignResponse.builder()
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "로그인에 실패했습니다.");
        }
    }

}
