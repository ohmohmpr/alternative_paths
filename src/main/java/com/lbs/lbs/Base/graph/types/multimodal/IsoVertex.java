package com.lbs.lbs.Base.graph.types.multimodal;

public interface IsoVertex extends Comparable<IsoVertex> {

	public String getName();

	public void setName(String name);

	public int getId();

	public void setId(int id);

	@Override
	public int compareTo(IsoVertex otherVertex);
}
