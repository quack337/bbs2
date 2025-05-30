package net.skhu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import net.skhu.config.MyUserDetails;
import net.skhu.entity.User;
import net.skhu.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginName(username)
                        .orElseThrow(() -> new UsernameNotFoundException(username));
        return new MyUserDetails(user);
    }

}
