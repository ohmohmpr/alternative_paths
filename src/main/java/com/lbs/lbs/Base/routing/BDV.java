package com.lbs.lbs.Base.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.AlternativePaths;
import com.lbs.lbs.Base.graph.types.WeightedArcData;
import com.lbs.lbs.Base.util.MinHeap;
import com.lbs.lbs.Base.util.MinHeap.HeapItem;

public class BDV<V, E extends WeightedArcData> {

	// Forward search
	private double starttime = 0;
	protected double dist_F[];
	protected double dist_F_opt[];
	protected double curr_dist_F = 0;
	protected int stamps_F[];
	protected HeapItem<DiGraphNode<V, E>> items_F[];
	public DiGraphNode<V, E> pred_F[];

	// Backward search
	protected double dist_B[];
	protected double dist_B_opt[];
	protected double curr_dist_B = 0;
	protected int stamps_B[];
	protected HeapItem<DiGraphNode<V, E>> items_B[];
	public DiGraphNode<V, E> pred_B[];

	// BD
	protected double shortestPathLength = Double.MAX_VALUE;
	protected int currentStamp = 0;
	public int commonNodeID;

	// BD variables
	protected double optimalShortestPathLength = Double.MAX_VALUE;
	protected AlternativePaths<V, E> optimalShortestPath = null;
	protected double epsilon = 0.25; // stretch longest admissible path can be.
	protected int p = 3; // number of alternative paths.
	public ArrayList<AlternativePaths<V, E>> alternativePaths;
	public Map<Integer, AlternativePaths<V,E>> id_dist_F_B;

	//alternative path conditions
	protected double gamma = 0.8;

	@SuppressWarnings("unchecked")
	public BDV(DiGraph<V, E> g) {
		this.dist_F = new double[g.n()];
		this.dist_F_opt = new double[g.n()];
		this.stamps_F = new int[g.n()];
		this.items_F = new HeapItem[g.n()];
		this.pred_F = new DiGraphNode[g.n()];

		this.dist_B = new double[g.n()];
		this.dist_B_opt = new double[g.n()];
		this.stamps_B = new int[g.n()];
		this.items_B = new HeapItem[g.n()];
		this.pred_B = new DiGraphNode[g.n()];

		this.alternativePaths = new ArrayList<AlternativePaths<V,E>>();
		this.id_dist_F_B = new HashMap<Integer, AlternativePaths<V,E>>();
	}


	//	public double getCurrDist() {
	//		return curr_dist;
	//	}

	//	public DiGraphNode<V, E> getPred(int nodeId) {
	//		return pred[nodeId];
	//	}

	/**
	 * Runs the algorithm starting at the source node. When the target is reached,
	 * the search is aborted and the distance to the target is returned. In case
	 * that the target is not reachable from the source, the method returns
	 * Double.MAX_VALUE.
	 * 
	 * @throws OutOfStreetNetworkException
	 */
	public double run(DiGraphNode<V, E> source, DiGraphNode<V, E> target) {
		run(source, target, new NodeVisitor<DiGraphNode<V, E>>() {
			@Override
			public boolean visit(DiGraphNode<V, E> node) {
				return node != target;
			}
		});
		//		if (stamps[target.getId()] == currentStamp) {
		//			return dist[target.getId()];
		//		}
		return Double.MAX_VALUE;
	}

	/**
	 * Runs the algorithm starting at the source node. Runs until each reachable
	 * node is visited.
	 * 
	 * @throws OutOfStreetNetworkException
	 */
	public void run(DiGraphNode<V, E> source) {

		run(source, source, new NodeVisitor<DiGraphNode<V, E>>() {
			@Override
			public boolean visit(DiGraphNode<V, E> node) {
				return true;
			}
		});
	}

	/**
	 * Runs the algorithm starting at the source node using the given NodeVisitor.
	 * Runs as long as the NodeVisitor's method visit returns true.
	 * 
	 * @throws OutOfStreetNetworkException
	 */
	public boolean run(DiGraphNode<V, E> source, DiGraphNode<V, E> target, NodeVisitor<DiGraphNode<V, E>> visitor) {
		return run(source, target, visitor, DEFAULT_ADJACENT_NODE_ITERATOR);
	}

	/**
	 * Runs the algorithm starting at the source node using the given NodeVisitor.
	 * Runs as long as the NodeVisitor's method visit returns true. Ignores the
	 * graph structure; adjacencies depend on the NodeIterator.
	 * 
	 * @throws OutOfStreetNetworkException
	 */
	public boolean run(DiGraphNode<V, E> source, DiGraphNode<V, E> target, NodeVisitor<DiGraphNode<V, E>> visitor, NodeIterator<V, E> nit) {

		// Initialize
		currentStamp++;
		dist_F[source.getId()] = starttime;
		pred_F[source.getId()] = null;

		dist_B[source.getId()] = 0;
		pred_B[source.getId()] = null;
		double weightOfArc;

		MinHeap<DiGraphNode<V, E>> queue_F = new MinHeap<DiGraphNode<V, E>>();
		MinHeap<DiGraphNode<V, E>> queue_B = new MinHeap<DiGraphNode<V, E>>();

		items_F[source.getId()] = queue_F.insertItem(starttime, source);
		stamps_F[source.getId()] = currentStamp;

		items_B[target.getId()] = queue_B.insertItem(starttime, target);
		stamps_B[target.getId()] = currentStamp;

		while (queue_F.size() > 0 && queue_B.size() > 0) {
			HeapItem<DiGraphNode<V, E>> item_F = queue_F.getMin();
			DiGraphNode<V, E> u_F = item_F.getValue();
			curr_dist_F = dist_F[u_F.getId()];

			HeapItem<DiGraphNode<V, E>> item_B = queue_B.getMin();
			DiGraphNode<V, E> u_B = item_B.getValue();
			curr_dist_B = dist_B[u_B.getId()];


			if (shortestPathLength > (1 + epsilon) * optimalShortestPathLength) {
				break;
			}
			// top_s < top_t -> Forward search
			if (curr_dist_F <= curr_dist_B) {
				queue_F.extractMin();
				//iterate throw the neighbors of u
				for (Iterator<DiGraphNode<V, E>> it = nit.getIterator(u_F); it.hasNext();) {
					DiGraphNode<V, E> v = it.next();

					weightOfArc = nit.getWeightOfCurrentArc(u_F, v);
					discoverNode_F(u_F, v, queue_F, dist_F[u_F.getId()] + weightOfArc);

					// if the node got already visited in the backward search -> new path
					if (items_B[v.getId()] != null) {
						commonNodeID = v.getId();
						// save the path
						shortestPathLength = dist_F[v.getId()] + dist_B[v.getId()];
						List<DiGraphNode<V, E>> path_for_add = getPath();
						AlternativePaths<V, E> alternativePath = new AlternativePaths<>(commonNodeID, 
										shortestPathLength,  dist_F[v.getId()]
										, dist_B[v.getId()], path_for_add);

						// check whether the path is the optimal path
						if (optimalShortestPathLength > shortestPathLength) {
							// check whether the optimal path is the first path
							// if (optimalShortestPath != null) {
								//alternativePaths.add(this.optimalShortestPath);
							// }
							optimalShortestPathLength = shortestPathLength;
							this.dist_B_opt = dist_B;
							this.dist_F_opt = dist_F;
							this.optimalShortestPath  = alternativePath;
							System.out.println("optimalShortestPath " + optimalShortestPathLength);
						}// if it is not the optimal path just add it to the alternative paths
						else {
							singleViaPath(commonNodeID, alternativePath);
//							alternativePaths.add(alternativePath);
						}
					}
				}
			} // top_s > top_t -> Backward search
			else {
				queue_B.extractMin();
				for (Iterator<DiGraphNode<V, E>> it = nit.getIterator(u_B); it.hasNext();) {
					DiGraphNode<V, E> v = it.next();

					weightOfArc = nit.getWeightOfCurrentArc(u_B, v);
					discoverNode_B(u_B, v, queue_B, dist_B[u_B.getId()] + weightOfArc);

					// if the node got already visited in the forward search -> new path
					if (items_F[v.getId()] != null) {
						commonNodeID = v.getId();
						shortestPathLength = dist_F[v.getId()] + dist_B[v.getId()];
						// save the path
						List<DiGraphNode<V, E>> path_for_add = getPath();
						
						AlternativePaths<V, E> alternativePath = new AlternativePaths<>(commonNodeID, 
								shortestPathLength,  dist_F[v.getId()]
								, dist_B[v.getId()], path_for_add);
						
						// check whether the path is the optimal path 
						if (optimalShortestPathLength > shortestPathLength) {
							// check whether the optimal path is the first path
							/*if (optimalShortestPath != null) { //-> necessary??
								alternativePaths.add(this.optimalShortestPath);
							}*/
							optimalShortestPathLength = shortestPathLength;
							this.optimalShortestPath  = alternativePath;
							System.out.println("optimalShortestPath " + optimalShortestPathLength);
						}// if it is not the optimal path just add it to the alternative paths
						else {
							singleViaPath(commonNodeID, alternativePath);
//							alternativePaths.add(alternativePath);
						}
					}
				}
			}

		}
		return true;
	}

	// discover_node in the forward search
	private void discoverNode_F(DiGraphNode<V, E> curr, DiGraphNode<V, E> target, MinHeap<DiGraphNode<V, E>> queue,
			double alt) {
		//checks whether the neighbor of u got investigated before and if that is the case whether the new distance is shorter
		if (stamps_F[target.getId()] < currentStamp || alt < dist_F[target.getId()]) {
			// update the distance and predecessor
			dist_F[target.getId()] = alt;
			pred_F[target.getId()] = curr;
			//checks whether the neighbor of u got investigated before and if that is the case whether neighbor of u is already in the queue
			if (stamps_F[target.getId()] < currentStamp || items_F[target.getId()] == null) {
				//put the neighbor of u in the queue
				items_F[target.getId()] = queue.insertItem(alt, target);
			} else {
				//decrease the key if the neighbor of u is already in the queue
				queue.decreaseKey(items_F[target.getId()], alt);
			}
			//update: mark the neighbor of u as investigated
			stamps_F[target.getId()] = currentStamp;
		}
	}
	// discover_node in the backward search
	private void discoverNode_B(DiGraphNode<V, E> curr, DiGraphNode<V, E> target, MinHeap<DiGraphNode<V, E>> queue,
			double alt) {
		//checks whether the neighbor of u got investigated before and if that is the case whether the new distance is shorter
		if (stamps_B[target.getId()] < currentStamp || alt < dist_B[target.getId()]) {
			// update the distance and predecessor
			dist_B[target.getId()] = alt;
			pred_B[target.getId()] = curr;
			//checks whether the neighbor of u got investigated before and if that is the case whether neighbor of u is already in the queue
			if (stamps_B[target.getId()] < currentStamp || items_B[target.getId()] == null) {
				//put the neighbor of u in the queue
				items_B[target.getId()] = queue.insertItem(alt, target);
			} else {
				//decrease the key if the neighbor of u is already in the queue
				queue.decreaseKey(items_B[target.getId()], alt);
			}
			//update: mark the neighbor of u as investigated
			stamps_B[target.getId()] = currentStamp;
		}
	}

	/**
	 * Assumes that the Dijkstra algorithm has been executed before. Returns the
	 * path of node to the target (stored in an ArrayList from start to target).
	 */
	// besser hier vergleichen??
	public List<DiGraphNode<V, E>> getPath() {
		LinkedList<DiGraphNode<V, E>> path = new LinkedList<DiGraphNode<V, E>>();

		if (stamps_F[commonNodeID] < currentStamp || stamps_B[commonNodeID] < currentStamp) {
			return new ArrayList<DiGraphNode<V, E>>();
		}
		// add the nodes form the forward search
		DiGraphNode<V, E> current_F = items_F[commonNodeID].getValue();
		while (current_F != null) {
			path.addFirst(current_F);
			current_F = pred_F[current_F.getId()];

		}
		// add the nodes form the backward search
		DiGraphNode<V, E> current_B = items_B[commonNodeID].getValue();
		while (current_B != null) {
			path.addLast(current_B);
			current_B = pred_B[current_B.getId()];
		}

		return new ArrayList<DiGraphNode<V, E>>(path);
	}

	public void singleViaPath(int commonNodeID, AlternativePaths<V,E> alternativePath) {
		
		// Create a hash map, named id_dist_F_B, in which contains commonNodeID and alternativePath
		// if commonNodeID is in the hash map, let compare
		if (id_dist_F_B.containsKey(commonNodeID)) {
			AlternativePaths<V,E> prev_altpath = id_dist_F_B.get(commonNodeID);
			// if the distance of the existing path longer than the new one, REPLACE
			if (shortestPathLength < prev_altpath.getdist()) {
				// if the distance(in forward direction) of the existing path longer than the new one, REPLACE
				// BUG HERE, can be improved.
				if (dist_F[commonNodeID] < prev_altpath.getdist_F()) {
					id_dist_F_B.put(commonNodeID, alternativePath);
					alternativePaths.add(alternativePath);
				}
			}
		} 
		// if commonNodeID is not in the hash map, ADD
		else {
			id_dist_F_B.put(commonNodeID, alternativePath);
			alternativePaths.add(alternativePath);
		}
	}
	
	
	public ArrayList<AlternativePaths<V, E>>  getPaths() {
		// compare
//		alternativePaths.sort(Comparator.comparing(a -> a.dist));
//		for (AlternativePaths<V, E> path : alternativePaths) {
//				System.out.println(path.dist + " " + path.commonNodeID);
//		}
//		id_dist_F_B.forEach((key, value) -> System.out.println(key + " " + value.dist));
		

		ArrayList<AlternativePaths<V,E>> ls_alternativePaths = limitedSharing();
		ArrayList<AlternativePaths<V,E>> answers = new ArrayList<AlternativePaths<V,E>>();

		System.out.println("\n Show path");
		ls_alternativePaths.sort(Comparator.comparing(a -> a.limited_sharing));
		for (AlternativePaths<V, E> path : ls_alternativePaths) {
			if (answers.size() < 4) {
				System.out.println(path.dist + " " + path.commonNodeID + " " + path.limited_sharing);
				answers.add(path);
			}
		}
		
		return answers;
	}

	
	public ArrayList<AlternativePaths<V,E>> limitedSharing() {

		ArrayList<AlternativePaths<V,E>> ls_alternativePaths = new ArrayList<AlternativePaths<V,E>>();
		ls_alternativePaths.add(this.optimalShortestPath);
	
		double [] opt_dist = getoptdistArray();
		int s = this.optimalShortestPath.path.size()-1;
		System.out.println("opt_dist[last] = " + opt_dist[this.optimalShortestPath.path.get(s).getId()]);
		System.out.println("opt_dist  = " + this.optimalShortestPathLength);
		System.out.println("diff = " + (opt_dist[this.optimalShortestPath.path.get(s).getId()]- this.optimalShortestPathLength));
	
		int investigated_paths_c = 0;
		for (AlternativePaths<V, E> path : alternativePaths) {
			investigated_paths_c++;
			double d = 0;
			int same_node_counter = 0;
			int prev_node_id = 0;
			for (DiGraphNode<V, E> node: path.path) {
	
				if (opt_dist[node.getId()] != 0 ) {
					if( same_node_counter == 0) {
						prev_node_id = node.getId();
						same_node_counter++;
					}
					if ( same_node_counter > 0) {
						d = d + opt_dist[node.getId()] - opt_dist[prev_node_id];
					}
	
				}else {
					same_node_counter = 0;
				}
	
			}
			if ( (d/this.optimalShortestPathLength) < gamma) {
				path.limited_sharing = d / this.optimalShortestPathLength;
				ls_alternativePaths.add(path);
			}
		}
		System.out.println("investigated paths " + investigated_paths_c);
		System.out.println("admissable path " + ls_alternativePaths.size());
		return ls_alternativePaths;
	}
	public double [] getoptdistArray() {
		double optdist_F[] = new double[this.dist_F.length];
		DiGraphNode<V, E> node_prv = null;
		boolean F = true;
		boolean start=true;
		int i =0;
		for (DiGraphNode<V, E> node: this.optimalShortestPath.path) {
//			System.out.println("node = " + node);
			
//			System.out.println("dist_F[node.getId()] = " + dist_F[node.getId()]);
			if (start){
				optdist_F[node.getId()] = this.dist_F[node.getId()];	
				node_prv = node;
				start = false;
			}else {
				if (this.dist_F[node.getId()] != 0.0 && F) {
					optdist_F[node.getId()] = this.dist_F[node.getId()];	
					node_prv = node;
				}else {
					F = false;
					optdist_F[node.getId()] = optdist_F[node_prv.getId()] + this.dist_B[node_prv.getId()] - this.dist_B[node.getId()];

					//System.out.println("optdist_F[node_prv.getId()] = " + optdist_F[node_prv.getId()]+ " , this.dist_B[node_prv.getId()] = "+ this.dist_B[node_prv.getId()] + " ,this.dist_B[node.getId() = "+ ( this.dist_B[node.getId()]));
					node_prv = node;
				}}
			i++;
			//System.out.println("d = " + optdist_F[node.getId()]);

		}//System.out.println("i = " + i);
		//System.out.println("i = " + i + " this.optimalShortestPath.path.size() -1: " + (this.optimalShortestPath.path.size() -1));
		/*	
		System.out.println("i = " + i + " ,dB = " + (-this.dist_B[node_id]+this.dist_B[this.optimalShortestPath.path.get(i-1).getId()]));
		//System.out.println("optdist_F[node_id] = " + optdist_F[node_id]);
		i++;
		node_id = this.optimalShortestPath.path.get(i).getId();

	}*/
		return optdist_F;
	}

	public boolean [] getinsideArray(ArrayList<DiGraphNode<V, E>> path, double[] dist ) {
		boolean inside[] = new boolean[dist.length];
		for (DiGraphNode<V, E> node : path) {
			inside[node.getId()] = true;
		}
		return inside;
	}

	public List<DiGraphNode<V, E>> getExploredNodes(DiGraphNode<V, E> target) {
		LinkedList<DiGraphNode<V, E>> path = new LinkedList<DiGraphNode<V, E>>();

		if (stamps_F[commonNodeID] < currentStamp || stamps_B[commonNodeID] < currentStamp) {
			return new ArrayList<DiGraphNode<V, E>>();
		}

		for(int i=0;i<stamps_F.length;i++)
			if (stamps_F[i] > 0) {
				path.addFirst(pred_F[i]);
			}

		for(int i=0;i<stamps_B.length;i++)
			if (stamps_B[i] > 0) {
				path.addFirst(pred_B[i]);
			}

		return new ArrayList<DiGraphNode<V, E>>(path);
	}

	//	public List<DiGraphArc<V, E>> getPathArcs(DiGraphNode<V, E> target) {
	//		LinkedList<DiGraphArc<V, E>> path = new LinkedList<DiGraphArc<V, E>>();
	//
	//		if (stamps[target.getId()] < currentStamp) {
	//			return new ArrayList<DiGraphArc<V, E>>();
	//		}
	//
	//		DiGraphNode<V, E> prev = null;
	//		DiGraphNode<V, E> current = target;
	//		while (current != null) {
	//			if (prev == null) {
	//				prev = current;
	//				current = pred[prev.getId()];
	//				continue;
	//			}
	//			path.addFirst(current.getFirstOutgoingArcTo(prev));
	//			prev = current;
	//			current = pred[prev.getId()];
	//		}
	//
	//		return new ArrayList<DiGraphArc<V, E>>(path);
	//	}

	/**
	 * Assumes that the dijkstra has been executed before. Returns the distance of
	 * <code>node</code> to the start node of the last run.
	 * 
	 * @param node
	 * @return distance of the node to the start node of the last run. If the node
	 *         is not reachable from the start node, then it returns
	 *         Double.MAX_VALUE.
	 */
	//	public double getDistance(DiGraphNode<V, E> node) {
	//		return stamps[node.getId()] < currentStamp ? Double.MAX_VALUE : dist[node.getId()];
	//	}

	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}



	public interface NodeVisitor<V> {
		/**
		 * Decides whether the query should be stopped at a specific node.
		 * 
		 * @param node node data of current minimum node
		 * @return stop query?
		 */
		boolean visit(V node);
	}

	public static interface NodeIterator<V, E extends WeightedArcData> {
		/**
		 * Returns an iterator of all adjacent nodes of node <code>s</code>.
		 * 
		 * @param s node to get the iterator of
		 * @return Iterator over all adjacent nodes
		 */
		Iterator<DiGraphNode<V, E>> getIterator(DiGraphNode<V, E> s);

		/**
		 * Returns the weight of the 'arc' from node <code>s</code> to node
		 * <code>t</code>. As the iterator can be used freely, this 'arc' must not be an
		 * actual arc in the graph.
		 * 
		 * @param s source node of the arc
		 * @param t target node of the arc
		 * @return weight of the arc
		 */
		double getWeightOfCurrentArc(DiGraphNode<V, E> s, DiGraphNode<V, E> t);
	}

	/**
	 * NodeIterator that depends mainly on the graph structure. Additional
	 * adjacencies may be added via an additional NodeIterator.
	 */
	public static class BasicAdjacentNodeIterator<V, E extends WeightedArcData> implements NodeIterator<V, E> {

		protected DiGraphArc<V, E> currArc;
		protected NodeIterator<V, E> addIt;
		protected boolean start;

		public BasicAdjacentNodeIterator(NodeIterator<V, E> additionalIterator) {
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
			Iterator<DiGraphArc<V, E>> it = s.getOutgoingArcs().iterator();
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

		/**
		 * NodeIterator that depends only on the graph structure.
		 */
		protected NodeIterator<V, E> DEFAULT_ADJACENT_NODE_ITERATOR = new BasicAdjacentNodeIterator<V, E>(null);

	}