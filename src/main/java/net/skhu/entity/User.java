package net.skhu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String loginName;
    String password;
    String name;
    String email;
    boolean enabled;
    String userType;
    String openId;

    @ManyToOne
    @JoinColumn(name="departmentId")
    Department department;
}
