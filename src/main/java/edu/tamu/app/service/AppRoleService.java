package edu.tamu.app.service;

import org.springframework.stereotype.Service;

import edu.tamu.app.enums.AppRole;
import edu.tamu.framework.model.IRole;
import edu.tamu.framework.service.CoreRoleService;

@Service
public class AppRoleService extends CoreRoleService {

    @Override
    public IRole valueOf(String role) {
        return AppRole.valueOf(AppRole.class, role);
    }
    
}
