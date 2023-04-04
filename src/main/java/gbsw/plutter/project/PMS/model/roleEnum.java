package gbsw.plutter.project.PMS.model;

import lombok.Getter;

@Getter
public enum roleEnum {
    ROLE_ADMIN("관리자"), ROLE_TEACHER("교사"), ROLE_STUDENT("학생");

    private String description;

    roleEnum(String description) {
        this.description = description;
    }
}
