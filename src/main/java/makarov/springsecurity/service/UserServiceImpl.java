package makarov.springsecurity.service;


import makarov.springsecurity.dao.RoleRepository;
import makarov.springsecurity.dao.UserRepository;
import makarov.springsecurity.dto.UserDTO;
import makarov.springsecurity.model.Role;
import makarov.springsecurity.model.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Set<String> getAllRolesNames() {
        return roleRepository
                .findAll()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
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
    public void saveOrUpdateUser(UserDTO userDTO) {
        User user;
        if (userDTO.getId() == null) {
            user = new User();
        } else {
            user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        }
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setAge(userDTO.getAge());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getRoles() == null) {
            user.setRoles(roleRepository.findAllByName("USER"));
        } else {
            user.setRoles(roleRepository.findAllByNameIn(userDTO.getRoles()));
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
        return userRepository.findById(id).get();
    }

}
