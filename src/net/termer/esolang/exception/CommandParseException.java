package net.termer.esolang.exception;

public class CommandParseException extends Exception {
	public int line = 0;
	
	public CommandParseException(String msg, int ln) {
		super(msg);
		line = ln;
	}
}
