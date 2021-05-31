package com.github.satellite.utils;

public class MathUtils {
	
	public static double getDistanceSq(double[] vec) {
		double dist=0;
		
		for(double d : vec) {
			dist += Math.pow(d, 2);
		}
		
		return Math.sqrt(dist);
	}
	
	public static double getDistanceAd(double[] vec) {
		double dist=0;
		
		for(double d : vec) {
			dist += d;
		}
		
		return dist;
	}
}
