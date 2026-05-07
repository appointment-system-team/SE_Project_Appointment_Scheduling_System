package com.appointment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Administrator;
import com.appointment.service.AccountValidationService;

class AdminRepositoryTest {

    @Test
    void shouldContainDefaultAdmin() {
        AdminRepository repository = new AdminRepository();

        List<Administrator> admins = repository.findAll();

        assertEquals(1, admins.size());
        assertEquals("admin", admins.get(0).getUsername());
        assertEquals("System Admin", admins.get(0).getFullName());
        assertEquals("maryamabdoh2005@gmail.com", admins.get(0).getEmail());
    }

    @Test
    void shouldReturnAdminWhenUsernameExists() {
        AdminRepository repository = new AdminRepository();

        Administrator admin = repository.findByUsername("admin");

        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());
    }

    @Test
    void shouldReturnNullWhenUsernameDoesNotExist() {
        AdminRepository repository = new AdminRepository();

        Administrator admin = repository.findByUsername("unknown");

        assertNull(admin);
    }

    @Test
    void shouldReturnAdminWhenEmailExists() {
        AdminRepository repository = new AdminRepository();

        Administrator admin = repository.findByEmail("maryamabdoh2005@gmail.com");

        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());
        assertEquals("maryamabdoh2005@gmail.com", admin.getEmail());
    }

    @Test
    void shouldReturnNullWhenEmailDoesNotExist() {
        AdminRepository repository = new AdminRepository();

        Administrator admin = repository.findByEmail("missing@gmail.com");

        assertNull(admin);
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        AdminRepository repository = new AdminRepository();

        assertTrue(repository.usernameExists("admin"));
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        AdminRepository repository = new AdminRepository();

        assertFalse(repository.usernameExists("unknown"));
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        AdminRepository repository = new AdminRepository();

        assertTrue(repository.emailExists("maryamabdoh2005@gmail.com"));
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        AdminRepository repository = new AdminRepository();

        assertFalse(repository.emailExists("missing@gmail.com"));
    }

    @Test
    void shouldThrowWhenAddingNullAdmin() {
        UserRepository userRepository = new UserRepository();
        AdminRepository adminRepository = new AdminRepository();
        AccountValidationService validationService =
                new AccountValidationService(userRepository, adminRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminRepository.addAdmin(null, validationService)
        );

        assertEquals("Admin cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenValidationServiceIsNull() {
        AdminRepository repository = new AdminRepository();
        Administrator admin = new Administrator(
                "admin2",
                "1234",
                "Admin Two",
                "admin2@gmail.com",
                "0599000002"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.addAdmin(admin, null)
        );

        assertEquals("Validation service cannot be null.", exception.getMessage());
    }

    @Test
    void shouldAddAdminSuccessfully() {
        UserRepository userRepository = new UserRepository();
        AdminRepository adminRepository = new AdminRepository();
        AccountValidationService validationService =
                new AccountValidationService(userRepository, adminRepository);

        Administrator admin = new Administrator(
                "admin2",
                "1234",
                "Admin Two",
                "admin2@gmail.com",
                "0599000002"
        );

        adminRepository.addAdmin(admin, validationService);

        assertEquals(2, adminRepository.findAll().size());
        assertNotNull(adminRepository.findByUsername("admin2"));
        assertNotNull(adminRepository.findByEmail("admin2@gmail.com"));
    }

    @Test
    void shouldReturnCopyFromFindAllNotOriginalList() {
        AdminRepository repository = new AdminRepository();

        List<Administrator> admins = repository.findAll();
        admins.clear();

        assertEquals(1, repository.findAll().size());
    }
}