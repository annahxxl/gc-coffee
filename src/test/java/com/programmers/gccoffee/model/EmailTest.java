package com.programmers.gccoffee.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void testInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            var email = new Email("test");
        });
    }

    @Test
    void testValidEmail() {
        String address = "test@email.com";
        var email = new Email(address);
        assertTrue(email.getAddress().equals(address));
    }

    @Test
    void testEqEmail() {
        var email1 = new Email("test@email.com");
        var email2 = new Email("test@email.com");
        assertEquals(email1, email2);
    }
}
