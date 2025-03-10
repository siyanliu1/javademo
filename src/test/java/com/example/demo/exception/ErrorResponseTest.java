package com.example.demo.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ErrorResponseTest {

    @Test
    public void testErrorResponse() {
        ErrorResponse error = new ErrorResponse("Initial message");
        assertEquals("Initial message", error.getMessage());
        error.setMessage("Updated message");
        assertEquals("Updated message", error.getMessage());
    }
}
