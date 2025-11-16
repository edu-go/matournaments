package com.ma.torneos;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}