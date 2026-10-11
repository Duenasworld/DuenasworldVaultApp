import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BenutzerTest {

    private SicherheitsService security;
    private Benutzer testUser;

    /**
     * Vor jedem einzelnen Testlauf erstellen wir einen frischen Sicherheitsdienst
     * und einen frischen Test-Benutzer.
     */
    @BeforeEach
    void setUp() {
        security = new SicherheitsService();
        // Wir registrieren einen Standardbenutzer für die Tests
        testUser = new Benutzer(
                "duenasworld_vault_test",
                "Geheim123!",
                "test@example.com",
                "Duenas",
                "World",
                "Meine Oma ist die Beste!",
                security
        );
    }

    // ==========================================
    // TESTS FÜR: getVollerName()
    // ==========================================

    @Test
    void testGetVollerNameLiefertKorrekteFormatierung() {
        assertEquals("Duenas World", testUser.getVollerName());
    }

    @Test
    void testGetVollerNameWennNamenOptionalNullSind() {
        // Wir testen den zweiten, überladenen Konstruktor ohne Vor-/Nachnamen
        Benutzer minimalerUser = new Benutzer("anonymous", "SicheresPW2026!", "anon@test.ch", "Meine Oma ist die Beste!", security);
        assertEquals("Kein Name hinterlegt", minimalerUser.getVollerName());
    }

    // ==========================================
    // TESTS FÜR: pruefePasswort()
    // ==========================================

    @Test
    void testPruefePasswortMitKorrektemUndFalschemPW() {
        assertTrue(testUser.pruefePasswort("Geheim123!", security));
        assertFalse(testUser.pruefePasswort("FalschesPasswort", security));
    }

    // ==========================================
    // TESTS FÜR: aenderePasswort()
    // ==========================================

    @Test
    void testAenderePasswortErfolgreich() {
        String altesSalt = testUser.getSaltPasswort();

        boolean erfolg = testUser.aenderePasswort("Geheim123!", "NeuesSafePW2026!", security);
        assertTrue(erfolg, "Das Passwort hätte erfolgreich geändert werden müssen.");
        assertNotEquals(altesSalt, testUser.getSaltPasswort(), "Das Salz hätte neu generiert werden müssen.");
        assertTrue(testUser.pruefePasswort("NeuesSafePW2026!", security));
    }

    @Test
    void testAenderePasswortSchlaegtFehlWennAltesPWInkorrekt() {
        boolean erfolg = testUser.aenderePasswort("FalschesAltesPW", "NeuesSafePW2026!", security);
        assertFalse(erfolg, "Die Änderung hätte wegen falschem alten Passwort blockiert werden müssen.");
    }

    @Test
    void testAenderePasswortSchlaegtFehlWennNeuesGleichAltemPW() {
        boolean erfolg = testUser.aenderePasswort("Geheim123!", "Geheim123!", security);
        assertFalse(erfolg, "Die Änderung hätte blockiert werden müssen, da neues PW = altes PW.");
    }

    @Test
    void testAenderePasswortSchlaegtFehlWennNeuesPWUnsicher() {
        boolean erfolg = testUser.aenderePasswort("Geheim123!", "kurz", security);
        assertFalse(erfolg, "Die Änderung hätte blockiert werden müssen, da das neue PW zu kurz ist.");
    }

    // =====================
    // TESTS FÜR DIE GETTER
    // =====================

    @Test
    void testGetEmailLiefertKorrekteAdresse() {
        // Prüft, ob der Getter den korrekten E-Mail-String zurückgibt
        assertEquals("test@example.com", testUser.getEmail());
    }

    @Test
    void testGetVerschluesseltesPasswortUndGetSaltPasswort() {
        // Verifiziert, dass Passwort-Hash und Salz existieren und gefüllt sind
        assertNotNull(testUser.getVerschluesseltesPasswort(), "Passworthash darf nicht null sein.");
        assertFalse(testUser.getVerschluesseltesPasswort().isEmpty(), "Passworthash darf nicht leer sein.");

        assertNotNull(testUser.getSaltPasswort(), "Salz darf nicht null sein.");
        assertFalse(testUser.getSaltPasswort().isEmpty(), "Salz darf nicht leer sein.");
    }

    @Test
    void testGetBenutzernameLiefertKorrekterBenutzername() {
        // Prüft, ob der Getter den korrekten Benutzernamen-String zurückgibt
        assertEquals("duenasworld_vault_test", testUser.getBenutzername());
    }

    @Test
    void testGetVerschluesseltenRecoveryKeyUndGetSaltRecoveryKey() {
        // Verifiziert, dass Passwort-Hash und Salz existieren und gefüllt sind
        assertNotNull(testUser.getVerschluesseltenRecoveryKey(), "Passworthash darf nicht null sein.");
        assertFalse(testUser.getVerschluesseltenRecoveryKey().isEmpty(), "Passworthash darf nicht leer sein.");

        assertNotNull(testUser.getSaltRecoveryKey(), "Salz darf nicht null sein.");
        assertFalse(testUser.getSaltRecoveryKey().isEmpty(), "Salz darf nicht leer sein.");
    }

}
