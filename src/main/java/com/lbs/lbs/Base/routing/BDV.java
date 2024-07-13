package com.lbs.lbs.Base.routing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.AlternativePaths;
import com.lbs.lbs.Base.graph.types.WeightedArcData;
import com.lbs.lbs.Base.graph.types.multimodal.GeofabrikData;
import com.lbs.lbs.Base.util.MinHeap;
import com.lbs.lbs.Base.util.MinHeap.HeapItem;
import com.lbs.lbs.DataSingulation.RoadGraphHolder;

public class BDV<V, E extends WeightedArcData> {

	// Forward search
	private double starttime = 0;
	protected double dist_F[];
	protected double curr_dist_F = 0;
	protected int stamps_F[];
	protected boolean visited_F[];
	protected HeapItem<DiGraphNode<V, E>> items_F[];
	public DiGraphNode<V, E> pred_F[];

	// Backward search
	protected double dist_B[];
	protected double curr_dist_B = 0;
	protected int stamps_B[];
	protected boolean visited_B[];
	protected HeapItem<DiGraphNode<V, E>> items_B[];
	public DiGraphNode<V, E> pred_B[];

	// BD
	protected double currentLength = Double.MAX_VALUE;
	protected int currentStamp = 0;
	public int commonNodeID;

	// BD variables
	protected double optimalLength = Double.MAX_VALUE;
	protected AlternativePaths<V, E> optimalShortestPath = null;
	public ArrayList<AlternativePaths<V, E>> alternativePaths;
	public Map<Integer, AlternativePaths<V,E>> id_dist_F_B;
	public DiGraphNode<V, E> viaNodeList;

	//alternative path conditions
	protected int numPaths = 3; // number of alternative paths.
	protected double epsilon = 0.25; // stretch longest admissible path can be.
	protected double gamma = 0.8;
	protected double alpha = 0.25;
	
	//alternative path result
	protected ArrayList<AlternativePaths<V, E>> result = new ArrayList<AlternativePaths<V, E>>();
	Dijkstra<Point2D, GeofabrikData> dj;

	@SuppressWarnings("unchecked")
	public BDV(DiGraph<V, E> g, int numPaths, double limSharing, double localOpt, double UBS) {
		this.dist_F = new double[g.n()];
		this.stamps_F = new int[g.n()];
		this.visited_F = new boolean[g.n()];
		this.items_F = new HeapItem[g.n()];
		this.pred_F = new DiGraphNode[g.n()];

		this.dist_B = new double[g.n()];
		this.stamps_B = new int[g.n()];
		this.visited_B = new boolean[g.n()];
		this.items_B = new HeapItem[g.n()];
		this.pred_B = new DiGraphNode[g.n()];
		
		this.numPaths = numPaths;
		this.epsilon = UBS;
		this.gamma = limSharing;
		this.alpha = localOpt;

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
	 * @throws Exception 
	 * 
	 * @throws OutOfStreetNetworkException
	 */
	public double run(DiGraphNode<V, E> source, DiGraphNode<V, E> target) throws Exception {
		RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
		this.dj = new Dijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
		
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

			if (currentLength > (1 + epsilon) * optimalLength) {
				break;
			}
			// top_s < top_t -> Forward search
			if (curr_dist_F <= curr_dist_B) {
				queue_F.extractMin();
				//iterate through the neighbors of u
				for (Iterator<DiGraphNode<V, E>> it = nit.getIterator(u_F); it.hasNext();) {
					DiGraphNode<V, E> v = it.next();

					weightOfArc = nit.getWeightOfCurrentArc(u_F, v);
					discoverNode_F(u_F, v, queue_F, dist_F[u_F.getId()] + weightOfArc);
					
					commonNodeID = v.getId();
					currentLength =  dist_F[commonNodeID] + dist_B[commonNodeID];
					// if the node got already investigated but not visited in the backward search -> new path
					if (items_B[v.getId()] != null && currentLength < (1 + epsilon) * optimalLength) {

						AlternativePaths<V, E> alternativePath = new AlternativePaths<>(
								commonNodeID, currentLength,
								getPath(), getPathArcs(), v
						);

						// check whether the path is the optimal path
						if (optimalLength > currentLength) {
							optimalLength = currentLength;
							this.optimalShortestPath  = alternativePath;
						}// if it is not the optimal path just add it to the alternative paths
						else {
							checkCommonNode(commonNodeID, alternativePath);
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

					// if the node got already investigated but not visited in the forward search -> new path
					commonNodeID = v.getId();
					currentLength =  dist_F[commonNodeID] + dist_B[commonNodeID];
	 				if (items_F[v.getId()] != null  && currentLength < (1 + epsilon) * optimalLength) {

						AlternativePaths<V, E> alternativePath = new AlternativePaths<>(
								commonNodeID, currentLength,
								getPath(), getPathArcs(), v
						);
					
						// check whether the path is the optimal path 
						if (optimalLength > currentLength) {
							optimalLength = currentLength;
							this.optimalShortestPath  = alternativePath;
						}// if it is not the optimal path just add it to the alternative paths
						else {
							checkCommonNode(commonNodeID, alternativePath);
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


	public void checkCommonNode(int commonNodeID, AlternativePaths<V,E> alternativePath) {
		
		// Create a hash map, named id_dist_F_B, in which contains commonNodeID and alternativePath
		// if commonNodeID is in the hash map, let compare
		if (id_dist_F_B.containsKey(commonNodeID)) {
			AlternativePaths<V,E> prev_altpath = id_dist_F_B.get(commonNodeID);
			// if the distance of the existing path longer than the new one, REPLACE
			if (currentLength < prev_altpath.getdist()) {
				id_dist_F_B.put(commonNodeID, alternativePath);
				alternativePaths.add(alternativePath);
//				System.out.println("\ncommonNodeID :" + commonNodeID);
			}
		} 
		// if commonNodeID is not in the hash map, ADD
		else {
			id_dist_F_B.put(commonNodeID, alternativePath);
			alternativePaths.add(alternativePath);
		}
	}
	
	
	public ArrayList<Triple<Double, Double, Triple< AlternativePaths<V,E>, Double, DiGraphNode<V, E>>> >  getPaths() throws Exception {

		//initialize:
		result.add(optimalShortestPath);
		result.get(0).limited_sharing = 0;
		boolean NomorePathstoSearch = false;

		// search throw the whole alternative path list as long as there are not numPath in the resultList
		while (result.size() < numPaths && NomorePathstoSearch == false) {
			ArrayList<AlternativePaths<V,E>> tmp_list_for_sort = new ArrayList<AlternativePaths<V,E>>();
			System.out.println("Current size of result: " + result.size());
			
			//test all path in the alternative path list to LimitedSharing, LocalOptimality and UBS
			for (AlternativePaths<V, E> path: alternativePaths) {
				boolean passLimitedSharing = limitedSharing(result, path);
				if (!passLimitedSharing) {
					continue;
				}
				boolean passLocalOptimality = localOptimality(path);
				if (!passLocalOptimality) {
					continue;
				}
				if (passLimitedSharing && passLocalOptimality) {
					tmp_list_for_sort.add(path);
				}
			}
			// sort the alternative paths which passed all three conditions by the performance in the cost_function
			if (tmp_list_for_sort.size() != 0) {
				tmp_list_for_sort.sort(Comparator.comparing(a -> a.getCostFunction()));
				AlternativePaths<V,E> bestOne = tmp_list_for_sort.get(0);
				// add the best on and remove it from the alternative path list
				result.add(bestOne);
				alternativePaths.remove(alternativePaths.indexOf(bestOne));
			} else {
				NomorePathstoSearch = true;
			}
			
		}
		// print the result in the console
		ArrayList<Triple<Double, Double, Triple<AlternativePaths<V,E>, Double, DiGraphNode<V, E>>> > showResult = 
				new ArrayList<Triple<Double, Double, Triple<AlternativePaths<V,E>, Double, DiGraphNode<V, E>>> >(); ;
		System.out.println("\nRESULTs");
		System.out.println("path,               Dist,             Cost");
		for (AlternativePaths<V, E> path: result) {
			System.out.println("path: " + path.getdist() + " " + path.getCostFunction());
			showResult.add(Triple.of(path.getdist(), path.getCostFunction(), Triple.of(path, 0.0, path.viaNode)));
			
		}
		
		return showResult;
	}
	
    //test the second condition: locally optimal
	public boolean localOptimality(AlternativePaths<V, E> path) throws Exception {
		// if already tested -> return result
		if (path.LocalOptimalitytested) {
			return path.passLocalOptimality;
		} else {
			//initialize the variable with true
			path.LocalOptimalitytested = true;
		}
		// search the list index of the common_node
		int counter_index = 0;
		for (DiGraphNode<V, E> node :path.path ) {
			counter_index = counter_index + 1;
			if (path.commonNodeID == node.getId()) {
				break;
			}
		}
		//initialize:
		List<DiGraphNode<V, E>>  nodes_F = path.path.subList(0, counter_index);
		List<DiGraphNode<V, E>>  nodes_B = path.path.subList(counter_index, path.path.size());

		int index_viaNode_F = counter_index-1;
		DiGraphNode<Point2D, GeofabrikData> viaNode = (DiGraphNode<Point2D, GeofabrikData>) nodes_F.get(index_viaNode_F);

		double T_F = this.alpha * this.optimalLength;
		double length_x = 0;

		DiGraphNode<Point2D, GeofabrikData> NODE_X = viaNode;
		DiGraphNode<V, E> viaNode_F = (DiGraphNode<V, E>) viaNode;
		// search the NODE_X with is at least T away in the forward direction
		while (index_viaNode_F != 0 && T_F > 0) {
			viaNode_F = nodes_F.get(index_viaNode_F);
			index_viaNode_F = index_viaNode_F - 1;
			DiGraphNode<V, E> predNode_F = nodes_F.get(index_viaNode_F);
			
			// subtract the distance of the test variable T_F
			T_F = T_F - viaNode_F.getFirstOutgoingArcTo(predNode_F).getArcData().getValue();
			if (T_F > 0) {
				//save the distance 
				length_x = length_x + viaNode_F.getFirstOutgoingArcTo(predNode_F).getArcData().getValue();				
			}
			// set those variable one step further
			NODE_X = (DiGraphNode<Point2D, GeofabrikData>) viaNode_F;
			viaNode_F = predNode_F;
		}
		//initialize:
		int index_viaNode_B = 0;

		double T_B = this.alpha * this.optimalLength;
		double length_y = 0;

		DiGraphNode<Point2D, GeofabrikData> NODE_Y = viaNode;
		DiGraphNode<V, E> viaNode_B = (DiGraphNode<V, E>) viaNode;
		// search the NODE_Y with is at least T away in the backward direction
		while (index_viaNode_B != nodes_B.size() - 1 && T_B > 0) {
			viaNode_B = nodes_B.get(index_viaNode_B);
			index_viaNode_B = index_viaNode_B + 1;
			DiGraphNode<V, E> predNode_B = nodes_B.get(index_viaNode_B);
			
			// subtract the distance of the test variable T_B
			T_B = T_B - viaNode_B.getFirstOutgoingArcTo(predNode_B).getArcData().getValue();
			if (T_B > 0) {
				//save the distance
				length_y = length_y + viaNode_B.getFirstOutgoingArcTo(predNode_B).getArcData().getValue();				
			}
			// set those variable one step further
			NODE_Y = (DiGraphNode<Point2D, GeofabrikData>) viaNode_B;
			viaNode_B = predNode_B;
		}

		double dj_length_X_Y = this.dj.run(NODE_X,NODE_Y);	

		//compare the length of the shortest path and the actual path
        if (Math.abs(dj_length_X_Y - (length_x+length_y)) < 1e-6 ){
        	path.passLocalOptimality = true;
        	return path.passLocalOptimality;
        } 
		System.out.println("DEAD -> localOptimality");
 
		return path.passLocalOptimality;
	}

	// test the first condition: limited sharing with the optimal path and all already found alternative paths
	public boolean limitedSharing(ArrayList<AlternativePaths<V,E>> result, AlternativePaths<V, E> path) {
		// if already tested and limited sharing amount was to big-> return result
		if (path.LimitedSharingtested == true && path.passLimitedSharing == false) {
			return path.passLimitedSharing;
		}
		// go to the whole result list to get the sharing with the optimal path and all thealready found alternative paths
		for (AlternativePaths<V,E> path_result: result) { // not necassary -> is contained in opt_dist
			// get an Array with the distance from the start node to all nodes on the path
			double [] opt_dist = getoptdistArray(path_result);
			//initialize:
			double sigma = 0;
			int same_node_counter = 0;
			int prev_node_id = 0;
			// check all nodes on the path:
			for (DiGraphNode<V, E> node: path.path) {
				// if the current node is in the optimal path
				if (opt_dist[node.getId()] != 0 ) {
					// and if also the previous node is in the optimal path
					if ( same_node_counter > 0) {
						// add the distance between current and previous node to sigma
						sigma = sigma + opt_dist[node.getId()] - opt_dist[prev_node_id];
					}//if the previous node is not in the optimal path just add the counter
					else {
						same_node_counter++;
					}
				}// if the current node is not in the optimal path -> set the counter to zero
				else {
					same_node_counter = 0;
				}
				prev_node_id = node.getId();
			}
			// check whether the sharing distance is bigger than gamma* the length of the optimal path
			if ( sigma/path.getdist() < gamma) {
				//save the sharing amount 
				path.limited_sharing = path.limited_sharing + sigma;
				path.passLimitedSharing = true;
			} else {
				path.limited_sharing = Double.MAX_VALUE;
				path.passLimitedSharing = false;
				path.LimitedSharingtested = true;
				return path.passLimitedSharing;
			}
		}
		
		return path.passLimitedSharing;
	}

	
	public double [] getoptdistArray(AlternativePaths<V,E> path_result) {
		//initialize:
		double optdist_F[] = new double[this.dist_F.length];
		
		DiGraphNode<V, E> node_prv = null;
		boolean F = true;
		boolean start=true;
		for (DiGraphNode<V, E> node: path_result.path) {
			//for the first node: the distance can be 0.0 -> no criteria to go to the backward search
			if (start){
				optdist_F[node.getId()] = this.dist_F[node.getId()];	
				node_prv = node;
				start = false;
			} else {
				//add the distances of the forward search 
				if (this.dist_F[node.getId()] != 0.0 && F) {
					optdist_F[node.getId()] = this.dist_F[node.getId()];	
					node_prv = node;
				} else {
					//compute the distances of the backward search as distances form start node to current node 
					F = false;
					optdist_F[node.getId()] = optdist_F[node_prv.getId()] + this.dist_B[node_prv.getId()] - this.dist_B[node.getId()];
					node_prv = node;
				}
			}
		}
		
		return optdist_F;
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

	public List<DiGraphArc<V, E>> getPathArcs() {
		DiGraphNode<V, E> target = items_F[commonNodeID].getValue();
		LinkedList<DiGraphArc<V, E>> path = new LinkedList<DiGraphArc<V, E>>();

		if (stamps_F[target.getId()] < currentStamp) {
			return new ArrayList<DiGraphArc<V, E>>();
		}

		DiGraphNode<V, E> prev = null;
		DiGraphNode<V, E> current = target;
		while (current != null) {
			if (prev == null) {
				prev = current;
				current = pred_F[prev.getId()];
				continue;
			}
			path.addFirst(current.getFirstOutgoingArcTo(prev));
			prev = current;
			current = pred_F[prev.getId()];
		}

		if (stamps_B[target.getId()] < currentStamp) {
			return new ArrayList<DiGraphArc<V, E>>();
		}

		DiGraphNode<V, E> prev_B = null;
		DiGraphNode<V, E> current_B = target;
		while (current_B != null) {
			if (prev_B == null) {
				prev_B = current_B;
				current_B = pred_B[prev_B.getId()];
				continue;
			}
			path.addLast(current_B.getFirstOutgoingArcTo(prev_B));
			prev_B = current_B;
			current_B = pred_B[prev_B.getId()];
		}
		
		return new ArrayList<DiGraphArc<V, E>>(path);
	}

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