package com.blackMonster.webkiosk;


public class ModifyTimetable {
	public static final int SINGLE_CLASS = 0;
	public static final int DOUBLE_CLASS = 1;
	public static final int EMPTY_CLASS = 1;
/*
	public static boolean move(int firstDay, int firstTime, String firstData, int secondDay,
			int secondTime, String secondData, Context context) {
		// current = first,,,, second = new
		String table = MainPrefs.getBatch(context);
		
		int firstType = getClassType(firstData);
		int secondType = getClassType(secondData);

		if (firstType == secondType)
			swap(firstDay, firstTime, firstData, secondDay, secondTime, secondData, context);
		else if (firstType == SINGLE_CLASS && secondType == EMPTY_CLASS)
			swap(firstDay, firstTime, firstData, secondDay, secondTime, secondData, context);
		else if (firstType == DOUBLE_CLASS && (secondType==SINGLE_CLASS || secondType == EMPTY_CLASS) )
			doubleSingle(firstDay, firstTime, firstData, secondDay, secondTime, secondData, context);
		else if (firstType == SINGLE_CLASS && secondType == DOUBLE_CLASS)
			singleDouble();

		return false;
	}

	private static void doubleSingle(int firstDay, int firstTime, String firstData, int secondDay, int secondTime, String secondData, Context context) {
		secondTime + 1 <= TimetableData.CLASS_END_TIME 
		
	}

	private static void swap(int firstDay, int firstTime, String firstData, int secondDay, int secondTime, String secondData, Context context) {

		TimetableData.updateRawData(firstDay, firstTime, secondData, MainPrefs.getBatch(context), context);
		TimetableData.updateRawData(secondDay, secondTime, firstData, MainPrefs.getBatch(context), context);
 
	
	}*/
}
