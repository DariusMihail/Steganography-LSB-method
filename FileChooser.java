import java.io.File;

import javax.swing.JFileChooser;

public class FileChooser {
	//selectezi un fisier pe care doresti sa-l encriptezi, adica o imagine, de preferat, JPEG sau PNG
	public static File MakeFileChooser() {
		JFileChooser chooser = new JFileChooser(); //creare obiect nou
		
		int option = chooser.showOpenDialog(null);  // null face sa se deschida in mijlocul paginii
		if(option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			return file;
		}												// daca se alege JFileChooser se intoarce imaginea selectata
		return null;
	}
	
}
