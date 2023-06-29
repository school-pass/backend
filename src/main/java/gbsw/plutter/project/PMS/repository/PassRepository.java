package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassRepository extends JpaRepository<Pass, Long> {
    List<Pass> findByMemberAndPlaceAndPassStatusIn(Member member, Place place, List<PassStatus> allowedStatuses);
    Pass findPassByMember_SerialNumberAndPlace(String SerialNumber, Place place);
    Pass findPassByMember(Member member);
    List<Pass> findAllByPassStatus(PassStatus passStatus);
}
