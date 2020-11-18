package es.vn.sb.utils;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

	public static int getRandomInt() {
		System.out.println(ThreadLocalRandom.current().nextInt(0, 2));
		return ThreadLocalRandom.current().nextInt(0, 2);
	}
	
	public static String getPodName() {
		return System.getenv("HOSTNAME");
	}
}
