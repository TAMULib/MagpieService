/* 
 * AppRestInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller.interceptor;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.framework.interceptor.CoreRestInterceptor;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

public class AppRestInterceptor extends CoreRestInterceptor {

    @Autowired
    private AppUserRepo userRepo;

    @Value("${app.authority.admins}")
    private String[] admins;

    @Autowired
    @Lazy
    private SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger logger = Logger.getLogger(AppStompInterceptor.class);

    // TODO: move static values into config
    @Override
    public Credentials getAnonymousCredentials() {
        Credentials anonymousCredentials = new Credentials();
        anonymousCredentials.setAffiliation("NA");
        anonymousCredentials.setLastName("Anonymous");
        anonymousCredentials.setFirstName("Role");
        anonymousCredentials.setNetid("anonymous-" + Math.round(Math.random() * 100000));
        anonymousCredentials.setUin("000000000");
        anonymousCredentials.setExp("1436982214754");
        anonymousCredentials.setEmail("helpdesk@library.tamu.edu");
        anonymousCredentials.setRole("ROLE_ANONYMOUS");
        return anonymousCredentials;
    }

    @Override
    public Credentials confirmCreateUser(Credentials credentials) {
        AppUser user = userRepo.findByUin(Long.parseLong(credentials.getUin()));

        if (user == null) {

            if (credentials.getRole() == null) {
                credentials.setRole("ROLE_USER");
            }
            String shibUin = credentials.getUin();
            for (String uin : admins) {
                if (uin.equals(shibUin)) {
                    credentials.setRole("ROLE_ADMIN");
                }
            }

            user = userRepo.create(Long.valueOf(shibUin), credentials.getFirstName(), credentials.getLastName(), credentials.getRole().toString());

            if (!credentials.getUin().equals("null")) {
                user.setUin(Long.parseLong(credentials.getUin()));
                user = userRepo.save(user);
            }

            logger.info(Long.parseLong(credentials.getUin()));

            simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));
        } else {
            credentials.setRole(user.getRole().toString());
        }

        return credentials;
    }

}
