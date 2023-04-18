package gbsw.plutter.project.PMS.dto;

import gbsw.plutter.project.PMS.model.Authority;
import gbsw.plutter.project.PMS.model.Member;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private Long id;
    private String name;
    private Integer grade;
    private Integer classes;
    private Integer number;
    private String account;
    private String password;
    private List<Authority> roles = new ArrayList<>();
    private String token;

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.account = member.getAccount();
        this.name = member.getName();
        this.grade = member.getGrade();
        this.classes = member.getClasses();
        this.number = member.getNumber();
        this.password = member.getPassword();
        this.roles = member.getRoles();
    }
}
