package com.lbs.lbs.Base.graph.types;

import java.util.ArrayList;
import java.util.List;

import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;

public class AlternativePaths<V, E> {

	public int commonNodeID;
	public double dist;
	public double dist_F;
	public double dist_B;
	public double limited_sharing = 0;
	public ArrayList<DiGraphNode<V, E>> path;
	public ArrayList<Double> weights;
	
	// 3 properties: stretch, sharing, detour path;
	
	public AlternativePaths(int commonNodeID, double dist, double dist_F, double dist_B
			, List<DiGraphNode<V, E>> path, List<Double> weights) {
		this.commonNodeID = commonNodeID;
		this.dist = dist;
		this.dist_F = dist_F;
		this.dist_B = dist_B;
		this.path = new ArrayList<DiGraphNode<V, E>>(path);
		this.weights = new ArrayList<Double>(weights);
	}
	
    public double getdist() {
        return dist;
    }
    
    public double getdist_F() {
        return dist_F;
    }
    
    public double getdist_B() {
        return dist_B;
    }
    
    public double getSizePath() {
        return path.size();
    }
    
    public double getSizeweight() {
        return weights.size();
    }

    public void printNodeAndWeight() {
    	
    	for (int i =0; i<weights.size(); ++i) {
    		System.out.println(path.get(i).getId() +", " + path.get(i) +", " +weights.get(i) );
    	}
    	
    }
    
    
    public double getTotalweight() {
    	
    	double total_weight = 0;
    	for ( double w_C : weights) {
    		total_weight = total_weight + w_C;
    	}

        return total_weight;
    }
    
    public void print() {
    	
    	System.out.println(dist+", "+commonNodeID+", "+limited_sharing+", "+weights.size());

    }
    
    

	
}
