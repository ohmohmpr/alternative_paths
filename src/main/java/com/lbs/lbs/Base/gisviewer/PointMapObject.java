package com.lbs.lbs.Base.gisviewer;

import com.lbs.lbs.Base.util.Envelope;

import java.awt.*;
import java.awt.geom.Point2D;

public class PointMapObject implements MapObject {

	protected Point2D myPoint;

	public PointMapObject(Point2D p) {
		myPoint = p;
	}

	@Override
	public void draw(Graphics2D g, Transformation t) {
		g.fillOval(t.getColumn(myPoint.getX()) - 2, t.getRow(myPoint.getY()) - 2, 5, 5);
	}

	@Override
	public Envelope getBoundingBox() {
		return new Envelope(myPoint.getX(), myPoint.getX(), myPoint.getY(), myPoint.getY());
	}

}
