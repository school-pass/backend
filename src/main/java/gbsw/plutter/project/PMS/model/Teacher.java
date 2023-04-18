package gbsw.plutter.project.PMS.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private Integer grade;

    @Column()
    private Integer classNum;

    @Column()
    private Integer number;

    @Column()
    private String name;
}
