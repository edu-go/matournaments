package com.ma.torneos.ui;

import com.ma.torneos.domain.Division;
import com.ma.torneos.service.CompetitorDaoJdbcImpl;
import com.ma.torneos.service.RegistrationDaoJdbcImpl;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionRegistrationsFrame extends JFrame {

    private final Division division;
    private final RegistrationDaoJdbcImpl regDao = new RegistrationDaoJdbcImpl();
    private final CompetitorDaoJdbcImpl competitorDao = new CompetitorDaoJdbcImpl();
    private final RegistrationTableModel model = new RegistrationTableModel();
    private final JTable table = new JTable(model);

    public DivisionRegistrationsFrame(Division division) {
        super("Inscriptos - " + division.getName());
        this.division = division;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(700, 350);
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel north = new JPanel(new BorderLayout());
        north.add(new JLabel("División: " + division.getName(), SwingConstants.LEFT),
                BorderLayout.WEST);
        add(north, BorderLayout.NORTH);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnConfirm = new JButton("Confirmar");
        JButton btnWithdraw = new JButton("Marcar retirado");
        JButton btnViewCompetitor = new JButton("Ver competidor");
        JButton btnRefresh = new JButton("Refrescar");

        south.add(btnConfirm);
        south.add(btnWithdraw);
        south.add(btnViewCompetitor);
        south.add(btnRefresh);

        add(south, BorderLayout.SOUTH);

        btnConfirm.addActionListener(e -> changeStatus("CONFIRMED"));
        btnWithdraw.addActionListener(e -> changeStatus("WITHDRAWN"));
        btnViewCompetitor.addActionListener(e -> showCompetitor());
        btnRefresh.addActionListener(e -> loadData());

        loadData();

        setLocationRelativeTo(null);
    }

    private void loadData() {
        List<RegistrationView> regs = regDao.findByDivisionId(division.getId());
        model.setData(regs);
    }

    private RegistrationView getSelectedRegistration() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return model.getAt(row);
    }

    private void changeStatus(String newStatus) {
        RegistrationView sel = getSelectedRegistration();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un inscripto.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Pequeña regla de negocio simple en UI:
        if ("CONFIRMED".equals(sel.getStatus()) && "CONFIRMED".equals(newStatus)) {
            JOptionPane.showMessageDialog(this, "Ya está confirmado.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if ("WITHDRAWN".equals(sel.getStatus()) && "WITHDRAWN".equals(newStatus)) {
            JOptionPane.showMessageDialog(this, "Ya está marcado como retirado.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "¿Cambiar estado a " + newStatus + " para: " + sel.getCompetitorName() + "?",
                "Confirmar cambio de estado",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        regDao.updateStatus(sel.getRegistrationId(), newStatus);
        loadData();
    }

    private void showCompetitor() {
        RegistrationView sel = getSelectedRegistration();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un inscripto.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        competitorDao.findById(sel.getCompetitorId()).ifPresentOrElse(
                c -> {
                    CompetitorDetailsDialog dlg = new CompetitorDetailsDialog(this, c);
                    dlg.setVisible(true);
                },
                () -> JOptionPane.showMessageDialog(this,
                        "No se encontró el competidor en la base de datos.",
                        "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    private static class RegistrationTableModel extends AbstractTableModel {
        private final String[] cols = {"ID Inscripción", "ID Competidor", "Competidor", "Estado"};
        private List<RegistrationView> data = new ArrayList<>();

        public void setData(List<RegistrationView> list) {
            this.data = list != null ? list : new ArrayList<>();
            fireTableDataChanged();
        }

        public RegistrationView getAt(int row) {
            return data.get(row);
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RegistrationView r = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> r.getRegistrationId();
                case 1 -> r.getCompetitorId();
                case 2 -> r.getCompetitorName();
                case 3 -> r.getStatus();
                default -> "";
            };
        }
    }
}
