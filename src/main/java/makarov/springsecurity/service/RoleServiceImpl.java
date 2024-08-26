package makarov.springsecurity.service;

import makarov.springsecurity.repository.RoleRepository;
import makarov.springsecurity.model.Role;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Set<Role> getAllRoles() {
        return (Set<Role>) roleRepository.findAll();
    }
}
