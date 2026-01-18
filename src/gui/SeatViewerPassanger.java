package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.UUID;

public class SeatViewerPassanger extends JPanel {
    private static final long serialVersionUID = 1L;

    // Veri Üyeleri
    private Flight flight;
    private Passanger passanger;
    private Map<String, Seat> seatMap;
    private ReservationManager resManager;
    private FlightManager flightManager; // Uçuşları kaydetmek için eklendi
    private Set<String> selectedSeats = new HashSet<>();
    private double finalPriceAfterDiscount = 0;

    // UI Bileşenleri
    private JLabel lblTotalPrice, lblBaggageStatus, lblLoyaltyInfo;
    private JTextField txtBaggageWeight;
    private JCheckBox chbUsePoints;
    private JRadioButton rbAccount, rbTroy;
    private JButton btnPay;

    private final int SEAT_SIZE = 35;
    private final String IMAGE_PATH = "savesAndImage/Image/";

    // Renk Teması
    private final Color THEME_GREEN = new Color(16, 185, 129);
    private final Color THEME_AMBER = new Color(245, 158, 11);
    private final Color THEME_BG = new Color(25, 25, 30);
    private final Color THEME_SIDE = new Color(35, 35, 40);

    // İkonlar
    private ImageIcon B_EMPTY, B_FULL, B_SELECTED, E_EMPTY, E_FULL, E_SELECTED;

    public SeatViewerPassanger(Flight flight, Passanger passanger, ReservationManager resManager,
            FlightManager flightManager) {
        this.flight = flight;
        this.passanger = passanger;
        this.resManager = resManager;
        this.flightManager = flightManager;
        this.seatMap = flight.getSeatMap();

        setLayout(new BorderLayout(0, 0));
        setBackground(THEME_BG);

        loadAllIcons();
        initializeUI();
    }

    private void loadAllIcons() {
        B_EMPTY = getScaledIcon(IMAGE_PATH + "B_EMPTY.png");
        B_FULL = getScaledIcon(IMAGE_PATH + "B_FULL.png");
        B_SELECTED = getScaledIcon(IMAGE_PATH + "B_SELECTED.PNG");
        E_EMPTY = getScaledIcon(IMAGE_PATH + "E_EMPTY.png");
        E_FULL = getScaledIcon(IMAGE_PATH + "E_FULL.png");
        E_SELECTED = getScaledIcon(IMAGE_PATH + "E_SELECTED.PNG");
    }

    private ImageIcon getScaledIcon(String path) {
        try {
            File file = new File(path);
            if (!file.exists())
                return null;
            Image img = ImageIO.read(file);
            return new ImageIcon(img.getScaledInstance(SEAT_SIZE, SEAT_SIZE, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }

    private void initializeUI() {
        // --- MERKEZ: KOLTUK HARİTASI ---
        JPanel seatContainer = new JPanel();
        seatContainer.setLayout(new BoxLayout(seatContainer, BoxLayout.Y_AXIS));
        seatContainer.setOpaque(false);
        seatContainer.setBorder(new EmptyBorder(10, 60, 10, 60));

        PlaneModel model = flight.getPlane().getPlaneModel();

        seatContainer.add(createSectionHeader("Business Class"));
        for (int i = 1; i <= model.getBusinessRow(); i++) {
            seatContainer.add(createRowPanel(i, new String[] { "A", "B" }, new String[] { "C", "D" }));
        }

        seatContainer.add(Box.createVerticalStrut(20));

        seatContainer.add(createSectionHeader("Economy Class"));
        int ecoStart = model.getBusinessRow() + 1;
        int ecoEnd = model.getBusinessRow() + model.getEconomyRow();
        for (int i = ecoStart; i <= ecoEnd; i++) {
            seatContainer.add(createRowPanel(i, new String[] { "A", "B", "C" }, new String[] { "D", "E", "F" }));
        }

        JScrollPane scrollPane = new JScrollPane(seatContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(THEME_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollBar(scrollPane.getVerticalScrollBar());
        add(scrollPane, BorderLayout.CENTER);

        // --- SAĞ YAN: ÖDEME PANELİ ---
        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(300, 0));
        sidePanel.setBackground(THEME_SIDE);
        sidePanel.setBorder(new EmptyBorder(30, 20, 30, 20));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        // Bagaj
        sidePanel.add(createSidebarLabel("BAGAJ AĞIRLIĞI (kg)", Color.GRAY, 11));
        sidePanel.add(Box.createVerticalStrut(8));
        txtBaggageWeight = new JTextField("0");
        txtBaggageWeight.setMaximumSize(new Dimension(260, 35));
        txtBaggageWeight.setBackground(new Color(45, 45, 50));
        txtBaggageWeight.setForeground(Color.WHITE);
        txtBaggageWeight.setCaretColor(Color.WHITE);
        txtBaggageWeight.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 75)));
        txtBaggageWeight.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateSummary();
            }
        });
        sidePanel.add(txtBaggageWeight);

        lblBaggageStatus = createSidebarLabel("Limit Dahilinde", Color.LIGHT_GRAY, 11);
        sidePanel.add(Box.createVerticalStrut(5));
        sidePanel.add(lblBaggageStatus);

        sidePanel.add(Box.createVerticalStrut(30));
        sidePanel.add(new JSeparator(JSeparator.HORIZONTAL));
        sidePanel.add(Box.createVerticalStrut(20));

        // Puan ve Ödeme Yöntemi
        chbUsePoints = new JCheckBox("Puan Kullan (" + passanger.getLoyaltyPoints() + " P)");
        styleToggleComponent(chbUsePoints, THEME_AMBER);
        chbUsePoints.addActionListener(e -> updateSummary());
        sidePanel.add(chbUsePoints);

        sidePanel.add(Box.createVerticalStrut(20));

        sidePanel.add(createSidebarLabel("ÖDEME YÖNTEMİ", Color.GRAY, 11));
        sidePanel.add(Box.createVerticalStrut(10));

        rbAccount = new JRadioButton("Hesaptan Öde (+Puan)");
        rbTroy = new JRadioButton("Troy Kart (Dış Banka)");
        styleToggleComponent(rbAccount, Color.WHITE);
        styleToggleComponent(rbTroy, Color.WHITE);

        ButtonGroup group = new ButtonGroup();
        group.add(rbAccount);
        group.add(rbTroy);
        rbAccount.setSelected(true);

        rbAccount.addActionListener(e -> lblLoyaltyInfo.setVisible(true));
        rbTroy.addActionListener(e -> lblLoyaltyInfo.setVisible(false));

        sidePanel.add(rbAccount);
        sidePanel.add(rbTroy);

        lblLoyaltyInfo = new JLabel("<html><i>* Puan kazandırır!</i></html>");
        lblLoyaltyInfo.setForeground(THEME_GREEN);
        lblLoyaltyInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblLoyaltyInfo.setBorder(new EmptyBorder(5, 25, 0, 0));
        sidePanel.add(lblLoyaltyInfo);

        sidePanel.add(Box.createVerticalGlue());

        // Fiyat (Genişletildi ve Ortaladı)
        lblTotalPrice = new JLabel("Toplam: 0 ₺");
        lblTotalPrice.setForeground(THEME_GREEN);
        lblTotalPrice.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotalPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTotalPrice.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalPrice.setMaximumSize(new Dimension(300, 60)); // Tüm alanı kaplaması için
        sidePanel.add(lblTotalPrice);

        sidePanel.add(Box.createVerticalStrut(20));

        btnPay = new JButton("Ödemeyi Tamamla");
        btnPay.setMaximumSize(new Dimension(300, 55));
        btnPay.setBackground(THEME_GREEN);
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.setFocusPainted(false);
        btnPay.setBorderPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.setEnabled(false);
        btnPay.addActionListener(e -> handlePayment());
        sidePanel.add(btnPay);

        add(sidePanel, BorderLayout.EAST);
    }

    private void styleToggleComponent(AbstractButton btn, Color textColor) {
        btn.setOpaque(false);
        btn.setForeground(textColor);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    private void updateSummary() {
        double basePrice = 0;
        double totalAllowance = 0;
        double weight = 0;
        try {
            weight = Double.parseDouble(txtBaggageWeight.getText());
        } catch (Exception e) {
        }

        for (String code : selectedSeats) {
            Seat s = seatMap.get(code);
            basePrice += s.getPrice(flight.getRoute());
            totalAllowance += s.getAllowance();
        }

        double excessFee = CalculatePrice.calculateExcessFee(totalAllowance, weight);
        double totalBeforeDiscount = basePrice + excessFee;

        double discount = 0;
        if (chbUsePoints.isSelected()) {
            discount = CalculatePrice.calculateLoyaltyDiscount(passanger.getLoyaltyPoints());
        }

        finalPriceAfterDiscount = Math.max(0, totalBeforeDiscount - discount);
        lblTotalPrice.setText(String.format("Toplam: %.0f ₺", finalPriceAfterDiscount));

        if (excessFee > 0) {
            lblBaggageStatus.setText("Aşım: +" + excessFee + " ₺");
            lblBaggageStatus.setForeground(new Color(239, 68, 68));
        } else {
            lblBaggageStatus.setText("Limit: " + totalAllowance + " kg (Dahil)");
            lblBaggageStatus.setForeground(THEME_GREEN);
        }

        btnPay.setEnabled(!selectedSeats.isEmpty());
    }

    private void handlePayment() {
        if (selectedSeats.isEmpty())
            return;
        if (rbTroy.isSelected()) {
            JOptionPane.showMessageDialog(this, "Troy Güvenli Ödeme Sayfasına Yönlendiriliyorsunuz...");
            Timer timer = new Timer(2000, e -> finalizeTransaction("Troy Kart ile ödeme başarılı!"));
            timer.setRepeats(false);
            timer.start();
        } else {
            if (passanger.spendBalance(finalPriceAfterDiscount)) {
                int earnedPoints = (int) finalPriceAfterDiscount;
                passanger.addPoints(earnedPoints);
                finalizeTransaction("Cüzdan ödemesi başarılı! +" + earnedPoints + " Puan kazandınız.");
            } else {
                JOptionPane.showMessageDialog(this, "Yetersiz bakiye! Cüzdanınızı doldurun.");
            }
        }
    }

    private void finalizeTransaction(String msg) {
        double totalDiscount = chbUsePoints.isSelected()
                ? CalculatePrice.calculateLoyaltyDiscount(passanger.getLoyaltyPoints())
                : 0;
        double totalAllowance = 0;
        double weight = 0;
        try {
            weight = Double.parseDouble(txtBaggageWeight.getText());
        } catch (Exception e) {
        }
        for (String code : selectedSeats)
            totalAllowance += seatMap.get(code).getAllowance();
        double totalExcess = CalculatePrice.calculateExcessFee(totalAllowance, weight);

        // Koltuk başına düşen maliyet ayarı (İndirim ve bagajı dağıtıyoruz)
        double discountPerSeat = totalDiscount / selectedSeats.size();
        double excessPerSeat = totalExcess / selectedSeats.size();

        if (chbUsePoints.isSelected())
            passanger.setLoyaltyPoints(0);

        StringBuilder summary = new StringBuilder(msg + "\n\nOLUŞTURULAN BİLETLER:\n");

        // --- Rezervasyon ve Bilet Oluşturma ---
        for (String code : selectedSeats) {
            boolean reserved = resManager.createReservation(flight, passanger, code);
            if (reserved) {
                // Son eklenen rezervasyonu çekiyoruz
                Reservation res = resManager.getAllReservations().stream()
                        .filter(r -> r.getSeat().getSeatCode().equals(code) && r.getpassanger().equals(passanger))
                        .reduce((first, second) -> second).orElse(null);

                if (res != null) {
                    double ticketPrice = res.getSeat().getPrice(flight.getRoute()) + excessPerSeat - discountPerSeat;

                    // Bilet oluştur ve dosyaya kaydet
                    Ticket ticket = resManager.createTicket(res, ticketPrice);

                    summary.append("- Koltuk: ").append(code)
                            .append(" | Bilet: ").append(ticket.getTicketID())
                            .append(String.format(" | Fiyat: %.0f TL\n", ticketPrice));
                }
            }
        }

        // === DOSYAYA KAYDETME İŞLEMLERİ ===

        // Uçuş bilgilerini kaydet (koltuk durumları değişti)
        if (flightManager != null) {
            flightManager.saveFlights();
        }

        // Yolcu bilgilerini kaydet (bakiye ve puan güncellemesi için)
        try {
            PersonManager pm = new PersonManager();
            pm.addPerson(passanger); // Mevcut kişiyi günceller
        } catch (Exception e) {
            System.err.println("Yolcu bilgileri kaydedilemedi: " + e.getMessage());
        }

        JOptionPane.showMessageDialog(this, summary.toString());
        selectedSeats.clear();
        updateSummary();
        this.repaint();
    }

    // --- YARDIMCI METOTLAR ---

    private JPanel createRowPanel(int rowNum, String[] left, String[] right) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        row.setOpaque(false);
        JLabel rowLabel = new JLabel(String.valueOf(rowNum));
        rowLabel.setForeground(new Color(100, 100, 105));
        rowLabel.setPreferredSize(new Dimension(25, SEAT_SIZE));
        row.add(rowLabel);
        for (String s : left)
            row.add(createSeatButton(s + rowNum));
        row.add(Box.createHorizontalStrut(20));
        for (String s : right)
            row.add(createSeatButton(s + rowNum));
        return row;
    }

    private JButton createSeatButton(String code) {
        Seat seat = seatMap.get(code);
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(SEAT_SIZE, SEAT_SIZE));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        updateButtonIcon(btn, seat, code);
        btn.addActionListener(e -> {
            if (seat.isReserved())
                return;
            if (selectedSeats.contains(code))
                selectedSeats.remove(code);
            else
                selectedSeats.add(code);
            updateButtonIcon(btn, seat, code);
            updateSummary();
        });
        return btn;
    }

    private void updateButtonIcon(JButton btn, Seat seat, String code) {
        if (seat == null)
            return;
        boolean isBus = seat.getTicketClass() == TicketClass.BUSINESS;
        if (seat.isReserved())
            btn.setIcon(isBus ? B_FULL : E_FULL);
        else if (selectedSeats.contains(code))
            btn.setIcon(isBus ? B_SELECTED : E_SELECTED);
        else
            btn.setIcon(isBus ? B_EMPTY : E_EMPTY);
    }

    private JLabel createSidebarLabel(String text, Color color, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", Font.BOLD, size));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel createSectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(150, 150, 160));
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(15, 0, 10, 0));
        return l;
    }

    private void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setPreferredSize(new Dimension(8, 0));
        scrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 65);
                this.trackColor = THEME_BG;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return new JButton() {
                    {
                        setPreferredSize(new Dimension(0, 0));
                    }
                };
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return new JButton() {
                    {
                        setPreferredSize(new Dimension(0, 0));
                    }
                };
            }
        });
    }
}

