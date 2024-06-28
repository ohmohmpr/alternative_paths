package com.lbs.lbs.Base.routing;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.multimodal.IsoEdge;
import com.lbs.lbs.Base.graph.types.multimodal.IsoVertex;
import com.lbs.lbs.Base.routing.BDV.NodeVisitor;

/**
 * Visitor used for <code>Dijkstra.run(...)</code>
 * 
 * This visitor stops the query once a given target is reached.
 * 
 * @author Axel Forsch
 *
 */
public class TargetVisitorBDV implements NodeVisitor<DiGraphNode<IsoVertex, IsoEdge>> {

	private int targetId;

	/**
	 * This visitor stops the query once a given target, described by its
	 * <code>targetId</code> is reached.
	 * 
	 * @param targetId id of the target node
	 */
	public TargetVisitorBDV(int targetId) {
		this.targetId = targetId;
	}

	@Override
	public boolean visit(DiGraphNode<IsoVertex, IsoEdge> node) {
		if (node.getId() == targetId) {
			return false;
		}
		return true;
	}
}
