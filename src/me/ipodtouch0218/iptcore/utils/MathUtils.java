package me.ipodtouch0218.iptcore.utils;

public class MathUtils {

	private MathUtils() {}
	
	public static float getRandomInRange(float min, float max) {
		if (min == max) { return min; }
		float range = max-min;
		
		return (float) (Math.random() * range) + min;
	}
	
	public static double getRandomInRange(double min, double max) {
		if (min == max) { return min; }
		double range = max-min;
		
		return Math.random() * range + min;
	}
	
	public static int getRandomInRange(int min, int max) {
		if (min == max) { return min; }
		int range = max-min;
		
		return (int) (Math.random() * range) + min;
	}
	
}
