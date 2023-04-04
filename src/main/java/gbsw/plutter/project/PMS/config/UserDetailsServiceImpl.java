package gbsw.plutter.project.PMS.config;

import gbsw.plutter.project.PMS.model.Member;
import gbsw.plutter.project.PMS.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private HttpServletResponse res;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> op = memberRepository.findByUsername(username);
        if(!op.isPresent()){
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
        }
        String token = jwtService.createToken(username, 60 * 1000);

        res.setHeader(Constant.AUTH_HEADER, token);
        Member member = op.get();
        return new SecurityUser(member);
    }
}
