package com.lbs.lbs.Base.graph.types.multimodal;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;

import java.util.Date;

public class ArrivalNode extends PublicTransportNode {

	private String route_name = "";
	private DiGraphNode<IsoVertex, IsoEdge> nextStreetNode;

	/**
	 * 
	 * @param time
	 * @param tripId
	 * @param stopSequence
	 */
	public ArrivalNode(Date time, String tripId, int stopSequence) {
		super(time, tripId, stopSequence);
	}

	/**
	 * 
	 * @param name
	 * @param id
	 * @param time
	 * @param tripId
	 * @param stopSequence
	 */
	public ArrivalNode(String name, int id, Date time, String tripId, int stopSequence) {
		super(name, id, time, tripId, stopSequence);
	}

	/**
	 * @param name
	 * @param id
	 * @param time
	 * @param tripId
	 * @param stopSequence
	 * @param route_name
	 */
	public ArrivalNode(String name, int id, Date time, String tripId, int stopSequence, String route_name) {
		super(name, id, time, tripId, stopSequence);
		this.route_name = route_name;
	}

	public String getRoute_name() {
		return route_name;
	}

	public void setRoute_name(String route_name) {
		this.route_name = route_name;
	}

	public DiGraphNode<IsoVertex, IsoEdge> getNextStreetNode() {
		return nextStreetNode;
	}

	public void setNextStreetNode(DiGraphNode<IsoVertex, IsoEdge> nextStreetNode) {
		this.nextStreetNode = nextStreetNode;
	}

	@Override
	public String toString() {
		return String.format("ArrivalNode[name=%s,time=%tR,route=%s]", name, time, route_name);
	}
}
