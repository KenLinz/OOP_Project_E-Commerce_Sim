package com.commerce.ooad.E_Commerce.service;

import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserSQL testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testUser = new UserSQL("john", "pass123", "john@email.com", "CO");
    }

    @Test
    public void testAuthenticateSuccess() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        Optional<UserSQL> result = authenticationService.authenticate("john", "pass123");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    @Test
    public void testAuthenticateWrongPassword() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        Optional<UserSQL> result = authenticationService.authenticate("john", "wrongpass");

        assertFalse(result.isPresent());
    }

    @Test
    public void testAuthenticateUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        Optional<UserSQL> result = authenticationService.authenticate("nobody", "pass123");

        assertFalse(result.isPresent());
    }

    @Test
    public void testRegisterSuccess() throws AuthenticationService.RegistrationException {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(testUser)).thenReturn(testUser);

        UserSQL result = authenticationService.register(testUser);

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        verify(userRepository).save(testUser);
    }

    @Test
    public void testRegisterEmptyUsername() {
        UserSQL invalidUser = new UserSQL("", "pass123", "test@email.com", "CO");

        assertThrows(AuthenticationService.RegistrationException.class, () -> {
            authenticationService.register(invalidUser);
        });
    }

    @Test
    public void testRegisterEmptyPassword() {
        UserSQL invalidUser = new UserSQL("john", "", "test@email.com", "CO");

        assertThrows(AuthenticationService.RegistrationException.class, () -> {
            authenticationService.register(invalidUser);
        });
    }

    @Test
    public void testRegisterEmptyEmail() {
        UserSQL invalidUser = new UserSQL("john", "pass123", "", "CO");

        assertThrows(AuthenticationService.RegistrationException.class, () -> {
            authenticationService.register(invalidUser);
        });
    }

    @Test
    public void testRegisterEmptyState() {
        UserSQL invalidUser = new UserSQL("john", "pass123", "test@email.com", "");

        assertThrows(AuthenticationService.RegistrationException.class, () -> {
            authenticationService.register(invalidUser);
        });
    }

    @Test
    public void testRegisterDuplicateUsername() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        assertThrows(AuthenticationService.RegistrationException.class, () -> {
            authenticationService.register(testUser);
        });
    }

    @Test
    public void testRegisterDuplicateEmail() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(testUser));

        assertThrows(AuthenticationService.RegistrationException.class, () -> {
            authenticationService.register(testUser);
        });
    }
}