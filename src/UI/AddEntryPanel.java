

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class AddEntryPanel extends JPanel {

    private MainFrame frame;
    private JTextField titleField;
    private JTextArea contentArea;
    private JComboBox<String> moodCombo;
    private JLabel messageLabel;

    public AddEntryPanel(MainFrame frame) {
        this.frame = frame;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F5F6FA"));

        add(createTopBar(),   BorderLayout.NORTH);
        add(createForm(),     BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────
    // TOP BAR
    // ─────────────────────────────────────────────
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.decode("#00B894"));
        topBar.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("➕  Add New Diary Entry");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        // Back button
        JButton backBtn = createTopButton("← Dashboard");
        backBtn.addActionListener(e -> {
            clearForm();
            frame.showScreen(MainFrame.DASHBOARD);
        });

        topBar.add(title,   BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);

        return topBar;
    }

    // ─────────────────────────────────────────────
    // FORM AREA
    // ─────────────────────────────────────────────
    private JPanel createForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.decode("#F5F6FA"));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#DFE6E9"), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // ── Date (auto, read-only) ──
        JLabel dateLabel = new JLabel("Date (Auto)");
        styleLabel(dateLabel);

        JTextField dateField = new JTextField(DiaryManager.getTodayDate());
        dateField.setEditable(false);
        dateField.setBackground(Color.decode("#F5F6FA"));
        dateField.setForeground(Color.decode("#636E72"));
        styleField(dateField);

        // ── Title ──
        JLabel titleLabel = new JLabel("Title *");
        styleLabel(titleLabel);

        titleField = new JTextField();
        titleField.setToolTipText("Enter diary title (required)");
        styleField(titleField);

        // ── Content ──
        JLabel contentLabel = new JLabel("Content *");
        styleLabel(contentLabel);

        contentArea = new JTextArea(6, 20);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setForeground(Color.decode("#2D3436"));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#DFE6E9"), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        contentScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // ── Mood ──
        JLabel moodLabel = new JLabel("Mood *");
        styleLabel(moodLabel);

        String[] moods = {"Happy", "Sad", "Excited", "Angry", "Anxious", "Grateful", "Tired", "Motivated", "Relaxed", "Bored"};
        moodCombo = new JComboBox<>(moods);
        moodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moodCombo.setBackground(Color.WHITE);
        moodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // ── Message label (success/error) ──
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Buttons ──
        JPanel btnPanel = createButtonPanel();

        // Add everything to card
        card.add(dateLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(dateField);
        card.add(Box.createVerticalStrut(15));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleField);
        card.add(Box.createVerticalStrut(15));

        card.add(contentLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(contentScroll);
        card.add(Box.createVerticalStrut(15));

        card.add(moodLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(moodCombo);
        card.add(Box.createVerticalStrut(20));

        card.add(messageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(btnPanel);

        wrapper.add(card);
        return wrapper;
    }

    // ─────────────────────────────────────────────
    // BUTTONS — Save + Clear
    // ─────────────────────────────────────────────
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton saveBtn  = createStyledButton("💾  Save Entry",  "#00B894", Color.WHITE);
        JButton clearBtn = createStyledButton("🗑  Clear",       "#DFE6E9", Color.decode("#2D3436"));

        saveBtn.addActionListener(e -> saveEntry());
        clearBtn.addActionListener(e -> clearForm());

        panel.add(saveBtn);
        panel.add(clearBtn);
        return panel;
    }

    // ─────────────────────────────────────────────
    // SAVE ENTRY logic
    // ─────────────────────────────────────────────
    private void saveEntry() {
        String title   = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String mood    = (String) moodCombo.getSelectedItem();

        // Validate
        if (title.isEmpty()) {
            showMessage("⚠ Title cannot be empty.", "#D63031");
            titleField.requestFocus();
            return;
        }
        if (content.isEmpty()) {
            showMessage("⚠ Content cannot be empty.", "#D63031");
            contentArea.requestFocus();
            return;
        }
        if (title.contains("|") || content.contains("|")) {
            showMessage("⚠ The '|' character is not allowed.", "#D63031");
            return;
        }

        // Save
        boolean success = DiaryManager.addEntry(title, content, mood);

        if (success) {
            showMessage("✔ Entry saved successfully!", "#00B894");
            clearForm();
        } else {
            showMessage("✘ Failed to save entry. Please try again.", "#D63031");
        }
    }

    // ─────────────────────────────────────────────
    // CLEAR FORM
    // ─────────────────────────────────────────────
    private void clearForm() {
        titleField.setText("");
        contentArea.setText("");
        moodCombo.setSelectedIndex(0);
        messageLabel.setText(" ");
    }

    // ─────────────────────────────────────────────
    // Show success or error message
    // ─────────────────────────────────────────────
    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setForeground(Color.decode(color));
    }

    // ─────────────────────────────────────────────
    // Style helpers
    // ─────────────────────────────────────────────
    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(Color.decode("#636E72"));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.decode("#2D3436"));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#DFE6E9"), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton createStyledButton(String text, String bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.decode(bgColor));
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    private JButton createTopButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.decode("#2d9e80"));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
