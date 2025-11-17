package com.ma.torneos.ui;

import com.ma.torneos.domain.Role;
import com.ma.torneos.domain.User;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private final User user;

    public MainWindow(User user) {
        super("Gestor de Torneos - Demo");
        this.user = user;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 420);
        setLayout(new BorderLayout());

        // Barra de menú simple, cambia según rol
        JMenuBar menu = new JMenuBar();

        JMenu mFile = new JMenu("Archivo");
        JMenuItem miSalir = new JMenuItem("Salir");
        miSalir.addActionListener(e -> System.exit(0));
        mFile.add(miSalir);
        menu.add(mFile);

        JMenu mTorneo = new JMenu("Torneo");
        JMenuItem miGestion = new JMenuItem("Gestionar Torneo (DEMO)");
        miGestion.addActionListener(e -> SwingUtilities.invokeLater(() ->
                new TournamentManagementFrame().setVisible(true)
        ));
        mTorneo.add(miGestion);

        // habilitar solo para ADMIN en demo
        if (user.getRole() == Role.ADMIN) {
            menu.add(mTorneo);
            JMenu mEscuela = new JMenu("Escuelas");
            JMenuItem miAsignarOwner = new JMenuItem("Asociar escuela con dueño");

            miAsignarOwner.addActionListener(e ->
                    SwingUtilities.invokeLater(() ->
                            new SchoolOwnerAssignmentFrame().setVisible(true)
                    )
            );

            mEscuela.add(miAsignarOwner);
            menu.add(mEscuela);
        }

        // Para OWNER: menú de Inscripción
        if (user.getRole() == Role.OWNER) {
            JMenu mInscripcion = new JMenu("Inscripción");
            JMenuItem miInscribir = new JMenuItem("Inscribir a torneo");

            miInscribir.addActionListener(e ->
                    SwingUtilities.invokeLater(() ->
                            new TournamentRegistrationFrame(user).setVisible(true)
                    )
            );

            mInscripcion.add(miInscribir);
            menu.add(mInscripcion);
        }

        JMenu mReportes = new JMenu("Reportes");
        JMenuItem miRep = new JMenuItem("Ver Reportes (DEMO)");
        mReportes.add(miRep);

        // habilitar para ADMIN y ASSISTANT
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.ASSISTANT) {
            menu.add(mReportes);
        }

        setJMenuBar(menu);

        // Panel central
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setText("""
                Sesión iniciada:
                - Usuario: %s
                - Rol: %s
                - Email: %s

                * Esta es una DEMO sin persistencia real (datos hardcodeados).
                * Menús habilitados según el Rol (ADMIN/ASSISTANT/OWNER).
                """.formatted(user.getUsername(), user.getRole(), user.getEmail()));
        add(new JScrollPane(info), BorderLayout.CENTER);

        // centrar
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth())/2, (screen.height - getHeight())/2);
    }
}
