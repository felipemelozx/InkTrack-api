package com.inktrack.core.utils;

import com.inktrack.core.exception.FieldDomainValidationException;
import com.inktrack.core.usecases.user.CreateUserRequestModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void validate_shouldPass_whenInputIsValid() {
        CreateUserRequestModel input = new CreateUserRequestModel("John Doe", "john@example.com", "Password123!");
        assertDoesNotThrow(() -> Validation.validate(input));
    }

    @Test
    void validate_shouldThrow_whenNameIsInvalid() {
        CreateUserRequestModel inputNullName = new CreateUserRequestModel(null, "john@example.com", "Password123!");
        FieldDomainValidationException exceptionNull = assertThrows(FieldDomainValidationException.class, () -> Validation.validate(inputNullName));
        assertEquals("name", exceptionNull.getFieldName());

        CreateUserRequestModel inputBlankName = new CreateUserRequestModel("", "john@example.com", "Password123!");
        FieldDomainValidationException exceptionBlank = assertThrows(FieldDomainValidationException.class, () -> Validation.validate(inputBlankName));
        assertEquals("name", exceptionBlank.getFieldName());
    }

    @Test
    void validate_shouldThrow_whenPasswordIsWeak() {
        CreateUserRequestModel inputWeak = new CreateUserRequestModel("John Doe", "john@example.com", "weak");
        FieldDomainValidationException exception = assertThrows(FieldDomainValidationException.class, () -> Validation.validate(inputWeak));
        assertEquals("password", exception.getFieldName());
    }

    @Test
    void validate_shouldThrow_whenEmailIsInvalid() {
        CreateUserRequestModel inputInvalidEmail = new CreateUserRequestModel("John Doe", "johnexample.com", "Password123!");
        FieldDomainValidationException exception = assertThrows(FieldDomainValidationException.class, () -> Validation.validate(inputInvalidEmail));
        assertEquals("email", exception.getFieldName());
    }

    @Test
    void isValidEmail_shouldReturnTrue_forValidEmail() {
        assertTrue(Validation.isValidEmail("test@example.com"));
        assertTrue(Validation.isValidEmail("user.name@domain.co.uk"));
    }

    @Test
    void isValidEmail_shouldReturnFalse_forInvalidEmail() {
        assertFalse(Validation.isValidEmail("plainaddress"));
        assertFalse(Validation.isValidEmail("@missingusername.com"));
        assertFalse(Validation.isValidEmail(null));
    }

    @Test
    void isStrongPassword_shouldReturnTrue_forStrongPassword() {
        assertTrue(Validation.isStrongPassword("StrongP@ss1"));
    }

    @Test
    void isStrongPassword_shouldReturnFalse_forWeakPasswords() {
        assertFalse(Validation.isStrongPassword("short")); 
        assertFalse(Validation.isStrongPassword("onlylowercase")); 
        assertFalse(Validation.isStrongPassword("ONLYUPPERCASE")); 
        assertFalse(Validation.isStrongPassword("NoSpecialChar1")); 
        assertFalse(Validation.isStrongPassword(null));
    }
}
