package com.ma.torneos.ui;

import javax.swing.*;
import java.awt.*;

public class SchoolCreateDialog extends JDialog {

    private final JTextField txtName = new JTextField(25);
    private final JTextField txtCity = new JTextField(20);
    private final JTextField txtPhone = new JTextField(15);

    private boolean confirmed = false;

    public SchoolCreateDialog(Window owner) {
        super(owner, "Nueva escuela", ModalityType.APPLICATION_MODAL);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx=1; form.add(txtName, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Ciudad:"), gbc);
        gbc.gridx=1; form.add(txtCity, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; form.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx=1; form.add(txtPhone, gbc); row++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Crear");
        JButton btnCancel = new JButton("Cancelar");
        buttons.add(btnOk);
        buttons.add(btnCancel);

        btnOk.addActionListener(e -> onCreate());
        btnCancel.addActionListener(e -> onCancel());

        getContentPane().setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void onCreate() {
        String name = txtName.getText().trim();
        String city = txtCity.getText().trim();

        if (name.isEmpty() || city.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre y ciudad son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
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

    public String getNameValue() {
        return txtName.getText().trim();
    }

    public String getCityValue() {
        return txtCity.getText().trim();
    }

    public String getPhoneValue() {
        return txtPhone.getText().trim();
    }
}
