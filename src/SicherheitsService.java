import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Der SicherheitsService stellt zentrale kryptografische Funktionen bereit.
 * Er kümmert sich um das Erzeugen von Salzen, das Hashen von Passwörtern
 * sowie um die Überprüfung von Sicherheitsrichtlinien.
 */
public class SicherheitsService {

    /**
     * Generiert ein kryptografisch sicheres, zufälliges Salz.
     * Das Salz wird als Hexadezimal-Zeichenkette zurückgegeben.
     *
     * @return Ein eindeutiger, zufälliger Salt-String (32 Zeichen lang).
     */
    public String generiereZufallssalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return HexFormat.of().formatHex(saltBytes);
    }

    /**
     * Verschlüsselt ein Klartext-Passwort kombiniert mit einem Salz.
     *
     * @param klartext Das zu verschlüsselnde Passwort.
     * @param salt     Das Salz des Benutzers (darf nicht leer sein).
     * @return Der berechnete SHA-256-Hash.
     * @throws IllegalArgumentException Wenn das Salz null oder komplett leer ist.
     */
    public String hashPasswort(String klartext, String salt) {
        // Wir prüfen den Fehlerfall aktiv ab und werfen eine Exception
        if (salt == null || salt.trim().isEmpty()) {
            throw new IllegalArgumentException("Das Salz darf nicht null oder leer sein!");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String kombination = klartext + salt;
            byte[] hashBytes = digest.digest(kombination.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Kritischer Fehler bei SHA-256", e);
        }
    }


    /**
     * Überprüft, ob ein eingegebenes Klartext-Passwort den Sicherheitsanforderungen entspricht.
     * Kriterien: Mindestens 8 Zeichen, mindestens ein Grossbuchstabe und ein Sonderzeichen.
     *
     * @param klartextPasswort Das zu prüfende Klartext-Passwort.
     * @return true, wenn alle Kriterien erfüllt sind, andernfalls false.
     */
    public boolean istPasswortSicher(String klartextPasswort) {
        if (klartextPasswort == null || klartextPasswort.length() < 8) {
            return false;
        }

        // Prüfen auf mindestens einen Großbuchstaben
        boolean hatGrossbuchstabe = klartextPasswort.matches(".*[A-Z].*");

        // Prüfen auf mindestens ein Sonderzeichen (alles, was kein Buchstabe oder Zahl ist)
        boolean hatSonderzeichen = klartextPasswort.matches(".*[^a-zA-Z0-9].*");

        return hatGrossbuchstabe && hatSonderzeichen;
    }
}



