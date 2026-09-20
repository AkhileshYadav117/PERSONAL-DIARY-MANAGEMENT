

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomePanel extends JPanel {

    private MainFrame frame;

    public WelcomePanel(MainFrame frame) {
        this.frame = frame;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout()); // Centers everything
        setBackground(Color.decode("#F5F6FA"));

        // Main card panel
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#DFE6E9"), 1),
            BorderFactory.createEmptyBorder(50, 60, 50, 60)
        ));

        // ── Diary Icon/Emoji Label ──
        JLabel iconLabel = new JLabel("📖", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── App Title ──
        JLabel titleLabel = new JLabel("Personal Diary", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.decode("#2D3436"));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Subtitle ──
        JLabel subtitleLabel = new JLabel("Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        subtitleLabel.setForeground(Color.decode("#4A90D9"));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Tagline ──
        JLabel taglineLabel = new JLabel("Your thoughts, your world — captured forever.", SwingConstants.CENTER);
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        taglineLabel.setForeground(Color.decode("#636E72"));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Open Diary Button ──
        JButton openButton = createOpenButton();

        // ── Made by label ──
        JLabel madeBy = new JLabel("A Mini Project | Core Java + File Handling", SwingConstants.CENTER);
        madeBy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        madeBy.setForeground(Color.decode("#B2BEC3"));
        madeBy.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add all elements to card with spacing
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(taglineLabel);
        card.add(Box.createVerticalStrut(35));
        card.add(openButton);
        card.add(Box.createVerticalStrut(25));
        card.add(madeBy);

        add(card); // Add card to center of panel
    }

    private JButton createOpenButton() {
        JButton btn = new JButton("Open My Diary  →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(Color.decode("#3a7abf"));
                } else if (getModel().isRollover()) {
                    g2.setColor(Color.decode("#5aa0e8"));
                } else {
                    g2.setColor(Color.decode("#4A90D9"));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 48));
        btn.setMaximumSize(new Dimension(220, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // When clicked → go to Dashboard
        btn.addActionListener(e -> frame.showScreen(MainFrame.DASHBOARD));

        return btn;
    }
}
