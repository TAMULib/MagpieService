package edu.tamu.app.service;

import org.springframework.stereotype.Service;

import edu.tamu.app.enums.AppRole;
import edu.tamu.weaver.user.model.IRole;
import edu.tamu.weaver.user.role.service.WeaverRoleService;

@Service
public class AppRoleService extends WeaverRoleService {

    @Override
    public IRole valueOf(String role) {
        return AppRole.valueOf(AppRole.class, role);
    }

}
