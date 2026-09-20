
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // These are the different "screens" of the app
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Screen name constants — used to switch between screens
    public static final String WELCOME   = "WELCOME";
    public static final String DASHBOARD = "DASHBOARD";
    public static final String ADD_ENTRY = "ADD_ENTRY";
    public static final String VIEW_ENTRIES = "VIEW_ENTRIES";
    public static final String SEARCH    = "SEARCH";

    public MainFrame() {
        initWindow();
        initPanels();
        showScreen(WELCOME); // Start on Welcome screen
    }

    // ─────────────────────────────────────────────
    // Setup the main window properties
    // ─────────────────────────────────────────────
    private void initWindow() {
        setTitle("Personal Diary Management System");
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(true);

        // Set app background color
        getContentPane().setBackground(Color.decode("#F5F6FA"));
    }

    // ─────────────────────────────────────────────
    // Setup CardLayout — switches between screens
    // ─────────────────────────────────────────────
    private void initPanels() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.decode("#F5F6FA"));

        // Add all screens to the card layout
        mainPanel.add(new WelcomePanel(this),      WELCOME);
        mainPanel.add(new DashboardPanel(this),    DASHBOARD);
        mainPanel.add(new AddEntryPanel(this),     ADD_ENTRY);
        mainPanel.add(new ViewEntriesPanel(this),  VIEW_ENTRIES);
        mainPanel.add(new SearchPanel(this),       SEARCH);

        add(mainPanel);
    }

    // ─────────────────────────────────────────────
    // Switch to any screen by name
    // Called from all panels like: frame.showScreen(MainFrame.DASHBOARD)
    // ─────────────────────────────────────────────
    public void showScreen(String screenName) {
    cardLayout.show(mainPanel, screenName);

    // Refresh dashboard every time we go to it
    if (screenName.equals(DASHBOARD)) {
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof DashboardPanel) {
                ((DashboardPanel) comp).refreshData();
            }
        }
    }

    // Load entries every time View Entries is opened
    if (screenName.equals(VIEW_ENTRIES)) {
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof ViewEntriesPanel) {
                ((ViewEntriesPanel) comp).loadEntries();
            }
        }
    }
}


    // ─────────────────────────────────────────────
    // Refresh the view entries panel
    // ─────────────────────────────────────────────
    public void refreshViewEntries() {
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof ViewEntriesPanel) {
                ((ViewEntriesPanel) comp).loadEntries();
            }
        }
    }
}
