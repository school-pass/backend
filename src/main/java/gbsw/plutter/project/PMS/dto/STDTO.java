package gbsw.plutter.project.PMS.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.Period;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class STDTO {
    private Long id;
    private Integer Period;
    private String startTime;
    private String endTime;
}
