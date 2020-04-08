import java.io.*;
import java.util.ArrayList;
import java.sql.*;

public class ReadingFrame {
    private static ArrayList<String> GevondenORFs;
    private static int GenoomSequentieId;
    private static int ORFId;
    private static int countInserted;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        String sequentie = Sequentie();
        ORFVoorspellen(sequentie);
        DatabaseOphalen();
        DatabaseToevoegen();
    }

    private static String Sequentie() throws IOException {
        String sequentie = "";
        File file = new File("C:\\Users\\ingev\\OneDrive\\Documenten\\Han jaar 2\\Tutor\\Blok 7\\Week4-6\\SD.fa");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String regel="";
        while ((regel=br.readLine())!=null){
            regel = regel.replace("\n", "");
            sequentie = sequentie + regel;
        }
        return sequentie;
    }


    private static void ORFVoorspellen(String sequentie){
        String codon = "";
        boolean ORF = false;
        String Orf = "";
        GevondenORFs = new ArrayList<String>();

        for (int i = 0; i < 3; i++){
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
    }

    private static void DatabaseOphalen() throws SQLException, ClassNotFoundException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        String strSelect = "select max(Sequentie_ID), max(ORF_ID) from Genoom, ORF";
        ResultSet rset = stmt.executeQuery(strSelect);
        while(rset.next()) {
            GenoomSequentieId = rset.getInt("max(Sequentie_ID)");
            ORFId = rset.getInt("max(ORF_ID)");
        }
    }

    private static void DatabaseToevoegen() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        Statement stmt = conn.createStatement();
        for (String gevondenORF : GevondenORFs) {
            String insert = String.format("insert into ORF values ('%s', %d, %d)", gevondenORF,
                    GenoomSequentieId, ++ORFId);
            countInserted = stmt.executeUpdate(insert);
        }
    }

}

