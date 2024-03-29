package kr.co.popoolserver.admin.security;

import kr.co.popoolserver.persistence.entitiy.AdminEntity;

public class AdminThreadLocal {
    private static final ThreadLocal<AdminEntity> adminThreadLocal;

    static {
        adminThreadLocal = new ThreadLocal<>();
    }

    public static AdminEntity get(){
        return adminThreadLocal.get();
    }

    public static void set(AdminEntity userEntity){
        adminThreadLocal.set(userEntity);
    }

    public static void remove(){
        adminThreadLocal.remove();
    }
}
