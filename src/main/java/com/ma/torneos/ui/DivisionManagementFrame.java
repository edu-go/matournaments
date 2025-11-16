package com.ma.torneos.ui;

import com.ma.torneos.domain.Division;
import com.ma.torneos.domain.Tournament;
import com.ma.torneos.service.DivisionService;
import com.ma.torneos.service.exception.BusinessException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionManagementFrame extends JFrame {

    private final Tournament tournament;
    private final DivisionService service = new DivisionService();
    private final DivisionTableModel model = new DivisionTableModel();
    private final JTable table = new JTable(model);

    public DivisionManagementFrame(Tournament tournament) {
        super("Divisiones - " + tournament.getName());
        this.tournament = tournament;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 400);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNew = new JButton("Nueva División");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Refrescar");
        buttons.add(btnNew);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        add(buttons, BorderLayout.NORTH);

        btnNew.addActionListener(e -> onNew());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
        setLocationRelativeTo(null);
    }

    private void loadData() {
        model.setData(service.listByTournament(tournament.getId()));
    }

    private Division getSelectedDivision() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return model.getAt(row);
    }

    private void onNew() {
        DivisionFormDialog dlg = new DivisionFormDialog(this, tournament.getId(), null);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            try {
                service.create(dlg.getDivision());
                loadData();
            } catch (BusinessException be) {
                JOptionPane.showMessageDialog(this, be.getMessage(),
                        "Reglas de negocio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al crear división:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        Division selected = getSelectedDivision();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una división.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        DivisionFormDialog dlg = new DivisionFormDialog(this, tournament.getId(), selected);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            try {
                service.update(dlg.getDivision());
                loadData();
            } catch (BusinessException be) {
                JOptionPane.showMessageDialog(this, be.getMessage(),
                        "Reglas de negocio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al actualizar división:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete() {
        Division selected = getSelectedDivision();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una división.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la división \"" + selected.getName() + "\"?",
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
                JOptionPane.showMessageDialog(this, "Error al eliminar división:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // TableModel interno
    private static class DivisionTableModel extends AbstractTableModel {
        private final String[] cols = {
                "ID", "Nombre", "Edad mín", "Edad máx",
                "Peso mín", "Peso máx", "Cinturón mín", "Cinturón máx", "Género"
        };
        private List<Division> data = new ArrayList<>();

        public void setData(List<Division> list) {
            this.data = list != null ? list : new ArrayList<>();
            fireTableDataChanged();
        }

        public Division getAt(int row) {
            return data.get(row);
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Division d = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> d.getId();
                case 1 -> d.getName();
                case 2 -> d.getAgeMin();
                case 3 -> d.getAgeMax();
                case 4 -> d.getWeightMin();
                case 5 -> d.getWeightMax();
                case 6 -> d.getBeltMin();
                case 7 -> d.getBeltMax();
                case 8 -> d.getGender();
                default -> "";
            };
        }
    }
}
