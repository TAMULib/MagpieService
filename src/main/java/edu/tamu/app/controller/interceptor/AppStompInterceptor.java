/* 
 * StompInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller.interceptor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.framework.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.Credentials;

/**
 * Stomp interceptor. Checks command, decodes and verifies token, either returns
 * error message to frontend or continues to controller.
 * 
 * @author
 *
 */
@Component
public class AppStompInterceptor extends CoreStompInterceptor {

    @Autowired
    private AppUserRepo userRepo;

    @Value("${app.authority.admins}")
    private String[] admins;

    @Value("${app.authority.managers}")
    private String[] managers;

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

    /**
     * @param credentials
     *            Credentials
     * 
     * @return shib
     * 
     * @see edu.tamu.framework.interceptor.CoreStompInterceptor#confirmCreateUser(edu.tamu.framework.model.Credentials)
     */
    public Credentials confirmCreateUser(Credentials credentials) {

        AppUser user = userRepo.findByUin(Long.parseLong(credentials.getUin()));

        if (user == null) {

            if (credentials.getRole() == null) {
                credentials.setRole("ROLE_USER");
            }

            String shibUin = credentials.getUin();

            for (String uin : managers) {
                if (uin.equals(shibUin)) {
                    credentials.setRole("ROLE_MANAGER");
                }
            }

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

        } else {
            credentials.setRole(user.getRole().toString());
        }

        return credentials;

    }

}
