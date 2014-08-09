package com.blackMonster.notifications;

public class Notificaton {

	public String link;
	public String title;

	public Notificaton() {
		link = "NA";
		title = "NA";
	}

	@Override
	public boolean equals(Object o) {
		Notificaton n = (Notificaton) o;
		if (link.equals(n.link) && title.equals(n.title))
			return true;
		else
			return false;

	}

	public boolean isEmpty() {
		return isEmpty(link) || isEmpty(title);
	}

	private static boolean isEmpty(String s) {
		if (s == null || s.equals("") || s.equals("NA"))
			return true;
		else
			return false;
	}
}
