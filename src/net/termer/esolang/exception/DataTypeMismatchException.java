package net.termer.esolang.exception;

public class DataTypeMismatchException extends Exception {
	public int line = 0;
	
	public DataTypeMismatchException(String msg, int ln) {
		super(msg);
		line = ln;
	}
}
