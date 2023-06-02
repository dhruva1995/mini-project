package dc.codecademy.miniproject.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dc.codecademy.miniproject.models.SecurityUser;
import dc.codecademy.miniproject.models.User;
import dc.codecademy.miniproject.repositories.UserRepository;

@Service
public class JPAUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));
    }

    public User saveUser(String username, String password) {
        User user = new User(username, this.encoder.encode(password), "ROLE_USER, USER");
        return userRepo.save(user);
    }

    public Optional<User> findByUsername(final String username) {
        return userRepo.findByUsername(username);
    }

}
