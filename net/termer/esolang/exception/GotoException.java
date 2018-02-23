package net.termer.esolang.exception;

public class GotoException extends Exception {
	public int line = 0;
	
	public GotoException(String msg, int ln) {
		super(msg);
		line = ln;
	}
}
