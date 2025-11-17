package com.ma.torneos.ui;

import com.ma.torneos.domain.School;
import com.ma.torneos.domain.User;
import com.ma.torneos.service.SchoolOwnerService;
import com.ma.torneos.service.exception.BusinessException;

import javax.swing.*;
import java.awt.*;

import java.util.List;

public class SchoolOwnerAssignmentFrame extends JFrame {

    private final SchoolOwnerService service = new SchoolOwnerService();

    private final JComboBox<School> cbSchool = new JComboBox<>();
    private final JComboBox<User> cbOwner = new JComboBox<>();
    private final JLabel lblCurrentOwner = new JLabel("Dueño actual: (sin asignar)");

    public SchoolOwnerAssignmentFrame() {
        super("Asociar Escuela con Usuario Dueño");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 260);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Escuela:"), gbc);
        gbc.gridx = 1; form.add(cbSchool, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; form.add(new JLabel("Usuario OWNER:"), gbc);
        gbc.gridx = 1; form.add(cbOwner, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(lblCurrentOwner, gbc);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNewSchool = new JButton("Nueva escuela");
        JButton btnSave = new JButton("Guardar asociación");
        JButton btnClose = new JButton("Cerrar");
        south.add(btnNewSchool);
        south.add(btnSave);
        south.add(btnClose);

        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        btnNewSchool.addActionListener(e -> onNewSchool());
        btnSave.addActionListener(e -> onSave());
        btnClose.addActionListener(e -> dispose());

        cbSchool.addActionListener(e -> updateCurrentOwnerLabel());

        loadData();

        setLocationRelativeTo(null);
    }

    private void loadData() {
        cbSchool.removeAllItems();
        cbOwner.removeAllItems();

        List<School> schools = service.listAllSchools();
        for (School s : schools) {
            cbSchool.addItem(s);
        }

        List<User> owners = service.listAllOwners();
        for (User u : owners) {
            cbOwner.addItem(u);
        }

        if (cbSchool.getItemCount() > 0) {
            cbSchool.setSelectedIndex(0);
            updateCurrentOwnerLabel();
        } else {
            lblCurrentOwner.setText("No hay escuelas cargadas.");
        }
    }

    private void updateCurrentOwnerLabel() {
        School s = (School) cbSchool.getSelectedItem();
        if (s == null) {
            lblCurrentOwner.setText("Dueño actual: (sin asignar)");
            return;
        }
        if (s.getOwnerUserId() == null) {
            lblCurrentOwner.setText("Dueño actual: (sin asignar)");
        } else {
            // Buscamos el owner en la lista del combo para mostrar su username
            User found = null;
            for (int i = 0; i < cbOwner.getItemCount(); i++) {
                User u = cbOwner.getItemAt(i);
                if (u.getId().equals(s.getOwnerUserId())) {
                    found = u;
                    break;
                }
            }
            if (found != null) {
                lblCurrentOwner.setText("Dueño actual: " + found.getUsername()
                        + " (" + found.getEmail() + ")");
            } else {
                lblCurrentOwner.setText("Dueño actual: (ID=" + s.getOwnerUserId() + ", no encontrado como OWNER)");
            }
        }
    }

    private void onSave() {
        School s = (School) cbSchool.getSelectedItem();
        User u = (User) cbOwner.getSelectedItem();

        if (s == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una escuela.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (u == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario OWNER.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "¿Asignar como dueño de \"" + s.getName() + "\" al usuario \"" +
                        u.getUsername() + "\"?",
                "Confirmar asociación",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            service.assignOwnerToSchool(s.getId(), u.getId());
            JOptionPane.showMessageDialog(this,
                    "Asociación guardada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // refrescamos escuelas para reflejar el nuevo owner_user_id
            loadData();
        } catch (BusinessException be) {
            JOptionPane.showMessageDialog(this,
                    be.getMessage(),
                    "Reglas de negocio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar asociación:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onNewSchool() {
        // Primero verificamos que haya un OWNER seleccionado
        User owner = (User) cbOwner.getSelectedItem();
        if (owner == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario OWNER para asociar a la nueva escuela.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SchoolCreateDialog dlg = new SchoolCreateDialog(this);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;

        try {
            service.createSchool(
                    owner.getId(),
                    dlg.getNameValue(),
                    dlg.getCityValue(),
                    dlg.getPhoneValue()
            );
            JOptionPane.showMessageDialog(this,
                    "Escuela creada correctamente y asociada a " + owner.getUsername() + ".",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadData();  // recarga combo de escuelas y etiqueta de dueño actual
        } catch (BusinessException be) {
            JOptionPane.showMessageDialog(this,
                    be.getMessage(),
                    "Reglas de negocio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al crear escuela:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
