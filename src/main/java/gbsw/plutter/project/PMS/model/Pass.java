package gbsw.plutter.project.PMS.model;
;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private String userUID;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PassStatus passStatus;
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "teacherId", referencedColumnName = "id")
    private Teacher teacher;
    @Column()
    private Integer placeId;
    @Column(nullable = false)
    private LocalDate createdAt;
    @Column()
    private LocalDate updatedAt;
}
