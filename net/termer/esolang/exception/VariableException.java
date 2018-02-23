package net.termer.esolang.exception;

public class VariableException extends Exception {
	public int line = 0;
	
	public VariableException(String msg, int ln) {
		super(msg);
		line = ln;
	}
}
