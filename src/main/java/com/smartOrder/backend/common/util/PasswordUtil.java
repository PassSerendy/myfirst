package com.smartOrder.backend.common.util;
import org.mindrot.jbcrypt.BCrypt;
public class PasswordUtil {
    //加密密码
    public static String encrypt(String rawPassword){
        return BCrypt.hashpw(rawPassword,BCrypt.gensalt());
    }

    //验证密码
    public static boolean check(String rawPassword,String encryptedPassword){
        return BCrypt.checkpw(rawPassword,encryptedPassword);
    }
}
