package com.ma.torneos.ui;

import com.ma.torneos.domain.Competitor;

import javax.swing.*;
import java.awt.*;
import java.time.Period;
import java.time.LocalDate;

public class CompetitorDetailsDialog extends JDialog {

    public CompetitorDetailsDialog(Window owner, Competitor c) {
        super(owner, "Detalle de Competidor", ModalityType.APPLICATION_MODAL);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(panel, gbc, row++, "Nombre completo:", c.getFullName());
        addRow(panel, gbc, row++, "ID:", String.valueOf(c.getId()));
        addRow(panel, gbc, row++, "Fecha de nacimiento:", String.valueOf(c.getBirthdate()));
        addRow(panel, gbc, row++, "Edad (aprox):", String.valueOf(calcAge(c.getBirthdate())) + " años");
        addRow(panel, gbc, row++, "Género:", c.getGender());
        addRow(panel, gbc, row++, "Cinturón:", c.getBeltLevel());
        addRow(panel, gbc, row++, "Peso (kg):", String.valueOf(c.getWeightKg()));

        JButton btnClose = new JButton("Cerrar");
        btnClose.addActionListener(e -> dispose());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnClose);

        getContentPane().setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(value), gbc);
    }

    private int calcAge(LocalDate birthdate) {
        if (birthdate == null) return 0;
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}
