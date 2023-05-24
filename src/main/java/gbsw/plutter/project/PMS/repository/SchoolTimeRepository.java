package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.SchoolTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolTimeRepository extends JpaRepository<SchoolTime, Long> {
    SchoolTime findByPeriod(Integer period);
}
