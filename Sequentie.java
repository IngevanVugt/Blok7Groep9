import javax.swing.*;
import java.io.*;
import java.sql.*;  // Using 'Connection', 'Statement' and 'ResultSet' classes in java.sql package

public class Sequentie {
    private File bestand;
    public String DNAsequentie = "";

    File fileChooser() throws SQLException {
        /**
         * Maakt een filechooser. Het gekozen bestand door de gebruiker wordt opgeslagen in File bestand.
         * @Output: Een bestand
         */
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            bestand = chooser.getSelectedFile();
        }
        return bestand;
    }

    String SequentieIDOPhalen(File bestand) throws SQLException{
        /**
         * Haalt de hoogste sequentieID van het laatst toegevoegde genoom op uit de database.
         * Deze waarde wordt opgeslagen in GenoomSequentieId en wordt doorgegeven naar de functie InlezenBestand
         * @Output: GenoomSequentieId
         */
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        String sequentieID = "select max(Sequentie_ID) from Genoom";
        ResultSet rset = stmt.executeQuery(sequentieID);
        while (rset.next()) {
            int GenoomSequentieId = rset.getInt("max(Sequentie_ID)");
            bestandInlezen(GenoomSequentieId, bestand);
        }
        return DNAsequentie;
    }

    private void bestandInlezen(int genoomSequentieId, File bestand) throws SQLException {
        /**
         * Leest het bestand in dat door de gebruiker is gekozen in de filechooser.
         * @Input: genoomSequentieId
         * @Output: DNAsequentie + bijhorende genoomSequentieId
         */
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(bestand);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fileStream != null;
        // wat doet deze 2 regels code? Als ik ze uitzet doet de functie het nog steeds
//        InputStreamReader input = new InputStreamReader(fileStream);
//        BufferedReader reader = new BufferedReader(input);
        try (BufferedReader br = new BufferedReader(new FileReader(bestand))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (!line.isEmpty()) {
                    line = line.toLowerCase();
                    if (!line.startsWith(">") && line.matches("^[cagt]+$")) {
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

    private void DNASeguentieInDatabase(String DNAsequentie, int genoomSequentieId) throws SQLException {
        /**
         * Zet de DNAsequentie met de bijhorende genoomSequentieId in de database.
         * @Input: DNAsequentie + bijhorende genoomSequentieId
         */
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        String strSelect = String.format("insert into Genoom values ('%s', %s)", DNAsequentie, genoomSequentieId);
        stmt.executeUpdate(strSelect);
    }
}