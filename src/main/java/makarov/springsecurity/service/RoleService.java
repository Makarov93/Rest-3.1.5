package makarov.springsecurity.service;

import makarov.springsecurity.model.Role;

import java.util.Set;

public interface RoleService {
    Set<Role> getAllRoles();
}
