package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightsPanelPassanger extends JPanel {
    private FlightManager fm;
    private AssetManager am;
    private JPanel cardsContainer;
    private JComboBox<String> depCombo, arrCombo;
    private JTextField fDate;
    private Passanger currentPassanger;
    private PassengerFrame parentFrame;

    // Seçili uçuşu takip etmek için (Görsel vurgu için opsiyonel)
    private JPanel selectedRow = null;

    public FlightsPanelPassanger(FlightManager fm, AssetManager am, Passanger currentPassanger, PassengerFrame parentFrame) {
        this.fm = fm;
        this.am = am;
        this.currentPassanger = currentPassanger;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(PassengerFrame.BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initializeTopFilter();
        initializeCenterContent();
        
        // Başlangıçta tüm uçuşları listele
        refreshFlightList(fm.getAllFlights());
    }

    private void initializeTopFilter() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(PassengerFrame.CARD_COLOR);
        topPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(PassengerFrame.TEXT_SUB), 
                "Uçuş Ara", TitledBorder.LEFT, TitledBorder.TOP, null, PassengerFrame.TEXT_MAIN));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        depCombo = new JComboBox<>();
        arrCombo = new JComboBox<>();
        fDate = new JTextField("2026-01-09", 10); // Varsayılan test tarihi
        JButton btnSearch = new JButton("Uçuşları Listele");
        
        styleCombo(depCombo);
        styleCombo(arrCombo);
        btnSearch.setBackground(PassengerFrame.ACCENT_BLUE);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        refreshCombos();

        gbc.gridx = 0; topPanel.add(createWhiteLabel("Kalkış:"), gbc);
        gbc.gridx = 1; topPanel.add(depCombo, gbc);
        gbc.gridx = 2; topPanel.add(createWhiteLabel("Varış:"), gbc);
        gbc.gridx = 3; topPanel.add(arrCombo, gbc);
        gbc.gridx = 4; topPanel.add(createWhiteLabel("Tarih:"), gbc);
        gbc.gridx = 5; topPanel.add(fDate, gbc);
        gbc.gridx = 6; topPanel.add(btnSearch, gbc);

        btnSearch.addActionListener(e -> performFilter());
        add(topPanel, BorderLayout.NORTH);
    }

    private void initializeCenterContent() {
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(PassengerFrame.BG_COLOR);

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PassengerFrame.BG_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Yan yana tasarımda sağ tarafa eklenen modern scrollbar
        styleScrollBar(scrollPane.getVerticalScrollBar());
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setPreferredSize(new Dimension(8, 0));
        scrollBar.setBackground(PassengerFrame.BG_COLOR);
        scrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 65);
                this.trackColor = PassengerFrame.BG_COLOR;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton jb = new JButton();
                jb.setPreferredSize(new Dimension(0, 0));
                return jb;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
                g2.dispose();
            }
        });
    }

    private void refreshFlightList(Map<Integer, Flight> filteredFlights) {
        cardsContainer.removeAll();
        
        if (filteredFlights.isEmpty()) {
            JLabel emptyMsg = new JLabel("Aradığınız kriterlere uygun uçuş bulunamadı.");
            emptyMsg.setForeground(PassengerFrame.TEXT_SUB);
            emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyMsg.setBorder(new EmptyBorder(50, 0, 0, 0));
            cardsContainer.add(emptyMsg);
        } else {
            for (Flight f : filteredFlights.values()) {
                cardsContainer.add(createFlightCard(f));
                cardsContainer.add(Box.createVerticalStrut(12));
            }
        }
        
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createFlightCard(Flight f) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        // Yan yana görünüm için yükseklik ve genişlik ayarı
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setBackground(PassengerFrame.CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        // Sol Kısım: Rota ve Saat
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);
        
        JLabel routeLbl = new JLabel(f.getRoute().getDeparture().getCity() + " ➔ " + f.getRoute().getArrival().getCity());
        routeLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        routeLbl.setForeground(Color.WHITE);
        
        JLabel timeLbl = new JLabel("🛫 " + f.getDepartureDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLbl.setForeground(PassengerFrame.TEXT_SUB);
        
        infoPanel.add(routeLbl);
        infoPanel.add(timeLbl);

        // Sağ Kısım: Fiyat ve Aksiyon
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setOpaque(false);

        double ecoPrice = CalculatePrice.calculateBasicPrice(f.getRoute(), TicketClass.ECONOMY);
        double busPrice = CalculatePrice.calculateBasicPrice(f.getRoute(), TicketClass.BUSINESS);
        
        String priceHTML = String.format("<html><div style='text-align: right;'><font color='#10B981'>Eco: %.0f ₺</font><br/>" +
                                         "<font color='#F59E0B'>Bus: %.0f ₺</font></div></html>", ecoPrice, busPrice);
        JLabel priceLbl = new JLabel(priceHTML);
        priceLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JButton btnSelect = new JButton("Koltuk Seç");
        btnSelect.setPreferredSize(new Dimension(100, 35));
        btnSelect.setBackground(PassengerFrame.ACCENT_BLUE);
        btnSelect.setForeground(Color.WHITE);
        btnSelect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSelect.setFocusPainted(false);
        btnSelect.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // BUTON AKSİYONU: ParentFrame üzerinden yan paneldeki SeatViewer'ı günceller
        btnSelect.addActionListener(e -> {
            parentFrame.showSeatSelection(f);
            // Görsel olarak seçili kartı vurgula (isteğe bağlı)
            if (selectedRow != null) selectedRow.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 55), 1));
            card.setBorder(BorderFactory.createLineBorder(PassengerFrame.ACCENT_BLUE, 1));
            selectedRow = card;
        });

        actionPanel.add(priceLbl);
        actionPanel.add(btnSelect);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);
        
        return card;
    }

    private void performFilter() {
        String dep = (String) depCombo.getSelectedItem();
        String arr = (String) arrCombo.getSelectedItem();
        String dateText = fDate.getText();
        
        try {
            Map<Integer, Flight> filtered = fm.getAllFlights().values().stream() 
                .filter(f -> f.getRoute().getDeparture().getCity().equals(dep))
                .filter(f -> f.getRoute().getArrival().getCity().equals(arr))
                .filter(f -> f.getDepartureDateTime().toLocalDate().toString().equals(dateText))
                .collect(Collectors.toMap(Flight::getFlightNum, f -> f));
            refreshFlightList(filtered);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Filtreleme hatası: Tarih formatını kontrol edin (YYYY-MM-DD)");
        }
    }

    public void refreshCombos() {
        depCombo.removeAllItems();
        arrCombo.removeAllItems();
        if (am != null && am.getAirports() != null) {
            am.getAirports().values().forEach(a -> {
                depCombo.addItem(a.getCity());
                arrCombo.addItem(a.getCity());
            });
        }
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(new Color(40, 40, 45));
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 75)));
    }

    private JLabel createWhiteLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }
}

