import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Deze class laat de GUI zien en zorgt voor interactie tussen verschillende classes
 * @author Inge van Vugt en Maite van den Noort
 * @version 2.0
 * @since 10-4-2020
 */


public class DeORFVoorspeller extends JFrame{

    private static JTextField textFieldBestand;
    private static File bestand;
    private static JTextArea textAreaDNASequentie;
    private static String DNASequentie;
    private static ArrayList<String> GevondenORFs;

    /**
     * De main zorgt voor een frame van de GUI en roept de functie createGUI().
     */
    public static void main(String[] args) {
        DeORFVoorspeller frame = new DeORFVoorspeller();
        frame.createGUI();
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    /**
     * Deze functie zorgt voor de layout van de GUI. Deze bestaat uit verschillende buttons die verscillende
     * classes aanroepen. Ook is er een tekstarea aanwezig voor de informatie die erin wordt weergegeven.
     */
    private void createGUI() {
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
        blader.addActionListener(new fileChooserButton()); // bestand kiezen
        panel1.add(blader);

        JButton open = new JButton("Open");
        open.addActionListener(new bestandOpenen()); //bestand openen
        panel1.add(open);

        JButton voorspel = new JButton("Voorspel de ORF's");
        voorspel.addActionListener(new Voorspel()); //ORF class
        panel1.add(voorspel);

        JButton blast = new JButton("Blast alle gevonden ORF's");
        blast.addActionListener(new Blast()); //blast class
        panel1.add(blast);

        textAreaDNASequentie = new JTextArea(20, 50);
        JScrollPane scrollableTextArea = new JScrollPane(textAreaDNASequentie); // een scrollbar aanmaken
        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel1.add(scrollableTextArea);
    }


    /**
     * Deze class zorgt ervoor dat er naar de andere class de functie voor de filechooser wordt aangeroepen
     * en dat het mogelijk wordt gemaakt om een bestand te kiezen, deze wordt weergegeven in de GUI.
     */
    static class fileChooserButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Sequentie inleesobject = new Sequentie();
            try {
                bestand = inleesobject.fileChooser(); // Gaat de FileChooser functie aanroepen
                textFieldBestand.setText(bestand.getAbsolutePath());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deze class zorgt ervoor dat er naar de andere class de functie voor het inlezen van het bestand wordt aangeroepen
     * Nadat deze is ingelezen en geen foutmeldingen zijn opgetreden wordt de sequentie weergegeven in de GUI.
     */
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

    /**
     * Deze class zorgt ervoor dat er naar de andere class de functie voor het voorspellen van de ORFs worden
     * voorspeld. Na het voorspellen van de ORFs worden alle gevonden ORFs in de GUI weergegeven.
     */
    static class Voorspel implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                GevondenORFs = new ReadingFrame().ORFVoorspellen(DNASequentie); //Functie voor ORFs voorspellen
                textAreaDNASequentie.append("De gevonden ORFs:" + "\n");
                for (String gevondenORF : GevondenORFs) {
                    textAreaDNASequentie.append(gevondenORF + "\n");
                }
                textAreaDNASequentie.append("\n");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deze class zorgt ervoor dat er naar de andere class de functie voor het blasten van alle ORFs wordt aangeroepen
     * Na het blasten wordt steeds het resultaat van de blast weergegeven in de GUI.
     */
    static class Blast implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEVent) {
            try {
                textAreaDNASequentie.append("waiting for blast results. this may take a while." + "\n");
                for(int i = 0; i < GevondenORFs.size(); i++) {
                    textAreaDNASequentie.append("blasting ORF no. " +  (i+1) + " please wait.\n");
                    new blastORFs();
                    String resultString = blastORFs.main(GevondenORFs, i); //ORF blasten
                    textAreaDNASequentie.append(resultString + "\n");
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}