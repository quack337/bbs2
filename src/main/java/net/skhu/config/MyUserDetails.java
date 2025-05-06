package net.skhu.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.skhu.entity.User;

@Data
@Slf4j
public class MyUserDetails implements UserDetails, OAuth2User {
    private static final long serialVersionUID = 1L;

    final boolean accountNonExpired = true;
    final boolean accountNonLocked = true;
    final boolean credentialsNonExpired = true;
    final String password;
    final String username;
    final boolean isEnabled;
    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    Map<String, Object> attributes;

    final String name;
    final String email;
    final String userType;

    public MyUserDetails(User user) {
        log.debug("------------------------------------");
        switch (user.getUserType()) {
        case "교수": authorities.add(new SimpleGrantedAuthority("ROLE_PROFESSOR")); break;
        case "학생": authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT")); break;
        case "관리자": authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                       authorities.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
                       authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
                       break;
        }
        this.username = user.getLoginName();
        this.password = user.getPassword();
        this.isEnabled = user.isEnabled();

        this.name = user.getName();
        this.email = user.getEmail();
        this.userType = user.getUserType();
    }

    public MyUserDetails(User user, Map<String, Object> attributes) {
        this(user);
        log.debug("====================================");
        log.debug(user.toString());
        log.debug(attributes.toString());
        log.debug("====================================");
        this.attributes = attributes;
        this.authorities.add(new SimpleGrantedAuthority("OAUTH2_USER"));
    }
}
