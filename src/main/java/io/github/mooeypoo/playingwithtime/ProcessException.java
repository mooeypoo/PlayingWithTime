package io.github.mooeypoo.playingwithtime;

import java.util.ArrayList;

public class ProcessException extends Exception {
	private static final long serialVersionUID = 1L;
	private String rank;
	private Exception exception;

	public ProcessException(String errorMessage) {
		super(errorMessage);
    }

	public static ProcessException create(ArrayList<String> errors) {
		String errorMessage = "Errors during processing. Some definitions were skipped:\n";
		for (String err : errors) {
			errorMessage += "- " + err + "\n";
		}
		return new ProcessException(errorMessage);
	}

	public ProcessException(String rank, String errorMessage, Exception ex) {
        super(errorMessage);
        
        this.rank = rank;
        this.exception = ex;
    }
	
	public String getRank() {
		return this.rank;
	}
	
	public StackTraceElement[] getStackTrace() {
		if (this.exception != null) {
			return this.exception.getStackTrace();
		}
		return null;
	}

}
