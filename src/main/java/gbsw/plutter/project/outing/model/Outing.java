package gbsw.plutter.project.outing.model;

import gbsw.plutter.project.outing.util.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Outing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer outingId;
    @Column(length = 255)
    private String outingReason;

    @Column(nullable = false)
    private LocalDateTime outingStart;

    @Column(nullable = false)
    private LocalDateTime outingEnd;

    @Convert(converter = BooleanToYNConverter.class)
    private boolean outingStatus;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;
    @Column(nullable = false)
    private LocalDate createdAt;
}
