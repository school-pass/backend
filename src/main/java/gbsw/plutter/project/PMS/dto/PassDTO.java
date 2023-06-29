package gbsw.plutter.project.PMS.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gbsw.plutter.project.PMS.model.PassStatus;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassDTO {
    private Long passId;
    private Long userId;
    private Long teacherId;
    private String detail;
    private String passReason;
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer confirm;
    private String placeIp;
    private PassStatus passStatus;
    private String passPlace;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
