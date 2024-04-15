package com.lbs.lbs.Base.gisviewer.own;

import com.lbs.lbs.Base.gisviewer.LineMapObject;
import com.lbs.lbs.Base.gisviewer.Transformation;

import java.awt.*;
import java.awt.geom.Point2D;

public class ExtendedLineMapObject extends LineMapObject {

	protected int myWidth = 1;

	public ExtendedLineMapObject(Point2D[] p) {
		super(p);
	}

	public ExtendedLineMapObject(Point2D p1, Point2D p2) {
		super(p1, p2);
	}

	public void setLinewidth(int width) {
		this.myWidth = width;
	}

	@Override
	public void draw(Graphics2D g, Transformation t) {
		Stroke originalStroke = g.getStroke();
		g.setStroke(new BasicStroke(myWidth));
		super.draw(g, t);
		g.setStroke(originalStroke);
	}
}
