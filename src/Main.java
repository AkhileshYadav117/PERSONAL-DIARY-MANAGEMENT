import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        // Ensure file exists before UI loads
        FileHandler.ensureFileExists();

        // Use system look and feel for better appearance on Windows
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If it fails, default Swing look is used — no problem
        }

        // Launch GUI on the Event Dispatch Thread (safe Swing practice)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
