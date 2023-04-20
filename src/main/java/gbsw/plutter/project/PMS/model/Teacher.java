package gbsw.plutter.project.PMS.model;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String serialNum;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tpermission tpermission;
    @OneToOne
    @JoinColumn(name = "userId",referencedColumnName = "id")
    private Member member;
    @Column()
    private String name;
}
