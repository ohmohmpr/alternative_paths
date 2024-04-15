package com.lbs.lbs.Base.graph.types;

/**
 * Definition of reachability. An object implementing this interface can either
 * be {@link Colored#REACHABLE} or {@link Colored#UNREACHABLE}. In case the
 * reachability is not set, the color is {@link Colored#UNDEFINED}.
 * 
 * @author Axel Forsch
 *
 */
public interface Colored {

	public static final int UNDEFINED = -1;
	public static final int REACHABLE = 0;
	public static final int UNREACHABLE = 1;

	public int getColor();

	public double getRemainingTime();

	public double getRemainingDist();

	public void setReachability(int color, double remDist);

	public static int edgeColor(int sourceColor, int targetColor) {
		if (sourceColor == UNDEFINED || targetColor == UNDEFINED)
			return UNDEFINED;
		if (sourceColor == UNREACHABLE || targetColor == UNREACHABLE)
			return UNREACHABLE;
		return REACHABLE;
	}
}
