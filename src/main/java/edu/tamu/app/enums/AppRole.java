/* 
 * Roles.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.enums;

import edu.tamu.framework.model.IRole;

public enum AppRole implements IRole {

    ROLE_ANONYMOUS(0), 
    ROLE_USER(1), 
    ROLE_ANNOTATOR(2), 
    ROLE_MANAGER(3), 
    ROLE_ADMIN(4);

    private int value;

    AppRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }

}