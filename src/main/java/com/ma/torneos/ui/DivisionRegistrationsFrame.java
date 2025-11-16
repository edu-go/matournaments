package com.ma.torneos.ui;

import com.ma.torneos.domain.Division;
import com.ma.torneos.service.RegistrationDaoJdbcImpl;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionRegistrationsFrame extends JFrame {

    private final Division division;
    private final RegistrationDaoJdbcImpl dao = new RegistrationDaoJdbcImpl();
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

        loadData();

        setLocationRelativeTo(null);
    }

    private void loadData() {
        List<RegistrationView> regs = dao.findByDivisionId(division.getId());
        model.setData(regs);
    }

    private static class RegistrationTableModel extends AbstractTableModel {
        private final String[] cols = {"ID Inscripción", "ID Competidor", "Competidor", "Estado"};
        private List<RegistrationView> data = new ArrayList<>();

        public void setData(List<RegistrationView> list) {
            this.data = list != null ? list : new ArrayList<>();
            fireTableDataChanged();
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
