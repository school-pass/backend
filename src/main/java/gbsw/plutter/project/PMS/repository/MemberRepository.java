package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.Member;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Transactional
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount(String account);
    Optional<Member> findBySerialNumber(String serialNumber);
    Optional<Member> findMemberByGradeAndClassesAndNumber(Integer grade, Integer classes, Integer number);
    Member findMemberByAccount(String account);
    Member findMemberById(Long id);

}
