/**
 * Das Datenmodell repräsentiert einen registrierten Benutzer im System.
 * Diese Klasse speichert die Benutzerdaten und delegiert sicherheitsrelevante
 * Operationen an den {@link SicherheitsService}.
 */
public class Benutzer {
    private String benutzername;
    private String passwortHash;
    private String salt;
    private String email;
    private String vorname;
    private String nachname;

    /**
     * Erstellt einen neuen Benutzer und berechnet direkt den initialen Passwort-Hash.
     *
     * @param benutzername      Eindeutiger Anmeldename des Benutzers.
     * @param klartextPasswort  Das vom Benutzer gewählte Passwort im Klartext.
     * @param email             Die E-Mail-Adresse des Benutzers.
     * @param vorname           Der Vorname des Benutzers.
     * @param nachname          Der Nachname des Benutzers.
     * @param security          Die Instanz des Sicherheitsdienstes für die Verschlüsselung.
     */
    public Benutzer(String benutzername, String klartextPasswort, String email, String vorname, String nachname, SicherheitsService security) {
        this.benutzername = benutzername;
        this.email = email;
        this.vorname = vorname;
        this.nachname = nachname;

        initialisereSaltundHashPasswort(klartextPasswort, security);
    }

    /**
     * Erstellt einen neuen Benutzer mit minimalen Daten (Vor- und Nachname sind optional).
     * Nutzt Constructor Chaining, um den Hauptkonstruktor aufzurufen.
     */
    public Benutzer(String benutzername, String klartextPasswort, String email, SicherheitsService security) {
        // Leitet die Anfrage an den oberen Konstruktor weiter und übergibt null für die Namen
        this(benutzername, klartextPasswort, email, null, null, security);
    }


    /**
     * Vergleicht ein eingegebenes Passwort mit dem im Benutzer hinterlegten Passwort-Hash.
     *
     * @param eingegebenesPasswort Das beim Login-Versuch eingegebene Klartext-Passwort.
     * @param security             Die Instanz des Sicherheitsdienstes.
     * @return true, wenn das Passwort korrekt ist, andernfalls false.
     */
    public boolean pruefePasswort(String eingegebenesPasswort, SicherheitsService security) {
        String neuBerechneterHash = security.hashPasswort(eingegebenesPasswort, this.salt);
        return this.passwortHash.equals(neuBerechneterHash);
    }

    /**
     * Ändert das Passwort des Benutzers. Validiert das alte Passwort, stellt sicher,
     * dass das neue Passwort nicht dem alten entspricht und prüft die Passwort-Richtlinien.
     *
     * @param altesPW  Das aktuelle Passwort zur Verifizierung.
     * @param neuesPW  Das gewünschte neue Passwort.
     * @param security Die Instanz des Sicherheitsdienstes.
     * @return true, wenn das Passwort erfolgreich geändert wurde, andernfalls false.
     */
    public boolean aenderePasswort(String altesPW, String neuesPW, SicherheitsService security) {
        // 1. Stimmt das alte Passwort?
        if (!pruefePasswort(altesPW, security)) {
            System.out.println("Fehler: Altes Passwort falsch.");
            return false;
        }

        // 2. Ist das neue Passwort identisch mit dem alten Passwort?
        // Wenn das neue Passwort die Prüfung für das alte Passwort besteht, ist es dasselbe!
        if (pruefePasswort(neuesPW, security)) {
            System.out.println("Fehler: Das neue Passwort darf nicht dem alten Passwort entsprechen.");
            return false;
        }

        // 3. Erfüllt das neue Passwort die Richtlinien (Länge, Großbuchstabe, Sonderzeichen)?
        if (!security.istPasswortSicher(neuesPW)) {
            System.out.println("Fehler: Das neue Passwort muss mindestens 8 Zeichen lang sein, " +
                    "einen Großbuchstaben und ein Sonderzeichen enthalten.");
            return false;
        }

        // 4. Wenn alle Prüfungen bestanden sind: Neues Salz und neuen Hash erzeugen
        this.salt = security.generiereZufallssalt();
        this.passwortHash = security.hashPasswort(neuesPW, this.salt);
        return true;
    }


    /**
     * Gibt den formatierten vollen Namen des Benutzers zurück.
     * Wenn keine Namen hinterlegt wurden, wird ein Hinweis zurückgegeben.
     *
     * @return Vorname und Nachname oder ein Platzhaltertext.
     */
    public String getVollerName() {
        if (this.vorname == null && this.nachname == null) {
            return "Kein Name hinterlegt";
        }
        return this.vorname + " " + this.nachname;
    }

    public String getBenutzername() {return this.benutzername;}
    public String getEmail() { return email; }
    public String getSalt() { return this.salt; }
    public String getVerschluesseltesPasswort() { return this.passwortHash; }

    /**
     * Setzte das Hash Passwort und generiere den Sicherheitstoken/Salt
     * @param klartextPasswort
     * @param security
     */
    private void initialisereSaltundHashPasswort(String klartextPasswort, SicherheitsService security) {
        // Logik an den SicherheitsService auslagern
        this.salt = security.generiereZufallssalt();
        this.passwortHash = security.hashPasswort(klartextPasswort, this.salt);
    }
}
