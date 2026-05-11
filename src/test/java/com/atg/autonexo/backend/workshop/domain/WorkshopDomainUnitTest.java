package com.atg.autonexo.backend.workshop.domain;

import com.atg.autonexo.backend.shared.domain.model.valueobjects.Email;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.UserId;
import com.atg.autonexo.backend.shared.domain.model.valueobjects.WorkshopId;
import com.atg.autonexo.backend.workshop.domain.exceptions.LocationNotFoundException;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Invitation;
import com.atg.autonexo.backend.workshop.domain.model.aggregates.Workshop;
import com.atg.autonexo.backend.workshop.domain.model.entities.ServiceTemplate;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.BusinessRegistration;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.InvitationCode;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.OpeningHours;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionStatus;
import com.atg.autonexo.backend.workshop.domain.model.valueobjects.SubscriptionTier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Workshop bounded context domain layer.
 *
 * <p>Each test is fully isolated — no Spring context, no database, no mocks.
 * Only real domain objects interact, exercising invariants encoded in the model.</p>
 *
 * <p>AAA pattern is strictly applied:
 * <ul>
 *   <li>// Arrange / ES: Organizar  — build the objects</li>
 *   <li>// Act    / ES: Actuar     — call the method under test</li>
 *   <li>// Assert / ES: Confirmar  — verify the outcome</li>
 * </ul>
 * For exception scenarios, {@code assertThrows} acts as the <em>Act</em> phase
 * and returns the exception for assertion in the separate <em>Assert</em> block.</p>
 */
@Tag("unit")
@DisplayName("Workshop BC — Domain Unit Tests")
class WorkshopDomainUnitTest {

    // Shared fixture — rebuilt fresh before every test to prevent state leakage
    private Workshop workshopUnderTest;

    @BeforeEach
    void setUp() {
        workshopUnderTest = new Workshop(
                new UserId(1L), "Taller Base Test", null, null, null);
    }

    // =========================================================================
    // WORKSHOP AGGREGATE — Constructor invariants
    // =========================================================================

    // ES: Riesgo cubierto: Si Workshop permite ownerUserId nulo, cualquier taller quedaría
    //     sin propietario y sería imposible verificar permisos, enviar notificaciones o
    //     asociar suscripciones. Un taller huérfano contamina todo el sistema.
    // EN: Risk covered: If Workshop allows a null ownerUserId, any workshop would be created
    //     without an owner, making it impossible to verify permissions, send notifications,
    //     or associate subscriptions. An orphan workshop corrupts the entire system.
    @Test
    @DisplayName("Workshop — null ownerUserId on creation must throw with a descriptive message")
    void createWorkshop_WhenOwnerUserIdIsNull_ShouldThrowIllegalArgumentException() {
        // Arrange / ES: Organizar
        UserId nullOwner = null;
        String validName = "Taller San Borja";

        // Act / ES: Actuar
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new Workshop(nullOwner, validName, null, null, null));

        // Assert / ES: Confirmar
        assertFalse(thrown.getMessage().isBlank(),
                "The exception must carry a descriptive message, not be empty");
    }

    // ES: Riesgo cubierto: Un taller sin nombre sería invisible en el catálogo público.
    //     Los clientes no podrían identificarlo y el motor de búsqueda devolvería resultados
    //     vacíos. Se incluyen: null, vacío, solo espacios y solo tabulaciones.
    // EN: Risk covered: A workshop without a name would be invisible in the public catalog.
    //     Customers could not identify it and the search engine would return empty results.
    //     Covers: null, empty, whitespace-only, and tab-only strings.
    @ParameterizedTest(name = "Nombre inválido [{index}]: \"{0}\" debe rechazarse")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Workshop — null or blank name on creation must throw IllegalArgumentException")
    void createWorkshop_WhenNameIsNullOrBlank_ShouldThrowIllegalArgumentException(String invalidName) {
        // Arrange / ES: Organizar
        UserId owner = new UserId(1L);

        // Act / ES: Actuar
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new Workshop(owner, invalidName, null, null, null));

        // Assert / ES: Confirmar
        assertFalse(thrown.getMessage().isBlank(),
                "The exception must carry a descriptive message");
    }

    // ES: Riesgo cubierto: Si el constructor exitoso no inicializa correctamente el estado
    //     del taller (activo, suscripción TRIAL, sin fotos), otras partes del sistema
    //     asumirían un estado inconsistente desde el primer momento.
    // EN: Risk covered: If the successful constructor does not correctly initialize workshop
    //     state (active, TRIAL subscription, no photos), other parts of the system would
    //     assume inconsistent state from the very first moment.
    @Test
    @DisplayName("Workshop — valid creation must produce an active workshop with TRIAL/FREE subscription")
    void createWorkshop_WithValidData_ShouldCreateActiveWorkshopWithExpectedInitialState() {
        // Arrange / ES: Organizar
        UserId owner = new UserId(99L);
        String name  = "Taller San Isidro";

        // Act / ES: Actuar
        Workshop workshop = new Workshop(owner, name, "Descripción", "Legal SA", null);

        // Assert / ES: Confirmar
        assertTrue(workshop.isActive(),
                "A newly created workshop must be active by default");
        assertTrue(workshop.isOwnedBy(99L),
                "Ownership must match the owner ID provided at construction");
        assertEquals(SubscriptionStatus.TRIAL, workshop.getSubscriptionStatus(),
                "Default subscription status must be TRIAL");
        assertEquals(SubscriptionTier.FREE, workshop.getSubscriptionTier(),
                "Default subscription tier must be FREE");
        assertTrue(workshop.getPhotoUrls().isEmpty(),
                "A new workshop must have no photos");
        assertTrue(workshop.getStaffMembers().isEmpty(),
                "A new workshop must have no staff members");
    }

    // =========================================================================
    // WORKSHOP AGGREGATE — Photo management
    // =========================================================================

    // ES: Riesgo cubierto: Sin el límite de 10 fotos, un taller podría subir fotos ilimitadas,
    //     saturando el almacenamiento en Cloudinary, degradando el rendimiento del catálogo
    //     y aumentando costos operativos sin control.
    // EN: Risk covered: Without the 10-photo limit, a workshop could upload unlimited photos,
    //     saturating Cloudinary storage, degrading catalog performance, and increasing
    //     operational costs with no control.
    @Test
    @DisplayName("Workshop — adding an 11th photo must throw IllegalStateException and keep count at 10")
    void addPhoto_WhenExceedingTenPhotoLimit_ShouldThrowIllegalStateException() {
        // Arrange / ES: Organizar
        for (int i = 0; i < 10; i++) {
            workshopUnderTest.addPhoto("https://cdn.cloudinary.com/autonexo/photo" + i + ".jpg");
        }

        // Act / ES: Actuar
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> workshopUnderTest.addPhoto("https://cdn.cloudinary.com/autonexo/photo10.jpg"));

        // Assert / ES: Confirmar
        assertTrue(thrown.getMessage().contains("10"),
                "Error message must reference the limit of 10 photos");
        assertEquals(10, workshopUnderTest.getPhotoUrls().size(),
                "Photo count must remain exactly 10 — the rejected photo must not be added");
    }

    // =========================================================================
    // WORKSHOP AGGREGATE — Subscription rules
    // =========================================================================

    // ES: Riesgo cubierto: Si CANCELLED o EXPIRED siguen siendo activos, un cliente que canceló
    //     seguiría usando features de pago sin costo. Esto destruye el modelo de ingresos.
    //     Se prueba ambos estados terminales en un único test parametrizado.
    // EN: Risk covered: If CANCELLED or EXPIRED are still considered active, a customer who
    //     cancelled would keep using paid features at no cost, destroying the revenue model.
    //     Both terminal states are verified via a single parameterized test.
    @ParameterizedTest(name = "Estado terminal {0} + fecha futura debe retornar suscripción INACTIVA")
    @EnumSource(value = SubscriptionStatus.class, names = {"CANCELLED", "EXPIRED"})
    @DisplayName("Subscription — CANCELLED and EXPIRED must always be inactive regardless of expiry date")
    void isSubscriptionActive_WhenStatusIsTerminated_ShouldReturnFalse(SubscriptionStatus terminatedStatus) {
        // Arrange / ES: Organizar
        workshopUnderTest.updateSubscription(
                terminatedStatus,
                SubscriptionTier.PREMIUM,
                LocalDateTime.now().plusDays(30)); // fecha futura — no debe importar

        // Act / ES: Actuar
        boolean result = workshopUnderTest.isSubscriptionActive();

        // Assert / ES: Confirmar
        assertFalse(result,
                terminatedStatus + " subscription must not be active, even with a future expiry date");
    }

    // ES: Riesgo cubierto: Si no se verifica la expiración por fecha, un taller cuya suscripción
    //     venció hace semanas aparecería como "ACTIVE", obteniendo acceso gratuito a features
    //     premium que ya no debería tener.
    // EN: Risk covered: If date-based expiration is not checked, a workshop whose subscription
    //     expired weeks ago would still appear as "ACTIVE", gaining free access to premium
    //     features it should no longer have.
    @Test
    @DisplayName("Subscription — ACTIVE status with a past expiry date must return inactive")
    void isSubscriptionActive_WhenExpirationDateHasPassed_ShouldReturnFalse() {
        // Arrange / ES: Organizar
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        workshopUnderTest.updateSubscription(SubscriptionStatus.ACTIVE, SubscriptionTier.PREMIUM, yesterday);

        // Act / ES: Actuar
        boolean result = workshopUnderTest.isSubscriptionActive();

        // Assert / ES: Confirmar
        assertFalse(result,
                "An ACTIVE subscription with a past expiry date must be considered inactive");
    }

    // ES: Riesgo cubierto: Si el caso positivo no se verifica, una regresión podría hacer que
    //     suscripciones válidas sean tratadas como inactivas, bloqueando injustamente a todos
    //     los talleres activos que pagan por el servicio.
    // EN: Risk covered: If the positive case is not verified, a regression could cause valid
    //     subscriptions to be treated as inactive, unjustly blocking all paying active workshops.
    @Test
    @DisplayName("Subscription — ACTIVE status with no expiry date must return active")
    void isSubscriptionActive_WhenStatusIsActiveWithNoExpiry_ShouldReturnTrue() {
        // Arrange / ES: Organizar
        workshopUnderTest.updateSubscription(SubscriptionStatus.ACTIVE, SubscriptionTier.BASIC, null);

        // Act / ES: Actuar
        boolean result = workshopUnderTest.isSubscriptionActive();

        // Assert / ES: Confirmar
        assertTrue(result,
                "An ACTIVE subscription with no expiry date must be considered active");
    }

    // ES: Riesgo cubierto: Si el plan FREE otorgara features premium, el modelo SaaS colapsaría.
    //     Ningún taller pagaría por planes superiores si el gratuito los incluye.
    // EN: Risk covered: If the FREE tier granted premium features, the SaaS model would collapse.
    //     No workshop would pay for higher-tier plans if the free plan already includes them.
    @Test
    @DisplayName("Premium access — FREE tier must never grant premium features, even with ACTIVE subscription")
    void canAccessPremiumFeatures_WhenTierIsFreeAndSubscriptionIsActive_ShouldReturnFalse() {
        // Arrange / ES: Organizar
        workshopUnderTest.updateSubscription(SubscriptionStatus.ACTIVE, SubscriptionTier.FREE, null);

        // Act / ES: Actuar
        boolean result = workshopUnderTest.canAccessPremiumFeatures();

        // Assert / ES: Confirmar
        assertFalse(result,
                "FREE tier must not grant premium features under any circumstances");
    }

    // ES: Riesgo cubierto: Si BASIC o PREMIUM no dan acceso premium, el cliente que pagó es
    //     bloqueado injustamente, generando churn inmediato, solicitudes de reembolso y daño
    //     reputacional. Se prueba ambos tiers pagos en un único test parametrizado.
    // EN: Risk covered: If BASIC or PREMIUM do not grant premium access, the paying customer
    //     is unjustly blocked, generating immediate churn, refund requests, and reputational
    //     damage. Both paid tiers are verified via a single parameterized test.
    @ParameterizedTest(name = "Tier {0} con suscripción ACTIVE debe conceder acceso premium")
    @EnumSource(value = SubscriptionTier.class, names = {"BASIC", "PREMIUM"})
    @DisplayName("Premium access — BASIC and PREMIUM tiers with ACTIVE subscription must grant premium features")
    void canAccessPremiumFeatures_WhenTierIsBasicOrPremiumAndActive_ShouldReturnTrue(SubscriptionTier paidTier) {
        // Arrange / ES: Organizar
        workshopUnderTest.updateSubscription(SubscriptionStatus.ACTIVE, paidTier, null);

        // Act / ES: Actuar
        boolean result = workshopUnderTest.canAccessPremiumFeatures();

        // Assert / ES: Confirmar
        assertTrue(result,
                "Tier " + paidTier + " with an ACTIVE subscription must grant access to premium features");
    }

    // =========================================================================
    // WORKSHOP AGGREGATE — Location management
    // =========================================================================

    // ES: Riesgo cubierto: Si eliminar una ubicación inexistente falla silenciosamente,
    //     las operaciones posteriores asumirían que la ubicación fue borrada, generando
    //     inconsistencias en horarios, disponibilidad de staff y gestión de citas.
    // EN: Risk covered: If removing a non-existent location fails silently, subsequent
    //     operations would assume the location was deleted, generating inconsistencies
    //     in schedules, staff availability, and appointment management.
    @Test
    @DisplayName("Location — removing a non-existent location must throw LocationNotFoundException")
    void removeLocation_WhenLocationDoesNotExist_ShouldThrowLocationNotFoundException() {
        // Arrange / ES: Organizar
        Long nonExistentLocationId = 9999L;

        // Act / ES: Actuar
        LocationNotFoundException thrown = assertThrows(
                LocationNotFoundException.class,
                () -> workshopUnderTest.removeLocation(nonExistentLocationId));

        // Assert / ES: Confirmar
        assertNotNull(thrown,
                "A LocationNotFoundException must be thrown for an unknown location ID");
    }

    // =========================================================================
    // INVITATION AGGREGATE
    // =========================================================================

    // ES: Riesgo cubierto: Si una invitación puede usarse dos veces, un código filtrado
    //     permitiría agregar empleados no autorizados al taller indefinidamente,
    //     comprometiendo la seguridad y la integridad del staff registrado.
    // EN: Risk covered: If an invitation can be used twice, a leaked code would allow
    //     adding unauthorized employees to the workshop indefinitely, compromising
    //     security and the integrity of the registered staff.
    @Test
    @DisplayName("Invitation — marking an already-used invitation as used must throw IllegalStateException")
    void markAsUsed_WhenInvitationAlreadyUsed_ShouldThrowIllegalStateException() {
        // Arrange / ES: Organizar
        Invitation invitation = buildValidFutureInvitation("ABCD1234", "staff@taller.com", 1L);
        invitation.markAsUsed(); // primera aceptación legítima

        // Act / ES: Actuar
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                invitation::markAsUsed); // segunda aceptación: debe fallar

        // Assert / ES: Confirmar
        String msg = thrown.getMessage().toLowerCase();
        assertTrue(msg.contains("used") || msg.contains("already"),
                "Exception message must indicate the invitation was already used. Got: " + thrown.getMessage());
    }

    // ES: Riesgo cubierto: Si una invitación expirada puede aceptarse, cualquier código antiguo
    //     filtrado o robado podría usarse para infiltrarse en un taller sin límite de tiempo.
    //     La expiración es la barrera de seguridad temporal fundamental.
    // EN: Risk covered: If an expired invitation can be accepted, any old leaked or stolen code
    //     could be used to infiltrate a workshop with no time limit. Expiration is the
    //     fundamental temporal security barrier.
    @Test
    @DisplayName("Invitation — marking an expired invitation as used must throw IllegalStateException")
    void markAsUsed_WhenInvitationIsExpired_ShouldThrowIllegalStateException() {
        // Arrange / ES: Organizar
        Invitation expiredInvitation = new Invitation(
                new InvitationCode("ABCD1234"),
                LocalDateTime.now().minusDays(3), // venció hace 3 días
                new Email("staff@taller.com"),
                new WorkshopId(1L),
                null);

        // Act / ES: Actuar
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                expiredInvitation::markAsUsed);

        // Assert / ES: Confirmar
        assertTrue(thrown.getMessage().toLowerCase().contains("expir"),
                "Exception message must indicate the invitation has expired. Got: " + thrown.getMessage());
    }

    // ES: Riesgo cubierto: Si la comparación de email es case-sensitive, un empleado que escriba
    //     su email en minúsculas no podrá aceptar una invitación enviada con mayúsculas.
    //     Esto genera tickets de soporte innecesarios y bloquea el onboarding de staff.
    // EN: Risk covered: If email comparison is case-sensitive, an employee who types their email
    //     in lowercase cannot accept an invitation sent with uppercase, generating unnecessary
    //     support tickets and blocking staff onboarding.
    @Test
    @DisplayName("Invitation — email matching must be case-insensitive for all casing variants")
    void isForEmail_WhenEmailDiffersByCase_ShouldMatchCaseInsensitively() {
        // Arrange / ES: Organizar
        Invitation invitation = new Invitation(
                new InvitationCode("ABCD1234"),
                LocalDateTime.now().plusDays(7),
                new Email("STAFF@TALLER.COM"),
                new WorkshopId(1L),
                null);

        // Act / ES: Actuar
        boolean matchesLowerCase  = invitation.isForEmail("staff@taller.com");
        boolean matchesMixedCase  = invitation.isForEmail("Staff@Taller.Com");
        boolean rejectsDifferent  = invitation.isForEmail("otro@taller.com");

        // Assert / ES: Confirmar
        assertTrue(matchesLowerCase,  "Lowercase email must match the stored uppercase email");
        assertTrue(matchesMixedCase,  "Mixed-case email must match the stored uppercase email");
        assertFalse(rejectsDifferent, "A completely different email address must not match");
    }

    // =========================================================================
    // VALUE OBJECTS
    // =========================================================================

    // ES: Riesgo cubierto: Con duración 0 o negativa, los algoritmos de disponibilidad
    //     de citas generarían slots inválidos o entrarían en loops infinitos, rompiendo
    //     el calendario de todo el taller. Se cubren: 0, -1, -30 y el mínimo entero.
    // EN: Risk covered: With 0 or negative duration, appointment availability algorithms
    //     would generate invalid slots or enter infinite loops, breaking the workshop's
    //     entire calendar. Covers: 0, -1, -30, and minimum integer.
    @ParameterizedTest(name = "Duración inválida: {0} min debe ser rechazada")
    @ValueSource(ints = {0, -1, -30, Integer.MIN_VALUE})
    @DisplayName("ServiceTemplate — non-positive duration must throw IllegalArgumentException")
    void createServiceTemplate_WhenDurationIsNotPositive_ShouldThrowIllegalArgumentException(int invalidDuration) {
        // Arrange / ES: Organizar
        String validName = "Cambio de Aceite";

        // Act / ES: Actuar
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceTemplate(null, null, validName, null, invalidDuration, null));

        // Assert / ES: Confirmar
        assertFalse(thrown.getMessage().isBlank(),
                "Exception must carry a descriptive message. Got: " + thrown.getMessage());
    }

    // ES: Riesgo cubierto: Un RUC inválido impediría la verificación fiscal del taller,
    //     exponiendo la plataforma a fraudes con empresas fantasma. En Perú el RUC tiene
    //     exactamente 11 dígitos numéricos. Se cubren: null, vacío, corto, largo y con letras.
    // EN: Risk covered: An invalid RUC would prevent tax verification of the workshop,
    //     exposing the platform to fraud with shell companies. In Peru, RUC has exactly
    //     11 numeric digits. Covers: null, empty, too short, too long, and with letters.
    @ParameterizedTest(name = "RUC inválido [{index}]: \"{0}\" debe ser rechazado")
    @NullSource
    @ValueSource(strings = {"12345", "123456789012", "ABCDE678901", ""})
    @DisplayName("BusinessRegistration — invalid RUC must throw IllegalArgumentException")
    void createBusinessRegistration_WhenRucIsInvalid_ShouldThrowIllegalArgumentException(String invalidRuc) {
        // Arrange / ES: Organizar
        // (invalidRuc es inyectado por @ParameterizedTest)

        // Act / ES: Actuar
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> BusinessRegistration.unverified(invalidRuc));

        // Assert / ES: Confirmar
        assertFalse(thrown.getMessage().isBlank(),
                "Exception must carry a descriptive message for RUC: " + invalidRuc);
    }

    // ES: Riesgo cubierto: Si se permite opensAt > closesAt, el sistema mostrará talleres
    //     como "abiertos" en horarios lógicamente imposibles, provocando citas en horarios
    //     en los que el taller está cerrado y generando no-shows masivos con clientes frustrados.
    // EN: Risk covered: If opensAt > closesAt is allowed, the system will show workshops as
    //     "open" during logically impossible hours, causing appointments to be booked when
    //     the workshop is actually closed, leading to mass no-shows and frustrated customers.
    @Test
    @DisplayName("OpeningHours — opening time AFTER closing time must throw IllegalArgumentException")
    void createOpeningHours_WhenOpeningTimeIsAfterClosingTime_ShouldThrowIllegalArgumentException() {
        // Arrange / ES: Organizar
        LocalTime openingTime = LocalTime.of(18, 0); // tarde — inversión intencional
        LocalTime closingTime = LocalTime.of(8,  0); // mañana

        // Act / ES: Actuar
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningHours(DayOfWeek.MONDAY, openingTime, closingTime, false));

        // Assert / ES: Confirmar
        assertFalse(thrown.getMessage().isBlank(),
                "Exception must carry a descriptive message for reversed times");
    }

    // ES: Riesgo cubierto: Un horario de 0 minutos (apertura == cierre) es tan inválido como
    //     uno invertido. Permitirlo crearía slots de duración cero en el calendario, haciendo
    //     que el sistema acepte citas para un taller que no tiene tiempo de atención real.
    // EN: Risk covered: A zero-minute schedule (open == close) is as invalid as a reversed one.
    //     Allowing it would create zero-duration slots in the calendar, causing the system
    //     to accept appointments for a workshop with no actual available time.
    @Test
    @DisplayName("OpeningHours — opening time EQUAL TO closing time must throw IllegalArgumentException")
    void createOpeningHours_WhenOpeningTimeEqualsClosingTime_ShouldThrowIllegalArgumentException() {
        // Arrange / ES: Organizar
        LocalTime sameTime = LocalTime.of(9, 0);

        // Act / ES: Actuar
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> new OpeningHours(DayOfWeek.MONDAY, sameTime, sameTime, false));

        // Assert / ES: Confirmar
        assertFalse(thrown.getMessage().isBlank(),
                "Exception must carry a descriptive message for equal open/close times");
    }

    // =========================================================================
    // HELPER
    // =========================================================================

    private Invitation buildValidFutureInvitation(String code, String email, Long workshopId) {
        return new Invitation(
                new InvitationCode(code),
                LocalDateTime.now().plusDays(7),
                new Email(email),
                new WorkshopId(workshopId),
                null);
    }
}
