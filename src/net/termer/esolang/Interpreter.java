package net.termer.esolang;

import java.util.ArrayList;
import java.util.HashMap;

import net.termer.esolang.exception.*;

public class Interpreter {
	public ArrayList<String> commands = new ArrayList<String>();
	public HashMap<String,Integer> labels = new HashMap<String,Integer>();
	public HashMap<String,Integer> variables = new HashMap<String,Integer>();
	private String openVariable = null;
	private String code = null;
	
	public Interpreter(String code) {
		this.code=code;
	}
	
	public void interpret(boolean ignoreExceptions) throws DataTypeMismatchException, VariableException, CommandParseException, GotoException {
		// First job is to enumerate all the lines of code
		// This saves time during execution and makes jumping to different lines easier
		// In addition, it defines labels for line jumping
		String[] rawCmds = code.split("\n");
		for(int i = 0; i < rawCmds.length; i++) {
			String cmd = rawCmds[i].trim();
			if(cmd.startsWith("DEFINE THE NEW LABEL ")) {
				labels.put(cmd.substring(21), new Integer(commands.size()));
				commands.add("#");
			} else {
				commands.add(cmd);
			}
		}
		// At this point, the code has been processed
		for(int i = 0; i < commands.size(); i++) {
			String cmd = commands.get(i);
			if(cmd.startsWith("PRINT THE CHARACTER WITH THE ASCII VALUE ")) {
				String arg0 = cmd.substring(41);
				try {
					Uyjhmnn.output((char)Integer.parseInt(arg0));
				} catch(Exception e) {
					if(!ignoreExceptions) {
						e.printStackTrace();
						throw new DataTypeMismatchException("argument must be an integer",i+1);
					}
				}
			} else if(cmd.startsWith("DECLARE THE NEW VARIABLE ")) {
				String arg0 = cmd.substring(25);
				if(variables.containsKey(arg0)) {
					if(!ignoreExceptions) {
						throw new VariableException("variable with the name "+arg0+" already exists",i+1);
					}
				} else {
					variables.put(arg0, new Integer(0));
				}
			} else if(cmd.startsWith("OPEN THE VARIABLE ")) {
				String arg0 = cmd.substring(18);
				if(variables.containsKey(arg0)) {
					openVariable=arg0;
				} else {
					if(!ignoreExceptions) {
						throw new VariableException("variable with the name "+arg0+" does not exist",i+1);
					}
				}
			} else if(cmd.startsWith("ASSIGN ")) {
				String arg0 = cmd.substring(7).split(" ")[0];
				if(arg0==null) {
					if(!ignoreExceptions) {
						throw new DataTypeMismatchException("argument cannot be null",i+1);
					}
				} else {
					if(openVariable==null) {
						if(!ignoreExceptions) {
							throw new VariableException("there is no open variable",i+1);
						}
					} else {
						variables.replace(openVariable,Integer.parseInt(arg0));
					}
				}
			} else if(cmd.startsWith("ADD ")) {
				String arg0 = cmd.substring(4).split(" ")[0];
				if(openVariable==null) {
					if(!ignoreExceptions) {
						throw new VariableException("there is no open variable",i+1);
					}
				} else {
					try {
						variables.replace(openVariable, new Integer(variables.get(openVariable).intValue()+Integer.parseInt(arg0)));
					} catch(Exception e) {
						if(!ignoreExceptions) {
							throw new DataTypeMismatchException("argument must be an integer",i+1);
						}
					}
				}
			} else if(cmd.startsWith("MULTIPLY THE OPEN VARIABLE BY ")) {
				String arg0 = cmd.substring(30);
				if(openVariable==null) {
					if(!ignoreExceptions) {
						throw new VariableException("there is no open variable",i+1);
					}
				} else {
					try {
						variables.replace(openVariable, new Integer(variables.get(openVariable).intValue()*Integer.parseInt(arg0)));
					} catch(Exception e) {
						if(!ignoreExceptions) {
							throw new DataTypeMismatchException("argument must be an integer",i+1);
						}
					}
				}
			} else if(cmd.startsWith("PRINT THE OPEN VARIABLE'S CHARACTER")) {
				if(openVariable==null) {
					if(!ignoreExceptions) {
						throw new VariableException("there is no open variable",i+1);
					}
				} else {
					Uyjhmnn.output((char)variables.get(openVariable).intValue());
				}
			} else if(cmd.startsWith("PRINT THE OPEN VARIABLE'S VALUE")) {
				if(openVariable==null) {
					if(!ignoreExceptions) {
						throw new VariableException("there is no open variable",i+1);
					}
				} else {
					Uyjhmnn.output(Integer.toString(variables.get(openVariable).intValue()));
				}
			} else if(cmd.startsWith("#")) {
				
			} else if(cmd.contains("EQUAL TO") && cmd.split(" ").length==9) {
				String arg0 = cmd.split(" ")[2];
				String arg1 = cmd.split(" ")[4];
				String arg2 = cmd.split(" ")[8];
				if(labels.containsKey(arg0)) {
					if(variables.containsKey(arg1)) {
						if(variables.containsKey(arg2)) {
							if(variables.get(arg1).intValue()==variables.get(arg2).intValue()) {
								i = labels.get(arg0).intValue();
							}
						} else {
							if(!ignoreExceptions) {
								throw new VariableException("variable with the name "+arg2+" does not exist",i+1);
							}
						}
					} else {
						if(!ignoreExceptions) {
							throw new VariableException("variable with the name "+arg1+" does not exist",i+1);
						}
					}
				} else {
					if(!ignoreExceptions) {
						throw new GotoException("no label exist with the name "+arg0,i+1);
					}
				}
			} else if(cmd.contains("GREATER THAN") && cmd.split(" ").length==9) {
				String arg0 = cmd.split(" ")[2];
				String arg1 = cmd.split(" ")[4];
				String arg2 = cmd.split(" ")[8];
				if(labels.containsKey(arg0)) {
					if(variables.containsKey(arg1)) {
						if(variables.containsKey(arg2)) {
							if(variables.get(arg1).intValue()>variables.get(arg2).intValue()) {
								i = labels.get(arg0).intValue();
							}
						} else {
							if(!ignoreExceptions) {
								throw new VariableException("variable with the name "+arg2+" does not exist",i+1);
							}
						}
					} else {
						if(!ignoreExceptions) {
							throw new VariableException("variable with the name "+arg1+" does not exist",i+1);
						}
					}
				} else {
					if(!ignoreExceptions) {
						throw new GotoException("no label exist with the name "+arg0,i+1);
					}
				}
			} else if(cmd.contains("LESS THAN") && cmd.split(" ").length==9) {
				String arg0 = cmd.split(" ")[2];
				String arg1 = cmd.split(" ")[4];
				String arg2 = cmd.split(" ")[8];
				if(labels.containsKey(arg0)) {
					if(variables.containsKey(arg1)) {
						if(variables.containsKey(arg2)) {
							if(variables.get(arg1).intValue()<variables.get(arg2).intValue()) {
								i = labels.get(arg0).intValue();
							}
						} else {
							if(!ignoreExceptions) {
								throw new VariableException("variable with the name "+arg2+" does not exist",i+1);
							}
						}
					} else {
						if(!ignoreExceptions) {
							throw new VariableException("variable with the name "+arg1+" does not exist",i+1);
						}
					}
				} else {
					if(!ignoreExceptions) {
						throw new GotoException("no label exist with the name "+arg0,i+1);
					}
				}
			} else if(cmd.startsWith("GET INPUT AND STORE INTO OPEN VARIABLE AS A CHARACTER")) {
				if(openVariable==null) {
					if(!ignoreExceptions) {
						throw new VariableException("there is no open variable",i+1);
					}
				} else {
					try {
						variables.replace(openVariable,new Integer(Uyjhmnn.inputCharacter()));
					} catch(Exception e) {
						System.err.println("There's a problem with the interpreter environment, please report the following to the author of this interpreter:");
						e.printStackTrace();
					}
				}
			} else if(cmd.startsWith("GET INPUT AND STORE INTO OPEN VARIABLE AS A NUMBER")) {
				try {
					variables.replace(openVariable,new Integer(Uyjhmnn.inputNumber()));
				} catch(Exception e) {
					System.err.println("There's a problem with the interpreter environment, please report the following to the author of this interpreter:");
					e.printStackTrace();
				}
			} else if(cmd.startsWith("JUMP TO ")) {
				String arg0 = cmd.substring(8);
				if(labels.containsKey(arg0)) {
					i = labels.get(arg0).intValue();
				} else {
					if(!ignoreExceptions) {
						throw new GotoException("no label exist with the name "+arg0,i+1);
					}
				}
			} else if(cmd.startsWith("END THIS PROGRAM")) {
				break;
			} else {
				if(!ignoreExceptions) {
					throw new CommandParseException("undefined command",i+1);
				}
			}
		}
		Uyjhmnn.output('\n');
	}
}
