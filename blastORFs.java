
import org.xml.sax.*;
import java.io.*;

import static org.biojava.nbio.ws.alignment.qblast.BlastAlignmentParameterEnum.ENTREZ_QUERY;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
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
     * @version 1.0
     * @since   2020-JAN-10
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

    public static void createTable() throws SQLException {
        /**
         * The createTable class creates a table to store BLAST results if it does not already exist
         */
        Connection conn = getConnection();
        PreparedStatement create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ORF_BLAST_results(" +
                "resultNum varchar(10), Hit_num int," +
                " Hit_id varchar(12), Hit_def varchar(255), Hit_accesion varchar(10), Hit_len int, Hsp_num int, " +
                "Hsp_bit_score Double, Hsp_score int, Hsp_evalue varchar(255), Hsp_query_from int, Hsp_query_to int, " +
                "Hsp_hit_from int, Hsp_hit_to int, Hsp_query_frame int, Hsp_hit_frame int, Hsp_identity int, " +
                "Hsp_positive int, Hsp_gaps int, Hsp_align_len int, Hsp_qseq varchar(255), Hsp_hseq varchar(255), " +
                "Hsp_midline varchar(255), PRIMARY KEY(resultNum))");
        create.execute();
        conn.close();
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

        int rowNum = 1;
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(ORFfile));
        String line = reader.readLine();
        int n = 0;
        StringBuilder NextORF = new StringBuilder("ORF " + ORFnum + ": \n");
        int Hit_num = 0;
        String Hsp_midline = null;
        String Hsp_hseq = null;
        String Hsp_qseq = null;
        int Hsp_align_len = 0;
        int Hsp_gaps = 0;
        int Hsp_positive = 0;
        int Hsp_identity = 0;
        int Hsp_hit_frame = 0;
        int Hsp_query_frame = 0;
        int Hsp_hit_to = 0;
        int Hsp_hit_from = 0;
        int Hsp_query_to = 0;
        int Hsp_query_from = 0;
        String Hsp_evalue = null;
        int Hsp_score = 0;
        Double Hsp_bit_score = null;
        int Hsp_num = 0;
        int Hit_len = 0;
        String Hit_accesion = null;
        String Hit_def = null;
        String Hit_id = null;
        while (line != null) {
            NextORF.append(line).append("\n");

            if (line.matches("^  <Hit_num>.*</Hit_num>$")) {
                Hit_num = Integer.parseInt(line.substring(11, line.length() - 10));
            } else if (line.matches("^  <Hit_id>.*</Hit_id>$")) {
                Hit_id = line.substring(10, line.length() - 9);
            } else if (line.matches("^  <Hit_def>.*</Hit_def>$")) {
                Hit_def = line.substring(11, line.length() - 10);
                if( Hit_def.length()>=255){
                    Hit_def = (Hit_def.substring(0, 251) + "...");
                }
            } else if (line.matches("^  <Hit_accession>.*</Hit_accession>$")) {
                Hit_accesion = line.substring(17, line.length() - 16);
            } else if (line.matches("^  <Hit_len>.*</Hit_len>$")) {
                Hit_len = Integer.parseInt(line.substring(11, line.length() - 10));
            } else if (line.matches("^      <Hsp_num>.*</Hsp_num>$")) {
                Hsp_num = Integer.parseInt(line.substring(15, line.length() - 10));
            } else if (line.matches("^      <Hsp_bit-score>.*</Hsp_bit-score>$")) {
                Hsp_bit_score = Double.parseDouble(line.substring(21, line.length() - 16));
            } else if (line.matches("^      <Hsp_score>.*</Hsp_score>$")) {
                Hsp_score = Integer.parseInt(line.substring(17, line.length() - 12));
            } else if (line.matches("^      <Hsp_evalue>.*</Hsp_evalue>$")) {
                Hsp_evalue = line.substring(18, line.length() - 13);
                if( Hsp_evalue.length()>=255){
                    Hsp_evalue = (Hsp_evalue.substring(0, 251) + "...");
                }
            } else if (line.matches("^      <Hsp_query-from>.*</Hsp_query-from>$")) {
                Hsp_query_from = Integer.parseInt(line.substring(22, line.length() - 17));
            } else if (line.matches("^      <Hsp_query-to>.*</Hsp_query-to>$")) {
                Hsp_query_to = Integer.parseInt(line.substring(20, line.length() - 15));
            } else if (line.matches("^      <Hsp_hit-from>.*</Hsp_hit-from>$")) {
                Hsp_hit_from = Integer.parseInt(line.substring(20, line.length() - 15));
            } else if (line.matches("^      <Hsp_hit-to>.*</Hsp_hit-to>$")) {
                Hsp_hit_to = Integer.parseInt(line.substring(18, line.length() - 13));
            } else if (line.matches("^      <Hsp_query-frame>.*</Hsp_query-frame>$")) {
                Hsp_query_frame = Integer.parseInt(line.substring(23, line.length() - 18));
            } else if (line.matches("^      <Hsp_hit-frame>.*</Hsp_hit-frame>$")) {
                Hsp_hit_frame = Integer.parseInt(line.substring(21, line.length() - 16));
            } else if (line.matches("^      <Hsp_identity>.*</Hsp_identity>$")) {
                Hsp_identity = Integer.parseInt(line.substring(20, line.length() - 15));
            } else if (line.matches("^      <Hsp_positive>.*</Hsp_positive>$")) {
                Hsp_positive = Integer.parseInt(line.substring(20, line.length() - 15));
            } else if (line.matches("^      <Hsp_gaps>.*</Hsp_gaps>$")) {
                Hsp_gaps = Integer.parseInt(line.substring(16, line.length() - 11));
            } else if (line.matches("^      <Hsp_align-len>.*</Hsp_align-len>$")) {
                Hsp_align_len = Integer.parseInt(line.substring(21, line.length() - 16));
            } else if (line.matches("^      <Hsp_qseq>.*</Hsp_qseq>$")) {
                Hsp_qseq = line.substring(16, line.length() - 11);
                if( Hsp_qseq.length()>=255){
                    Hsp_qseq = (Hsp_qseq.substring(0, 251) + "...");
                }
            } else if (line.matches("^      <Hsp_hseq>.*</Hsp_hseq>$")) {
                Hsp_hseq = line.substring(16, line.length() - 11);
                if( Hsp_hseq.length()>=255){
                    Hsp_hseq = (Hsp_hseq.substring(0, 251) + "...");
                }
            } else if (line.matches("^      <Hsp_midline>.*</Hsp_midline>$")) {
                Hsp_midline = line.substring(19, line.length() - 14);
                if( Hsp_midline.length()>=255){
                    Hsp_midline = (Hsp_midline.substring(0, 251) + "...");
                }
            } else if (line.matches("^</Hit>$")) {
                String resultNum = String.valueOf(ORFnum) + "." + String.valueOf(Hit_num);//TODO dit is de primary key dus als we meerdere dingen willen invullen moet hier ook de naam van het ingevoerde genoom bij
                PreparedStatement posted = conn.prepareStatement("INSERT INTO ORF_BLAST_results (resultNum, " +
                        "Hit_num, Hit_id, Hit_def, Hit_accesion, Hit_len, Hsp_num, Hsp_bit_score, Hsp_score, " +
                        "Hsp_evalue, Hsp_query_from, Hsp_query_to, Hsp_hit_from, Hsp_hit_to, Hsp_query_frame, " +
                        "Hsp_hit_frame, Hsp_identity, Hsp_positive, Hsp_gaps, Hsp_align_len, Hsp_qseq, Hsp_hseq, " +
                        "Hsp_midline) VALUES ('" + resultNum + "', '" + Hit_num + "', '" + Hit_id + "', " +
                        "'" + Hit_def + "', '" + Hit_accesion + "', '" + Hit_len + "', '" + Hsp_num + "', " +
                        "'" + Hsp_bit_score + "', '" + Hsp_score + "', '" + Hsp_evalue + "', '" + Hsp_query_from + "', " +
                        "'" + Hsp_query_to + "', '" + Hsp_hit_from + "', '" + Hsp_hit_to + "', " +
                        "'" + Hsp_query_frame + "', '" + Hsp_hit_frame + "', '" + Hsp_identity + "', " +
                        "'" + Hsp_positive + "', '" + Hsp_gaps + "', '" + Hsp_align_len + "', '" + Hsp_qseq + "', " +
                        "'" + Hsp_hseq + "', '" + Hsp_midline + "')");
                posted.execute();
                rowNum += 1;
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
        createTable();
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