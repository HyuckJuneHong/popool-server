package kr.co.popoolserver.common.infra.interceptor;

import kr.co.popoolserver.user.domain.UserEntity;

public class UserThreadLocal {

    private static final ThreadLocal<UserEntity> userThreadLocal;

    static {
        userThreadLocal = new ThreadLocal<>();
    }

    public static UserEntity get(){
        return userThreadLocal.get();
    }

    public static void set(UserEntity userEntity){
        userThreadLocal.set(userEntity);
    }

    public static void remove(){
        userThreadLocal.remove();
    }
}
