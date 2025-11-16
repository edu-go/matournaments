package com.ma.torneos.ui;

import com.ma.torneos.domain.Tournament;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TournamentFormDialog extends JDialog {

    private final JTextField txtName = new JTextField(25);
    private final JTextField txtDate = new JTextField(10); // yyyy-MM-dd
    private final JTextField txtLocation = new JTextField(25);
    private final JTextField txtRuleset = new JTextField(20);
    private final JComboBox<String> cbStatus =
            new JComboBox<>(new String[]{"DRAFT", "OPEN", "CLOSED", "ARCHIVED"});

    private boolean confirmed = false;
    private Tournament tournament;  // null = nuevo

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TournamentFormDialog(Window owner, Tournament toEdit) {
        super(owner, "Torneo", ModalityType.APPLICATION_MODAL);
        this.tournament = toEdit;

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; form.add(txtName, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Fecha (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; form.add(txtDate, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Lugar:"), gbc);
        gbc.gridx = 1; form.add(txtLocation, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Reglamento:"), gbc);
        gbc.gridx = 1; form.add(txtRuleset, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; form.add(cbStatus, gbc); row++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        buttons.add(btnOk);
        buttons.add(btnCancel);

        btnOk.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> onCancel());

        getContentPane().setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        if (tournament != null) {
            txtName.setText(tournament.getName());
            txtDate.setText(tournament.getStartDate().format(FMT));
            txtLocation.setText(tournament.getLocation());
            txtRuleset.setText(tournament.getRuleset());
            cbStatus.setSelectedItem(tournament.getStatus());
        } else {
            cbStatus.setSelectedItem("DRAFT");
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void onSave() {
        String name = txtName.getText().trim();
        String dateStr = txtDate.getText().trim();
        String location = txtLocation.getText().trim();
        String ruleset = txtRuleset.getText().trim();
        String status = (String) cbStatus.getSelectedItem();

        if (name.isEmpty() || dateStr.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre, fecha y lugar son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Use yyyy-MM-dd.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tournament == null) {
            tournament = new Tournament(null, name, date, location, ruleset, status);
        } else {
            tournament.setName(name);
            tournament.setStartDate(date);
            tournament.setLocation(location);
            tournament.setRuleset(ruleset);
            tournament.setStatus(status);
        }

        confirmed = true;
        dispose();
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Tournament getTournament() {
        return tournament;
    }
}
