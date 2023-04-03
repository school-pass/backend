package gbsw.plutter.project.PMS.dto;

import gbsw.plutter.project.PMS.model.PassStatus;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassDTO {
    private Long passId;
    private Long userId;
    private Long teacherId;
    private String passReason;
    private LocalDateTime passStart;
    private LocalDateTime passExpiration;
    private String passToken;
    private PassStatus passStatus;
    private String passPlace;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
