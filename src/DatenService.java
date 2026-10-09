import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Der DatenService ist für das dauerhafte Speichern (Persistenz) und
 * Laden von Benutzerdaten auf der Festplatte zuständig.
 */
public class DatenService {

    private final String dateiPfad;

    /**
     * Erstellt einen neuen DatenService und legt fest, wie die Speicherdatei heissen soll.
     *
     * @param dateiPfad Der Name oder Pfad der Datei (z.B. "benutzer.txt").
     */
    public DatenService(String dateiPfad) {
        this.dateiPfad = dateiPfad;
    }

    /**
     * Speichert einen Benutzer in der Textdatei. Die Datenfelder werden
     * durch ein Trennzeichen (Semikolon) separiert in einer Zeile abgelegt.
     *
     * @param user Der zu speichernde Benutzer.
     * @return true, wenn das Speichern erfolgreich war, andernfalls false.
     */
    public boolean speichereBenutzer(Benutzer user) {
        if (user == null) {
            return false;
        }

        // Sicherheitsschranke gegen doppelte Namen
        if (existiertBenutzername(user.getBenutzername())) {
            System.out.println("Registrierungsfehler: Der Benutzername '" + user.getBenutzername() + "' ist bereits vergeben!");
            return false; // Abbruch, nicht speichern!
        }

        // Wir bauen eine Zeile im CSV-Format: Wert;Wert;Wert
        String datenZeile = user.getBenutzername() + ";"
                + user.getVerschluesseltesPasswort() + ";"
                + user.getSalt() + ";"
                + user.getEmail() + ";"
                + user.getVollerName(); // Vorname/Nachname stecken hier drin

        // FileWriter mit 'true' sorgt dafür, dass neue Benutzer unten ANGEHÄNGT werden
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dateiPfad, true))) {
            writer.write(datenZeile);
            writer.newLine(); // Zeilenumbruch für den nächsten Benutzer
            return true;
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sucht in der Textdatei nach einem Benutzernamen und prüft, ob das eingegebene
     * Passwort mit dem dort gespeicherten Hash (unter Verwendung des gespeicherten Salts) übereinstimmt.
     *
     * @param gesuchterName      Der eingegebene Benutzername beim Login.
     * @param eingegebenesPW     Das eingegebene Klartext-Passwort beim Login.
     * @param security           Der Sicherheitsdienst zur Hash-Berechnung.
     * @return true, wenn der Benutzer existiert und das Passwort stimmt, andernfalls false.
     */
    public boolean ladeUndPruefeLogin(String gesuchterName, String eingegebenesPW, SicherheitsService security) {
        File datei = new File(dateiPfad);
        if (!datei.exists()) {
            return false; // Keine Datei da = noch kein Benutzer registriert
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(datei))) {
            String zeile;
            // Wir lesen die Datei Zeile für Zeile durch
            while ((zeile = reader.readLine()) != null) {
                // Zeile anhand des Semikolons in ein Array aufteilen
                String[] teile = zeile.split(";");

                if (teile.length >= 4) {
                    String dateiBenutzername = teile[0];
                    String dateiHash = teile[1];
                    String dateiSalt = teile[2];

                    // Wenn der Benutzername übereinstimmt, prüfen wir das Passwort
                    if (dateiBenutzername.equalsIgnoreCase(gesuchterName)) {
                        // Wir berechnen den Hash des eingegebenen Passworts mit dem Salz aus der Datei
                        String neuBerechneterHash = security.hashPasswort(eingegebenesPW, dateiSalt);
                        // Wenn der berechnete Hash dem gespeicherten entspricht -> Login erfolgreich!
                        return dateiHash.equals(neuBerechneterHash);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }

        return false; // Benutzer nicht gefunden oder Passwort falsch
    }

    /**
     * Überprüft, ob ein bestimmter Benutzername bereits in der Systemdatei existiert.
     *
     * @param benutzername Der zu prüfende Benutzername.
     * @return true, wenn der Name bereits vergeben ist, andernfalls false.
     */
    public boolean existiertBenutzername(String benutzername) {
        File datei = new File(dateiPfad);
        if (!datei.exists()) {
            return false; // Keine Datei da = Name kann noch nicht existieren
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(datei))) {
            String zeile;
            while ((zeile = reader.readLine()) != null) {
                String[] teile = zeile.split(";");
                if (teile.length > 0) {
                    String gespeicherterName = teile[0]; // Der Benutzername steht ganz vorne
                    if (gespeicherterName.equalsIgnoreCase(benutzername)) {
                        return true; // Treffer! Name existiert bereits
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Prüfen des Benutzernamens: " + e.getMessage());
        }

        return false; // Name wurde nicht gefunden und ist frei
    }

}
