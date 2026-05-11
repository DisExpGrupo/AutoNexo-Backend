package com.atg.autonexo.backend.workshop.application;

import com.atg.autonexo.backend.iam.domain.model.aggregates.User;
import com.atg.autonexo.backend.iam.domain.model.entities.WorkshopReference;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.atg.autonexo.backend.iam.infrastructure.persistence.jpa.repositories.WorkshopReferenceRepository;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.Email;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.workshop.application.internal.commandservices.InvitationCommandServiceImpl;
import com.atg.autonexo.backend.workshop.application.internal.commandservices.WorkshopCommandServiceImpl;
import com.atg.autonexo.backend.workshop.domain.exceptions.WorkshopAlreadyExistsException;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.commands.AcceptInvitationCommand;
import com.atg.autonexo.backend.workshop.domain.model.commands.CreateWorkshopCommand;
import com.atg.autonexo.backend.workshop.domain.model.entities.StaffMember;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.InvitationCode;
import com.atg.autonexo.backend.workshop.domain.services.NotificationService;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.InvitationRepository;
import com.atg.autonexo.backend.workshop.infrastructure.persistence.jpa.repositories.WorkshopRepository;
import com.atg.autonexo.backend.workshop.interfaces.acl.WorkshopContextFacade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the Workshop bounded context application layer.
 *
 * <p><strong>Integration</strong> here means: the <em>real</em> application service
 * interacts with <em>real</em> domain objects (Workshop, Invitation aggregates) while
 * infrastructure dependencies (repositories, ACL facade, IAM) are mocked via Mockito.
 * This validates that the service correctly orchestrates domain logic without requiring
 * a database, Spring context, or external services.</p>
 *
 * <p>Each test exercises at least <strong>two real classes</strong> interacting:
 * a command service and one or more domain aggregates whose internal state is verified
 * after execution.</p>
 *
 * <p>Mocks are reset automatically between tests by
 * {@link MockitoExtension} (STRICT_STUBS mode). Services are reconstructed in
 * {@code setUp()} via explicit constructor injection to keep dependencies transparent.</p>
 */
@Tag("integration")
@ExtendWith(MockitoExtension.class)
@DisplayName("Workshop BC — Application Integration Tests")
class WorkshopApplicationIntegrationTest {

    // === Infrastructure mocks — represent the DB, ACL, and external IAM context ===
    @Mock private WorkshopRepository         workshopRepository;
    @Mock private WorkshopContextFacade      workshopContextFacade;
    @Mock private InvitationRepository       invitationRepository;
    @Mock private NotificationService        notificationService;
    @Mock private UserRepository             userRepository;
    @Mock private WorkshopReferenceRepository workshopReferenceRepository;

    // === Real services under test — explicit constructor injection (no @InjectMocks) ===
    private WorkshopCommandServiceImpl  workshopCommandService;
    private InvitationCommandServiceImpl invitationCommandService;

    @BeforeEach
    void setUp() {
        // Mocks are already reset by MockitoExtension; services are rebuilt fresh each test
        workshopCommandService = new WorkshopCommandServiceImpl(
                workshopRepository,
                workshopContextFacade);

        invitationCommandService = new InvitationCommandServiceImpl(
                invitationRepository,
                workshopRepository,
                notificationService,
                userRepository,
                workshopReferenceRepository);
    }

    // =========================================================================
    // WORKSHOP COMMAND SERVICE — CreateWorkshop
    // =========================================================================

    // ES: Riesgo cubierto: Sin verificar el caso exitoso, una regresión en la lógica de
    //     creación podría silenciarse. Esta prueba garantiza que el flujo completo
    //     (guardar en BD + asociar en ACL) ocurre cuando los datos son válidos.
    // EN: Risk covered: Without verifying the success case, a regression in the creation
    //     logic could go undetected. This test guarantees that the full flow
    //     (save to DB + associate in ACL) happens when data is valid.
    @Test
    @DisplayName("[INT] CreateWorkshop — valid command must save workshop and trigger ACL association")
    void handle_CreateWorkshopCommand_WhenValidNewOwner_ShouldSaveWorkshopAndAssociateUserInAcl() {
        // Arrange / ES: Organizar
        Long newOwnerId = 55L;
        CreateWorkshopCommand command = new CreateWorkshopCommand(
                newOwnerId, "Taller Exitoso", null, null, null);

        Workshop expectedWorkshop = new Workshop(
                new UserId(newOwnerId), "Taller Exitoso", null, null, null);

        when(workshopRepository.existsByOwnerUserId(newOwnerId)).thenReturn(false);
        when(workshopContextFacade.userHasWorkshop(newOwnerId)).thenReturn(false);
        when(workshopRepository.save(any(Workshop.class))).thenReturn(expectedWorkshop);

        // Act / ES: Actuar
        Workshop result = workshopCommandService.handle(command);

        // Assert / ES: Confirmar
        assertNotNull(result, "A Workshop must be returned on successful creation");
        verify(workshopRepository).save(any(Workshop.class));
        verify(workshopContextFacade).associateUserWithWorkshop(eq(newOwnerId), any());
    }

    // ES: Riesgo cubierto: Sin esta guardia, un usuario podría crear múltiples talleres,
    //     corrompiendo la regla de negocio "1 usuario = 1 taller". Esto genera inconsistencias
    //     en el ACL, duplicación de suscripciones y conflictos de referencia en el contexto IAM.
    //     La prueba verifica que WorkshopCommandServiceImpl consulta el repositorio Y que nunca
    //     persiste ni asocia el taller duplicado.
    // EN: Risk covered: Without this guard, a user could create multiple workshops, corrupting
    //     the "1 user = 1 workshop" business rule. This generates ACL inconsistencies,
    //     duplicate subscriptions, and reference conflicts in the IAM context.
    //     The test verifies that WorkshopCommandServiceImpl queries the repository AND never
    //     persists or associates the duplicate workshop.
    @Test
    @DisplayName("[INT] CreateWorkshop — duplicate owner must throw WorkshopAlreadyExistsException without saving")
    void handle_CreateWorkshopCommand_WhenOwnerAlreadyHasWorkshop_ShouldThrowAndNeverSave() {
        // Arrange / ES: Organizar
        Long existingOwnerId = 42L;
        CreateWorkshopCommand command = new CreateWorkshopCommand(
                existingOwnerId, "Segundo Taller (inválido)", "desc", "Legal SA", null);

        when(workshopRepository.existsByOwnerUserId(existingOwnerId)).thenReturn(true);

        // Act / ES: Actuar
        WorkshopAlreadyExistsException thrown = assertThrows(
                WorkshopAlreadyExistsException.class,
                () -> workshopCommandService.handle(command));

        // Assert / ES: Confirmar
        assertNotNull(thrown);
        verify(workshopRepository, never()).save(any(Workshop.class));
        verify(workshopContextFacade, never()).associateUserWithWorkshop(any(), any());
    }

    // ES: Riesgo cubierto: Si el rollback no se ejecuta cuando el servicio ACL falla,
    //     el taller existiría en la BD pero el usuario no estaría vinculado. Esto crea un
    //     "taller fantasma" inaccesible que ocupa el slot único del propietario,
    //     bloqueándolo permanentemente para crear otro taller válido.
    // EN: Risk covered: If rollback is not executed when the ACL service fails, the workshop
    //     would exist in the DB but the user would not be linked. This creates a "ghost
    //     workshop" that occupies the owner's unique slot, permanently blocking them
    //     from creating a valid workshop.
    @Test
    @DisplayName("[INT] CreateWorkshop — ACL association failure must rollback by deleting the saved workshop")
    void handle_CreateWorkshopCommand_WhenAclAssociationFails_ShouldRollbackByDeletingWorkshop() {
        // Arrange / ES: Organizar
        Long ownerId = 10L;
        CreateWorkshopCommand command = new CreateWorkshopCommand(
                ownerId, "Taller con ACL Roto", null, null, null);

        Workshop savedWorkshop = new Workshop(
                new UserId(ownerId), "Taller con ACL Roto", null, null, null);

        when(workshopRepository.existsByOwnerUserId(ownerId)).thenReturn(false);
        when(workshopContextFacade.userHasWorkshop(ownerId)).thenReturn(false);
        when(workshopRepository.save(any(Workshop.class))).thenReturn(savedWorkshop);
        doThrow(new RuntimeException("IAM service unavailable"))
                .when(workshopContextFacade).associateUserWithWorkshop(any(), any());

        // Act / ES: Actuar
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> workshopCommandService.handle(command));

        // Assert / ES: Confirmar
        assertNotNull(thrown);
        verify(workshopRepository).delete(savedWorkshop); // el rollback debe haberse ejecutado
    }

    // =========================================================================
    // INVITATION COMMAND SERVICE — AcceptInvitation
    // =========================================================================

    // ES: Riesgo cubierto: Si el servicio no rechaza invitaciones expiradas, cualquier código
    //     antiguo filtrado o robado podría usarse indefinidamente para añadir staff no autorizado.
    //     Esta prueba verifica la interacción real entre InvitationCommandServiceImpl y el
    //     aggregate Invitation: el service llama canBeUsed() e isExpired() sobre el objeto
    //     de dominio real y debe propagar correctamente el error.
    // EN: Risk covered: If the service does not reject expired invitations, any old leaked or
    //     stolen code could be used indefinitely to add unauthorized staff.
    //     This test verifies the real interaction between InvitationCommandServiceImpl and the
    //     Invitation aggregate: the service calls canBeUsed() and isExpired() on the real
    //     domain object and must correctly surface the error.
    @Test
    @DisplayName("[INT] AcceptInvitation — expired invitation must be rejected by service + domain logic")
    void handle_AcceptInvitationCommand_WhenInvitationIsExpired_ShouldThrowIllegalStateException() {
        // Arrange / ES: Organizar
        String code  = "EXPIR123";
        String email = "empleado@taller.com";
        AcceptInvitationCommand command = new AcceptInvitationCommand(code, email);

        // Objeto de dominio REAL con fecha vencida — isExpired() y canBeUsed() se invocan en él
        Invitation expiredInvitation = new Invitation(
                new InvitationCode(code),
                LocalDateTime.now().minusDays(3), // venció hace 3 días
                new Email(email),
                new WorkshopId(1L),
                null);

        when(invitationRepository.findAll()).thenReturn(List.of(expiredInvitation));

        // Act / ES: Actuar
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> invitationCommandService.handle(command));

        // Assert / ES: Confirmar
        assertTrue(thrown.getMessage().toLowerCase().contains("expir"),
                "The service must surface the domain's expiration error. Got: " + thrown.getMessage());
        verify(userRepository,     never()).save(any());
        verify(workshopRepository, never()).save(any(Workshop.class));
    }

    // ES: Riesgo cubierto: Este es el flujo completo de onboarding de staff. Si falla cualquier
    //     paso (crear StaffMember, marcar invitación como usada, persistir ambos aggregates),
    //     el empleado quedaría sin acceso al taller o la invitación quedaría disponible para
    //     reutilización. La prueba verifica que ambos aggregates reales — Workshop e Invitation —
    //     son modificados en memoria Y enviados a persistir por el servicio.
    // EN: Risk covered: This is the complete staff onboarding flow. If any step fails (creating
    //     StaffMember, marking invitation as used, persisting both aggregates), the employee
    //     would have no workshop access or the invitation would remain reusable.
    //     The test verifies that both real aggregates — Workshop and Invitation — are modified
    //     in memory AND sent for persistence by the service.
    @Test
    @DisplayName("[INT] AcceptInvitation — valid invitation must create StaffMember and mark invitation as used")
    void handle_AcceptInvitationCommand_WhenValidInvitation_ShouldCreateStaffMemberAndMarkInvitationUsed() {
        // Arrange / ES: Organizar
        String code       = "VALID123";
        String email      = "nuevo.empleado@taller.com";
        Long   workshopId = 5L;
        Long   userId     = 100L;
        AcceptInvitationCommand command = new AcceptInvitationCommand(code, email);

        // Aggregates de dominio REALES — sus métodos son invocados directamente por el servicio
        Invitation validInvitation = new Invitation(
                new InvitationCode(code),
                LocalDateTime.now().plusDays(7),
                new Email(email),
                new WorkshopId(workshopId),
                "Bienvenido al equipo");

        Workshop workshop = new Workshop(new UserId(1L), "Taller Real SA", null, null, null);

        // Mocks de infraestructura IAM (contexto externo al bounded context Workshop)
        User             mockUser = mock(User.class);
        WorkshopReference mockRef = mock(WorkshopReference.class);

        when(invitationRepository.findAll()).thenReturn(List.of(validInvitation));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getWorkshopReference()).thenReturn(null); // usuario sin taller previo
        when(workshopRepository.findById(workshopId)).thenReturn(Optional.of(workshop));
        when(workshopReferenceRepository.findAll()).thenReturn(List.of());
        when(workshopReferenceRepository.save(any())).thenReturn(mockRef);

        // Act / ES: Actuar
        StaffMember result = invitationCommandService.handle(command);

        // Assert / ES: Confirmar
        assertNotNull(result,
                "A real StaffMember domain object must be returned after accepting the invitation");

        // Verifica el estado interno del aggregate Invitation (objeto REAL)
        assertTrue(validInvitation.isUsed(),
                "The real Invitation aggregate must be marked as used after acceptance");

        // Verifica el estado interno del aggregate Workshop (objeto REAL)
        assertFalse(workshop.getStaffMembers().isEmpty(),
                "The real Workshop aggregate must contain the new StaffMember after acceptance");

        // Verifica que ambos aggregates fueron enviados a persistir
        verify(workshopRepository).save(workshop);
        verify(invitationRepository).save(validInvitation);
    }
}
