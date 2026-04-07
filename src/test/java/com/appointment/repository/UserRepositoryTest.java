package com.appointment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.appointment.domain.User;
import com.appointment.service.AccountValidationService;

public class UserRepositoryTest {

    @Test
    void shouldContainDefaultUsers() {
        UserRepository repository = new UserRepository();

        List<User> users = repository.findAll();

        assertEquals(2, users.size());
        assertEquals("zeina1", users.get(0).getUsername());
        assertEquals("ameer1", users.get(1).getUsername());
    }

    @Test
    void shouldFindUserByUsername() {
        UserRepository repository = new UserRepository();

        User user = repository.findByUsername("zeina1");

        assertNotNull(user);
        assertEquals("Zeina Anabtawi", user.getFullName());
        assertEquals("zeina1@gmail.com", user.getEmail());
    }

    @Test
    void shouldReturnNullWhenUsernameNotFound() {
        UserRepository repository = new UserRepository();

        User user = repository.findByUsername("unknown");

        assertNull(user);
    }

    @Test
    void shouldFindUserByEmail() {
        UserRepository repository = new UserRepository();

        User user = repository.findByEmail("ameer1@gmail.com");

        assertNotNull(user);
        assertEquals("ameer1", user.getUsername());
        assertEquals("Ameer Abu Shams", user.getFullName());
    }

    @Test
    void shouldReturnNullWhenEmailNotFound() {
        UserRepository repository = new UserRepository();

        User user = repository.findByEmail("notfound@gmail.com");

        assertNull(user);
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {
        UserRepository repository = new UserRepository();

        assertTrue(repository.usernameExists("zeina1"));
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        UserRepository repository = new UserRepository();

        assertFalse(repository.usernameExists("no_user"));
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        UserRepository repository = new UserRepository();

        assertTrue(repository.emailExists("ameer1@gmail.com"));
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        UserRepository repository = new UserRepository();

        assertFalse(repository.emailExists("missing@gmail.com"));
    }

    @Test
    void shouldThrowWhenAddingNullUser() {
        UserRepository userRepository = new UserRepository();
        AdminRepository adminRepository = new AdminRepository();
        AccountValidationService validationService =
                new AccountValidationService(userRepository, adminRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userRepository.addUser(null, validationService)
        );

        assertEquals("User cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenValidationServiceIsNull() {
        UserRepository repository = new UserRepository();
        User user = new User("newuser", "1234", "New User", "newuser@gmail.com", "0599000010");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.addUser(user, null)
        );

        assertEquals("Validation service cannot be null.", exception.getMessage());
    }

    @Test
    void shouldAddUserSuccessfully() {
        UserRepository userRepository = new UserRepository();
        AdminRepository adminRepository = new AdminRepository();
        AccountValidationService validationService =
                new AccountValidationService(userRepository, adminRepository);

        User user = new User("newuser", "1234", "New User", "newuser@gmail.com", "0599000010");

        userRepository.addUser(user, validationService);

        assertEquals(3, userRepository.findAll().size());
        assertNotNull(userRepository.findByUsername("newuser"));
        assertNotNull(userRepository.findByEmail("newuser@gmail.com"));
    }

    @Test
    void shouldReturnCopyFromFindAllNotOriginalList() {
        UserRepository repository = new UserRepository();

        List<User> users = repository.findAll();
        users.clear();

        assertEquals(2, repository.findAll().size());
    }
}