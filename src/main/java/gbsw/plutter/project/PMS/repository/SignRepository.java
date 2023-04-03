package gbsw.plutter.project.outing.repository;

import gbsw.plutter.project.outing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface SignRepository extends JpaRepository<User, Integer> {

}
