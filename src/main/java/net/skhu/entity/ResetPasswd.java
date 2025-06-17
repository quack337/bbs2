package net.skhu.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="resetpasswd")
public class ResetPasswd {
    @Id
    String guid;

    LocalDateTime createdAt;
    LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(name="userId")
    User user;
}
