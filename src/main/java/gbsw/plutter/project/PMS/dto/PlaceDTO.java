package gbsw.plutter.project.PMS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceDTO {
    private Long id;
    private Long teacherId;
    private Integer capacity;
    private Integer maxCapacity;
    private Integer floor;
    private String ipAddress;
    private String location;
    private String detail;
}
