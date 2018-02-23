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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.termer.esolang.exception.CommandParseException;
import net.termer.esolang.exception.DataTypeMismatchException;
import net.termer.esolang.exception.GotoException;
import net.termer.esolang.exception.VariableException;

public class Uyjhmnn {
	public static boolean gui = true;
	public static boolean ignoreErrors = false;
	
	public static void main(String[] args) {
		Thread.currentThread().setName("jUyjhmnn");
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
		if(flags.contains("pkg")) {
			gui=false;
			System.out.println("Creating package...");
		}
		if(flags.contains("ignore-errors")) {
			ignoreErrors = true;
		}
		if(gui) {
			UyjWindow.start();
		}
		if(arguments.size()>0) {
			try {
				FileInputStream fin = new FileInputStream(new File(arguments.get(0)));
				String script = "";
				while(fin.available()>0) {
					script+=(char)fin.read();
				}
				fin.close();
				if(gui) {
					UyjWindow.w.bottom.setText(script);
				} else {
					if(flags.contains("pkg")) {
						new Uyjhmnn(arguments.get(0));
					} else {
						new Interpreter(script).interpret(ignoreErrors);
					}
				}
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
			System.out.println("Usage: java -jar uyjhmnn.jar [script] [flags]");
			System.out.println("\nFlags");
			System.out.println("  -nogui : disable IDE and GUI");
			System.out.println("  -ignore-errors : ignore scripting errors");
			System.out.println("  -pkg : package the specified script as a standalone executable jar file");
			System.out.println("\nSpecifying a script with nogui enabled causes the script to be executed in the\ncommand line, otherwise it will be loaded into the IDE if nogui is not enabled.");
		}
	}
	
	public static void output(char out) {
		if(gui) {
			OutputWindow.open();
			OutputWindow.append(out);
		} else {
			System.out.print(out);
		}
	}
	public static void output(String out) {
		if(gui) {
			OutputWindow.open();
			OutputWindow.append(out);
		} else {
			System.out.print(out);
		}
	}
	public static int inputNumber() throws IOException {
		int r = 0;
		if(gui) {
			while(true) {
				try {
					r=Integer.parseInt(JOptionPane.showInputDialog("Please enter a number:"));
					break;
				} catch(Exception e) {} 
			}
		} else {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				String ln = in.readLine();		
				try {
					r=Integer.parseInt(ln);
					break;
				} catch(Exception e) {}
			}
		}
		return r;
	}
	public static int inputCharacter() throws IOException {
		int r = 0;
		if(gui) {
			try {
				r=(int)JOptionPane.showInputDialog("Please enter a character:").charAt(0);
			} catch(Exception e) {}
		} else {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			r = in.read();
		}
		return r;
	}
	
	public Uyjhmnn(String script) throws Exception {
		System.out.println("Extracting interpreter...");
		ExportResource("/standalone.jar");
		ZipFile zf = new ZipFile("standalone.jar");
		System.out.println("Extracting script...");
		File dir = new File(Double.toString(Math.random()*10).replace(".", ""));
		dir.mkdir();
		File tmpScript = new File(dir.toString()+"/"+script);
		Files.copy(new File(script).toPath(), tmpScript.toPath(), StandardCopyOption.REPLACE_EXISTING);
		tmpScript.renameTo(new File(dir.toString()+"/script.uyj"));
		System.out.println("Compiling...");
		zf.addFile(new File(dir.toString()+"/script.uyj"), new ZipParameters());
		System.out.println("Finishing up...");
		new File(dir.toString()+"/script.uyj").delete();
		dir.delete();
		new File("standalone.jar").renameTo(new File(script+".jar"));
		System.out.println("Package created as "+script+".jar");
	}
	
	public String ExportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = getClass().getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
//            stream.close();
//            resStreamOut.close();
        }

        return jarFolder + resourceName;
    }
}
