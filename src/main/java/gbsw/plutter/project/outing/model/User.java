package gbsw.plutter.project.outing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Integer grade;
    @Column(nullable = false)
    private Integer classes;
    @Column(nullable = false)
    private Integer number;
    @ManyToOne
    @JoinColumn(name="teacherId")
    private Teacher teacher;
    @Column(length = 8, nullable = false)
    private String password;
    @Column(length = 10, nullable = false)
    private String salt;
}
