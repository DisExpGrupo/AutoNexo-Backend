package com.atg.autonexo.backend.iam;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.atg.autonexo.backend.iam.domain.model.valueobjects.Roles;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration test for UsersController
 * <p>
 * This test validates the complete IAM flow including user registration and authentication.
 * </p>
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class UsersControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUserSignupAndSigninFlow() throws Exception {
        // Test data
        String signupJson = """
            {
                "email": "test@example.com",
                "password": "password123",
                "firstName": "John",
                "lastName": "Doe",
                "phoneNumber": "1234567890",
                "requestedRole": "CAR_OWNER"
            }
            """;

        String signinJson = """
            {
                "email": "test@example.com",
                "password": "password123"
            }
            """;

        // Test user registration
        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson))
                .andExpect(status().isCreated());

        // Test user authentication
        mockMvc.perform(post("/api/v1/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signinJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testSignupWithInvalidData() throws Exception {
        String invalidSignupJson = """
            {
                "email": "invalid-email",
                "password": "123",
                "firstName": "",
                "lastName": "Doe",
                "phoneNumber": "123",
                "requestedRole": "CAR_OWNER"
            }
            """;

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidSignupJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSignupWithWorkshopManagerRole() throws Exception {
        String workshopManagerSignupJson = """
            {
                "email": "manager@workshop.com",
                "password": "password123",
                "firstName": "Jane",
                "lastName": "Manager",
                "phoneNumber": "0987654321",
                "requestedRole": "WORKSHOP_MANAGER"
            }
            """;

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(workshopManagerSignupJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testSignupWithWorkshopEmployeeRole() throws Exception {
        String workshopEmployeeSignupJson = """
            {
                "email": "employee@workshop.com",
                "password": "password123",
                "firstName": "Bob",
                "lastName": "Employee",
                "phoneNumber": "1122334455",
                "requestedRole": "WORKSHOP_EMPLOYEE"
            }
            """;

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(workshopEmployeeSignupJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testSigninWithInvalidCredentials() throws Exception {
        String invalidSigninJson = """
            {
                "email": "nonexistent@example.com",
                "password": "wrongpassword"
            }
            """;

        mockMvc.perform(post("/api/v1/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidSigninJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAvailableRoles() throws Exception {
        mockMvc.perform(get("/api/v1/users/available-roles"))
                .andExpect(status().isOk());
    }
}
