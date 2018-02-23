package net.termer.esolang;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.termer.esolang.exception.CommandParseException;
import net.termer.esolang.exception.DataTypeMismatchException;
import net.termer.esolang.exception.GotoException;
import net.termer.esolang.exception.VariableException;

public class UyjWindow extends JFrame {
	public static UyjWindow w = null;
	
	private JTextArea top = null;
	public JTextArea bottom = null;
	private JProgressBar secondsBar = null;
	private JButton interpret = null;
	private int codeTimer = 0;
	
	private UyjWindow() {
		//Initialize and setup some stuff
		super("jUyjhmn n");
		setSize(640,510);
		getContentPane().setBackground(Color.RED);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout());
		setResizable(false);
		
		//Add components
		top = new JTextArea(18,55);
		top.setEditable(false);
		top.setBackground(Color.GREEN);
		top.setSelectedTextColor(Color.ORANGE);
		top.setSelectionColor(Color.BLUE);
		
		bottom = new JTextArea(10,26);
		bottom.setBackground(Color.WHITE);
		bottom.setForeground(Color.MAGENTA);
		bottom.setSelectedTextColor(Color.CYAN);
		bottom.setSelectionColor(Color.PINK);
		bottom.setLineWrap(false);
		bottom.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		secondsBar = new JProgressBar();
		secondsBar.setMinimum(0);
		secondsBar.setMaximum(100);
		secondsBar.setValue(0);
		secondsBar.setStringPainted(true);
		new CodeTimer().start();
		
		interpret = new JButton("<html><b><s>JUST INTERPRET MY CODE ALREADY!!!</s></b></html>");
		interpret.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(OutputWindow.initialized()) {
						OutputWindow.clear();
					}
					new Interpreter(bottom.getText()).interpret(Uyjhmnn.ignoreErrors);
				} catch (DataTypeMismatchException | VariableException
						| CommandParseException | GotoException e) {
					Uyjhmnn.output("\nScript error!!1!");
					Uyjhmnn.output("\n"+e.getClass().getName()+": "+e.getMessage());
					if(codeTimer*2<100) {
						codeTimer=codeTimer*2;
					} else {
						codeTimer=99;
					}
				}
			}
		});
		
		getContentPane().add(new JScrollPane(top));
		getContentPane().add(new JScrollPane(bottom));
		getContentPane().add(secondsBar);
		getContentPane().add(interpret);
		
		//Ready for viewing
		setVisible(true);
	}
	
	public static void start() {
		w = new UyjWindow();
	}
	
	private class CodeTimer extends Thread {
		public CodeTimer() {
			setName("CodeTimer");
		}
		
		public void run() {
			try {
				while(true) {
				sleep(100);
				codeTimer++;
				if(codeTimer==100) {
					codeTimer=0;
					if(bottom.getText().split("\n").length>0) {
						ArrayList<String> lns = new ArrayList<String>();
						for(String ln : bottom.getText().split("\n")) {
							lns.add(ln);
						}
						if((lns.get(0).trim()+' ').charAt(0)!=' ') {
							top.setText(top.getText()+"\n"+lns.get(0));
						}
						lns.remove(0);
						if(lns.size()>0) {
							String txt = lns.get(0);
							for(int i = 1; i < lns.size(); i++) {
								txt+="\n"+lns.get(i);
							}
							bottom.setText(txt);
						} else {
							bottom.setText("");
						}
					}
				}
				secondsBar.setValue(codeTimer);
				secondsBar.setString(Integer.toString((100-codeTimer)/10)+" seconds");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}