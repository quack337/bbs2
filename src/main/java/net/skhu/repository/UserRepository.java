package net.skhu.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import net.skhu.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>  {
    Optional<User> findByLoginName(String longinName);
}
