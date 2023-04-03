package gbsw.plutter.project.outing.model;
;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class Pass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passId;

    @Column(length = 255)
    private String passReason;

    @Column(nullable = false)
    private LocalDateTime passStart;

    @Column(nullable = false)
    private LocalDateTime passExpiration;

    @Column(nullable = false)
    private String passToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PassStatus passStatus;

    @Column(nullable = false)
    private String passPlace;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column()
    private LocalDate updatedAt;
}