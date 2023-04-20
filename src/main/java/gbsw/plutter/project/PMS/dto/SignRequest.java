package gbsw.plutter.project.PMS.dto;

import lombok.*;

@Getter
@Setter
public class SignRequest {
        private String name;
        private String serialNum;
        private String account;
        private String password;
        private Integer permission;
}
