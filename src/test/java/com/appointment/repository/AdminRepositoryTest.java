package com.appointment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Administrator;

class AdminRepositoryTest {

    @Test
    void shouldReturnAdminWhenUsernameExists() {
        AdminRepository repository = new AdminRepository();

        Administrator admin = repository.findByUsername("admin");

        assertEquals("admin", admin.getUsername());
    }

    @Test
    void shouldReturnNullWhenUsernameDoesNotExist() {
        AdminRepository repository = new AdminRepository();

        Administrator admin = repository.findByUsername("unknown");

        assertNull(admin);
    }
}