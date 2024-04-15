package com.lbs.lbs.Base.graph.types;

import com.lbs.lbs.Base.graph.GeometricGraph;
import com.lbs.lbs.Base.util.Envelope;
import com.lbs.lbs.Base.util.QuadTree;

import java.awt.geom.Point2D;

public class RoadGraph<V extends Point2D, E extends WeightedArcData> extends GeometricGraph<V, E> {

	public RoadGraph(Envelope e) {
		this.qt = new QuadTree<DiGraphNode<V, E>>(e);
	}

	public DiGraphNode<V, E> getDiGraphNode(Point2D position, double eps) {
		return qt.getNode(position.getX(), position.getY(), eps);
	}
}
