import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class DeORFVoorspeller extends JFrame{

    private JLabel titel;
    private JLabel labelBestand;
    private JTextField textField; // dit kan weg, weet niet hoe ik dit vanuit de andere class hierin moet zetten
    private JButton blader;
    private JButton voorspel;

    public static void main(String[] args) {
        DeORFVoorspeller frame = new DeORFVoorspeller();
        frame.greateGUI();
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    private void greateGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        window.setLayout(null);
        window.setBackground(Color.cyan);

        titel = new JLabel("Bestand met een DNA sequentie");
        titel.setBounds(10, 15, 400, 20);
        window.add(titel);

        labelBestand = new JLabel("Bestand");
        labelBestand.setBounds(10, 40, 50, 20);
        window.add(labelBestand);

        textField = new JTextField(25);
        textField.setBounds(70, 40, 250, 20);
        window.add(textField);

        blader = new JButton("Blader");
        blader.setBounds(340, 40, 100, 20);
        blader.addActionListener(new fileChooserButton());
        window.add(blader);

        voorspel = new JButton("Voorspel de ORF's");
        voorspel.setBounds(70, 70, 140, 20);
        voorspel.addActionListener(new Voorspel());
        window.add(voorspel);

        // print orf resultaten

        //print blast resultaten
    }

    class fileChooserButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Sequentie inleesobject = new Sequentie(); // miss inleesobject nog hernoemen
            try {
                inleesobject.fileChooser(); // roept de functie fileChooser aan in Sequentie
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    class Voorspel implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Sequentie inleesobject = new Sequentie(); // miss inleesobject nog hernoemen
            //inleesobject.NAAM VAN DE FUNCTIE VOOR VOORSPELLEN(); // roept de functie ...... aan in orfVoorspellen
        }
    }
}


