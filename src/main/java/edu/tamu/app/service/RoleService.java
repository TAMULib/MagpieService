package edu.tamu.app.service;

import org.springframework.stereotype.Service;

import edu.tamu.app.model.Role;
import edu.tamu.weaver.user.model.IRole;
import edu.tamu.weaver.user.role.service.WeaverRoleService;

@Service
public class RoleService extends WeaverRoleService {

    @Override
    public IRole valueOf(String role) {
        return Role.valueOf(Role.class, role);
    }

}
