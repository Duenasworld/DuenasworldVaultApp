# DuenasworldVaultApp

Eine sichere, performante und modular aufgebaute Java-Tresoranwendung zur Verwaltung von Benutzerdaten und Passwörtern. Dieses Projekt wurde nach modernen Software-Architektur-Prinzipien (Separation of Concerns, Single-Responsibility-Prinzip) und unter strenger Einhaltung von Sicherheitsstandards entwickelt.

## 🚀 Features

* **Sicheres Passwort-Hashing:** Verwendung des modernen SHA-256-Algorithmus zur Einwegverschlüsselung von Passwörtern.
* **Kryptografisches Salting:** Generierung eines individuellen, sicheren Zufallssalzes (`SecureRandom`) pro Benutzer, um Angriffe über Rainbow Tables effektiv zu verhindern.
* **Strikte Passwort-Richtlinien:** Automatisierte Prüfung auf Passwortstärke (Mindestlänge, Großbuchstaben und Sonderzeichen mittels regulärer Ausdrücke).
* **Persistenter Dateispeicher:** Dauerhafte Speicherung der Daten auf der Festplatte im CSV-Format über ressourcenschonende und sichere I/O-Streams (`try-with-resources`).
* **Eindeutigkeits-Garantie:** Integrierter Dubletten-Check, der die Registrierung doppelter Benutzernamen in der Systemdatei verhindert.

## 🛠️ Software-Architektur

Das Projekt erzwingt eine strikte Trennung von Datenmodell und Geschäftslogik (Kopplung über Dependency Injection):

* **`Benutzer`:** Reines Datenmodell (Entity) zur Repräsentation der User-Attribute (inkl. Constructor Chaining für optionale Parameter).
* **`SicherheitsService`:** Zentrale Krypto-Komponente, isoliert von den Datenklassen (Zuständig für Hashing, Salts und Validierung).
* **`DatenService`:** Persistenz-Schicht für alle Lese- und Schreiboperationen auf der Festplatte.

## 🧪 Qualitätssicherung (Testing)

Das Projekt verfügt über eine umfassende Testsuite mit **JUnit 5**, die alle Kernkomponenten absichert. 

* **Automatisierte Unit-Tests:** Umfassende Abdeckung aller logischen Pfade.
* **Exception-Testing:** Gezielte Überprüfung von Fehlerzuständen und korrupten Dateistrukturen mittels `assertThrows`.
* **Temporäre Dateiverarbeitung:** Verwendung flüchtiger Testdateien (`createTempFile`), um Rückstände auf der Festplatte nach den Testläufen zu vermeiden.

> 📈 **Testabdeckung:** Die geschäftskritischen Klassen (`Benutzer`, `SicherheitsService`, `DatenService`) weisen eine herausragende Code-Abdeckung (bis zu **100% Line & Method Coverage**) auf.

## 📋 Voraussetzungen & Start

* **Java-Version:** Java 21 oder höher (entwickelt und optimiert unter OpenJDK 25)
* **IDE:** IntelliJ IDEA (keine externen Build-Tools oder Maven-Dependencies erforderlich)

1. Klonen Sie das Repository.
2. Öffnen Sie den Ordner `DuenasworldVaultApp` in IntelliJ.
3. Starten Sie die Anwendung über die `Main.java`.
