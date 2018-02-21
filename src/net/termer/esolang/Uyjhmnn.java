/**
	-jUyjhmn n-
	An interpreter for the esolang "Uyjhmn n" by Termer <termer.net>
	You can learn more about Uyjhmn n at its wiki page:
	       https://esolangs.org/wiki/Uyjhmn_n
	This code is open source, and licensed under the Tremor 1.0
	license, which can be found here: http://termer.net/license/tremor.txt and
	http://web.archive.org/web/20180220230954/https://termer.net/license/tremor.txt
	
	Original Uyjhmn n interpreter by Truttle1
	
	P.S. Woo you can use # for comments with this interpreter,
	     also JUMP TO [l] for faster loops, subroutines, etc
**/

package net.termer.esolang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.termer.esolang.exception.CommandParseException;
import net.termer.esolang.exception.DataTypeMismatchException;
import net.termer.esolang.exception.GotoException;
import net.termer.esolang.exception.VariableException;

public class Uyjhmnn {
	private static boolean gui = true;
	private static boolean ignoreErrors = false;
	
	public static void main(String[] args) {
		ArrayList<String> flags = new ArrayList<String>();
		ArrayList<String> arguments = new ArrayList<String>();
		for(String arg : args) {
			if(arg.startsWith("-")) {
				flags.add(arg.substring(1));
			} else {
				arguments.add(arg);
			}
		}
		if(flags.contains("nogui")) {
			gui=false;
		}
		if(flags.contains("ignore-errors")) {
			ignoreErrors = true;
		}
		if(arguments.size()>0) {
			try {
				FileInputStream fin = new FileInputStream(new File(arguments.get(0)));
				String script = "";
				while(fin.available()>0) {
					script+=(char)fin.read();
				}
				fin.close();
				new Interpreter(script).interpret(ignoreErrors);
			} catch(Exception e) {
				int ln = 0;
				if(e.getClass()==CommandParseException.class) {
					ln = ((CommandParseException)e).line;
				} else if(e.getClass()==DataTypeMismatchException.class) {
					ln = ((DataTypeMismatchException)e).line;
				} else if(e.getClass()==GotoException.class) {
					ln = ((GotoException)e).line;
				} else if(e.getClass()==VariableException.class) {
					ln = ((VariableException)e).line;
				} else if(e.getClass()==FileNotFoundException.class) {
					System.out.println("==Cannot find the script specified==");
				} else {
					System.out.println("The following is an interpreter error. Please report it to the author of this software.");
				}
				if(ln==0) {
					e.printStackTrace();
				} else {
					output("\nError executing script at line "+Integer.toString(ln)+"\n");
					output(e.getClass().getName()+": "+e.getMessage()+"\n");
				}
			}
		} else {
			System.out.println("Please specify the path to a script");
		}
	}
	
	public static void output(char out) {
		System.out.print(out);
	}
	public static void output(String out) {
		System.out.print(out);
	}
	public static int inputNumber() throws IOException {
		int r = 0;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			String ln = in.readLine();		
			try {
				r=Integer.parseInt(ln);
				break;
			} catch(Exception e) {}
		}
		return r;
	}
	public static int inputCharacter() throws IOException {
		int r = 0;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		r = in.read();		
		return r;
	}
}
