package gbsw.plutter.project.PMS.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teacherId;

    @Column()
    private Integer grade;

    @Column()
    private Integer classNum;

    @Column()
    private Integer number;

    @OneToOne()
    @JoinColumn(name = "user", referencedColumnName = "userId")
    private Member memberId;

}
