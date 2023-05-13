package gbsw.plutter.project.PMS.model;
;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "placeId", nullable = false)
    private Place place;

    @Column(name = "passReason")
    private String passReason;

    @Column(name = "passStart")
    private LocalDateTime passStart;

    @Column(name = "validPass")
    private LocalDateTime validPass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PassStatus passStatus;

    @Column(name = "UID")
    private String uid;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}

