public class Argon2

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon2 {
    private String benutzername;
    private String passwortHash; // Enthält alle Parameter, Salz und Hash

    // Erstellt eine Argon2-Instanz (Standard: Argon2id – die sicherste Variante)
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    // Argon2-Konfigurationsparameter definieren:
    private static final int ITERATIONS = 3;     // Wie oft wird die Schleife durchlaufen? (Zeit)
    private static final int MEMORY = 65536;     // Wie viel RAM wird blockiert? (64 MB)
    private static final int PARALLELISM = 4;    // Wie viele CPU-Kerne/Threads parallel nutzen?

    // 1. REGISTRIERUNG
    public Argon2(String benutzername, String klartextPasswort) {
        this.benutzername = benutzername;

        // char[] statt String ist bei Argon2 Pflicht für maximale Speichersicherheit!
        char[] passwortChars = klartextPasswort.toCharArray();
        try {
            // Erzeugt vollautomatisch ein zufälliges Salz und hasht das Passwort
            this.passwortHash = argon2.hash(ITERATIONS, MEMORY, PARALLELISM, passwortChars);
        } finally {
            // Wichtig: Passwort-Inhalt sofort im RAM überschreiben/löschen
            argon2.wipeArray(passwortChars);
        }
    }

    // 2. LOGIN-VALIDIERUNG
    public boolean pruefePasswort(String eingegebenesKlartextPasswort) {
        char[] passwortChars = eingegebenesKlartextPasswort.toCharArray();
        try {
            // verify() liest Salz und Parameter aus "this.passwortHash" aus,
            // hasht das eingegebene Passwort damit und vergleicht das Ergebnis zeitsicher (Constant-Time).
            return argon2.verify(this.passwortHash, passwortChars);
        } finally {
            argon2.wipeArray(passwortChars);
        }
    }

    public String getVerschluesseltesPasswort() {
        return this.passwortHash;
    }
}
