

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class ViewEntriesPanel extends JPanel {

    private MainFrame frame;
    private JTable entriesTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public ViewEntriesPanel(MainFrame frame) {
        this.frame = frame;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F5F6FA"));

        add(createTopBar(),    BorderLayout.NORTH);
        add(createTableArea(), BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);
    }

    // ─────────────────────────────────────────────
    // TOP BAR
    // ─────────────────────────────────────────────
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.decode("#6C5CE7"));
        topBar.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("📋  All Diary Entries");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton backBtn = createTopButton("← Dashboard");
        backBtn.addActionListener(e -> frame.showScreen(MainFrame.DASHBOARD));

        topBar.add(title,   BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);
        return topBar;
    }

    // ─────────────────────────────────────────────
    // TABLE AREA
    // ─────────────────────────────────────────────
    private JPanel createTableArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#F5F6FA"));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Table columns
        String[] columns = {"ID", "Date", "Title", "Mood", "Content Preview"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // table is read-only
            }
        };

        entriesTable = new JTable(tableModel);
        styleTable();

        // Double-click to view full entry
        entriesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSelectedEntry();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(entriesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#DFE6E9"), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────────────────────────────────
    // BOTTOM ACTION BAR
    // ─────────────────────────────────────────────
    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#DFE6E9")),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        // Left: action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton viewBtn   = createActionButton("👁  View",   "#4A90D9");
        JButton editBtn   = createActionButton("✏  Edit",   "#6C5CE7");
        JButton deleteBtn = createActionButton("🗑  Delete", "#D63031");
        JButton refreshBtn = createActionButton("↻  Refresh", "#636E72");

        viewBtn.addActionListener(e   -> viewSelectedEntry());
        editBtn.addActionListener(e   -> editSelectedEntry());
        deleteBtn.addActionListener(e -> deleteSelectedEntry());
        refreshBtn.addActionListener(e -> loadEntries());

        btnPanel.add(viewBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        // Right: status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.decode("#636E72"));

        bar.add(btnPanel,    BorderLayout.WEST);
        bar.add(statusLabel, BorderLayout.EAST);
        return bar;
    }

    // ─────────────────────────────────────────────
    // VIEW full entry in a dialog
    // ─────────────────────────────────────────────
    private void viewSelectedEntry() {
        int row = entriesTable.getSelectedRow();
        if (row == -1) {
            setStatus("Please select an entry first.", "#D63031");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        DiaryEntry entry = findEntryById(id);
        if (entry == null) return;

        // Build dialog content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setBackground(Color.WHITE);

        content.add(makeDialogRow("ID",      String.valueOf(entry.getId())));
        content.add(Box.createVerticalStrut(8));
        content.add(makeDialogRow("Date",    entry.getDate()));
        content.add(Box.createVerticalStrut(8));
        content.add(makeDialogRow("Title",   entry.getTitle()));
        content.add(Box.createVerticalStrut(8));
        content.add(makeDialogRow("Mood",    entry.getMood()));
        content.add(Box.createVerticalStrut(12));

        JLabel contentHeading = new JLabel("Content:");
        contentHeading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        contentHeading.setForeground(Color.decode("#636E72"));

        JTextArea contentArea = new JTextArea(entry.getContent());
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.decode("#F5F6FA"));
        contentArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setPreferredSize(new Dimension(400, 120));

        content.add(contentHeading);
        content.add(Box.createVerticalStrut(5));
        content.add(scroll);

        JOptionPane.showMessageDialog(frame, content, "Diary Entry — " + entry.getTitle(), JOptionPane.PLAIN_MESSAGE);
    }

    // ─────────────────────────────────────────────
    // EDIT selected entry in a dialog
    // ─────────────────────────────────────────────
    private void editSelectedEntry() {
        int row = entriesTable.getSelectedRow();
        if (row == -1) {
            setStatus("Please select an entry to edit.", "#D63031");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        DiaryEntry entry = findEntryById(id);
        if (entry == null) return;

        // Edit form inside dialog
        JTextField titleField = new JTextField(entry.getTitle(), 30);
        JTextArea contentArea = new JTextArea(entry.getContent(), 5, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        String[] moods = {"Happy", "Sad", "Excited", "Angry", "Anxious", "Grateful", "Tired", "Motivated", "Relaxed", "Bored"};
        JComboBox<String> moodCombo = new JComboBox<>(moods);
        moodCombo.setSelectedItem(entry.getMood());

        JPanel editForm = new JPanel();
        editForm.setLayout(new BoxLayout(editForm, BoxLayout.Y_AXIS));
        editForm.add(new JLabel("Title:"));
        editForm.add(titleField);
        editForm.add(Box.createVerticalStrut(10));
        editForm.add(new JLabel("Content:"));
        editForm.add(new JScrollPane(contentArea));
        editForm.add(Box.createVerticalStrut(10));
        editForm.add(new JLabel("Mood:"));
        editForm.add(moodCombo);

        int result = JOptionPane.showConfirmDialog(frame, editForm,
            "Edit Entry #" + id, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newTitle   = titleField.getText().trim();
            String newContent = contentArea.getText().trim();
            String newMood    = (String) moodCombo.getSelectedItem();

            if (newTitle.isEmpty() || newContent.isEmpty()) {
                setStatus("Title and content cannot be empty.", "#D63031");
                return;
            }
            if (newTitle.contains("|") || newContent.contains("|")) {
                setStatus("The '|' character is not allowed.", "#D63031");
                return;
            }

            boolean updated = DiaryManager.updateEntry(id, newTitle, newContent, newMood);
            if (updated) {
                setStatus("Entry #" + id + " updated successfully.", "#00B894");
                loadEntries();
            } else {
                setStatus("Update failed.", "#D63031");
            }
        }
    }

    // ─────────────────────────────────────────────
    // DELETE selected entry with confirmation
    // ─────────────────────────────────────────────
    private void deleteSelectedEntry() {
        int row = entriesTable.getSelectedRow();
        if (row == -1) {
            setStatus("Please select an entry to delete.", "#D63031");
            return;
        }

        int id    = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 2);

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to delete:\n\"" + title + "\"?\n\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = DiaryManager.deleteEntry(id);
            if (deleted) {
                setStatus("Entry #" + id + " deleted successfully.", "#00B894");
                loadEntries();
            } else {
                setStatus("Delete failed.", "#D63031");
            }
        }
    }

    // ─────────────────────────────────────────────
    // LOAD entries into table
    // ─────────────────────────────────────────────
    public void loadEntries() {
        tableModel.setRowCount(0); // Clear table

        ArrayList<DiaryEntry> entries = DiaryManager.getAllEntries();

        if (entries.isEmpty()) {
            setStatus("No diary entries found.", "#636E72");
            return;
        }

        for (DiaryEntry entry : entries) {
            String preview = entry.getContent();
            if (preview.length() > 40) preview = preview.substring(0, 40) + "...";

            tableModel.addRow(new Object[]{
                entry.getId(),
                entry.getDate(),
                entry.getTitle(),
                entry.getMood(),
                preview
            });
        }

        setStatus("Showing " + entries.size() + " entries.", "#636E72");
    }

    // ─────────────────────────────────────────────
    // Helper: find entry by ID
    // ─────────────────────────────────────────────
    private DiaryEntry findEntryById(int id) {
        ArrayList<DiaryEntry> entries = DiaryManager.getAllEntries();
        for (DiaryEntry e : entries) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // Helper: dialog row label + value
    // ─────────────────────────────────────────────
    private JPanel makeDialogRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label + ": ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.decode("#636E72"));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(Color.decode("#2D3436"));
        row.add(lbl);
        row.add(val);
        return row;
    }

    // ─────────────────────────────────────────────
    // Style the table
    // ─────────────────────────────────────────────
    private void styleTable() {
        entriesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        entriesTable.setRowHeight(38);
        entriesTable.setGridColor(Color.decode("#DFE6E9"));
        entriesTable.setSelectionBackground(Color.decode("#D6EAF8"));
        entriesTable.setSelectionForeground(Color.decode("#2D3436"));
        entriesTable.setShowVerticalLines(false);
        entriesTable.setFillsViewportHeight(true);

                // Header styling with custom renderer (fixes Windows look-and-feel override)
        JTableHeader header = entriesTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#6C5CE7"));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                label.setOpaque(true);
                return label;
            }
        });


        // Column widths
        entriesTable.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        entriesTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Date
        entriesTable.getColumnModel().getColumn(2).setPreferredWidth(180);  // Title
        entriesTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // Mood
        entriesTable.getColumnModel().getColumn(4).setPreferredWidth(300);  // Preview
    }

    private void setStatus(String msg, String color) {
        statusLabel.setText(msg + "  ");
        statusLabel.setForeground(Color.decode(color));
    }

    private JButton createTopButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.decode("#5a4fcf"));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createActionButton(String text, String color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(Color.decode(color));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
        return btn;
    }
}
