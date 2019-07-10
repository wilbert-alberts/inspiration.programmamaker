package nl.popkoortheinspiration.programmamaker.shared;

import java.io.Serializable;
import java.util.Date;

public class Song implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5845863490274066232L;
	private String title;
	private int length;
	private Date inRepertoireSince;
	private boolean isChristmasSong;
	
	public Song() {
	}
	
	public Song(String t, int l, Date ir, boolean isCS) {
		title = t;
		length = l;
		inRepertoireSince = ir;
		isChristmasSong=isCS;
	}

	public String getTitle() {
		return title;
	}
	
	public String getTitleAndDuration() {
		return title + " (" + getDurationAsString() + ")";
	}

	public int getLength() {
		return length;
	}
	
	public String getDurationAsString() {
		int minutes = length / 60;
		int seconds = length % 60;

		String result = minutes + ":";
		if (seconds < 10)
			result += "0";
		result += seconds;

		return result;
	}

	public boolean isChristmasSong() {
		return isChristmasSong;
	}

	public Date getInRepertoireSince() {
		return inRepertoireSince;
	}	
}
