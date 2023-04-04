package gbsw.plutter.project.PMS.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResultDto {
    private boolean result;
    private String msg;
    private Object obj;

    public ResultDto(boolean result, String msg, Object obj) {
        this.result = result;
        this.msg = msg;
        this.obj = obj;
    }
}
