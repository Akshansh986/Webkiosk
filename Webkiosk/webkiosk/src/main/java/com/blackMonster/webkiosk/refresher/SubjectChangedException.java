package com.blackMonster.webkiosk.refresher;


public class SubjectChangedException extends Exception{
	public SubjectChangedException() {
	}

	 public SubjectChangedException(String message)
     {
        super(message);
     }
	 @Override
	public void printStackTrace() {
		// Log.e("BadHtmlSourceException", "Bad Html file");
		 super.printStackTrace();
	}
}
