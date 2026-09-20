
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SearchPanel extends JPanel {

    private MainFrame frame;
    private JTextField searchField;
    private JPanel resultsPanel;
    private JLabel statusLabel;

    public SearchPanel(MainFrame frame) {
        this.frame = frame;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F5F6FA"));

        add(createTopBar(),      BorderLayout.NORTH);
        add(createSearchArea(),  BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────
    // TOP BAR
    // ─────────────────────────────────────────────
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.decode("#FDCB6E"));
        topBar.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("🔍  Search Diary Entries");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.decode("#2D3436"));

        JButton backBtn = createTopButton("← Dashboard");
        backBtn.addActionListener(e -> frame.showScreen(MainFrame.DASHBOARD));

        topBar.add(title,   BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);
        return topBar;
    }

    // ─────────────────────────────────────────────
    // SEARCH AREA
    // ─────────────────────────────────────────────
    private JPanel createSearchArea() {
        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(Color.decode("#F5F6FA"));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // ── Search Box ──
        JPanel searchBox = new JPanel(new BorderLayout(10, 0));
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#DFE6E9"), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setForeground(Color.decode("#2D3436"));
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setToolTipText("Search by title, content or mood");

        JButton searchBtn = createSearchButton();

        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(searchBtn,  BorderLayout.EAST);

        // Press Enter to search
        searchField.addActionListener(e -> performSearch());

        // ── Hint label ──
        JLabel hintLabel = new JLabel("  Search by title, content or mood — press Enter or click Search");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(Color.decode("#B2BEC3"));

        // ── Status label ──
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(Color.decode("#636E72"));

        // ── Results panel ──
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.decode("#F5F6FA"));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.decode("#F5F6FA"));

        // ── Top section (search box + hint + status) ──
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(Color.decode("#F5F6FA"));
        topSection.add(searchBox);
        topSection.add(Box.createVerticalStrut(6));
        topSection.add(hintLabel);
        topSection.add(Box.createVerticalStrut(10));
        topSection.add(statusLabel);

        main.add(topSection,  BorderLayout.NORTH);
        main.add(scrollPane,  BorderLayout.CENTER);

        return main;
    }

    // ─────────────────────────────────────────────
    // PERFORM SEARCH
    // ─────────────────────────────────────────────
    private void performSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            statusLabel.setText("Please enter a keyword to search.");
            statusLabel.setForeground(Color.decode("#D63031"));
            resultsPanel.removeAll();
            resultsPanel.revalidate();
            resultsPanel.repaint();
            return;
        }

        ArrayList<DiaryEntry> results = DiaryManager.searchEntries(keyword);

        resultsPanel.removeAll();

        if (results.isEmpty()) {
            // Empty state
            JLabel noResult = new JLabel("No entries found for: \"" + keyword + "\"");
            noResult.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noResult.setForeground(Color.decode("#B2BEC3"));
            noResult.setAlignmentX(Component.CENTER_ALIGNMENT);

            resultsPanel.add(Box.createVerticalStrut(40));
            resultsPanel.add(noResult);

            statusLabel.setText("0 results found.");
            statusLabel.setForeground(Color.decode("#D63031"));
        } else {
            statusLabel.setText(results.size() + " result(s) found for: \"" + keyword + "\"");
            statusLabel.setForeground(Color.decode("#00B894"));

            for (DiaryEntry entry : results) {
                resultsPanel.add(createResultCard(entry, keyword));
                resultsPanel.add(Box.createVerticalStrut(10));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    // ─────────────────────────────────────────────
    // One result card
    // ─────────────────────────────────────────────
    private JPanel createResultCard(DiaryEntry entry, String keyword) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, Color.decode("#FDCB6E")),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Left: title + preview
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel(entry.getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(Color.decode("#2D3436"));

        String preview = entry.getContent();
        if (preview.length() > 80) preview = preview.substring(0, 80) + "...";
        JLabel previewLbl = new JLabel(preview);
        previewLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        previewLbl.setForeground(Color.decode("#636E72"));

        leftPanel.add(titleLbl);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(previewLbl);

        // Right: date + mood
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JLabel dateLbl = new JLabel(entry.getDate());
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(Color.decode("#636E72"));
        dateLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel moodLbl = new JLabel(entry.getMood());
        moodLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        moodLbl.setForeground(Color.decode("#4A90D9"));
        moodLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(dateLbl);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(moodLbl);

        card.add(leftPanel,  BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        // Click card to view full entry
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showFullEntry(entry);
            }
            public void mouseEntered(MouseEvent e) {
                card.setBackground(Color.decode("#FFFBF0"));
                leftPanel.setBackground(Color.decode("#FFFBF0"));
                rightPanel.setBackground(Color.decode("#FFFBF0"));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                leftPanel.setBackground(Color.WHITE);
                rightPanel.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    // ─────────────────────────────────────────────
    // Show full entry in dialog
    // ─────────────────────────────────────────────
    private void showFullEntry(DiaryEntry entry) {
        JTextArea area = new JTextArea(entry.getContent());
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(Color.decode("#F5F6FA"));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 150));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(new JLabel("Date: " + entry.getDate() + "   |   Mood: " + entry.getMood()));
        content.add(Box.createVerticalStrut(10));
        content.add(scroll);

        JOptionPane.showMessageDialog(frame, content,
            entry.getTitle(), JOptionPane.PLAIN_MESSAGE);
    }

    // ─────────────────────────────────────────────
    // Buttons
    // ─────────────────────────────────────────────
    private JButton createSearchButton() {
        JButton btn = new JButton("Search");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Color.decode("#FDCB6E"));
        btn.setForeground(Color.decode("#2D3436"));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 36));
        btn.addActionListener(e -> performSearch());
        return btn;
    }

    private JButton createTopButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.decode("#e6b84a"));
        btn.setForeground(Color.decode("#2D3436"));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
