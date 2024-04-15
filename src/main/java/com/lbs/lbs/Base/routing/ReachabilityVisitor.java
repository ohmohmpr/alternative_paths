package com.lbs.lbs.Base.routing;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.multimodal.IsoEdge;
import com.lbs.lbs.Base.graph.types.multimodal.IsoVertex;
import com.lbs.lbs.Base.routing.Dijkstra.NodeVisitor;

/**
 * Visitor used for <code>Dijkstra.run(...)</code>
 * 
 * This visitor stops the query once a given maximum time is exceeded.
 * 
 * @author Axel Forsch
 *
 */
public class ReachabilityVisitor implements NodeVisitor<DiGraphNode<IsoVertex, IsoEdge>> {
	private Dijkstra<IsoVertex, IsoEdge> dij;

	private long maxtime;

	/**
	 * This visitor stops the query once a given <code>maxtime<\code> is exceeded.
	 * 
	 * @param maxtime maximum allowed time
	 * @param dij     Dijstra instance
	 */
	public ReachabilityVisitor(long maxtime, Dijkstra<IsoVertex, IsoEdge> dij) {
		this.dij = dij;
		this.maxtime = maxtime;
	}

	@Override
	public boolean visit(DiGraphNode<IsoVertex, IsoEdge> node) {
		if (this.dij.getCurrDist() > maxtime) {
			return false;
		}
		return true;
	}
}
