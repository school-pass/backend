package gbsw.plutter.project.PMS.config;

import gbsw.plutter.project.PMS.model.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@Getter
@Setter
@Slf4j
public class SecurityUser extends User {
    private Member member;

    public SecurityUser(Member member) {
        super(member.getName(), member.getPassword(), AuthorityUtils.createAuthorityList(member.getRole().toString()));
        this.member = member;
    }
}
