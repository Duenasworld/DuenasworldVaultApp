import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SicherheitsServiceTest {

    private SicherheitsService security;

    /**
     * Wird automatisch VOR jedem einzelnen @Test ausgeführt.
     * So starten wir bei jedem Testlauf mit einem frischen, sauberen Objekt.
     */
    @BeforeEach
    void setUp() {
        security = new SicherheitsService();
    }

    // ==========================================
    // TESTS FÜR: istPasswortSicher()
    // ==========================================

    @Test
    void testPasswortErfuelltAlleKriterien() {
        // Ein perfektes Passwort (lang, Großbuchstabe, Sonderzeichen) muss true liefern
        assertTrue(security.istPasswortSicher("SafePassword2026!"),
                "Das Passwort hätte als sicher eingestuft werden müssen.");
    }

    @Test
    void testPasswortZuKurzLiefertFalse() {
        // Zu kurz (unter 8 Zeichen) trotz Großbuchstabe und Sonderzeichen
        assertFalse(security.istPasswortSicher("Ab1!"),
                "Zu kurze Passwörter dürfen nicht erlaubt sein.");
    }

    @Test
    void testPasswortOhneSonderzeichenLiefertFalse() {
        // Lang und Großbuchstabe vorhanden, aber kein Sonderzeichen
        assertFalse(security.istPasswortSicher("SicheresPasswortOhneSonderzeichen123"),
                "Passwörter ohne Sonderzeichen dürfen nicht erlaubt sein.");
    }

    @Test
    void testPasswortOhneGrossbuchstabeLiefertFalse() {
        // Lang und Sonderzeichen vorhanden, aber alles klein geschrieben
        assertFalse(security.istPasswortSicher("geheimnis123!"),
                "Passwörter ohne Großbuchstaben dürfen nicht erlaubt sein.");
    }

    @Test
    void testPasswortAlsNullLiefertFalse() {
        // Wenn gar kein Passwort übergeben wird (null), muss die Methode sicher false liefern
        assertFalse(security.istPasswortSicher(null),
                "Ein Null-Wert darf nicht als sicheres Passwort akzeptiert werden.");
    }

    // ==========================================
    // TESTS FÜR: generiereZufallssalt()
    // ==========================================

    @Test
    void testGeneriereZufallssaltErzeugtEinzigartigeWerte() {
        // Zwei nacheinander generierte Salze dürfen niemals identisch sein!
        String salt1 = security.generiereZufallssalt();
        String salt2 = security.generiereZufallssalt();

        assertNotEquals(salt1, salt2, "Zwei generierte Salze dürfen nicht identisch sein.");
        assertNotNull(salt1, "Das generierte Salz darf nicht null sein.");
    }

    // ==========================================
    // TESTS FÜR: hashPasswort()
    // ==========================================

    @Test
    void testHashPasswortLiefertGleichenHashFuerGleicheEingabe() {
        String passwort = "MeinPasswort123!";
        String salt = "abc123salt";

        // Da Hashing deterministisch ist, muss bei gleicher Eingabe exakt derselbe Hash entstehen
        String hash1 = security.hashData(passwort, salt);
        String hash2 = security.hashData(passwort, salt);

        assertEquals(hash1, hash2, "Gleiches Passwort und Salz müssen denselben Hash erzeugen.");
    }

    @Test
    void testHashPasswortAendertSichBeiAnderemSalt() {
        String passwort = "MeinPasswort123!";

        // Das gleiche Passwort mit zwei verschiedenen Salzen muss völlig andere Hashes liefern
        String hash1 = security.hashData(passwort, "saltA");
        String hash2 = security.hashData(passwort, "saltB");

        assertNotEquals(hash1, hash2, "Unterschiedliche Salze müssen unterschiedliche Hashes erzeugen.");
    }

    @Test
    void testHashPasswortMitLeeremSaltWirftException() {
        // assertThrows erwartet: 1. Die genaue Exception-Klasse, 2. Den Code als Lambda-Ausdruck () -> ...
        IllegalArgumentException geworfeneException = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    security.hashData("MeinPasswort123!", ""); // Hier übergeben wir ein leeres Salz
                },
                "Es hätte eine IllegalArgumentException geworfen werden müssen."
        );

        // Optional: Wir können sogar prüfen, ob der Text in der Fehlermeldung stimmt!
        assertEquals("Das Salz darf nicht null oder leer sein!", geworfeneException.getMessage());
    }

}
