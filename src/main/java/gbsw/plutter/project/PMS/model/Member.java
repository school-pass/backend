package gbsw.plutter.project.PMS.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false)
    private String name;
    @Column()
    private Integer grade;
    @Column()
    private Integer classes;
    @Column()
    private Integer number;
    @Column(length = 8, nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private roleEnum role;
    @Column(length = 10, nullable = false)
    private String salt;
}
