package com.lbs.lbs.Base.graph.types;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.multimodal.GeofabrikData;

public class AlternativePaths<V, E extends WeightedArcData> {

	public int commonNodeID;
	public double dist;
	public double limited_sharing = 0;
	public boolean passLimitedSharing = false;
	public boolean LimitedSharingtested = false;
	public boolean passLocalOptimality = false;
	public boolean LocalOptimalitytested = false;
	public DiGraphNode<V, E> viaNode = null;
	
	public ArrayList<DiGraphNode<V, E>> path;
	public ArrayList<DiGraphArc<V, E>> pathArcs;
	
	public AlternativePaths(
			int commonNodeID, double dist,
			List<DiGraphNode<V, E>> path, 
			List<DiGraphArc<V, E>> pathArcs, 
			DiGraphNode<V, E> viaNode) {
		this.commonNodeID = commonNodeID;
		this.dist = dist;
		this.path = new ArrayList<DiGraphNode<V, E>>(path);
		this.pathArcs = new ArrayList<DiGraphArc<V, E>>(pathArcs);
		this.viaNode = viaNode;
	}
	
    public double getdist() {
        return dist;
    }
    
    public double getSizePath() {
        return path.size();
    }
    
    public double getCostFunction() {
        return 2*dist + limited_sharing;
    }

    public void printNodeAndWeight() {
    	
    	for (int i =0; i<pathArcs.size(); ++i) {
    		System.out.println(path.get(i).getId() +", " + path.get(i) +", " +pathArcs.get(i) );
    	}
    	
    }
    
    public double getTotalPathArcs() {
    	
    	double totalPathArcs = 0;
    	for ( DiGraphArc<V, E> arc : pathArcs) {
    		totalPathArcs = totalPathArcs + arc.getArcData().getValue();
    	}

        return totalPathArcs;
    }
    
    public void print() {
    	
    	System.out.println(dist+", "+commonNodeID+", "+limited_sharing+", "+getCostFunction()+", "+ (dist - getTotalPathArcs()));

    }
    
    

	
}
