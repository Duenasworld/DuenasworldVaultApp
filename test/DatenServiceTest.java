import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class DatenServiceTest {

    private SicherheitsService security;
    private DatenService datenService;
    private File temporaereDatei;
    private Benutzer testUser;

    /**
     * Wird VOR jedem einzelnen Test aufgerufen.
     * Erstellt eine leere, temporäre Datei und frische Service-Instanzen.
     */
    @BeforeEach
    void setUp() throws IOException {
        security = new SicherheitsService();

        // Erzeugt eine flüchtige Datei im Temp-Ordner des Betriebssystems
        temporaereDatei = File.createTempFile("tresor_test_", ".txt");

        // Wir übergeben dem DatenService den Pfad der temporären Datei
        datenService = new DatenService(temporaereDatei.getAbsolutePath());

        // Standard-Testbenutzer vorbereiten
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

    /**
     * Wird NACH jedem einzelnen Test aufgerufen.
     * Löscht die temporäre Datei von der Festplatte, damit kein Müll zurückbleibt.
     */
    @AfterEach
    void tearDown() {
        if (temporaereDatei != null && temporaereDatei.exists()) {
            temporaereDatei.delete();
        }
    }

    // ==========================================
    // TESTS FÜR: speichereBenutzer() & existiertBenutzername()
    // ==========================================

    @Test
    void testSpeichereBenutzerErfolgreich() {
        // Ein neuer Benutzername darf vor dem Speichern noch nicht existieren
        assertFalse(datenService.existiertBenutzername("duenasworld_vault_test"));

        // Das Speichern muss erfolgreich sein (true)
        boolean gespeichert = datenService.speichereBenutzer(testUser);
        assertTrue(gespeichert, "Der Benutzer hätte erfolgreich gespeichert werden müssen.");

        // JETZT muss der Benutzername als vergeben markiert sein
        assertTrue(datenService.existiertBenutzername("duenasworld_vault_test"));
    }

    @Test
    void testSpeichereDoppeltenBenutzernameWirdBlockiert() {
        // Erste Registrierung klappt
        assertTrue(datenService.speichereBenutzer(testUser));

        // Zweiter Registrierungsversuch mit exakt demselben Namen (aber z.B. anderer E-Mail)
        Benutzer doppelterUser = new Benutzer("duenasworld_vault_test",
                "AnderesPW123!",
                "andere@mail.ch",
                "andere Sicherheitsabfrage",
                security);

        // Das Speichern muss vom DatenService abgewiesen werden (false)
        boolean gespeichert = datenService.speichereBenutzer(doppelterUser);
        assertFalse(gespeichert, "Doppelte Registrierungen dürfen nicht auf der Festplatte landen.");
    }

    @Test
    void testSpeichereBenutzerNullLiefertFalse() {
        // Wenn null übergeben wird, darf nichts passieren und false zurückkommen
        assertFalse(datenService.speichereBenutzer(null));
    }

    // ==========================================
    // TESTS FÜR: ladeUndPruefeLogin()
    // ==========================================

    @Test
    void testLadeUndPruefeLoginErfolgreich() {
        // Erst Benutzer speichern
        datenService.speichereBenutzer(testUser);

        // Login mit den exakt richtigen Daten prüfen (Soll: true)
        boolean loginErfolgreich = datenService.ladeUndPruefeLogin("duenasworld_vault_test", "Geheim123!", security);
        assertTrue(loginErfolgreich, "Der Login mit korrekten Daten hätte funktionieren müssen.");
    }

    @Test
    void testLadeUndPruefeLoginMitFalschemPasswortLiefertFalse() {
        datenService.speichereBenutzer(testUser);

        // Richtiger Name, aber falsches Passwort (Soll: false)
        boolean loginErfolgreich = datenService.ladeUndPruefeLogin("duenasworld_vault_test", "FalschesPW", security);
        assertFalse(loginErfolgreich, "Login mit falschem Passwort darf nicht erfolgreich sein.");
    }

    @Test
    void testLadeUndPruefeLoginMitFalschemNamenLiefertFalse() {
        datenService.speichereBenutzer(testUser);

        // Falscher Name, richtiges Passwort (Soll: false)
        boolean loginErfolgreich = datenService.ladeUndPruefeLogin("unbekannter_user", "Geheim123!", security);
        assertFalse(loginErfolgreich, "Ein unbekannter Benutzer darf sich nicht einloggen können.");
    }

    @Test
    void testLoginWennNochKeineDateiExistiert() {
        // Wir löschen die Datei absichtlich vor dem Test
        temporaereDatei.delete();

        // Wenn noch überhaupt keine Datei auf der Platte existiert, muss der Login direkt false liefern
        boolean loginErfolgreich = datenService.ladeUndPruefeLogin("irgendwer", "pw", security);
        assertFalse(loginErfolgreich, "Login ohne bestehende Datenbankdatei muss fehlschlagen.");
    }

    @Test
    void testLadeUndPruefeLoginMitBeschaedigterZeileLiefertFalse() throws IOException {
        // Wir manipulieren die temporäre Datei und schreiben eine ungültige Zeile hinein
        java.nio.file.Files.writeString(
                java.nio.file.Paths.get(temporaereDatei.getAbsolutePath()),
                "kaputter_user;nurEinTeil" // Zu wenige Elemente (unter 4 Semikolons)
        );

        // Die Methode muss die Zeile sicher überspringen und false liefern
        boolean loginErfolgreich = datenService.ladeUndPruefeLogin("kaputter_user", "pw", security);
        assertFalse(loginErfolgreich, "Ein Login mit einer beschädigten Zeile darf nicht erfolgreich sein.");
    }

    @Test
    void testSpeichereBenutzerSchlaegtFehlBeiSchreibgeschuetzterDatei() {
        // Wir machen die Datei für das System absolut unbeschreibbar (schreibgeschützt)
        temporaereDatei.setReadOnly();

        // Das Speichern muss nun fehlschlagen und kontrolliert false zurückgeben
        boolean gespeichert = datenService.speichereBenutzer(testUser);
        assertFalse(gespeichert, "Bei einem Schreibfehler muss die Methode false zurückgeben.");

        // Datei wieder beschreibbar machen, damit der tearDown() aufräumen kann
        temporaereDatei.setWritable(true);
    }

    // ==========================================
    // TESTS FÜR: ladeUndPruefePasswortRecovery()
    // ==========================================

    @Test
    void ladeUndPruefePasswortRecoveryErfolgreich() {
        // Erst Benutzer speichern
        datenService.speichereBenutzer(testUser);

        // Login mit den exakt richtigen Daten prüfen (Soll: true)
        boolean loginErfolgreich = datenService.ladeUndPruefePasswortRecovery("duenasworld_vault_test", "test@example.com", "Meine Oma ist die Beste!", security);
        assertTrue(loginErfolgreich, "Der Login mit korrekten Daten hätte funktionieren müssen.");
    }

    /*
            testUser = new Benutzer(
                "duenasworld_vault_test",
                "Geheim123!",
                "test@example.com",
                "Duenas",
                "World",
                "Meine Oma ist die Beste!",
                security
     */

    @Test
    void ladeUndPruefePasswortRecoveryMitFalschemRecoveryKeyLiefertFalse() {
        datenService.speichereBenutzer(testUser);

        // Richtiger Name, aber falsches Passwort (Soll: false)
        boolean loginErfolgreich = datenService.ladeUndPruefePasswortRecovery("duenasworld_vault_test", "test@example.com", "Mein Opa ist der Beste!", security);
        assertFalse(loginErfolgreich, "Ein falscher RecoveryKey darf nicht dazu führen, dass ein Passwort neu gesetzt werden kann.");
    }

    @Test
    void ladeUndPruefePasswortRecoveryMitFalschemNamenLiefertFalse() {
        datenService.speichereBenutzer(testUser);

        // Falscher Name, richtiges Passwort (Soll: false)
        boolean loginErfolgreich = datenService.ladeUndPruefePasswortRecovery("unbekannter_user", "test@example.com", "Meine Oma ist die Beste!", security);
        assertFalse(loginErfolgreich, "Ein unbekannter Benutzer darf nicht ein Passwort neu setzten können.");
    }

    @Test
    void ladeUndPruefePasswortRecoveryMitFalscherEmailLiefertFalse() {
        datenService.speichereBenutzer(testUser);

        // Falscher Name, richtiges Passwort (Soll: false)
        boolean loginErfolgreich = datenService.ladeUndPruefePasswortRecovery("unbekannter_user", "falsche@email.com", "Meine Oma ist die Beste!", security);
        assertFalse(loginErfolgreich, "Eine falsch erfasste Email darf nicht dazu führen, dass ein Passwort neu gesetzt werden kann.");
    }
}
