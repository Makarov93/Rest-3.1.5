package makarov.springsecurity.service;

import makarov.springsecurity.dto.UserDTO;
import makarov.springsecurity.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAllUsers();

    void saveOrUpdateUser(UserDTO userDTO);

    void deleteUser(Long id);

    User getUserById(Long id);

    User findByEmail(String name);

    Set<String> getAllRolesNames();

    void initializeDB();
}
