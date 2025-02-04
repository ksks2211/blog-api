package org.example.postapi.domain.user.entity;


/**
 * @author rival
 * @since 2025-01-15
 */


public enum Privilege {

    READ_POST,
    MANAGE_POST;


    public String getPrivilege(){
        return this.name()+"_PRIVILEGE";
    }
}
