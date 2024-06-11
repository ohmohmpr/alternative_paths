package com.lbs.lbs.Base.graph.types;

import java.util.ArrayList;
import java.util.List;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;

public class AlternativePaths<V, E> {

	public int commonNodeID;
	public double dist;
	public ArrayList<DiGraphNode<V, E>> path;
	// 3 properties: stretch, sharing, detour path;
	
	public AlternativePaths(int commonNodeID, double dist, List<DiGraphNode<V, E>> list) {
		this.commonNodeID = commonNodeID;
		this.dist = dist;
		this.path = new ArrayList<DiGraphNode<V, E>>(list);
	}
	
	
}
