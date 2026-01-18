package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class WalletPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private Passanger passanger;
    private JLabel lblCurrentBalance;
    private JLabel lblPoints;
    private JTextField txtAmount;

    // Renk Teması (PassengerFrame ile uyumlu)
    private static final Color BG_COLOR = new Color(18, 18, 20);
    private static final Color CARD_COLOR = new Color(28, 28, 32);
    private static final Color ACCENT_BLUE = new Color(99, 102, 241);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    private static final Color TEXT_MAIN = new Color(240, 240, 240);
    private static final Color TEXT_SUB = new Color(150, 150, 155);
    private static final Color BORDER_COLOR = new Color(50, 50, 55);

    public WalletPanel(Passanger passanger) {
        this.passanger = passanger;

        setLayout(new BorderLayout(0, 30));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(40, 60, 40, 60));

        initializeUI();

        // Panel görünür olduğunda verileri güncelle
        addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                refreshBalance();
            }

            @Override
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {
            }
        });
    }

    private void initializeUI() {
        // === BAŞLIK ===
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("💰 Cüzdanım");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Bakiye yükleyerek uçuş satın alabilirsiniz");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SUB);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // === MERKEZ PANEL ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Mevcut Bakiye Kartı
        JPanel balanceCard = createBalanceCard();
        balanceCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bakiye Yükleme Kartı
        JPanel loadCard = createLoadCard();
        loadCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hızlı Yükleme Kartı
        JPanel quickLoadCard = createQuickLoadCard();
        quickLoadCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(balanceCard);
        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(loadCard);
        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(quickLoadCard);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Mevcut bakiye kartı
     */
    private JPanel createBalanceCard() {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(new Color(35, 35, 45));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_GREEN, 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        card.setMaximumSize(new Dimension(500, 140));
        card.setPreferredSize(new Dimension(500, 140));

        // Sol: İkon
        JLabel iconLabel = new JLabel("💳");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        card.add(iconLabel, BorderLayout.WEST);

        // Merkez: Bakiye bilgisi
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Mevcut Bakiye");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(TEXT_SUB);

        lblCurrentBalance = new JLabel(String.format("%.2f ₺", passanger.getBalance()));
        lblCurrentBalance.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblCurrentBalance.setForeground(ACCENT_GREEN);

        lblPoints = new JLabel("⭐ " + passanger.getLoyaltyPoints() + " Puan");
        lblPoints.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPoints.setForeground(ACCENT_ORANGE);

        infoPanel.add(lblTitle);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblCurrentBalance);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblPoints);

        card.add(infoPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Bakiye yükleme kartı
     */
    private JPanel createLoadCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)));
        card.setMaximumSize(new Dimension(500, 180));
        card.setPreferredSize(new Dimension(500, 180));

        JLabel lblTitle = new JLabel("Bakiye Yükle");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_MAIN);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel("Yüklemek istediğiniz tutarı girin:");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(TEXT_SUB);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Input alanı
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);
        inputPanel.setMaximumSize(new Dimension(430, 45));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtAmount = new JTextField();
        txtAmount.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAmount.setBackground(new Color(45, 45, 50));
        txtAmount.setForeground(TEXT_MAIN);
        txtAmount.setCaretColor(TEXT_MAIN);
        txtAmount.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JButton btnLoad = new JButton("Yükle");
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoad.setBackground(ACCENT_GREEN);
        btnLoad.setForeground(Color.WHITE);
        btnLoad.setFocusPainted(false);
        btnLoad.setBorderPainted(false);
        btnLoad.setPreferredSize(new Dimension(100, 45));
        btnLoad.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLoad.addActionListener(e -> loadBalance());

        inputPanel.add(txtAmount, BorderLayout.CENTER);
        inputPanel.add(btnLoad, BorderLayout.EAST);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(15));
        card.add(inputPanel);

        return card;
    }

    /**
     * Hızlı yükleme kartı
     */
    private JPanel createQuickLoadCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 35, 20, 35)));
        card.setMaximumSize(new Dimension(500, 130));
        card.setPreferredSize(new Dimension(500, 130));

        JLabel lblTitle = new JLabel("Hızlı Yükleme");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(TEXT_MAIN);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hızlı yükleme butonları
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int[] amounts = { 1000, 2500, 5000, 10000 };
        for (int amount : amounts) {
            JButton btn = createQuickLoadButton(amount);
            buttonsPanel.add(btn);
        }

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(15));
        card.add(buttonsPanel);

        return card;
    }

    /**
     * Hızlı yükleme butonu oluşturur
     */
    private JButton createQuickLoadButton(int amount) {
        JButton btn = new JButton(amount + " ₺");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(50, 50, 60));
        btn.setForeground(TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_BLUE);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(50, 50, 60));
            }
        });

        btn.addActionListener(e -> {
            txtAmount.setText(String.valueOf(amount));
            loadBalance();
        });

        return btn;
    }

    /**
     * Bakiye yükleme işlemi
     */
    private void loadBalance() {
        String amountText = txtAmount.getText().trim();

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir tutar girin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 1000) {
                JOptionPane.showMessageDialog(this, "Tutar 1.000 ₺'den büyük olmalıdır!", "Uyarı",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (amount > 100000) {
                JOptionPane.showMessageDialog(this, "Tek seferde en fazla 100.000 ₺ yükleyebilirsiniz!", "Uyarı",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Bakiye ekle
            passanger.addBalance(amount);

            // Kaydet
            try {
                PersonManager pm = new PersonManager();
                pm.addPerson(passanger);
            } catch (Exception e) {
                System.err.println("Kaydetme hatası: " + e.getMessage());
            }

            // UI güncelle
            lblCurrentBalance.setText(String.format("%.2f ₺", passanger.getBalance()));
            txtAmount.setText("");

            JOptionPane.showMessageDialog(this,
                    String.format("✅ %.2f ₺ başarıyla yüklendi!\n\nYeni Bakiye: %.2f ₺", amount,
                            passanger.getBalance()),
                    "Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçersiz tutar! Lütfen sayı girin.", "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Bakiye bilgisini günceller (dışardan çağrılabilir)
     */
    public void refreshBalance() {
        lblCurrentBalance.setText(String.format("%.2f ₺", passanger.getBalance()));
    }
}


