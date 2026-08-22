OTA SULLA CARTELLA LIB:
Questa directory è intenzionalmente vuota. 

Il progetto utilizza Apache Maven come sistema di build e gestione delle dipendenze (vedere il file pom.xml nella root del progetto). Tutte le librerie esterne necessarie alla compilazione e all'esecuzione (tra cui HikariCP, il driver JDBC di PostgreSQL e JavaFX) vengono scaricate e gestite automaticamente da Maven.

Non è pertanto richiesta la presenza manuale di file .jar all'interno di questa cartella.
