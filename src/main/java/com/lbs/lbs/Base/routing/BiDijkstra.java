package com.lbs.lbs.Base.routing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.AlternativePaths;
import com.lbs.lbs.Base.graph.types.WeightedArcData;
import com.lbs.lbs.Base.util.MinHeap;
import com.lbs.lbs.Base.util.MinHeap.HeapItem;

public class BiDijkstra<V, E extends WeightedArcData> {


	private double starttime = 0;
	protected double dist_F[];
	protected double curr_dist_F = 0;
	protected int stamps_F[];
	protected HeapItem<DiGraphNode<V, E>> items_F[];
	public DiGraphNode<V, E> pred_F[];

	protected double dist_B[];
	protected double curr_dist_B = 0;
	protected int stamps_B[];
	protected HeapItem<DiGraphNode<V, E>> items_B[];
	public DiGraphNode<V, E> pred_B[];

	protected double shortestPathLength = Double.MAX_VALUE;
	protected int currentStamp = 0;
	public int commonNodeID;
	
	@SuppressWarnings("unchecked")
	public BiDijkstra(DiGraph<V, E> g) {
		this.dist_F = new double[g.n()];
		this.stamps_F = new int[g.n()];
		this.items_F = new HeapItem[g.n()];
		this.pred_F = new DiGraphNode[g.n()];
		
		this.dist_B = new double[g.n()];
		this.stamps_B = new int[g.n()];
		this.items_B = new HeapItem[g.n()];
		this.pred_B = new DiGraphNode[g.n()];
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

		currentStamp++;
		dist_F[source.getId()] = starttime;
		pred_F[source.getId()] = null;
		
		dist_B[source.getId()] = starttime;
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

			if ((curr_dist_F + curr_dist_B) >= shortestPathLength) {
				System.out.println("shortestPathLength: " + shortestPathLength);
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
					
					int nodeID = v.getId();
					double currentLength =  dist_F[nodeID] + dist_B[nodeID];
					if (items_B[v.getId()] != null && shortestPathLength > currentLength) {
						commonNodeID = v.getId();
						shortestPathLength = currentLength;
					}
				}
			} else {
				queue_B.extractMin();
				for (Iterator<DiGraphNode<V, E>> it = nit.getIterator(u_B); it.hasNext();) {
					DiGraphNode<V, E> v = it.next();

					weightOfArc = nit.getWeightOfCurrentArc(u_B, v);
					discoverNode_B(u_B, v, queue_B, dist_B[u_B.getId()] + weightOfArc);

					int nodeID = v.getId();
					double currentLength =  dist_F[nodeID] + dist_B[nodeID];
	 				if (items_F[v.getId()] != null && shortestPathLength > currentLength) {
						commonNodeID = v.getId();
						shortestPathLength = currentLength;
					}
				}
			}
		}
		return true;
	}

	private void discoverNode_F(DiGraphNode<V, E> curr, DiGraphNode<V, E> target, MinHeap<DiGraphNode<V, E>> queue,
			double alt) {
		if (stamps_F[target.getId()] < currentStamp || alt < dist_F[target.getId()]) {
			dist_F[target.getId()] = alt;
			pred_F[target.getId()] = curr;

			if (stamps_F[target.getId()] < currentStamp || items_F[target.getId()] == null) {
				items_F[target.getId()] = queue.insertItem(alt, target);
			} else {
				queue.decreaseKey(items_F[target.getId()], alt);
			}
			stamps_F[target.getId()] = currentStamp;
		}
	}

	private void discoverNode_B(DiGraphNode<V, E> curr, DiGraphNode<V, E> target, MinHeap<DiGraphNode<V, E>> queue,
			double alt) {
		if (stamps_B[target.getId()] < currentStamp || alt < dist_B[target.getId()]) {
			dist_B[target.getId()] = alt;
			pred_B[target.getId()] = curr;
			if (stamps_B[target.getId()] < currentStamp || items_B[target.getId()] == null) {
				items_B[target.getId()] = queue.insertItem(alt, target);
			} else {
				queue.decreaseKey(items_B[target.getId()], alt);
			}
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

		DiGraphNode<V, E> current_F = items_F[commonNodeID].getValue();
		while (current_F != null) {
			path.addFirst(current_F);
			current_F = pred_F[current_F.getId()];
		}
		
		DiGraphNode<V, E> current_B = items_B[commonNodeID].getValue();
		while (current_B != null) {
			path.addLast(current_B);
			current_B = pred_B[current_B.getId()];
		}

		return new ArrayList<DiGraphNode<V, E>>(path);
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