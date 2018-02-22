package net.termer.esolang;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class OutputWindow extends JFrame {
	private static JTextArea text = null;
	private static OutputWindow w = null;
	
	private OutputWindow() {
		super("Output");
		setSize(350,600);
		text = new JTextArea();
		text.setBackground(Color.BLACK);
		text.setForeground(Color.WHITE);
		text.setEditable(false);
		JScrollPane scroll = new JScrollPane(text);
		getContentPane().add(scroll);
		setVisible(true);
	}
	
	public static boolean initialized() {
		return w!=null;
	}
	
	public static void open() {
		if(w==null) {
			w = new OutputWindow();
		}
		w.setVisible(true);
	}
	public static void append(char c) {
		text.setText(text.getText()+c);
	}
	public static void append(String s) {
		text.setText(text.getText()+s);
	}
	public static void clear() {
		text.setText("");
	}
}
