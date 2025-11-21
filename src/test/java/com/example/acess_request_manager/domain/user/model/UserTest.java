package com.example.acess_request_manager.domain.user.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void createUser_Success() {
        User user = new User();

        user.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        user.setEmail("teste@gmail.com");
        user.setPassword(BCrypt.hashpw("senhaSegura", BCrypt.gensalt()));
        user.setDepartment(Department.TI);

        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), user.getId());
        assertEquals("teste@gmail.com", user.getEmail());
        assertEquals(BCrypt.hashpw("senhaSegura", user.getPassword()), user.getPassword());
        assertEquals(Department.TI, user.getDepartment());
    }
}
