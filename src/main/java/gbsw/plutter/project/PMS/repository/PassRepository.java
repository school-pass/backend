package gbsw.plutter.project.PMS.repository;

import gbsw.plutter.project.PMS.model.Pass;
import gbsw.plutter.project.PMS.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassRepository extends JpaRepository<Pass, Long> {
    Pass findPassByIMEIAndPlace(String IMEI, Place place);

    Pass findPassByIMEI(String IMEI);
}
