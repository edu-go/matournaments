package com.ma.torneos.ui;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.User;
import com.ma.torneos.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField txtUser = new JTextField(20);
    private final JPasswordField txtPass = new JPasswordField(20);
    private final JButton btnLogin = new JButton("Ingresar");
    private final AuthService authService = new AuthService(new JdbcConfig());

    public LoginFrame() {
        super("Gestor de Torneos - Login (DEMO)");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(380, 200);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("Usuario:"), gbc);
        gbc.gridx=1; form.add(txtUser, gbc);

        gbc.gridx=0; gbc.gridy=1; form.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx=1; form.add(txtPass, gbc);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnLogin);

        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> doLogin());

        // centrar
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth())/2, (screen.height - getHeight())/2);
    }

    private void doLogin() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese usuario y contraseña.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User logged = authService.authenticate(u, p);
        if (logged == null) {
            JOptionPane.showMessageDialog(this, "Credenciales inválidas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Abrimos ventana principal y cerramos el login
        SwingUtilities.invokeLater(() -> {
            new MainWindow(logged).setVisible(true);
            dispose();
        });
    }
}
