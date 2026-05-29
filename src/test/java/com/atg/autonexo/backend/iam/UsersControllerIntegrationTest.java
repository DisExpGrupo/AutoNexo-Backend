package com.atg.autonexo.backend.iam;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Email;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.InvitationCode;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.InvitationRepository;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test for UsersController
 * <p>
 * This test validates the complete IAM flow including user registration and authentication.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UsersControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private WorkshopRepository workshopRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

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
        String ownerSignupJson = """
            {
                "email": "owner@workshop.com",
                "password": "password123",
                "firstName": "Workshop",
                "lastName": "Owner",
                "phoneNumber": "9998887776",
                "requestedRole": "CAR_OWNER"
            }
            """;

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ownerSignupJson))
                .andExpect(status().isCreated());

        User owner = userRepository.findByEmail("owner@workshop.com").orElseThrow();

        Workshop workshop = new Workshop(new UserId(owner.getId()), "Test Workshop", null, null, null);
        workshop = workshopRepository.save(workshop);

        String invitationCode = "T3STCODE";
        Invitation invitation = new Invitation(
                new InvitationCode(invitationCode),
                LocalDateTime.now().plusDays(7),
                new Email("employee@workshop.com"),
                new WorkshopId(workshop.getId()),
                "Welcome to the team!"
        );
        invitationRepository.save(invitation);

        String workshopEmployeeSignupJson = """
            {
                "email": "employee@workshop.com",
                "password": "password123",
                "firstName": "Bob",
                "lastName": "Employee",
                "phoneNumber": "1122334455",
                "requestedRole": "WORKSHOP_EMPLOYEE",
                "invitationCode": "%s"
            }
            """.formatted(invitationCode);

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
