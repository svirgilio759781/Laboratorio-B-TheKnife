"Applicazione client-server per la gestione di un sistema di Ristoranti con varie funzionalita (Recensioni, Prenotazione, Visita, ...), realizzata con Java e JavaFX."]

Prerequisiti
Per compilare ed eseguire questo progetto, assicurati di avere installato:

JDK 17 (o superiore)

Apache Maven 3.x

PostgreSQL (con database configurato tramite pgAdmin 4)

Configurazione Database
Il database utilizzato è PostgreSQL. Per il corretto funzionamento, assicurarsi che:
1. Il server PostgreSQL sia attivo.
2. Esista un database chiamato 'TheKnifeVirgilio'.
3. Le credenziali di accesso al database siano configurate correttamente nella classe di connessione.

Attualmente, le impostazioni sono definite all'interno della classe:
`DBManager`.

I parametri correnti sono:
- URL: jdbc:postgresql://localhost:5432/TheKnifeVirgilio
- User: postgres

Compilazione
Il progetto utilizza Maven come gestore di build. Per compilare il progetto ed ottenere gli eseguibili .jar, apri un terminale nella cartella principale del progetto ed esegui:
mvn clean package
I file compilati (serverTK.jar e clientTK.jar) saranno disponibili nella cartella /target.

Esecuzione
Per avviare l'applicazione, segui questi passaggi (in ordine):
Avvio del Server:
java -jar target/serverTK.jar
Avvio del Client:
(Apri un nuovo terminale)
java -jar target/clientTK.jar

Nota anche se avvi prima il client e poi il server funzionerà, ma il client non si connetterà correttamente al database e non sarà in grado di recuperare o inviare dati

Nota sulle dipendenze: Il progetto utilizza Apache Maven per la gestione automatica delle dipendenze. Tutte le librerie necessarie (PostgreSQL driver, HikariCP, JavaFX) verranno scaricate e configurate automaticamente all'apertura del progetto tramite il file pom.xml. Non è necessario includere file .jar esterni manualmente.
