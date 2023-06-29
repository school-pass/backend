package gbsw.plutter.project.PMS.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    @Column(unique = true)
    private String ipAddress;

    @Column()
    private Integer capacity;

    @Column()
    private Integer maxCapacity;

    @Column()
    private String location;

    @Column()
    private Integer floor;

    @Column()
    private String locationDetail;
}