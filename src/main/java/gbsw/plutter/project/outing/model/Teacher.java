package gbsw.plutter.project.outing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teacherId;
    @Column(nullable = false)
    private String name;
    @Column()
    private Integer grade;
    @Column()
    private Integer classes;
    @Column(length = 8, nullable = false)
    private String password;
    @OneToMany
    @JoinColumn(name="userId")
    private List<User> user;

    @Column(length = 10, nullable = false)
    private String salt;
}
