package com.ma.torneos.ui;

import com.ma.torneos.domain.Division;

import javax.swing.*;
import java.awt.*;

public class DivisionFormDialog extends JDialog {

    private final JTextField txtName = new JTextField(25);
    private final JTextField txtAgeMin = new JTextField(5);
    private final JTextField txtAgeMax = new JTextField(5);
    private final JTextField txtWeightMin = new JTextField(5);
    private final JTextField txtWeightMax = new JTextField(5);
    private final JTextField txtBeltMin = new JTextField(10);
    private final JTextField txtBeltMax = new JTextField(10);
    private final JComboBox<String> cbGender =
            new JComboBox<>(new String[]{"M", "F", "X"});

    private boolean confirmed = false;
    private Division division;
    private final Long tournamentId;

    public DivisionFormDialog(Window owner, Long tournamentId, Division toEdit) {
        super(owner, "División", ModalityType.APPLICATION_MODAL);
        this.tournamentId = tournamentId;
        this.division = toEdit;

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; form.add(txtName, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Edad mín:"), gbc);
        gbc.gridx = 1; form.add(txtAgeMin, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Edad máx:"), gbc);
        gbc.gridx = 1; form.add(txtAgeMax, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Peso mín:"), gbc);
        gbc.gridx = 1; form.add(txtWeightMin, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Peso máx:"), gbc);
        gbc.gridx = 1; form.add(txtWeightMax, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Cinturón mín:"), gbc);
        gbc.gridx = 1; form.add(txtBeltMin, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Cinturón máx:"), gbc);
        gbc.gridx = 1; form.add(txtBeltMax, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Género:"), gbc);
        gbc.gridx = 1; form.add(cbGender, gbc); row++;

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

        if (division != null) {
            txtName.setText(division.getName());
            txtAgeMin.setText(String.valueOf(division.getAgeMin()));
            txtAgeMax.setText(String.valueOf(division.getAgeMax()));
            txtWeightMin.setText(String.valueOf(division.getWeightMin()));
            txtWeightMax.setText(String.valueOf(division.getWeightMax()));
            txtBeltMin.setText(division.getBeltMin());
            txtBeltMax.setText(division.getBeltMax());
            cbGender.setSelectedItem(division.getGender());
        } else {
            cbGender.setSelectedItem("M");
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void onSave() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ageMin, ageMax;
        double wMin, wMax;
        try {
            ageMin = Integer.parseInt(txtAgeMin.getText().trim());
            ageMax = Integer.parseInt(txtAgeMax.getText().trim());
            wMin = Double.parseDouble(txtWeightMin.getText().trim());
            wMax = Double.parseDouble(txtWeightMax.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Edad/Peso deben ser numéricos.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String beltMin = txtBeltMin.getText().trim();
        String beltMax = txtBeltMax.getText().trim();
        String gender = (String) cbGender.getSelectedItem();

        if (division == null) {
            division = new Division(null, tournamentId, name,
                    ageMin, ageMax, wMin, wMax, beltMin, beltMax, gender);
        } else {
            division.setName(name);
            division.setTournamentId(tournamentId);
            division.setAgeMin(ageMin);
            division.setAgeMax(ageMax);
            division.setWeightMin(wMin);
            division.setWeightMax(wMax);
            division.setBeltMin(beltMin);
            division.setBeltMax(beltMax);
            division.setGender(gender);
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

    public Division getDivision() {
        return division;
    }
}
