package net.skhu.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import net.skhu.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>  {

    Optional<User> findByLoginName(String loginName);
    Optional<User> findByOpenId(String openId);
    Page<User> findByLoginNameStartsWith(String loginName, Pageable pageable);
    Page<User> findByNameStartsWith(String loginName, Pageable pageable);
    Page<User> findByUserType(String userType, Pageable pageable);
}
