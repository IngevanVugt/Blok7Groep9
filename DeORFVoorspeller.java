import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class DeORFVoorspeller extends JFrame{

    private static JTextField textFieldBestand;
    private static File bestand;
    private static JTextArea textAreaDNASequentie;
    private static String DNASequentie;
    private static ArrayList<String> GevondenORFs;

    public static void main(String[] args) {
        DeORFVoorspeller frame = new DeORFVoorspeller();
        frame.greateGUI();
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    private void greateGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        window.setLayout(new FlowLayout());

        JPanel panel1 = new JPanel();
        panel1.setPreferredSize(new Dimension(600, 600));
        panel1.setBackground(Color.cyan);
        window.add(panel1);

        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(400, 20));
        panel2.setBackground(Color.cyan);
        panel1.add(panel2);

        JLabel titel = new JLabel("Bestand met een DNA sequentie");
        panel2.add(titel);

        JPanel panel3 = new JPanel();
        panel3.setBackground(Color.cyan);
        panel3.setPreferredSize(new Dimension(600, 30));
        panel1.add(panel3);

        JLabel labelBestand = new JLabel("Bestand:");
        panel3.add(labelBestand);

        textFieldBestand = new JTextField(25);
        panel3.add(textFieldBestand);

        JButton blader = new JButton("Blader");
        blader.addActionListener(new fileChooserButton());
        panel1.add(blader);

        JButton open = new JButton("Open");
        open.addActionListener(new bestandOpenen());
        panel1.add(open);

        JButton voorspel = new JButton("Voorspel de ORF's");
        voorspel.addActionListener(new Voorspel());
        panel1.add(voorspel);

        JButton blast = new JButton("Blast alle gevonden ORF's");
        blast.addActionListener(new Blast());
        panel1.add(blast);

        textAreaDNASequentie = new JTextArea(20, 50);
        JScrollPane scrollableTextArea = new JScrollPane(textAreaDNASequentie);
        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel1.add(scrollableTextArea);

        // print orf resultaten

        //print blast resultaten
    }
    static class fileChooserButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Sequentie inleesobject = new Sequentie(); // miss inleesobject nog hernoemen
            try {
                bestand = inleesobject.fileChooser(); // roept de functie fileChooser aan in Sequentie
                textFieldBestand.setText(bestand.getAbsolutePath());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static class bestandOpenen implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent actionEvent){
            try {
                DNASequentie = new Sequentie().SequentieIDOPhalen(bestand);
                textAreaDNASequentie.append("De Sequentie is succesvol ingeladen:" + "\n");
                textAreaDNASequentie.append(DNASequentie + "\n" + "\n");
            } catch (SQLException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    static class Voorspel implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                ArrayList<String> GevondenORFs = new ReadingFrame().ORFVoorspellen(DNASequentie);
                textAreaDNASequentie.append("De gevonden ORFs:" + "\n");
                int numORF = 1;
                for (String gevondenORF : GevondenORFs) {
                    textAreaDNASequentie.append(numORF + ": " + gevondenORF + "\n");
                    numORF += 1;
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    static class Blast implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEVent) {
            int ORFnum = 0;

            try {
                textAreaDNASequentie.append("waiting for blast results. this may take a while." + "\n");
                for (String gevondenORF : GevondenORFs) {
                    textAreaDNASequentie.append("blasting ORF no. " +  ORFnum + "please wait.\n");
                    String resultString = new blastORFs().main(GevondenORFs, ORFnum);
                    textAreaDNASequentie.append(resultString + "\n");
                    ORFnum += 1;
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
