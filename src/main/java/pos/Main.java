package pos;

import pos.controller.LoginController;
import pos.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // default look and feel is fine
        }
        SwingUtilities.invokeLater(() -> new LoginController(new LoginFrame()).show());
    }
}
