package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;

public class PersonInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Create the panel.
	 */
	public static void main(String[] args) {
	    JFrame fr = new JFrame("Register");
	    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    fr.setSize(400, 350);
	    fr.getContentPane().add(new PersonInfoPanel());
	    fr.setVisible(true);
	}

	
	public PersonInfoPanel() {
		setBackground(SystemColor.controlHighlight);
		setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(50, 50));
		panel.setPreferredSize(new Dimension(100, 10));
		panel.setBackground(SystemColor.text);
		panel.setBounds(10, 11, 101, 100);
		add(panel);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(220, 10, 150, 20);
		add(textField_1);
		
		JButton btnNewButton = new JButton("PP EKLE");
		btnNewButton.setBounds(10, 120, 101, 22);
		add(btnNewButton);
		
		JLabel nameLbl = new JLabel("İsim");
		nameLbl.setFont(new Font("Ink Free", Font.BOLD, 14));
		nameLbl.setMinimumSize(new Dimension(50, 10));
		nameLbl.setMaximumSize(new Dimension(50, 10));
		nameLbl.setPreferredSize(new Dimension(50, 10));
		nameLbl.setBounds(120, 10, 100, 20);
		add(nameLbl);
		
		JLabel lblEposta = new JLabel("e-posta:");
		lblEposta.setPreferredSize(new Dimension(50, 10));
		lblEposta.setMinimumSize(new Dimension(50, 10));
		lblEposta.setMaximumSize(new Dimension(50, 10));
		lblEposta.setFont(new Font("Ink Free", Font.BOLD, 14));
		lblEposta.setBounds(120, 40, 100, 20);
		add(lblEposta);
		
		JLabel nameLbl_1_1 = new JLabel("Doğum Tarihi");
		nameLbl_1_1.setPreferredSize(new Dimension(50, 10));
		nameLbl_1_1.setMinimumSize(new Dimension(50, 10));
		nameLbl_1_1.setMaximumSize(new Dimension(50, 10));
		nameLbl_1_1.setFont(new Font("Ink Free", Font.BOLD, 14));
		nameLbl_1_1.setBounds(120, 70, 100, 20);
		add(nameLbl_1_1);
		
		JFormattedTextField formattedTextField = new JFormattedTextField();
		formattedTextField.setBounds(220, 70, 150, 20);
		add(formattedTextField);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(220, 40, 150, 20);
		add(textField_2);
		
		JLabel nameLbl_1_1_1 = new JLabel("Cinsiyet:");
		nameLbl_1_1_1.setPreferredSize(new Dimension(50, 10));
		nameLbl_1_1_1.setMinimumSize(new Dimension(50, 10));
		nameLbl_1_1_1.setMaximumSize(new Dimension(50, 10));
		nameLbl_1_1_1.setFont(new Font("Ink Free", Font.BOLD, 14));
		nameLbl_1_1_1.setBounds(117, 100, 100, 20);
		add(nameLbl_1_1_1);
		
		JLabel nameLbl_1_1_1_1 = new JLabel("ID:");
		nameLbl_1_1_1_1.setPreferredSize(new Dimension(50, 10));
		nameLbl_1_1_1_1.setMinimumSize(new Dimension(50, 10));
		nameLbl_1_1_1_1.setMaximumSize(new Dimension(50, 10));
		nameLbl_1_1_1_1.setFont(new Font("Ink Free", Font.BOLD, 14));
		nameLbl_1_1_1_1.setBounds(117, 130, 100, 20);
		add(nameLbl_1_1_1_1);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Kadın");
		rdbtnNewRadioButton.setBounds(220, 100, 70, 22);
		add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnErkek = new JRadioButton("Erkek");
		rdbtnErkek.setBounds(300, 100, 70, 22);
		add(rdbtnErkek);
		
		JLabel nameLbl_1_1_2 = new JLabel("Buraya ID gelecek");
		nameLbl_1_1_2.setPreferredSize(new Dimension(50, 10));
		nameLbl_1_1_2.setMinimumSize(new Dimension(50, 10));
		nameLbl_1_1_2.setMaximumSize(new Dimension(50, 10));
		nameLbl_1_1_2.setFont(new Font("Ink Free", Font.BOLD, 14));
		nameLbl_1_1_2.setBounds(220, 130, 150, 20);
		add(nameLbl_1_1_2);
		
		JButton btnNewButton_1 = new JButton("Kayıt");
		btnNewButton_1.setFont(new Font("Ink Free", Font.BOLD, 20));
		btnNewButton_1.setBounds(210, 170, 90, 25);
		add(btnNewButton_1);
		
		JButton btnNewButton_1_1 = new JButton("İptal");
		btnNewButton_1_1.setFont(new Font("Ink Free", Font.BOLD, 20));
		btnNewButton_1_1.setBounds(100, 170, 90, 25);
		add(btnNewButton_1_1);

	}
}


