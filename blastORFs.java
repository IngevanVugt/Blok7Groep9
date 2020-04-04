
import org.xml.sax.*;
import java.io.*;

import static org.biojava.nbio.ws.alignment.qblast.BlastAlignmentParameterEnum.ENTREZ_QUERY;
import java.io.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.biojava.nbio.core.sequence.io.util.IOUtils;
import org.biojava.nbio.ws.alignment.qblast.*;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



public class blastORFs {
    /**
     * The blastORFS class accepts a list of ORF sequences as input and outputs BLAST results
     *
     * @author  Wouter Gaykema
     * @version 2.0
     * @since   2020-APR-4
     */
    private static final String BLAST_OUTPUT_FILE = "blastOutput.xml";    // file to save blast results to
    private static final List<String> ORFS = Arrays.asList("MKWVTFISLLFLFSSAYSRGVFRRDAHKSEVAHRFKDLGEENFKALVLIAFAQYLQQCP");     // Blast query sequence

    public static Connection getConnection() throws SQLException {
        /**
         * The getConnection function makes a connection to the mySQL database that stores the BLAST data
         */
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        return conn;
    }

    private static String convertXMLFileToString(String ORFfile, int ORFnum)
            throws IOException, SQLException {
        /**
         * The convertXMLFileToString class takes an XML file as input, puts it in a text document and
         * adds each hit to the table
         *
         * input:
         * ORFfile; the XML file to read
         * ORFnum; the number of the ORF
         *
         * output:
         * NextORF; the XML file as string
         */

        Connection conn = getConnection();

        BufferedReader reader;
        reader = new BufferedReader(new FileReader(ORFfile));
        String line = reader.readLine();
        int n = 0;
        StringBuilder NextORF = new StringBuilder("ORF " + ORFnum + ": \n");
        String Hsp_midline = null;
        String Hsp_hseq = null;
        String Hsp_qseq = null;
        int Hsp_identity = 0;
        int Hsp_query_to = 0;
        int Hsp_query_from = 0;
        String Hsp_evalue = null;
        String Hit_accesion = null;
        String Hit_def = null;
        String Hit_id = null;
        int QueryCover = 0;
        String hit_seq = null;
        int Blast_id = 0;
        int ORF_Id = 0;
        Statement stmt = conn.createStatement();
        String strSelect = "select max(BlastResultaten_ID), max (ORF_ID) from BlastResultaten, ORF";
        ResultSet rset = stmt.executeQuery(strSelect);
        while(rset.next()) {
            Blast_id = rset.getInt("max(BlastResultaten_ID)") + 1;
            ORF_Id = rset.getInt("max(ORF_ID)");
        }
        while (line != null) {
            NextORF.append(line).append("\n");

             if (line.matches("^  <Hit_def>.*</Hit_def>$")) {
                Hit_def = line.substring(11, line.length() - 10);
            } else if (line.matches("^  <Hit_accession>.*</Hit_accession>$")) {
                Hit_accesion = line.substring(17, line.length() - 16);
            } else if (line.matches("^      <Hsp_evalue>.*</Hsp_evalue>$")) {
                Hsp_evalue = line.substring(18, line.length() - 13);
            } else if (line.matches("^      <Hsp_query-from>.*</Hsp_query-from>$")) {
                Hsp_query_from = Integer.parseInt(line.substring(22, line.length() - 17));
            } else if (line.matches("^      <Hsp_query-to>.*</Hsp_query-to>$")) {
                Hsp_query_to = Integer.parseInt(line.substring(20, line.length() - 15));
            } else if (line.matches("^      <Hsp_qseq>.*</Hsp_qseq>$")) {
                Hsp_qseq = line.substring(16, line.length() - 11);
            } else if (line.matches("^      <Hsp_hseq>.*</Hsp_hseq>$")) {
                Hsp_hseq = line.substring(16, line.length() - 11);
            } else if (line.matches("^      <Hsp_midline>.*</Hsp_midline>$")) {
                Hsp_midline = line.substring(19, line.length() - 14);
            } else if (line.matches("^      <Hsp_identity>.*</Hsp_identity>$")) {
                Hsp_identity = Integer.parseInt(line.substring(20, line.length() - 15));
            } else if (line.matches("^</Hit>$")) {
                QueryCover = (Hsp_query_to - Hsp_query_from) + 1;
                hit_seq = Hsp_qseq + "\n" + Hsp_midline + "\n" + Hsp_hseq;
                PreparedStatement posted = conn.prepareStatement("INSERT INTO BlastResultaten (E_value, " +
                        "Query_cover, Percentage_identity, Accessiecode, Titel, EiwitSequentie, ORF_ORF_ID, " +
                        "BlastResultaten_ID) VALUES ('" + Hsp_evalue + "', '" + QueryCover + "', '" + Hsp_identity + "', " +
                        "'" + Hit_accesion + "', '" + Hit_def + "', '" + hit_seq + "', '" + ORF_Id + "', " +
                        "'" + Blast_id + "')");
                posted.execute();
            }
            // read next line
            line = reader.readLine();

        }
        conn.close();
        return NextORF.toString();
    }

    public static String GenerateXML(String ORF) {
        /**
         * The GenerateXML function gets BLAST results and puts them in an XML file
         *
         * input:
         * ORF; a single ORF sequence
         *
         * output:
         * XMLfile; an XML file of blast results
         */
        NCBIQBlastService service = new NCBIQBlastService();

        // set alignment options
        NCBIQBlastAlignmentProperties props = new NCBIQBlastAlignmentProperties();
        props.setBlastProgram(BlastProgramEnum.blastp);
        props.setBlastDatabase("swissprot");
        props.setAlignmentOption(ENTREZ_QUERY, "");
        // set output options
        NCBIQBlastOutputProperties outputProps = new NCBIQBlastOutputProperties();

        String rid = null;          // blast request ID
        FileWriter writer = null;
        BufferedReader reader = null;
        String XMLfile = null;
        try {
            // send blast request and save request id
            rid = service.sendAlignmentRequest(ORF, props);

            // wait until results become available.
            while (!service.isReady(rid)) {
                System.out.println("Waiting for results. Sleeping for 5 seconds");
                Thread.sleep(5000);
            }

            // read results when they are ready
            InputStream in = service.getAlignmentResults(rid, outputProps);
            reader = new BufferedReader(new InputStreamReader(in));

            // write blast output to file
            File f = new File(BLAST_OUTPUT_FILE);
            System.out.println("Saving query results in file " + f.getAbsolutePath());
            writer = new FileWriter(f);

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + System.getProperty("line.separator"));
            }
            XMLfile = f.getAbsolutePath();
            return XMLfile;


        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return XMLfile;

        } finally {
            // clean up
            IOUtils.close(writer);
            IOUtils.close(reader);

            // delete results from blast server
            service.sendDeleteRequest(rid);

        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        /**
         * The main function ties together all the other functions
         */
        int ORFnum = 1;
        String BlastString = "";
        List<String> ORFS = Arrays.asList("MKWVTFISLLFLFSSAYSRGVFRRDAHKSEVAHRFKDLGEENFKALVLIAFAQYLQQCP");     // Blast query sequence//TODO dit zijn de ORF sequenties
        for (int i = 0; i< ORFS.size(); i++) {
            String ORFfile = GenerateXML(ORFS.get(i));
            String NextORF = convertXMLFileToString(ORFfile, ORFnum);
            BlastString += NextORF;
            ORFnum += 1;
        }
        System.out.println(BlastString);

    }

}