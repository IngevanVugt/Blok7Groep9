
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
    private static final String BLAST_OUTPUT_FILE = "blastOutput.xml";    // file to save blast results to
    private static final List<String> ORFS = Arrays.asList("MKWVTFISLLFLFSSAYSRGVFRRDAHKSEVAHRFKDLGEENFKALVLIAFAQYLQQCP");     // Blast query sequence

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                "NRCP73s0H5", "T10HedMmpO");
        return conn;
    }

    public static void createTable() throws SQLException {
        Connection conn = getConnection();
        PreparedStatement create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ORF_BLAST_results(" +
                "resultNum varchar(10), Hit_num int," +
                " Hit_id varchar(12), Hit_def varchar(255), Hit_accesion varchar(10), Hit_len int, Hsp_num int, " +
                "Hsp_bit_score Double, Hsp_score int, Hsp_evalue varchar(255), Hsp_query_from int, Hsp_query_to int, " +
                "Hsp_hit_from int, Hsp_hit_to int, Hsp_query_frame int, Hsp_hit_frame int, Hsp_identity int, " +
                "Hsp_positive int, Hsp_gaps int, Asp_align_len int, Hsp_qseq varchar(255), Hsp_hseq varchar(255), " +
                "Hsp_midline varchar(255), PRIMARY KEY(resultNum)");
        create.executeUpdate();
    }

    private static String convertXMLFileToString(String ORFfile, int ORFnum, String blastResults) throws IOException, SQLException {

        Connection conn = getConnection();

        int rowNum = 1;
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(ORFfile));
        String line = reader.readLine();
        int n = 0;
        String NextORF = "ORF " + ORFnum + ": \n";
        while (line != null) {
            blastResults += line + "/n";
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
            if (line.matches("^  <Hit_num>.*</Hit_num>$")) {
                Hit_num = Integer.parseInt(line.substring(11, line.length() - 10));
            } else if (line.matches("^  <Hit_id>.*</Hit_id>$")) {
                Hit_id = line.substring(10, line.length() - 9);
            } else if (line.matches("^  <Hit_def>.*</Hit_def>$")) {
                Hit_def = line.substring(11, line.length() - 10);
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
            } else if (line.matches("^      <Hsp_hseq>.*</Hsp_hseq>$")) {
                Hsp_hseq = line.substring(16, line.length() - 11);
            } else if (line.matches("^      <Hsp_midline>.*</Hsp_midline>$")) {
                Hsp_midline = line.substring(19, line.length() - 14);
            } else if (line.matches("^</Hit>$")) {
                String resultNum = String.valueOf(ORFnum) + "." + String.valueOf(Hit_num);
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
                posted.executeUpdate();
                rowNum += 1;
            }
            // read next line
            line = reader.readLine();

        }
        return blastResults;
    }

    public static String GenerateXML(String ORF) { //TODO make this a seperate function in for loop, ORFnum goes 1+
        NCBIQBlastService service = new NCBIQBlastService();

        // set alignment options
        NCBIQBlastAlignmentProperties props = new NCBIQBlastAlignmentProperties();
        props.setBlastProgram(BlastProgramEnum.blastp);
        props.setBlastDatabase("swissprot");
        //props.setAlignmentOption(ENTREZ_QUERY, "\"serum albumin\"[Protein name] AND mammals[Organism]");
        props.setAlignmentOption(ENTREZ_QUERY, "");
        // set output options
        NCBIQBlastOutputProperties outputProps = new NCBIQBlastOutputProperties();
        // in this example we use default values set by constructor (XML format, pairwise alignment, 100 descriptions and alignments)

        // Example of two possible ways of setting output options

// outputProps.setAlignmentNumber(200); // outputProps.setOutputOption(BlastOutputParameterEnum.ALIGNMENTS, “200”);

        String rid = null;          // blast request ID
        FileWriter writer = null;
        BufferedReader reader = null;
        String XMLfile = null;
        try {
            // send blast request and save request id
            rid = service.sendAlignmentRequest(ORF, props);

            // wait until results become available. Alternatively, one can do other computations/send other alignment requests
            while (!service.isReady(rid)) {
                System.out.println("Waiting for results. Sleeping for 5 seconds");
                Thread.sleep(5000);
            }

            // read results when they are ready
            InputStream in = service.getAlignmentResults(rid, outputProps);
            reader = new BufferedReader(new InputStreamReader(in));

            // write blast output to specified file
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

            // delete given alignment results from blast server (optional operation)
            service.sendDeleteRequest(rid);

        }
    }

    public static void main(String[] args) throws SQLException, IOException {

        createTable();
        int ORFnum = 1;
        String BlastString = "";
        List<String> ORFS = Arrays.asList("MKWVTFISLLFLFSSAYSRGVFRRDAHKSEVAHRFKDLGEENFKALVLIAFAQYLQQCP");     // Blast query sequence
        for (int i = 0; i< ORFS.size(); i++) {
            String ORFfile = GenerateXML(ORFS.get(i));
            String NextORF = convertXMLFileToString(ORFfile, ORFnum, BlastString);
            BlastString += NextORF;
            ORFnum += 1;
        }

    }

}