package gbsw.plutter.project.PMS.model;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column()
    private Integer grade;
    @Column()
    private Integer classes;
    @Column()
    private Integer number;
    @Column()
    private String name;
    @Column(nullable = false)
    private String serialNumber;
    @Column(nullable = false)
    private String password;
    @Column(unique = true)
    private String account;
    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Authority> authorities = new ArrayList<>();

    public void addAuthority(Authority authority) {
        authorities.add(authority);
        authority.setMember(this);
    }
}
