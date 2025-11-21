package com.example.acess_request_manager.application.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.example.acess_request_manager.domain.user.model.Department;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class UserDtoTest {

    @Test
    void testConstructorAndGetters() {
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        Department dept = Department.RH;

        UserDto dto = new UserDto(id, email, dept);

        assertEquals(id, dto.id());
        assertEquals(email, dto.email());
        assertEquals(dept, dto.department());
    }
}
