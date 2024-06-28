package com.lbs.lbs.Base.routing;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.WeightedArcData;
import com.lbs.lbs.Base.graph.types.multimodal.GeofabrikData;
import com.lbs.lbs.Base.routing.BDV.NodeIterator;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Iterator for usage in Dijkstra.run(...)
 * 
 * This iterator is used to iterate over GeofabrikData, only allowing roads
 * which are usable by pedestrians. To that extend, roads of class motorway,
 * motorway_link, trunk and trunk_link are skipped in the iterator.
 * 
 * @author Axel Forsch
 *
 * @param <V> type of node data in the graph
 * @param <E> type of arc data in the graph
 */
public class GeofabrikRoadIteratorBDV<V, E extends WeightedArcData> implements NodeIterator<V, E> {

	protected DiGraphArc<V, E> currArc;
	protected NodeIterator<V, E> addIt;
	protected boolean start;

	public GeofabrikRoadIteratorBDV(NodeIterator<V, E> additionalIterator) {
		this.addIt = additionalIterator;
		this.start = true;
		this.currArc = null;
	}

	@Override
	public double getWeightOfCurrentArc(DiGraphNode<V, E> s, DiGraphNode<V, E> t) {
		if (currArc != null) {
			return currArc.getArcData().getValue();
		}
		if (start) {
			return Double.NaN;
		}
		return addIt.getWeightOfCurrentArc(s, t);
	}

	@Override
	public Iterator<DiGraphNode<V, E>> getIterator(DiGraphNode<V, E> s) {
		LinkedList<DiGraphArc<V, E>> outgoingArcs = new LinkedList<>();
		String fclass;
		for (var arc : s.getOutgoingArcs()) {
			if (arc.getArcData() instanceof GeofabrikData) {
				fclass = ((GeofabrikData) arc.getArcData()).fclass();
				if (fclass.equals("motorway_link") || fclass.equals("motorway") || fclass.equals("trunc"))
					continue;
			}
			outgoingArcs.add(arc);
		}
		Iterator<DiGraphArc<V, E>> it = outgoingArcs.iterator();

		Iterator<DiGraphNode<V, E>> extraIt;
		if (addIt == null) {
			extraIt = null;
		} else {
			extraIt = addIt.getIterator(s);
		}
		return new Iterator<DiGraphNode<V, E>>() {

			@Override
			public boolean hasNext() {
				if (it.hasNext()) {
					return true;
				}
				if (extraIt == null) {
					return false;
				}
				return extraIt.hasNext();
			}

			@Override
			public DiGraphNode<V, E> next() {
				start = false;
				if (it.hasNext()) {
					currArc = it.next();
					return currArc.getTarget();
				}
				if (extraIt == null) {
					return null;
				}
				currArc = null;
				return extraIt.next();
			}
		};
	}
}
