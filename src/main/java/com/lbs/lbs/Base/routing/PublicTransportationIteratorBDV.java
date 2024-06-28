package com.lbs.lbs.Base.routing;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.multimodal.*;
//import com.lbs.lbs.Base.routing.Dijkstra.NodeIterator;
import com.lbs.lbs.Base.routing.BDV.NodeIterator;

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
public class PublicTransportationIteratorBDV implements NodeIterator<IsoVertex, IsoEdge> {

	private long defaultTransferTime;

	BDV<IsoVertex, IsoEdge> bdv;

	private HashMap<Integer, LinkedList<DiGraphNode<IsoVertex, IsoEdge>>> transferNodes;
	private HashMap<Integer, Integer> transferTimes;

	/**
	 * Initializes a <code>PublicTransportationIterator</code>.
	 * 
	 * @param transferNodes       list of all transfer nodes
	 * @param transferTimes       mapping of last stops to transfer times
	 * @param bdv            	  instance of BDV
	 * @param defaultTransferTime default time needed for a transfer at a station
	 */
	public PublicTransportationIteratorBDV(
			final HashMap<Integer, LinkedList<DiGraphNode<IsoVertex, IsoEdge>>> transferNodes,
			final HashMap<Integer, Integer> transferTimes, BDV<IsoVertex, IsoEdge> bdv,
			long defaultTransferTime) {
		this.bdv = bdv;
		this.transferNodes = transferNodes;
		this.transferTimes = transferTimes;
		this.defaultTransferTime = defaultTransferTime;
	}

	@Override
	public Iterator<DiGraphNode<IsoVertex, IsoEdge>> getIterator(DiGraphNode<IsoVertex, IsoEdge> s) {
		LinkedList<DiGraphNode<IsoVertex, IsoEdge>> a = new LinkedList<>();
		if (s.getNodeData() instanceof RoadNode && ((RoadNode) s.getNodeData()).isNextToStop()) {
			DiGraphNode<IsoVertex, IsoEdge> nextTransfer = getNextTransfer((RoadNode) s.getNodeData(),
					(long) this.bdv.getDistance(s));
//			System.out.println("this.bdv.getDistance(s))" + this.bdv.getDistance(s));
			if (nextTransfer != null)
				a.add(nextTransfer);
		} 
		else if (s.getNodeData() instanceof ArrivalNode) {
			DiGraphNode<IsoVertex, IsoEdge> street = ((ArrivalNode) s.getNodeData()).getNextStreetNode();
			if (bdv.direction == "FORWARD") {
				if (bdv.stamps_F[street.getId()] != bdv.currentStamp || bdv.pred_F[street.getId()] == null)
					a.add(street);
			} else if (bdv.direction == "BACKWARD") {
				if (bdv.stamps_B[street.getId()] != bdv.currentStamp || bdv.pred_B[street.getId()] == null)
					a.add(street);
			}
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
		// SOETHING WRONG HERE
		if (s.getNodeData() instanceof RoadNode && t.getNodeData() instanceof TransferNode) {
			TransferNode tr = (TransferNode) t.getNodeData();
			double transferTime = tr.getTime().getTime() / 1000.0; // in seconds
			double time = transferTime - bdv.getCurrDist() + getTransferTime(tr.getId());
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
