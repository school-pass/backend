package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.Authority;
import gbsw.plutter.project.PMS.model.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findAuthorityByName(String role);
    @Modifying
    @Query("DELETE FROM Authority a WHERE a.member.id = :memberId")
    void deleteAuthoritiesByMemberId(Long memberId);

    Authority findAuthorityByMember(Member member);

    List<Authority> findAuthoritiesByName(String name);
}
