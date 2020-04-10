import java.util.ArrayList;
import java.sql.*;

/**
 * Deze class voorspelt van een DNA sequentie mogelijke ORFs
 * @author Inge van Vugt
 * @version 2.0
 * @since 04-04-2020
 */

public class ReadingFrame {
    private static ArrayList<String> GevondenORFs;
    private static int GenoomSequentieId;
    private static int ORFId;


    /**
     * Deze class gaat de ORFs voorspellen van de meegegeven DNA sequentie.
     * @param sequentie dit is de aangegeven sequentie van de gebruiker
     * @return GevondenORFs, dit is een lijst met alle voorspelde ORFs
     * @throws SQLException vanwege de connectie met de database
     * @throws ClassNotFoundException vanwege het aanroepen van een andere class
     */
    public ArrayList<String> ORFVoorspellen(String sequentie) throws SQLException, ClassNotFoundException {
        String codon = "";
        boolean ORF = false;
        String Orf = "";
        GevondenORFs = new ArrayList<String>();

        for (int i = 0; i < 3; i++){        // dit vanwege de verschillende reading frames
            for(int j = i; j < sequentie.length(); j++){
                codon = codon + sequentie.charAt(j);
                if (codon.length() == 3){
                    if ((codon.equals("atg") & !ORF)){
                        ORF = true;
                    }
                    if (ORF){
                        Orf = Orf + codon;
                    }
                    if (codon.equals("taa") || codon.equals("tag") || codon.equals("tga")){
                        if (ORF){
                            ORF = false;
                            GevondenORFs.add(Orf);
                            Orf = "";
                        }
                    }
                    codon = "";
                }
            }
        }
        DatabaseOphalen();
        return GevondenORFs;
    }

    /**
     * Deze class gaat de max Sequentie_ID en max ORF_ID uit de database ophalen.
     * @throws SQLException vanwege de connectie met de database
     * @throws ClassNotFoundException vanwege het aanroepen van een andere class
     */
    public static void DatabaseOphalen() throws SQLException, ClassNotFoundException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        try{
            String strSelect = "select max(Sequentie_ID) from Genoom";      //query
            ResultSet rset = stmt.executeQuery(strSelect);
            while(rset.next()) {
                GenoomSequentieId = rset.getInt("max(Sequentie_ID)");
            }
            Statement stmt2 = conn.createStatement();
            String strSelect2 = "select max(ORF_ID) from ORF";      //query
            ResultSet rset2 = stmt2.executeQuery(strSelect2);
            while(rset2.next()) {
                ORFId = rset2.getInt("max(ORF_ID)");
            }
        }catch (java.sql.SQLIntegrityConstraintViolationException e){
            String strSelect = "select max(Sequentie_ID) from Genoom";      //query
            ResultSet rset = stmt.executeQuery(strSelect);
            while (rset.next()){
                GenoomSequentieId = rset.getInt("max(Sequentie_ID)");
                ORFId = 0;
            }
        }
        DatabaseToevoegen();
    }

    /**
     * Deze class gaat alle voorspelde ORFs in de database toevoegen
     * @throws SQLException vanwege de connectie met de database
     */
    public static void DatabaseToevoegen() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        for (String gevondenORF : GevondenORFs) {
            String insert = String.format("insert into ORF values ('%s', %d, %d)", gevondenORF,
                    GenoomSequentieId, ++ORFId);        //query
            stmt.executeUpdate(insert);
        }
    }

}
