package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


@Transactional
public interface SignRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPasswordLike(String password);
}