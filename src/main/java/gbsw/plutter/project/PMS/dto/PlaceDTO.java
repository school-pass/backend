package gbsw.plutter.project.PMS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceDTO {
    private Integer teacherId;
    private Integer capacity;
    private Integer maxCapacity;
    private Integer floor;
    private String location;
    private String particular;
}
