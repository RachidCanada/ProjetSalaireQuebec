import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class Main extends JFrame {
    private JTextField salaireAnnuelBrutField, heuresTravailSemaineField, semainesTravailleesAnneeField;
    private JEditorPane resultArea;

    public Main() {
        setTitle("Calculateur de Salaire Net");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
    }

    private void createUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2));

        JLabel salaireLabel = new JLabel("Salaire Annuel Brut : " );
        salaireAnnuelBrutField = new JTextField();
        JLabel heuresLabel = new JLabel("Heures travaillées par semaine : "  );
        heuresTravailSemaineField = new JTextField();
        JLabel semainesLabel = new JLabel("Semaines travaillées par an : " );
        semainesTravailleesAnneeField = new JTextField();

        JButton calculerButton = new JButton("Calculer");
        calculerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculerSalaire();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(calculerButton);

        resultArea = new JEditorPane();
        resultArea.setContentType("text/html");
        resultArea.setEditable(false);

        mainPanel.add(salaireLabel);
        mainPanel.add(salaireAnnuelBrutField);
        mainPanel.add(heuresLabel);
        mainPanel.add(heuresTravailSemaineField);
        mainPanel.add(semainesLabel);
        mainPanel.add(semainesTravailleesAnneeField);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private void afficherResultats(double salaireAnnuelBrut, double salaireAnnuelNet, double salaireHebdomadaireNet, double salaireHoraireNet, double rrq, double ae, double rqap, double impotQuebec, double impotFederal) {
        DecimalFormat df = new DecimalFormat("#.##");

        String resultText = "<html><body style='font-size:14px;'>";

        resultText += "<b>Résultats :</b><br>" +
                "<b>Salaire annuel brut :</b> " + df.format(salaireAnnuelBrut) + " $<br>" +
                "<b>Salaire annuel net :</b> " + df.format(salaireAnnuelNet) + " $<br>" +
                "<b>Salaire hebdomadaire net :</b> " + df.format(salaireHebdomadaireNet) + " $<br>" +
                "<b>Salaire horaire net :</b> " + df.format(salaireHoraireNet) + " $<br><br>" +
                "<b>Retenues Salariales et Impôts :</b><br>" +
                "Cotisation au RRQ : " + df.format(rrq) + " $<br>" +
                "Cotisation à l'AE : " + df.format(ae) + " $<br>" +
                "Cotisation au RQAP : " + df.format(rqap) + " $<br>" +
                "Impôt Québec : " + df.format(impotQuebec) + " $<br>" +
                "Impôt Fédéral : " + df.format(impotFederal) + " $";


        resultText += "</body></html>";

        resultArea.setText(resultText);
    }

    private void calculerSalaire() {
        try {
            double salaireAnnuelBrut = Double.parseDouble(salaireAnnuelBrutField.getText());
            int heuresTravailSemaine = Integer.parseInt(heuresTravailSemaineField.getText());
            int semainesTravailleesAnnee = Integer.parseInt(semainesTravailleesAnneeField.getText());

            double rrq = calculerRRQ(salaireAnnuelBrut);
            double ae = calculerAE(salaireAnnuelBrut);
            double rqap = calculerRQAP(salaireAnnuelBrut);
            double[] impots = calculerImpots(salaireAnnuelBrut);

            double salaireAnnuelNet = salaireAnnuelBrut - impots[0] - rrq - ae - rqap;
            double salaireHebdomadaireNet = salaireAnnuelNet / semainesTravailleesAnnee;
            double salaireHoraireNet = salaireHebdomadaireNet / heuresTravailSemaine;

            afficherResultats(salaireAnnuelBrut, salaireAnnuelNet, salaireHebdomadaireNet, salaireHoraireNet, rrq, ae, rqap, impots[0], impots[1]);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides.", "Erreur de Saisie", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculerRRQ(double salaireAnnuelBrut) {
        double tauxCotisationRRQ = 0.118; // Taux de cotisation au RRQ en 2021
        return salaireAnnuelBrut * tauxCotisationRRQ / 2;
    }

    private double calculerAE(double salaireAnnuelBrut) {
        double tauxCotisationAE = 0.0118; // Taux de cotisation à l'AE pour les Québécois en 2021
        return Math.min(930.08, salaireAnnuelBrut * tauxCotisationAE);
    }

    private double calculerRQAP(double salaireAnnuelBrut) {
        double tauxCotisationRQAP = 0.00692; // Taux de cotisation au RQAP en 2021
        return Math.min(577.82, salaireAnnuelBrut * tauxCotisationRQAP);
    }

    private double[] calculerImpots(double salaireAnnuelBrut) {
        double impotQuebec = calculerImpotQuebec(salaireAnnuelBrut);
        double impotFederal = calculerImpotFederal(salaireAnnuelBrut);
        return new double[]{impotQuebec, impotFederal};
    }

    private double calculerImpotQuebec(double revenuImposable) {
        double montantNonImposableQuebec = 18056;
        double impotQuebec = 0;

        if (revenuImposable > montantNonImposableQuebec) {
            if (revenuImposable <= 51780) {
                impotQuebec = (revenuImposable - montantNonImposableQuebec) * 0.14;
            } else if (revenuImposable <= 103545) {
                impotQuebec = (51780 - montantNonImposableQuebec) * 0.14 + (revenuImposable - 51780) * 0.19;
            } else if (revenuImposable <= 126000) {
                impotQuebec = (51780 - montantNonImposableQuebec) * 0.14 + (103545 - 51780) * 0.19 + (revenuImposable - 103545) * 0.24;
            } else {
                impotQuebec = (51780 - montantNonImposableQuebec) * 0.14 + (103545 - 51780) * 0.19 + (126000 - 103545) * 0.24 + (revenuImposable - 126000) * 0.2575;
            }
        }
        return impotQuebec;
    }

    private double calculerImpotFederal(double revenuImposable) {
        double montantNonImposableFederal = 14156;
        double montantNonImposableFederalAugmente = 15705;
        double impotFederal = 0;

        if (revenuImposable > montantNonImposableFederal) {
            if (revenuImposable <= 55867) {
                impotFederal = (revenuImposable - montantNonImposableFederal) * 0.12525;
            } else if (revenuImposable <= 111733) {
                impotFederal = (55867 - montantNonImposableFederal) * 0.12525 + (revenuImposable - 55867) * 0.15;
            } else if (revenuImposable <= 173205) {
                impotFederal = (55867 - montantNonImposableFederal) * 0.12525 + (111733 - 55867) * 0.15 + (revenuImposable - 111733) * 0.205;
            } else if (revenuImposable <= 246752) {
                impotFederal = (55867 - montantNonImposableFederal) * 0.12525 + (111733 - 55867) * 0.15 + (173205 - 111733) * 0.205 + (revenuImposable - 173205) * 0.26;
            } else {
                impotFederal = (55867 - montantNonImposableFederal) * 0.12525 + (111733 - 55867) * 0.15 + (173205 - 111733) * 0.205 + (246752 - 173205) * 0.26 + (revenuImposable - 246752) * 0.3355;
            }

            if (revenuImposable <= montantNonImposableFederalAugmente) {
                impotFederal -= (montantNonImposableFederalAugmente - montantNonImposableFederal) * 0.12525;
            }
        }

        return impotFederal;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
