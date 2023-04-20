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
    private String serialNum;
    private String account;
    private String password;
    private List<Authority> roles = new ArrayList<>();
    private String token;
}
