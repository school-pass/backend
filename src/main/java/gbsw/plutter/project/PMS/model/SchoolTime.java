package gbsw.plutter.project.PMS.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchoolTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period")
    //1
    private Integer period;

    @Column(name = "startTime")
    //"08:40:00"
    private LocalTime startTime;

    //"09:30:00"
    @Column(name = "endTime")
    private LocalTime endTime;
}
