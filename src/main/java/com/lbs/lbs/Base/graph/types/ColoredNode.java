package com.lbs.lbs.Base.graph.types;

import java.awt.geom.Point2D;

/**
 * The color of the node defines its reachability. On top of that,
 * <code>ColoredNode</code> stores the remaining time at the node. For a more
 * detailed description of the color see {@link Colored}
 * 
 * @author Axel Forsch
 *
 */
public class ColoredNode extends Point2D.Double implements Colored {

	private static final long serialVersionUID = 790547863434886732L;

	private int color;
	private double remainingTime;

	private boolean fixed;

	public ColoredNode(double x, double y) {
		this(x, y, UNDEFINED, java.lang.Double.MAX_VALUE);
	}

	public ColoredNode(Point2D location) {
		this(location.getX(), location.getY());
	}

	public ColoredNode(ColoredNode copy) {
		this(copy.getX(), copy.getY(), copy.getColor(), copy.getRemainingTime());
	}

	public ColoredNode(double x, double y, int color, double remainingTime) {
		this.setLocation(x, y);
		this.color = color;
		this.remainingTime = remainingTime;
		this.fixed = false;
	}

	public ColoredNode(Point2D location, int color, double remainingDist) {
		this(location.getX(), location.getY(), color, remainingDist);
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public void setReachability(int color, double remainingTime) {
		if (!fixed) {
			this.color = color;
			this.remainingTime = remainingTime;
		}
	}

	public void setReachability(ColoredNode copy) {
		setReachability(copy.getColor(), copy.getRemainingTime());
	}

	public void resetReachabilityToUndefined() {
		this.color = UNDEFINED;
		this.remainingTime = -java.lang.Double.MAX_VALUE;
	}

	@Override
	public double getRemainingTime() {
		return remainingTime;
	}

	@Override
	public String toString() {
		String ret = "ColoredNode[" + super.toString() + ", color=";
		switch (this.color) {
		case UNDEFINED:
			ret += "UNDEFINED";
			break;
		case REACHABLE:
			ret += "REACHABLE";
			break;
		case UNREACHABLE:
			ret += "UNREACHABLE";
			break;
		default:
			throw new IllegalArgumentException("Unknown color " + color);
		}
		ret += ", remDist=" + remainingTime + "]";
		return ret;
	}

	@Override
	public double getRemainingDist() {
		return remainingTime * WalkingData.WALKING_SPEED;
	}

	/**
	 * Node color can be fixed to avoid any modification.
	 */
	public void fixReachability() {
		this.fixed = true;
	}
}
