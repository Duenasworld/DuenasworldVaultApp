public class Main {
    public static void main(String[] args) {
        // 1. Die benötigten Services starten
        SicherheitsService security = new SicherheitsService();
        DatenService datenService = new DatenService("duenasworld_vault.txt");

        System.out.println("--- REGISTRIERUNG ---");
        // 2. Einen neuen Benutzer im RAM erstellen
        Benutzer neuRegistriert = new Benutzer(
                "Duenasworld2",
                "SuperSicher123!",
                "duenasworld@vault.ch",
                "Duena",
                "W.",
                "Meine Mama ist die Beste!",
                security
        );

        // 3. Auf der Festplatte speichern
        boolean gespeichert = datenService.speichereBenutzer(neuRegistriert);
        System.out.println("Benutzer erfolgreich auf Festplatte gesichert: " + gespeichert);


        System.out.println("\n--- LOGIN-VERSUCH (Direkt von Festplatte) ---");
        // 4. Wir versuchen uns einzuloggen. Das Programm liest nun live die Datei!

        // Test A: Falsches Passwort
        boolean loginFalsch = datenService.ladeUndPruefeLogin("duenasworld2026", "FalschesPW", security);
        System.out.println("Login mit falschem PW erfolgreich? " + loginFalsch); // Sollte false sein

        // Test B: Korrektes Passwort
        boolean loginRichtig = datenService.ladeUndPruefeLogin("duenasworld2026", "SuperSicher123!", security);
        System.out.println("Login mit korrektem PW erfolgreich? " + loginRichtig); // Sollte true sein
    }
}
