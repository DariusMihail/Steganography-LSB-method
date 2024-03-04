import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

public class MainPage {

	private JFrame frame;
	private JTextField messageField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainPage window = new MainPage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel buttonsPanel = new JPanel();
		frame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton encryptButton = new JButton("Encrypt");
		buttonsPanel.add(encryptButton);
		
		JButton decryptButton = new JButton("Decrypt");
		buttonsPanel.add(decryptButton);
		
		JPanel messagePanel = new JPanel();
		frame.getContentPane().add(messagePanel, BorderLayout.CENTER);
		messagePanel.setLayout(null);
		
		JLabel messageLabel = new JLabel("Message:");
		messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		messageLabel.setBounds(44, 59, 65, 24);
		messagePanel.add(messageLabel);
		
		messageField = new JTextField();
		messageField.setBounds(112, 63, 300, 20);
		messagePanel.add(messageField);
		messageField.setColumns(10);
		
		
		// de aici adaug cod eu
		
		//actionlistener asculta de butoane, cand un buton este apasat, se executa cerinta
		
		//actionlistener pentru butonul de encrypt
		ActionListener encryptListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File imageFile = FileChooser.MakeFileChooser();  //luam fisierul din fileChooser
				if(imageFile != null) {
					EncryptLSB.Encrypt(imageFile, messageField.getText());  // va cripta mesajul in imagine
				}
			}
		};
		encryptButton.addActionListener(encryptListener);
		
		//actionlistener pentru butonul de decrypt
		ActionListener decryptListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DecryptLSB.Decrypt();
			}
		};
		
		decryptButton.addActionListener(decryptListener);
		
	}
}
