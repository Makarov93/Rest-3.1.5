package makarov.springsecurity.service;

import makarov.springsecurity.repository.RoleRepository;
import makarov.springsecurity.repository.UserRepository;
import makarov.springsecurity.model.Role;
import makarov.springsecurity.model.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("There is no %s user", email));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), mapRolesToAuthority(user.getRoles()));
    }

    @Override
    public Set<String> getAllRolesNames() {
        return roleRepository
                .findAll()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthority(Set<Role> roles) {
        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void initializeDB() {
        Role adminRole = new Role("ADMIN");
        Role userRole = new Role("USER");

        User user = new User(null, "admin", "admin", (byte) 35, "admin@mail.com", "admin", Set.of(adminRole, userRole));
        User admin = new User(null, "user", "user", (byte) 30, "user@mail.com", "user", Set.of(userRole));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        userRepository.save(user);
        userRepository.save(admin);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void saveOrUpdateUser(User user) {
        if (user.getId() != null && !userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null) {
            user.setRoles(roleRepository.findAllByName("USER"));
        } else {
            user.setRoles(roleRepository.findAllByNameIn(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
