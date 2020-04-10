import javax.swing.*;
import java.io.*;
import java.sql.*;  // Using 'Connection', 'Statement' and 'ResultSet' classes in java.sql package


/**
 * Deze class kan het inlezen van een bestand en een controle uitvoeren of het DNA bevat of niet.
 * @author Inge van Vugt en Maite van den Noort
 * @version 2.0
 * @since 05-04-2020
 */

public class Sequentie {
    private File bestand;
    public String DNAsequentie = "";


    /**
     * Maakt een filechooser. Het gekozen bestand door de gebruiker wordt opgeslagen in File bestand.
     * @return bestand, de path naar het gekozen bestand.
     */
    File fileChooser(){
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            bestand = chooser.getSelectedFile();
        }
        return bestand;
    }

    /**
     * Haalt de hoogste sequentieID van het laatst toegevoegde genoom op uit de database.
     * Deze waarde wordt opgeslagen in GenoomSequentieId en wordt doorgegeven naar de functie InlezenBestand
     * @param bestand is het bestand wat gekozen is door de gebruiker
     * @return DNAsequentie, is een string met daarin de DNAsequentie
     * @throws SQLException vanwege de connectie met de database
     */
    String SequentieIDOPhalen(File bestand) throws SQLException{
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        String sequentieID = "select max(Sequentie_ID) from Genoom"; // query voor de database
        ResultSet rset = stmt.executeQuery(sequentieID); // query uitvoeren
        while (rset.next()) {
            int GenoomSequentieId = rset.getInt("max(Sequentie_ID)");
            bestandInlezen(GenoomSequentieId, bestand);
        }
        return DNAsequentie;
    }

    /**
     * Leest het bestand in dat door de gebruiker is gekozen in de filechooser.
     * @param genoomSequentieId is een getal waarmee de sequentie in de database wordt toegevoegd
     * @param bestand is het gekozen bestand door de gebruiker wat moet worden geopend
     * @throws SQLException vanwege de connectie met de database
     */
    private void bestandInlezen(int genoomSequentieId, File bestand) throws SQLException {
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(bestand);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fileStream != null;
        try (BufferedReader br = new BufferedReader(new FileReader(bestand))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (!line.isEmpty()) {
                    line = line.toLowerCase();
                    if (!line.startsWith(">") && line.matches("^[cagtn]+$")) { // checkt voor DNA
                        DNAsequentie = DNAsequentie + line;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Er is geen DNA sequentie gevonden");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!DNAsequentie.equals("")){
            genoomSequentieId++;
            DNASeguentieInDatabase(DNAsequentie, genoomSequentieId);
        }
    }

    /**
     * Zet de DNAsequentie met de bijhorende genoomSequentieId in de database.
     * @param DNAsequentie is een string dat bestaat uit een DNA sequentie
     * @param genoomSequentieId is een getal waarmee de sequentie in de database wordt toegevoegd
     * @throws SQLException vanwege de connectie met de database
     */
    private void DNASeguentieInDatabase(String DNAsequentie, int genoomSequentieId) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        String strSelect = String.format("insert into Genoom values ('%s', %s)", DNAsequentie, genoomSequentieId);
        stmt.executeUpdate(strSelect); // DNA sequentie toevoegen aan de database
    }
}
