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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private Member member;

    @Column(name = "serialNum", unique = true)
    private String serialNum;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tpermission tpermission;
}
