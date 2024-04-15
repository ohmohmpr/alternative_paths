package com.lbs.lbs.Base.graph.types.multimodal;

public class PublicTransportEdge extends IsoEdge {

	public PublicTransportEdge(double weight, int type) {
		super(weight, type);
	}

	@Override
	public String toString() {
		return "PublicTransportEdge[type=" + getType() + "; weight=" + getValue() + "]";
	}

}
