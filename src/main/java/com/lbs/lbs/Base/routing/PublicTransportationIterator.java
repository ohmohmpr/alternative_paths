package com.lbs.lbs.Base.routing;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.multimodal.*;
import com.lbs.lbs.Base.routing.Dijkstra.NodeIterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Iterator for usage in Dijkstra.run(...)
 * 
 * This iterator is used to iterate over graphs constructed using the
 * <code>Time-expanded model</code>, to query shortest paths.
 * 
 * @author Axel Forsch
 *
 */
public class PublicTransportationIterator implements NodeIterator<IsoVertex, IsoEdge> {

	private long defaultTransferTime;

	Dijkstra<IsoVertex, IsoEdge> dij;

	private HashMap<Integer, LinkedList<DiGraphNode<IsoVertex, IsoEdge>>> transferNodes;
	private HashMap<Integer, Integer> transferTimes;

	/**
	 * Initializes a <code>PublicTransportationIterator</code>.
	 * 
	 * @param transferNodes       list of all transfer nodes
	 * @param transferTimes       mapping of last stops to transfer times
	 * @param dijkstra            instance of Dijkstra
	 * @param defaultTransferTime default time needed for a transfer at a station
	 */
	public PublicTransportationIterator(
			final HashMap<Integer, LinkedList<DiGraphNode<IsoVertex, IsoEdge>>> transferNodes,
			final HashMap<Integer, Integer> transferTimes, Dijkstra<IsoVertex, IsoEdge> dijkstra,
			long defaultTransferTime) {
		this.dij = dijkstra;
		this.transferNodes = transferNodes;
		this.transferTimes = transferTimes;
		this.defaultTransferTime = defaultTransferTime;
	}

	@Override
	public Iterator<DiGraphNode<IsoVertex, IsoEdge>> getIterator(DiGraphNode<IsoVertex, IsoEdge> s) {
		LinkedList<DiGraphNode<IsoVertex, IsoEdge>> a = new LinkedList<>();
		if (s.getNodeData() instanceof RoadNode && ((RoadNode) s.getNodeData()).isNextToStop()) {
			DiGraphNode<IsoVertex, IsoEdge> nextTransfer = getNextTransfer((RoadNode) s.getNodeData(),
					(long) this.dij.getDistance(s));
			if (nextTransfer != null)
				a.add(nextTransfer);
		} else if (s.getNodeData() instanceof ArrivalNode) {
			DiGraphNode<IsoVertex, IsoEdge> street = ((ArrivalNode) s.getNodeData()).getNextStreetNode();
			if (dij.stamps[street.getId()] != dij.currentStamp || dij.pred[street.getId()] == null)
				a.add(street);
		}
		if (a.size() == 1)
			if (a.getFirst() == null)
				a.removeFirst();
		return a.iterator();
	}

	@Override
	public double getWeightOfCurrentArc(DiGraphNode<IsoVertex, IsoEdge> s, DiGraphNode<IsoVertex, IsoEdge> t) {
		Objects.requireNonNull(s);
		Objects.requireNonNull(t);
		if (s.getNodeData() instanceof RoadNode && t.getNodeData() instanceof TransferNode) {
			TransferNode tr = (TransferNode) t.getNodeData();
			double transferTime = tr.getTime().getTime() / 1000.0; // in seconds
			double time = transferTime - dij.getCurrDist() + getTransferTime(tr.getId());
			// Transferzeit muss zwischen 0 und einer Woche liegen
			return (time + 7 * 86400) % (7 * 86400);
		}
		if (s.getNodeData() instanceof ArrivalNode && t.getNodeData() instanceof RoadNode) {
			long time = getTransferTime(s.getNodeData().getId());
			return time;
		}
		return 0;
	}

	/**
	 * Returns the transfer time a a specific stop, given by its id.
	 * 
	 * @return transfer time in seconds
	 */
	public long getTransferTime(int stop_id) {
		if (this.transferTimes.containsKey(stop_id)) {
			return (long) this.transferTimes.get(stop_id) / 2;
		} else {
			return defaultTransferTime / 2;
		}
	}

	/**
	 * Returns the (temporally) next transfer node at a specific
	 * <code>RoadNode</code> to a given <code>time</code>.
	 * 
	 * @param arr  node
	 * @param time current time in milliseconds
	 * @return (temporally) next transfer node
	 */
	public DiGraphNode<IsoVertex, IsoEdge> getNextTransfer(RoadNode arr, long time) {
		int arrId = arr.getNextStopId();
		long transTime = this.getTransferTime(arrId);
		LinkedList<DiGraphNode<IsoVertex, IsoEdge>> t = this.transferNodes.get(arrId);

		for (DiGraphNode<IsoVertex, IsoEdge> d : t) {
			double transferTime = ((PublicTransportNode) d.getNodeData()).getTime().getTime() / 1000.0; // in seconds
			if (time < transferTime + transTime) {
				return d;
			}
		}
		return t.getFirst();
	}
}
