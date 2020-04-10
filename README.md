# Blok7Groep9

De code van groep 9 
Makers: Inge van Vugt, Maite van den Noort en Wouter Gaykema

Voor het runnen van de code:
  - De code wordt uitgevoerd in IntelliJ
  - Voor de database connection is het volgende bestand nodig:
  - mysql-connector-java-8.0.19
	~ deze is te downloaden op de volgende manier:
		* http://dev.mysql.com/downloads ⇒ "Connector/J" ⇒ Connector/J 8.0.{xx}, waarbij de xx voor de laatste versie is ⇒ In "Select Operating 		  System", choose "Platform Independent" ⇒ ZIP Archive (e.g., "mysql-connector-java-8.0.{xx}.zip" ⇒ "No thanks, just start my download".
		* UNZIP het bestand in jou project directory
		* Ga in het mapje en zoek het .jar bestand: "mysql-connector-java-8.0.{xx}.jar". Kopiëer de gehele path.
	~ Het toevoegen in java:
		* In IntelliJ inksboven op "File" en dan "Project structure"
		* Ga naar "libraries" en dan op het plusje en kies "Java"
		* Vul daarin de path van de vorige stap en druk op "OK"
  - De benodigde Maven libaries
	~ Deze zijn op de volgende manier toe te voegen
		* In IntelliJ inksboven op "File" en dan "Project structure"
		* Ga naar "libraries" en dan op het plusje en kies "From Maven"
		* Zoek daarin de volgende 2 en voeg deze toe:
			org.biojava:biojava-core:5.3.0
			org.biojava:biojava-ws:5.3.0