package gbsw.plutter.project.PMS.dto;

import gbsw.plutter.project.PMS.model.roleEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String name;
    private Integer grade;
    private Integer classes;
    private Integer number;
    private String password;
    private roleEnum role;
    private String salt;
}
