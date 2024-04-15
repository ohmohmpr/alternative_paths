/**
 * 
 */
package com.lbs.lbs.Base.graph.types.multimodal;

import java.util.Date;

public class DepartureNode extends PublicTransportNode {

	/**
	 * @param time
	 * @param tripId
	 * @param stopSequence
	 */
	public DepartureNode(Date time, String tripId, int stopSequence) {
		super(time, tripId, stopSequence);
	}

	/**
	 * @param name
	 * @param id
	 * @param time
	 * @param tripId
	 * @param stopSequence
	 */
	public DepartureNode(String name, int id, Date time, String tripId, int stopSequence) {
		super(name, id, time, tripId, stopSequence);
	}

	@Override
	public String toString() {
		return String.format("DepartureNode[name=%s,time=%tR]", name, time);
	}

}
