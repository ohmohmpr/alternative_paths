package com.lbs.lbs.Base.gisviewer.own;

import com.lbs.lbs.Base.gisviewer.PointMapObject;
import com.lbs.lbs.Base.gisviewer.Transformation;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class FlagMapObject extends PointMapObject {

	protected Color myColor;
	protected double myScale;

	public FlagMapObject(Point2D position) {
		this(position, Color.GREEN);
	}

	public FlagMapObject(Point2D position, Color color) {
		this(position, color, 2);
	}

	public FlagMapObject(Point2D position, Color color, double scale) {
		super(position);
		this.myColor = color;
		this.myScale = scale;
	}

	@Override
	public void draw(Graphics2D g, Transformation t) {
		super.draw(g, t);

		int c = t.getColumn(myPoint.getX()) + 1;
		int r = t.getRow(myPoint.getY());
		int h = -(int) Math.round(16 * myScale);
		int w = (int) Math.round(8 * myScale);

		Path2D flagOutline = new Path2D.Double();
		flagOutline.moveTo(c + 1, r + h * 11 / 16);
		flagOutline.lineTo(c + 1, r + h);
		flagOutline.quadTo(c + 1 + w * 2 / 8, r + h * 15 / 16, c + 1 + w * 4 / 8, r + h);
		flagOutline.quadTo(c + 1 + w * 6 / 8, r + h * 17 / 16, c + 1 + w, r + h);
		flagOutline.lineTo(c + 1 + w, r + h * 11 / 16);
		flagOutline.quadTo(c + 1 + w * 6 / 8, r + h * 12 / 16, c + 1 + w * 4 / 8, r + h * 11 / 16);
		flagOutline.quadTo(c + 1 + w * 2 / 8, r + h * 10 / 16, c + 1, r + h * 11 / 16);
		flagOutline.closePath();

		Area flag = new Area(flagOutline);

		// store graphics original values
		Color originalColor = g.getColor();
		Stroke originalStroke = g.getStroke();

		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke((float) myScale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		g.drawLine(c, r, c, r + h);

		g.setColor(myColor);
		g.fill(flag);

		// restore previous graphics object
		g.setColor(originalColor);
		g.setStroke(originalStroke);
	}
}
