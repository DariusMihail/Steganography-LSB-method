import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class EncryptLSB {
	
	/*
	 * Driver method
	 */
	public static void Encrypt(File imageFile, String message) {
		String directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString(); //ia directorul sursa
		String newImageFileString = directory + "\\export.png";  //acesta va fi numele imaginii pe care o exportez, din directoul "directory"
		File newImageFile = new File(newImageFileString);
		
		BufferedImage image;
		
		// iau imaginea si o pun intr-un buffer pentru a o putea manipula
		try {
			image = ImageIO.read(imageFile);
			//functia GetImageToEncryt o creez eu
			// cu aceasta functie copiez imaginea intr-un buffer pentru a o putea manipula fiindca vreau sa lucrez cu copia ei, ci nu cu originalul
			BufferedImage imageToEncrypt = GetImageToEncrypt(image);
			Pixel[] pixels = GetPixelArray(imageToEncrypt);
			String[] messageBinary = ConvertMessageToBinary(message); //functie pe care o creez eu pt a tranfsorma mesajul meu in binar
			EncodeMessageBinaryInPixels(pixels, messageBinary);
			ReplacePixelsInNewBufferedImage(pixels, imageToEncrypt);
			SaveNewFile(imageToEncrypt, newImageFile);  //am criptat mesajul in RGB
			
		} catch(IOException e) {
			
		}
												
	}
	
	//ColorModel ne spune despre R,G si B cuprinse intre 0 si 255, valorile lor
	
	private static BufferedImage GetImageToEncrypt(BufferedImage image) {
		ColorModel colorModel = image.getColorModel();
		boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
	}
	
	//tranfosrm imageToEncrypt, care reprezinta copia imaginii pe care o folosesc intr-un vector, in loc de o matrice
	private static Pixel[] GetPixelArray(BufferedImage imageToEncrypt) {
		int height =  imageToEncrypt.getHeight();
		int width =  imageToEncrypt.getWidth();
		Pixel[] pixels = new Pixel[width * height];
		int count = 0;
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Color colorToAdd = new Color(imageToEncrypt.getRGB(x,y));
				pixels[count] = new Pixel(x, y, colorToAdd);
				count++;
			}
		}
		
		return pixels;
	}
	
	//transform mesajul meu in binar
	// prima data transform mesajul in cod ASCII, iar apoi in binar(a => 97 => 010101110)
	private static String[] ConvertMessageToBinary(String message) {
		int[] messageAscii = ConvertMessageToAscii(message);
		String[] messageBinary = ConvertAsciiToBinary(messageAscii);
		return messageBinary;
	}
	
	private static int[] ConvertMessageToAscii(String message) {
		int[] messageAscii = new int[message.length()]; 
		for(int i = 0; i < message.length(); i++) {
			messageAscii[i] = (int) message.charAt(i); 
		}
		return messageAscii;
	}
	
	private static String[] ConvertAsciiToBinary(int[] asciiValues) {
		String[] messageBinary = new String[asciiValues.length];
		for(int i = 0; i < asciiValues.length; i++) {
			String binary = LeftPadZeros(Integer.toBinaryString(asciiValues[i]));
			messageBinary[i] =  binary;
		}
		return messageBinary;
	}
	
	private static String LeftPadZeros(String binary) {
		StringBuilder sb = new StringBuilder("00000000");
		int offset = 8 - binary.length();
		for(int i = 0; i < binary.length(); i++) {
			sb.setCharAt(i+offset, binary.charAt(i));
		}
		return sb.toString();
	}
	
	// functie pentru a ascunde mesajul in imaginea
	private static void EncodeMessageBinaryInPixels(Pixel[] pixels, String[] messageBinary) {
		int pixelIndex = 0;  // variabila pentru a stii unde ne aflam
		boolean isLastCharacter = false;  //si sa vedem daca pixelii din mesaj se potriveste cu ultimul pixel din imagine
		for(int i = 0; i < messageBinary.length; i++) {
			Pixel[] currentPixels = new Pixel[] {pixels[pixelIndex],pixels[pixelIndex+1], pixels[pixelIndex+2]};  //urmatorii 3 pixeli 
			if(i + 1 == messageBinary.length) { 
				isLastCharacter = true;
			}
			ChangePixelsColor(messageBinary[i], currentPixels, isLastCharacter); // schimba culoarea pixelului curent pentru a ascunde mesajul
			pixelIndex = pixelIndex + 3;
		}
	}
	
	private static void ChangePixelsColor(String messageBinary, Pixel[] pixels, boolean isLastCharacter) {
		int messageIndex = 0;
		for(int  i = 0; i < pixels.length - 1; i++) {
			char[] messageBinaryCharacter = new char[] {messageBinary.charAt(messageIndex), messageBinary.charAt(messageIndex+1), messageBinary.charAt(messageIndex+2)};
			String[] pixelRGBBinary = GetPixelsRGBBinary(pixels[i], messageBinaryCharacter);
			pixels[i].setColor(GetNewPixelColor(pixelRGBBinary));
			messageIndex = messageIndex + 3;
		}
		if(isLastCharacter == false) {
			char[] messageBinaryChars = new char[] {messageBinary.charAt(messageIndex), messageBinary.charAt(messageIndex+1), '1'};
			String[] pixelRGBBinary = GetPixelsRGBBinary(pixels[pixels.length - 1], messageBinaryChars);
			pixels[pixels.length - 1].setColor(GetNewPixelColor(pixelRGBBinary));
		}
		else {
			char[] messageBinaryChars = new char[] {messageBinary.charAt(messageIndex), messageBinary.charAt(messageIndex+1), '0'};
			String[] pixelRGBBinary = GetPixelsRGBBinary(pixels[pixels.length - 1], messageBinaryChars);
			pixels[pixels.length - 1].setColor(GetNewPixelColor(pixelRGBBinary));
		}
	}
	
	//cu aceasta functie voi transofrm pixelii R,G si B in int, apoi in binar si schimb LSB binarului
	private static String[] GetPixelsRGBBinary(Pixel pixel, char[] messageBinaryChars) {
		String[] pixelRGBBinary = new String[3];
		pixelRGBBinary[0] = ChangePixelBinary(Integer.toBinaryString(pixel.getColor().getRed()), messageBinaryChars[0]);
		pixelRGBBinary[1] = ChangePixelBinary(Integer.toBinaryString(pixel.getColor().getGreen()), messageBinaryChars[1]);
		pixelRGBBinary[2] = ChangePixelBinary(Integer.toBinaryString(pixel.getColor().getBlue()), messageBinaryChars[2]);
		
		return pixelRGBBinary;
	}
	
	private static String ChangePixelBinary(String pixelBinary, char messageBinaryChar) {
		StringBuilder sb = new StringBuilder(pixelBinary);
		sb.setCharAt(pixelBinary.length()-1, messageBinaryChar); //setez ultimul pixel din  pixelBinary la ultimul caracter din messageBinary
		return sb.toString();
		}
	
	//cu aceasta functie voi schimba toate cele 3 ale stringului inapoi valori in int 
	private static Color GetNewPixelColor(String[] colorBinary) {
		return new Color(Integer.parseInt(colorBinary[0], 2), Integer.parseInt(colorBinary[1],2), Integer.parseInt(colorBinary[2], 2));
	}
	
	private static void ReplacePixelsInNewBufferedImage(Pixel[] pixels, BufferedImage imageToEncrypt) {
		for(int i = 0; i < pixels.length; i++) {
			imageToEncrypt.setRGB(pixels[i].getX(), pixels[i].getY(), pixels[i].getColor().getRGB());
		}
	}
	
	private static void SaveNewFile(BufferedImage newImage, File newImageFile) {
		try {
			ImageIO.write(newImage, "png", newImageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
