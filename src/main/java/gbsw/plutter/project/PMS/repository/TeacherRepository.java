package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findTeacherByMember_Id(Long id);
}
