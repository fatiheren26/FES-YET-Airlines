package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AssetPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private AssetManager am;
    private JComboBox<String> modelCombo;
    private DefaultTableModel airportModel, modelModel, fleetModel;

    public AssetPanel(AssetManager am) {
        this.am = am;
        setLayout(new BorderLayout(10, 10));
        initialize();
        updateAll();
    }

    private void initialize() {
        // --- ÜST FORM ALANI (3 EŞİT PARÇA) ---
        JPanel forms = new JPanel(new GridLayout(1, 3, 15, 10));
        
        forms.add(createAirportForm());
        forms.add(createModelForm());
        forms.add(createPurchaseForm());

        add(forms, BorderLayout.NORTH);

        // --- ALT TABLO ALANI ---
        JPanel tables = new JPanel(new GridLayout(1, 3, 10, 10));
        tables.add(createManagedTable("Havalimanları", airportModel = new DefaultTableModel(new Object[]{"Şehir", "Adı"}, 0), 0));
        tables.add(createManagedTable("Modeller", modelModel = new DefaultTableModel(new Object[]{"Marka-Seri", "Hız"}, 0), 1));
        tables.add(createManagedTable("Filo", fleetModel = new DefaultTableModel(new Object[]{"Kuyruk No", "Model"}, 0), 2));
        add(tables, BorderLayout.CENTER);
    }

    private JPanel createAirportForm() {
        JPanel p = createTitledPanel("Havalimanı Kaydı");
        JTextField fCity = new JTextField(), fName = new JTextField(), fLat = new JTextField(), fLon = new JTextField();
        
        layoutDoubleField(p, "Şehir:", fCity, "Adı:", fName, 0);
        layoutDoubleField(p, "Enlem:", fLat, "Boylam:", fLon, 1);
        
        JButton btn = createFullWidthGreenButton("Havalimanı Kaydet");
        btn.addActionListener(e -> { 
            am.addAirport(fCity.getText(), fName.getText(), Double.parseDouble(fLat.getText()), Double.parseDouble(fLon.getText())); 
            updateAll(); 
        });
        layoutButtonFullWidth(p, btn, 2);
        return p;
    }

    private JPanel createModelForm() {
        JPanel p = createTitledPanel("Model Fabrikası");
        JTextField fBrand = new JTextField(), fSeries = new JTextField(4);
        JTextField fSpeed = new JTextField(), fFuel = new JTextField();
        JTextField fBRow = new JTextField(), fERow = new JTextField();

        layoutModelRow(p, "Marka:", fBrand, "Seri:", fSeries, 0);
        layoutDoubleField(p, "Hız:", fSpeed, "Yakıt:", fFuel, 1);
        layoutDoubleField(p, "Bsn. Sıra:", fBRow, "Eko. Sıra:", fERow, 2);

        JButton btn = createFullWidthGreenButton("Model Tanımla");
        btn.addActionListener(e -> {
            am.definePlaneModel(fBrand.getText(), fSeries.getText(), 
                Integer.parseInt(fBRow.getText()), Integer.parseInt(fERow.getText()), 
                Integer.parseInt(fSpeed.getText()), Integer.parseInt(fFuel.getText()));
            updateAll(); 
        });
        layoutButtonFullWidth(p, btn, 3);
        return p;
    }

    private JPanel createPurchaseForm() {
        JPanel p = createTitledPanel("Envanter Satın Al");
        modelCombo = new JComboBox<>();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; p.add(modelCombo, gbc);
        
        JButton btn = createFullWidthGreenButton("Uçak Satın Al");
        btn.addActionListener(e -> { 
            am.buyPlane((String)modelCombo.getSelectedItem());
            updateAll(); 
        });
        layoutButtonFullWidth(p, btn, 1);
        return p;
    }

    // --- MODERN BUTON VE YAYILIM AYARLARI ---

    private JButton createFullWidthGreenButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(40, 167, 69));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(0, 25)); // İnce ve modern
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return b;
    }

    private void layoutButtonFullWidth(JPanel p, JButton btn, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 0, 0); // Üstten boşluk, yanlardan sıfır
        gbc.gridy = y; 
        gbc.gridx = 0; 
        gbc.gridwidth = 4; // Satırı tamamen kapla
        gbc.fill = GridBagConstraints.HORIZONTAL; // Genişliğe yay
        gbc.weighty = 1.0; // En alta itilmesini sağlar
        gbc.anchor = GridBagConstraints.SOUTH; // Alt kenara yasla
        p.add(btn, gbc);
    }

    private void layoutDoubleField(JPanel p, String l1, JTextField f1, String l2, JTextField f2, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,5,2,5); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridy = y;
        gbc.gridx = 0; p.add(new JLabel(l1), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; p.add(f1, gbc);
        gbc.gridx = 2; p.add(new JLabel(l2), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; p.add(f2, gbc);
    }

    private void layoutModelRow(JPanel p, String l1, JTextField fBrand, String l2, JTextField fSeries, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,5,2,5); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridy = y;
        gbc.gridx = 0; p.add(new JLabel(l1), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8; p.add(fBrand, gbc);
        gbc.gridx = 2; p.add(new JLabel(l2), gbc);
        gbc.gridx = 3; gbc.weightx = 0.2; p.add(fSeries, gbc);
    }

    private JPanel createManagedTable(String title, DefaultTableModel m, int type) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new TitledBorder(title));
        JTextField search = new JTextField();
        JTable jt = new JTable(m);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m);
        jt.setRowSorter(sorter);
        search.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + search.getText())); }
        });

        JButton del = new JButton("Seçileni Sil");
        del.setBackground(new Color(220, 53, 69));
        del.setForeground(Color.WHITE);
        del.setFont(new Font("Segoe UI", Font.BOLD, 11));
        del.addActionListener(e -> {
            int row = jt.getSelectedRow();
            if(row != -1) {
                String key = (String) m.getValueAt(jt.convertRowIndexToModel(row), (type == 0 ? 1 : 0));
                if(type == 0) am.removeAirport(key);
                else if(type == 1) am.removeModel(key);
                else am.removePlane(key);
                updateAll();
            }
        });

        p.add(search, BorderLayout.NORTH);
        p.add(new JScrollPane(jt), BorderLayout.CENTER);
        p.add(del, BorderLayout.SOUTH);
        return p;
    }

    private void updateAll() {
        airportModel.setRowCount(0); am.getAirports().forEach((k, v) -> airportModel.addRow(new Object[]{v.getCity(), k}));
        modelModel.setRowCount(0); modelCombo.removeAllItems();
        am.getModels().forEach((k, v) -> { 
            modelModel.addRow(new Object[]{k, v.getAvarageVelocity()}); 
            modelCombo.addItem(k); 
        });
        fleetModel.setRowCount(0); am.getFleet().forEach((k, v) -> 
            fleetModel.addRow(new Object[]{k, v.getPlaneModel().getFullModelName()}));
    }

    private JPanel createTitledPanel(String t) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder(t));
        return p;
    }
}

