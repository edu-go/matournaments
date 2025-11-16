package com.ma.torneos.ui;

import com.ma.torneos.domain.Tournament;
import com.ma.torneos.service.TournamentService;
import com.ma.torneos.service.exception.BusinessException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentManagementFrame extends JFrame {

    private final JComboBox<String> cbFilterStatus =
            new JComboBox<>(new String[]{"TODOS", "DRAFT", "OPEN", "CLOSED", "ARCHIVED"});

    private List<Tournament> allTournaments = new ArrayList<>();
    private final TournamentService service = new TournamentService();
    private final TournamentTableModel model = new TournamentTableModel();
    private final JTable table = new JTable(model);

    public TournamentManagementFrame() {
        super("Gestión de Torneos");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNew = new JButton("Nuevo");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Refrescar");
        JButton btnApplyFilter = new JButton("Aplicar filtro");
        buttons.add(btnNew);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(new JLabel("Estado:"));
        buttons.add(cbFilterStatus);
        buttons.add(btnApplyFilter);
        buttons.add(btnRefresh);
        add(buttons, BorderLayout.NORTH);

        btnNew.addActionListener(e -> onNew());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnApplyFilter.addActionListener(e -> applyFilter());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
        setLocationRelativeTo(null);
    }

    private void loadData() {
        allTournaments = service.listAll();
        applyFilter();
    }

    private void applyFilter() {
        String statusFilter = (String) cbFilterStatus.getSelectedItem();
        if (statusFilter == null || statusFilter.equals("TODOS")) {
            model.setData(allTournaments);
            return;
        }
        List<Tournament> filtered = new java.util.ArrayList<>();
        for (Tournament t : allTournaments) {
            if (statusFilter.equalsIgnoreCase(t.getStatus())) {
                filtered.add(t);
            }
        }
        model.setData(filtered);
    }

    private Tournament getSelectedTournament() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return model.getAt(row);
    }

    private void onNew() {
        TournamentFormDialog dlg = new TournamentFormDialog(this, null);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            try {
                service.create(dlg.getTournament());
                loadData();
            } catch (BusinessException be) {
                JOptionPane.showMessageDialog(this, be.getMessage(),
                        "Reglas de negocio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al crear torneo:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        Tournament selected = getSelectedTournament();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        TournamentFormDialog dlg = new TournamentFormDialog(this, selected);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            try {
                service.update(dlg.getTournament());
                loadData();
            } catch (BusinessException be) {
                JOptionPane.showMessageDialog(this, be.getMessage(),
                        "Reglas de negocio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al actualizar torneo:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete() {
        Tournament selected = getSelectedTournament();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el torneo \"" + selected.getName() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                service.delete(selected.getId());
                loadData();
            } catch (BusinessException be) {
                JOptionPane.showMessageDialog(this, be.getMessage(),
                        "Reglas de negocio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar torneo:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ===== TableModel interno =====
    private static class TournamentTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Nombre", "Fecha", "Lugar", "Reglamento", "Estado"};
        private List<Tournament> data = new ArrayList<>();

        public void setData(List<Tournament> list) {
            this.data = list != null ? list : new ArrayList<>();
            fireTableDataChanged();
        }

        public Tournament getAt(int row) {
            return data.get(row);
        }

        @Override
        public int getRowCount() { return data.size(); }

        @Override
        public int getColumnCount() { return cols.length; }

        @Override
        public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Tournament t = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> t.getId();
                case 1 -> t.getName();
                case 2 -> t.getStartDate();
                case 3 -> t.getLocation();
                case 4 -> t.getRuleset();
                case 5 -> t.getStatus();
                default -> "";
            };
        }
    }
}
