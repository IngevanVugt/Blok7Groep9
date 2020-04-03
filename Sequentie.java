import javax.swing.*;
import java.io.*;
import java.sql.*;  // Using 'Connection', 'Statement' and 'ResultSet' classes in java.sql package


public class Sequentie {
    private File bestand;
    public String DNAsequentie = "";

    void fileChooser() throws SQLException {
        /**
         * Maakt een filechooser. Het gekozen bestand door de gebruiker wordt opgeslagen in File bestand.
         * @Output: Een bestand
         */
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            //textField.setText(String.valueOf(chooser.getSelectedFile())); //weet niet of ik vanuit hier dit in de gui kan zetten
            bestand = chooser.getSelectedFile();
            SequentieIDOPhalen();
        }
    }

    private void SequentieIDOPhalen() throws SQLException{
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
            System.out.println(GenoomSequentieId);
            bestandInlezen(GenoomSequentieId);
        }
    }

    private void bestandInlezen(int genoomSequentieId) throws SQLException {
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
        InputStreamReader input = new InputStreamReader(fileStream);
        BufferedReader reader = new BufferedReader(input);

        try (BufferedReader br = new BufferedReader(new FileReader(bestand))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (!line.isEmpty()) {
                    if (!line.startsWith(">") && line.matches("^[CAGT]+$")) { // hier gaat nog iets mis
                        DNAsequentie = line;
                        genoomSequentieId++;
                        //System.out.println("hoi het is een DNA sequentie" + genoomSequentieId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Er is geen DNA sequentie gevonden");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DNASeguentieInDatabase();
    }

    private void DNASeguentieInDatabase() throws SQLException {
        /**
         * Zet de DNAsequentie met de bijhorende genoomSequentieId in de database.
         * @Input: DNAsequentie + bijhorende genoomSequentieId
         */
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        String strSelect = "select max(Sequentie_ID) from Genoom";
        ResultSet rset = stmt.executeQuery(strSelect);
        while (rset.next()) {
            int GenoomSequentieId = rset.getInt("max(Sequentie_ID)");
            System.out.println(GenoomSequentieId);
        }
    }
}
