package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.File;

public class FlightsPanelAdm extends JPanel {
    private static final long serialVersionUID = 1L;
    private FlightManager fm;
    private AssetManager am;
    private ReservationManager rm;
    private DefaultTableModel tableModel;
    private JTable flightTable;
    private JComboBox<String> planeCombo, depCombo, arrCombo;

    // Seat Viewer Panel
    private JPanel seatViewerPanel;
    private JPanel passengerInfoPanel;
    private Flight selectedFlight;

    // İkonlar
    private static final String IMAGE_PATH = "savesAndImage/Image/";
    private static ImageIcon B_EMPTY, B_FULL, E_EMPTY, E_FULL;
    private static final int SEAT_SIZE = 32;

    // Renkler
    private static final Color BG_COLOR = new Color(25, 25, 30);
    private static final Color CARD_COLOR = new Color(35, 35, 45);
    private static final Color ACCENT_BLUE = new Color(99, 102, 241);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color TEXT_MAIN = new Color(240, 240, 240);
    private static final Color TEXT_SUB = new Color(150, 150, 155);

    public FlightsPanelAdm(FlightManager fm, AssetManager am) {
        this.fm = fm;
        this.am = am;
        this.rm = new ReservationManager();
        setLayout(new BorderLayout(10, 10));
        loadIcons();
        initializeTopPanel();
        initializeBottomPanel();
        updateTable();
    }

    private void initializeTopPanel() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new TitledBorder("Sefer Planlama"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        planeCombo = new JComboBox<>();
        depCombo = new JComboBox<>();
        arrCombo = new JComboBox<>();
        refreshCombos();

        JTextField fDate = new JTextField("2024-12-30");
        JTextField fTime = new JTextField("14:00");
        JButton btnAdd = new JButton("Sefer Kaydet");

        btnAdd.addActionListener(e -> {
            try {
                String depKey = (String) depCombo.getSelectedItem();
                String arrKey = (String) arrCombo.getSelectedItem();

                if (depKey != null && depKey.equals(arrKey)) {
                    JOptionPane.showMessageDialog(this, "HATA: Kalkış ve varış noktası aynı olamaz!", "Geçersiz Rota",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Plane p = am.getFleet().get(planeCombo.getSelectedItem());
                Airport dep = am.getAirports().get(depKey);
                Airport arr = am.getAirports().get(arrKey);
                Route r = new Route(dep, arr);
                LocalDate d = LocalDate.parse(fDate.getText());
                LocalTime t = LocalTime.parse(fTime.getText());

                if (fm.isPlaneConflict(p, r, d, t)) {
                    JOptionPane.showMessageDialog(this,
                            "HATA: Uçak bu saatlerde meşgul (Bakım dahil ±1 saat çakışması)!");
                } else {
                    fm.addFlight(p, r, d, t);
                    updateTable();
                    JOptionPane.showMessageDialog(this, "Sefer eklendi.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Veri formatını kontrol edin!");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Uçak:"), gbc);
        gbc.gridx = 1;
        topPanel.add(planeCombo, gbc);
        gbc.gridx = 2;
        topPanel.add(new JLabel("Kalkış:"), gbc);
        gbc.gridx = 3;
        topPanel.add(depCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Tarih:"), gbc);
        gbc.gridx = 1;
        topPanel.add(fDate, gbc);
        gbc.gridx = 2;
        topPanel.add(new JLabel("Varış:"), gbc);
        gbc.gridx = 3;
        topPanel.add(arrCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(new JLabel("Saat:"), gbc);
        gbc.gridx = 1;
        topPanel.add(fTime, gbc);
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        topPanel.add(btnAdd, gbc);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initializeBottomPanel() {
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));

        // --- ARAMA ÇUBUĞU ---
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Sefer Ara (ID, Uçak veya Şehir)"));
        listPanel.add(searchField, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[] { "ID", "Uçak", "Kalkış", "Varış", "Kalkış Zamanı", "Tahmini İniş" }, 0);
        flightTable = new JTable(tableModel);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        flightTable.setRowSorter(sorter);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        });

        listPanel.add(new JScrollPane(flightTable), BorderLayout.CENTER);

        // --- SİLME VE DÜZENLEME BUTONLARI ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        // Düzenleme Butonu
        JButton btnEdit = new JButton("✏️ Seçili Seferi Düzenle");
        btnEdit.setBackground(new Color(99, 102, 241));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));

        btnEdit.addActionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row != -1) {
                int modelRow = flightTable.convertRowIndexToModel(row);
                int flightId = (int) tableModel.getValueAt(modelRow, 0);
                Flight flight = fm.getAllFlights().get(flightId);

                if (flight != null) {
                    showEditDialog(flight);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen düzenlemek istediğiniz seferi seçin.");
            }
        });

        // Silme Butonu
        JButton btnDelete = new JButton("🗑️ Seçili Seferi Sil");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));

        btnDelete.addActionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row != -1) {
                int modelRow = flightTable.convertRowIndexToModel(row);
                int flightId = (int) tableModel.getValueAt(modelRow, 0);

                int confirm = JOptionPane.showConfirmDialog(this, "Sefer silinecek, emin misiniz?", "Silme Onayı",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    fm.removeFlight(flightId);
                    updateTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen silmek istediğiniz seferi seçin.");
            }
        });

        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        // === SEAT VIEWER PANEL ===
        seatViewerPanel = new JPanel(new BorderLayout());
        seatViewerPanel.setBackground(Color.WHITE);
        seatViewerPanel.setBorder(new TitledBorder("Koltuk Görünümü"));

        JLabel lblSelectFlight = new JLabel("← Tablodan bir uçuş seçin");
        lblSelectFlight.setHorizontalAlignment(JLabel.CENTER);
        lblSelectFlight.setForeground(Color.GRAY);
        seatViewerPanel.add(lblSelectFlight, BorderLayout.CENTER);

        // Yolcu bilgi paneli (altta)
        passengerInfoPanel = new JPanel(new BorderLayout());
        passengerInfoPanel.setBackground(Color.WHITE);
        passengerInfoPanel.setPreferredSize(new Dimension(0, 150));
        passengerInfoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblPassengerInfo = new JLabel("Dolu bir koltuğa tıklayarak yolcu bilgisini görün");
        lblPassengerInfo.setForeground(Color.GRAY);
        lblPassengerInfo.setHorizontalAlignment(JLabel.CENTER);
        passengerInfoPanel.add(lblPassengerInfo, BorderLayout.CENTER);

        seatViewerPanel.add(passengerInfoPanel, BorderLayout.SOUTH);

        // Tablo seçim listener'ı
        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = flightTable.getSelectedRow();
                if (row != -1) {
                    int modelRow = flightTable.convertRowIndexToModel(row);
                    int flightId = (int) tableModel.getValueAt(modelRow, 0);
                    selectedFlight = fm.getAllFlights().get(flightId);
                    if (selectedFlight != null) {
                        updateSeatViewer(selectedFlight);
                    }
                }
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, seatViewerPanel);
        split.setDividerLocation(600);
        add(split, BorderLayout.CENTER);
    }

    public void updateTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        fm.getAllFlights().values().forEach(f -> {
            tableModel.addRow(new Object[] {
                    f.getFlightNum(), f.getPlane().getTailNumber(),
                    f.getRoute().getDeparture().getCity(), f.getRoute().getArrival().getCity(),
                    f.getDepartureDateTime().format(dtf), f.getEstimatedLandingTime().format(dtf)
            });
        });
    }

    public void refreshCombos() {
        planeCombo.removeAllItems();
        depCombo.removeAllItems();
        arrCombo.removeAllItems();

        am.getFleet().keySet().forEach(planeCombo::addItem);
        am.getAirports().keySet().forEach(k -> {
            depCombo.addItem(k);
            arrCombo.addItem(k);
        });
    }

    /**
     * Uçuş düzenleme dialogu
     */
    private void showEditDialog(Flight flight) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sefer Düzenle", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Uçuş bilgileri (sadece görüntüleme)
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Sefer No:"), gbc);
        gbc.gridx = 1;
        JLabel lblFlightNo = new JLabel(String.valueOf(flight.getFlightNum()));
        lblFlightNo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contentPanel.add(lblFlightNo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Rota:"), gbc);
        gbc.gridx = 1;
        String routeStr = flight.getRoute().getDeparture().getCity() + " → " + flight.getRoute().getArrival().getCity();
        contentPanel.add(new JLabel(routeStr), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Uçak:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(new JLabel(flight.getPlane().getTailNumber()), gbc);

        // Kalkış Tarihi (düzenlenebilir)
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("Kalkış Tarihi:"), gbc);
        gbc.gridx = 1;
        JTextField txtDate = new JTextField(flight.getDepartureDate().format(dateFormatter), 15);
        contentPanel.add(txtDate, gbc);

        // Kalkış Saati (düzenlenebilir)
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(new JLabel("Kalkış Saati:"), gbc);
        gbc.gridx = 1;
        JTextField txtTime = new JTextField(flight.getDepartureTime().format(timeFormatter), 15);
        contentPanel.add(txtTime, gbc);

        // Tahmini İniş (otomatik hesaplanacak)
        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPanel.add(new JLabel("Tahmini İniş:"), gbc);
        gbc.gridx = 1;
        JLabel lblLanding = new JLabel(flight.getEstimatedLandingTime().format(dtf));
        lblLanding.setForeground(new Color(99, 102, 241));
        lblLanding.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contentPanel.add(lblLanding, gbc);

        // Önizleme butonu
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton btnPreview = new JButton("🔄 İniş Zamanını Önizle");
        btnPreview.addActionListener(e -> {
            try {
                LocalDate newDate = LocalDate.parse(txtDate.getText());
                LocalTime newTime = LocalTime.parse(txtTime.getText());

                // Geçici hesaplama
                Flight temp = new Flight(0, flight.getRoute(), flight.getPlane(), newDate, newTime, null);
                lblLanding.setText(temp.getEstimatedLandingTime().format(dtf));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Geçersiz tarih/saat formatı!\nTarih: yyyy-MM-dd\nSaat: HH:mm");
            }
        });
        contentPanel.add(btnPreview, gbc);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnSave = new JButton("💾 Kaydet");
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);

        JButton btnCancel = new JButton("İptal");
        btnCancel.setFocusPainted(false);

        btnSave.addActionListener(e -> {
            try {
                LocalDate newDate = LocalDate.parse(txtDate.getText());
                LocalTime newTime = LocalTime.parse(txtTime.getText());

                // Uçuşu güncelle
                flight.setDepartureDate(newDate);
                flight.setDepartureTime(newTime);

                // Dosyaya kaydet
                fm.saveFlights();

                // Tabloyu güncelle
                updateTable();

                JOptionPane.showMessageDialog(dialog,
                        "Sefer güncellendi!\n\nYeni Kalkış: " + newDate + " " + newTime +
                                "\nTahmini İniş: " + flight.getEstimatedLandingTime().format(dtf),
                        "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Hata: Geçersiz tarih/saat formatı!\nTarih: yyyy-MM-dd\nSaat: HH:mm");
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // === İKON YÜKLEME ===
    private void loadIcons() {
        B_EMPTY = getScaledIcon(IMAGE_PATH + "B_EMPTY.png");
        B_FULL = getScaledIcon(IMAGE_PATH + "B_FULL.png");
        E_EMPTY = getScaledIcon(IMAGE_PATH + "E_EMPTY.png");
        E_FULL = getScaledIcon(IMAGE_PATH + "E_FULL.png");
    }

    private ImageIcon getScaledIcon(String path) {
        try {
            File file = new File(path);
            if (!file.exists())
                return null;
            Image img = ImageIO.read(file);
            if (img == null)
                return null;
            Image scaledImg = img.getScaledInstance(SEAT_SIZE, SEAT_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateSeatViewer(Flight flight) {
        // Merkez paneli temizle
        Component centerComp = ((BorderLayout) seatViewerPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null) {
            seatViewerPanel.remove(centerComp);
        }

        // Koltuk grid'i oluştur
        JPanel seatContainer = new JPanel();
        seatContainer.setLayout(new BoxLayout(seatContainer, BoxLayout.Y_AXIS));
        seatContainer.setBackground(BG_COLOR);

        Map<String, Seat> seatMap = flight.getSeatMap();
        PlaneModel model = flight.getPlane().getPlaneModel();

        // Business Class
        seatContainer.add(createSectionTitle("✈ Business Class"));
        for (int i = 1; i <= model.getBusinessRow(); i++) {
            seatContainer.add(createRowPanel(i, new String[] { "A", "B" }, new String[] { "C", "D" }, seatMap, flight));
        }

        seatContainer.add(Box.createVerticalStrut(10));

        // Economy Class
        seatContainer.add(createSectionTitle("💺 Economy Class"));
        int ecoStart = model.getBusinessRow() + 1;
        int ecoEnd = model.getBusinessRow() + model.getEconomyRow();
        for (int i = ecoStart; i <= ecoEnd; i++) {
            seatContainer.add(
                    createRowPanel(i, new String[] { "A", "B", "C" }, new String[] { "D", "E", "F" }, seatMap, flight));
        }

        JScrollPane scroll = new JScrollPane(seatContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(BG_COLOR);

        seatViewerPanel.add(scroll, BorderLayout.CENTER);
        seatViewerPanel.revalidate();
        seatViewerPanel.repaint();
    }

    private JLabel createSectionTitle(String title) {
        JLabel l = new JLabel(title);
        l.setForeground(TEXT_MAIN);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(8, 0, 5, 0));
        return l;
    }

    private JPanel createRowPanel(int rowNum, String[] leftGroup, String[] rightGroup, Map<String, Seat> seatMap,
            Flight flight) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 1));
        rowPanel.setOpaque(false);

        JLabel lblNum = new JLabel(String.valueOf(rowNum), SwingConstants.RIGHT);
        lblNum.setForeground(TEXT_SUB);
        lblNum.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lblNum.setPreferredSize(new Dimension(20, SEAT_SIZE));
        rowPanel.add(lblNum);

        for (String c : leftGroup) {
            rowPanel.add(createSeatButton(c + rowNum, seatMap, flight));
        }
        rowPanel.add(Box.createHorizontalStrut(12));
        for (String c : rightGroup) {
            rowPanel.add(createSeatButton(c + rowNum, seatMap, flight));
        }

        return rowPanel;
    }

    private JLabel createSeatButton(String code, Map<String, Seat> seatMap, Flight flight) {
        Seat seat = seatMap.get(code);
        JLabel seatLabel = new JLabel();
        seatLabel.setPreferredSize(new Dimension(SEAT_SIZE, SEAT_SIZE));

        if (seat != null) {
            boolean isBusiness = seat.getTicketClass() == TicketClass.BUSINESS;
            boolean isReserved = seat.isReserved();

            seatLabel.setIcon(isBusiness ? (isReserved ? B_FULL : B_EMPTY) : (isReserved ? E_FULL : E_EMPTY));

            seatLabel.setToolTipText(code + (isReserved ? " (Dolu)" : " (Boş)"));
            seatLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Tıklama eventi - dolu koltuklar için yolcu bilgisi göster
            seatLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (seat.isReserved()) {
                        showPassengerInfo(flight, code);
                    } else {
                        showEmptySeatInfo(code, seat);
                    }
                }
            });
        }

        return seatLabel;
    }

    // === YOLCU BİLGİSİ GÖSTERME ===
    private void showPassengerInfo(Flight flight, String seatCode) {
        // Rezervasyonu bul
        Reservation reservation = null;
        for (Reservation res : rm.getAllReservations()) {
            if (res.getFlight().getFlightNum() == flight.getFlightNum() &&
                    res.getSeat().getSeatCode().equals(seatCode)) {
                reservation = res;
                break;
            }
        }

        passengerInfoPanel.removeAll();

        if (reservation != null) {
            Passanger passanger = reservation.getpassanger();

            // Mini kimlik kartı
            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setBackground(CARD_COLOR);
            card.setBorder(new EmptyBorder(10, 15, 10, 15));

            // Sol: Avatar
            JLabel avatar = new JLabel(passanger.getGender() == 1 ? "👨" : "👩");
            avatar.setFont(new Font("Segoe UI", Font.PLAIN, 40));
            card.add(avatar, BorderLayout.WEST);

            // Merkez: Bilgiler
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);

            JLabel lblName = new JLabel(passanger.getName());
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblName.setForeground(TEXT_MAIN);

            JLabel lblId = new JLabel("ID: " + passanger.getID());
            lblId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblId.setForeground(TEXT_SUB);

            JLabel lblSeat = new JLabel("Koltuk: " + seatCode + " | Rezervasyon: " + reservation.getReservationCode());
            lblSeat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblSeat.setForeground(ACCENT_BLUE);

            infoPanel.add(lblName);
            infoPanel.add(Box.createVerticalStrut(3));
            infoPanel.add(lblId);
            infoPanel.add(Box.createVerticalStrut(3));
            infoPanel.add(lblSeat);

            card.add(infoPanel, BorderLayout.CENTER);

            // Sağ: Bakiye
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setOpaque(false);

            JLabel lblBalance = new JLabel(String.format("%.0f ₺", passanger.getBalance()));
            lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblBalance.setForeground(ACCENT_GREEN);

            JLabel lblPoints = new JLabel("⭐ " + passanger.getLoyaltyPoints() + " P");
            lblPoints.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblPoints.setForeground(new Color(212, 175, 55));

            rightPanel.add(lblBalance);
            rightPanel.add(lblPoints);

            card.add(rightPanel, BorderLayout.EAST);

            passengerInfoPanel.add(card, BorderLayout.CENTER);
        } else {
            JLabel lblNotFound = new JLabel("Rezervasyon bilgisi bulunamadı (Koltuk: " + seatCode + ")");
            lblNotFound.setForeground(new Color(220, 53, 69));
            lblNotFound.setHorizontalAlignment(JLabel.CENTER);
            passengerInfoPanel.add(lblNotFound, BorderLayout.CENTER);
        }

        passengerInfoPanel.revalidate();
        passengerInfoPanel.repaint();
    }

    private void showEmptySeatInfo(String code, Seat seat) {
        passengerInfoPanel.removeAll();

        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel icon = new JLabel("💺");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        card.add(icon, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Boş Koltuk: " + code);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_MAIN);

        String classType = seat.getTicketClass() == TicketClass.BUSINESS ? "Business Class" : "Economy Class";
        JLabel lblClass = new JLabel("Sınıf: " + classType);
        lblClass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblClass.setForeground(seat.getTicketClass() == TicketClass.BUSINESS ? new Color(245, 158, 11) : ACCENT_GREEN);

        JLabel lblStatus = new JLabel("✅ Müsait - Rezervasyona Açık");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(ACCENT_GREEN);

        infoPanel.add(lblTitle);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblClass);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(lblStatus);

        card.add(infoPanel, BorderLayout.CENTER);

        passengerInfoPanel.add(card, BorderLayout.CENTER);
        passengerInfoPanel.revalidate();
        passengerInfoPanel.repaint();
    }
}

