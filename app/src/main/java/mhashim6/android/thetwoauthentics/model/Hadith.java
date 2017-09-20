package mhashim6.android.thetwoauthentics.model;

/**
 * Created by mhashim6 on 25/06/2017.
 */

public class Hadith {

	private final int muhaddith;
	private final int num;
	private final String text;

	public Hadith(int muhaddith, int num, String text) {
		this.muhaddith = muhaddith;
		this.num = num;
		this.text = text;
	}

	public int muhaddith() {
		return muhaddith;
	}

	public int number() {
		return num;
	}

	public String text() {
		return text;
	}
//===================================================
}
