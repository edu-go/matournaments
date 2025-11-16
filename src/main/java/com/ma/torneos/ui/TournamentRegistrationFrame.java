package com.ma.torneos.ui;

import com.ma.torneos.domain.Competitor;
import com.ma.torneos.domain.School;
import com.ma.torneos.domain.Tournament;
import com.ma.torneos.domain.User;
import com.ma.torneos.service.RegistrationService;
import com.ma.torneos.service.exception.BusinessException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentRegistrationFrame extends JFrame {

    private final User owner;
    private final RegistrationService service = new RegistrationService();

    private final JComboBox<School> cbSchool = new JComboBox<>();
    private final JComboBox<Tournament> cbTournament = new JComboBox<>();
    private final CompetitorTableModel model = new CompetitorTableModel();
    private final JTable tblCompetitors = new JTable(model);

    public TournamentRegistrationFrame(User owner) {
        super("Inscripción a Torneo - Dueño de escuela");
        this.owner = owner;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 450);
        setLayout(new BorderLayout());

        JPanel north = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx=0; gbc.gridy=row; north.add(new JLabel("Dueño:"), gbc);
        gbc.gridx=1; north.add(new JLabel(owner.getUsername() + " (" + owner.getEmail() + ")"), gbc); row++;

        gbc.gridx=0; gbc.gridy=row; north.add(new JLabel("Escuela:"), gbc);
        gbc.gridx=1; north.add(cbSchool, gbc); row++;

        gbc.gridx=0; gbc.gridy=row; north.add(new JLabel("Torneo (OPEN):"), gbc);
        gbc.gridx=1; north.add(cbTournament, gbc); row++;

        add(north, BorderLayout.NORTH);

        add(new JScrollPane(tblCompetitors), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegister = new JButton("Inscribir seleccionado");
        JButton btnClose = new JButton("Cerrar");
        south.add(btnRegister);
        south.add(btnClose);
        add(south, BorderLayout.SOUTH);

        cbSchool.addActionListener(e -> loadCompetitorsForSelectedSchool());
        btnRegister.addActionListener(e -> doRegister());
        btnClose.addActionListener(e -> dispose());

        loadSchools();
        loadTournaments();

        setLocationRelativeTo(null);
    }

    private void loadSchools() {
        cbSchool.removeAllItems();
        var schools = service.findSchoolsForOwner(owner.getId());
        for (School s : schools) {
            cbSchool.addItem(s);
        }
        if (cbSchool.getItemCount() > 0) {
            cbSchool.setSelectedIndex(0);
            loadCompetitorsForSelectedSchool();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron escuelas asociadas a este usuario.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadCompetitorsForSelectedSchool() {
        School s = (School) cbSchool.getSelectedItem();
        if (s == null) {
            model.setData(new ArrayList<>());
            return;
        }
        var competitors = service.findCompetitorsBySchool(s.getId());
        model.setData(competitors);
    }

    private void loadTournaments() {
        cbTournament.removeAllItems();
        var tournaments = service.findOpenTournaments();
        for (Tournament t : tournaments) {
            cbTournament.addItem(t);
        }
        if (cbTournament.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay torneos en estado OPEN disponibles para inscripción.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private Competitor getSelectedCompetitor() {
        int row = tblCompetitors.getSelectedRow();
        if (row < 0) return null;
        return model.getAt(row);
    }

    private void doRegister() {
        School s = (School) cbSchool.getSelectedItem();
        Tournament t = (Tournament) cbTournament.getSelectedItem();
        Competitor c = getSelectedCompetitor();

        if (s == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una escuela.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo (OPEN).",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un competidor.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "¿Inscribir a " + c.getFullName() +
                        " en el torneo \"" + t.getName() + "\"?",
                "Confirmar inscripción",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            service.register(owner.getId(), s.getId(), c.getId(), t.getId());
            JOptionPane.showMessageDialog(this,
                    "Inscripción realizada correctamente (estado PENDING).",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (BusinessException be) {
            JOptionPane.showMessageDialog(this,
                    be.getMessage(),
                    "Reglas de negocio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al inscribir:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== TableModel internos =====
    private static class CompetitorTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Nombre", "Nacimiento", "Género", "Cinturón", "Peso (kg)"};
        private List<Competitor> data = new ArrayList<>();

        public void setData(List<Competitor> list) {
            this.data = list != null ? list : new ArrayList<>();
            fireTableDataChanged();
        }

        public Competitor getAt(int row) {
            return data.get(row);
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Competitor c = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> c.getId();
                case 1 -> c.getFullName();
                case 2 -> c.getBirthdate();
                case 3 -> c.getGender();
                case 4 -> c.getBeltLevel();
                case 5 -> c.getWeightKg();
                default -> "";
            };
        }
    }
}
