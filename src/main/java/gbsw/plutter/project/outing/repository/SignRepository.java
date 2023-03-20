package gbsw.plutter.project.outing.repository;

import gbsw.plutter.project.outing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.Map;

public interface SignRepository extends JpaRepository<User, Integer> {
    public HashMap<String, Object> findByPasswordLike(String password);
}
