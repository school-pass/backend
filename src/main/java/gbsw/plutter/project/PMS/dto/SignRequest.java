package gbsw.plutter.project.PMS.dto;

import lombok.*;

@Getter
@Setter
public class SignRequest {
        private String name;
        private Integer grade;
        private Integer classes;
        private Integer number;
        private String account;
        private String password;
        private Integer permission;
}
