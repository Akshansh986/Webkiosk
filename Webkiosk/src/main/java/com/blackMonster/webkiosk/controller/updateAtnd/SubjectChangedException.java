package com.blackMonster.webkiosk.controller.updateAtnd;

/**
 * Exception raised if different subjects is returned by crawler than that stored in local DB.
 * (Semester change usually results in this exception)
 */
public class SubjectChangedException extends Exception{
	public SubjectChangedException() {
	}

	 public SubjectChangedException(String message)
     {
        super(message);
     }
	 @Override
	public void printStackTrace() {
		 super.printStackTrace();
	}
}
