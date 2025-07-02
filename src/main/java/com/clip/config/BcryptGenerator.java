package com.clip.config;

//INSERT INTO USERS (USER_ID, NICK_NAME, PASSWORD)
//VALUES ('testuser', '테스트유저', '$2a$10$RzZIfRSrHTe4/ia9C72JReBB42mNEqE5jFPfbZJzfUMphE41z6eWy');

public class BcryptGenerator {
    public static void main(String[] args) {
        System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("testpass"));
    }
}
