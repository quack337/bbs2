package net.skhu.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import net.skhu.entity.ResetPasswd;

public interface ResetPasswdRepository extends JpaRepository<ResetPasswd, String>  {
    Optional<ResetPasswd> findByGuid(String guid);
}
