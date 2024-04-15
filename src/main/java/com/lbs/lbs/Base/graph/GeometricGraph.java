package com.lbs.lbs.Base.graph;

import com.lbs.lbs.Base.util.QuadTree;

import java.awt.geom.Point2D;
import java.util.HashSet;

/**
 * DiGraph where the nodes have a geometric location. Important to note is, that
 * this graph structure enforces, that nodes locations are unique. No two nodes
 * can have the same location!
 * 
 * @author ???
 *
 * @param <V> type of node data
 * @param <E> type of edge data
 */
public abstract class GeometricGraph<V extends Point2D, E> extends DiGraph<V, E> {

	protected QuadTree<DiGraphNode<V, E>> qt;

	public void setQuadTree(QuadTree<DiGraphNode<V, E>> qt) {
		this.qt = qt;
	}

	public QuadTree<DiGraphNode<V, E>> getQuadTree() {
		return qt;
	}

	public DiGraphNode<V, E> getDiGraphNode(double lon, double lat) {
		return qt.getNode(lon, lat);
	}

	/**
	 * If no node is present at location <code>v</code>, a node is added at this
	 * location. Otherwise nothing happens.
	 * 
	 * @param v location of new node
	 * @return newly inserted node if no node existed at location <code>v</code>
	 *         beforehand, else <code>null</code>
	 */
	@Override
	public DiGraphNode<V, E> addNode(V v) {
		if (qt.getNode(v.getX(), v.getY()) == null) {
			DiGraphNode<V, E> n = super.addNode(v);
			qt.add(n, v.getX(), v.getY());
			return n;
		}
		return null;
	}

	@Override
	public void removeNodes(HashSet<DiGraphNode<V, E>> nodesToBeRemoved) {
		super.removeNodes(nodesToBeRemoved);
		for (DiGraphNode<V, E> n : nodesToBeRemoved) {
			qt.removeNode(n.getNodeData().getX(), n.getNodeData().getY(), n);
		}
	}
}
