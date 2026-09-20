

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

public class DashboardPanel extends JPanel {

    private MainFrame frame;
    private JLabel totalEntriesLabel;
    private JLabel dateLabel;
    private JPanel recentEntriesPanel;

    public DashboardPanel(MainFrame frame) {
        this.frame = frame;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F5F6FA"));

        add(createTopBar(),    BorderLayout.NORTH);
        add(createMainArea(),  BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────
    // TOP BAR — App name + date
    // ─────────────────────────────────────────────
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.decode("#4A90D9"));
        topBar.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        // Left: App title
        JLabel appTitle = new JLabel("📖  My Personal Diary");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setForeground(Color.WHITE);

        // Right: Today's date
        dateLabel = new JLabel(DiaryManager.getTodayDate());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(Color.decode("#D6EAF8"));

        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(dateLabel, BorderLayout.EAST);

        return topBar;
    }

    // ─────────────────────────────────────────────
    // MAIN AREA — Stats + Buttons + Recent entries
    // ─────────────────────────────────────────────
    private JPanel createMainArea() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(Color.decode("#F5F6FA"));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        main.add(createStatsAndButtons(), BorderLayout.NORTH);
        main.add(createRecentSection(),   BorderLayout.CENTER);

        return main;
    }

    // ─────────────────────────────────────────────
    // STATS CARD + ACTION BUTTONS
    // ─────────────────────────────────────────────
    private JPanel createStatsAndButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.decode("#F5F6FA"));

        // Stat card — Total Entries
        totalEntriesLabel = new JLabel("0", SwingConstants.CENTER);
        panel.add(createStatCard("Total Entries", totalEntriesLabel, "#4A90D9"));

        // Action buttons
        panel.add(createActionCard("➕  Add Entry",    "#00B894", MainFrame.ADD_ENTRY));
        panel.add(createActionCard("📋  View All",     "#6C5CE7", MainFrame.VIEW_ENTRIES));
        panel.add(createActionCard("🔍  Search",       "#FDCB6E", MainFrame.SEARCH));

        return panel;
    }

    // ─────────────────────────────────────────────
    // Single stat card (shows a number)
    // ─────────────────────────────────────────────
    private JPanel createStatCard(String label, JLabel valueLabel, String color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#DFE6E9"), 1),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JLabel title = new JLabel(label, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        title.setForeground(Color.decode("#636E72"));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.decode(color));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    // ─────────────────────────────────────────────
    // Single action card (clickable button card)
    // ─────────────────────────────────────────────
    private JPanel createActionCard(String label, String color, String screen) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.decode(color));
        card.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);

        card.add(lbl);

        // Click anywhere on card to navigate
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                frame.showScreen(screen);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(Color.decode(color).darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.decode(color));
            }
        });

        return card;
    }

    // ─────────────────────────────────────────────
    // RECENT ENTRIES section (bottom half)
    // ─────────────────────────────────────────────
    private JPanel createRecentSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(Color.decode("#F5F6FA"));

        // Section heading
        JLabel heading = new JLabel("  Recent Entries");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setForeground(Color.decode("#2D3436"));

        // Container for recent entry rows
        recentEntriesPanel = new JPanel();
        recentEntriesPanel.setLayout(new BoxLayout(recentEntriesPanel, BoxLayout.Y_AXIS));
        recentEntriesPanel.setBackground(Color.decode("#F5F6FA"));

        JScrollPane scrollPane = new JScrollPane(recentEntriesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.decode("#F5F6FA"));

        section.add(heading,    BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    // ─────────────────────────────────────────────
    // One recent entry row card
    // ─────────────────────────────────────────────
    private JPanel createEntryRow(DiaryEntry entry) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, Color.decode("#4A90D9")),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Left: Title
        JLabel titleLbl = new JLabel(entry.getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(Color.decode("#2D3436"));

        // Right: Date + Mood
        JLabel rightLbl = new JLabel(entry.getDate() + "  |  " + entry.getMood());
        rightLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rightLbl.setForeground(Color.decode("#636E72"));

        row.add(titleLbl, BorderLayout.WEST);
        row.add(rightLbl, BorderLayout.EAST);

        return row;
    }

    // ─────────────────────────────────────────────
    // REFRESH — called every time Dashboard is shown
    // ─────────────────────────────────────────────
    public void refreshData() {
        // Update total count
        int total = DiaryManager.getTotalEntries();
        totalEntriesLabel.setText(String.valueOf(total));

        // Update recent entries list
        recentEntriesPanel.removeAll();

        ArrayList<DiaryEntry> entries = DiaryManager.getAllEntries();

        if (entries.isEmpty()) {
            JLabel emptyLabel = new JLabel("  No diary entries yet. Click 'Add Entry' to start!", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.decode("#B2BEC3"));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            recentEntriesPanel.add(Box.createVerticalStrut(30));
            recentEntriesPanel.add(emptyLabel);
        } else {
            // Show most recent 5 entries (last entries in list)
            int start = Math.max(0, entries.size() - 5);
            for (int i = entries.size() - 1; i >= start; i--) {
                recentEntriesPanel.add(createEntryRow(entries.get(i)));
                recentEntriesPanel.add(Box.createVerticalStrut(8));
            }
        }

        recentEntriesPanel.revalidate();
        recentEntriesPanel.repaint();
    }
}
