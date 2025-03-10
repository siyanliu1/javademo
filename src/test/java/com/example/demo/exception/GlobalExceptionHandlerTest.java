//package com.example.demo.exception;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.ResponseEntity;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class GlobalExceptionHandlerTest {
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Test
//    public void testHandleUserNotFoundException() throws Exception {
//        GlobalExceptionHandler handler = new GlobalExceptionHandler();
//        UserNotFoundException ex = new UserNotFoundException("Pilot User not found!");
//        ResponseEntity<?> response = handler.handleUserNotFoundException(ex);
//        assertEquals(404, response.getStatusCodeValue());
//        // Handle both possibilities: if response body is already an ErrorResponse, or if it's a String (JSON)
//        ErrorResponse error = extractErrorResponse(response);
//        assertEquals("Pilot User not found!", error.getMessage());
//    }
//
//    @Test
//    public void testHandleGenericException() throws Exception {
//        GlobalExceptionHandler handler = new GlobalExceptionHandler();
//        Exception ex = new Exception("Some error occurred");
//        ResponseEntity<?> response = handler.handleGenericException(ex);
//        assertEquals(500, response.getStatusCodeValue());
//        ErrorResponse error = extractErrorResponse(response);
//        assertTrue(error.getMessage().contains("Some error occurred"));
//    }
//
//    private ErrorResponse extractErrorResponse(ResponseEntity<?> response) throws Exception {
//        Object body = response.getBody();
//        if (body instanceof ErrorResponse) {
//            return (ErrorResponse) body;
//        } else if (body instanceof String) {
//            // Convert the JSON string to ErrorResponse
//            return mapper.readValue((String) body, ErrorResponse.class);
//        } else {
//            fail("Unexpected response body type: " + (body != null ? body.getClass() : "null"));
//            return null; // Unreachable
//        }
//    }
//}
