package makarov.springsecurity.controller;


import makarov.springsecurity.dao.RoleRepository;
import makarov.springsecurity.dto.UserDTO;
import makarov.springsecurity.model.Role;
import makarov.springsecurity.model.User;
import makarov.springsecurity.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;

    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return new ArrayList<>(userService.getAllUsers());
    }

    @GetMapping("/users/roles")
    public ResponseEntity<Collection<Role>> getAllRoles() {
        return new ResponseEntity<>(roleRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public UserDTO addNewUser(@RequestBody UserDTO userDTO) {
        userService.saveOrUpdateUser(userDTO);
        return userDTO;
    }

    @PutMapping("/users")
    public UserDTO editUser(@RequestBody UserDTO userDTO) {
        userService.saveOrUpdateUser(userDTO);
        return userDTO;
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/users/currentUser")
    public User getCurrentUser(Principal principal) {
        return userService.findByEmail(principal.getName());
    }
}
