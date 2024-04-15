package com.lbs.lbs.Base.graph.types;

/**
 * Data used to differentiate and convert between time and distance using a
 * predefined {@link WalkingData#WALKING_SPEED}.
 * 
 * @author Axel Forsch
 *
 */
public interface WalkingData extends WeightedArcData {

	public static final double WALKING_SPEED = 5 / 3.6; // m/s

	public double getValueAsTime();

	public double getValueAsDist();
}
