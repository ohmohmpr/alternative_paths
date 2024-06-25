package com.lbs.lbs.Base.routing;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.WeightedArcData;
import com.lbs.lbs.Base.routing.Dijkstra.NodeIterator;
import com.lbs.lbs.Base.util.MinHeap;
import com.lbs.lbs.Base.util.MinHeap.HeapItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple implementation of the Bidirectional Dijkstra algorithm. The data structures are
 * initialized. Due to some internal time stamp mechanism, the class supports
 * multiple executions without the need to reinitialize the data structures.
 */
public class BDV_2<V, E extends WeightedArcData> {

	private double starttime = 0;
	private DiGraphNode<V, E> via_node = null;
	protected double dist_F[];
	protected double dist_B[];
	protected double mu = Double.POSITIVE_INFINITY;
	protected int stamps_F[];
	protected int stamps_B[];
	protected HeapItem<DiGraphNode<V, E>> items[];
	public DiGraphNode<V, E> pred_F[];
	public DiGraphNode<V, E> pred_B[];

	protected int currentStamp = 0;
	
	protected double epsilon = 0.25; // stretch longest admissible path can be.

	@SuppressWarnings("unchecked")
	public BDV_2(DiGraph<V, E> g) {
		this.dist_F = new double[g.n()];
		this.dist_B = new double[g.n()];
		this.stamps_F = new int[g.n()];
		this.stamps_B = new int[g.n()];
		this.items = new HeapItem[g.n()];
		this.pred_B = new DiGraphNode[g.n()];
		this.pred_F = new DiGraphNode[g.n()];
	}

	@SuppressWarnings("unchecked")
	public BDV_2(DiGraph<V, E> g, double startTime) {
		this.dist_F = new double[g.n()];
		this.dist_B = new double[g.n()];
		this.stamps_F = new int[g.n()];
		this.stamps_B = new int[g.n()];
		this.items = new HeapItem[g.n()];
		this.pred_F = new DiGraphNode[g.n()];
		this.pred_B = new DiGraphNode[g.n()];
		this.starttime = startTime;
	}

	public double getCurrDist() {
		return this.mu;
	}

	public DiGraphNode<V, E> getPred(int nodeId) {
		return pred_F[nodeId];
	}

	/**
	 * Runs the algorithm starting at the source node. When the target is reached,
	 * the search is aborted and the distance to the target is returned.
	 * 
	 * @throws OutOfStreetNetworkException
	 
	public double run(DiGraphNode<V, E> source, DiGraphNode<V, E> target) {
		run(source,target);
		return this.mu;
	}*/



	/**
	 * Runs the algorithm starting at the source node using the given NodeVisitor.
	 * Runs as long as the NodeVisitor's method visit returns true.
	 * 
	 * @throws OutOfStreetNetworkException
	 
	public boolean run(DiGraphNode<V, E> source,DiGraphNode<V, E> target, NodeVisitor<DiGraphNode<V, E>> visitor) {
		return run(source, target, visitor, DEFAULT_ADJACENT_NODE_ITERATOR);
	}
*/
	/**
	 * Runs the algorithm starting at the source node using the given NodeVisitor.
	 * Runs as long as the NodeVisitor's method visit returns true. Ignores the
	 * graph structure; adjacencies depend on the NodeIterator.
	 * 
	 * @throws OutOfStreetNetworkException
	 */
	public double run(DiGraphNode<V, E> source,DiGraphNode<V, E> target, NodeIterator<V, E> nit) {
		currentStamp++;
		dist_F[source.getId()] = starttime;
		/*double[] dist_F = new double[this.n];
		double[] dist_B = new double[this.n];
		dist_F[source.getId()] = starttime;*/
		pred_F[source.getId()] = null;
		pred_B[target.getId()] = null;
		double weightOfArc;

		MinHeap<DiGraphNode<V, E>> queue_F = new MinHeap<DiGraphNode<V, E>>();
		MinHeap<DiGraphNode<V, E>> queue_B = new MinHeap<DiGraphNode<V, E>>();

		items[source.getId()] = queue_F.insertItem(starttime, source);
		items[target.getId()] = queue_B.insertItem(0, target);
		stamps_F[source.getId()] = currentStamp;
		stamps_B[target.getId()] = currentStamp;
		
		DiGraphNode<V, E> top_s = source;
		DiGraphNode<V, E> top_t = target;
		
		while (queue_F.size() > 0 && queue_B.size() > 0) {
			
			
			
			if (dist_F[top_s.getId()] + dist_B[top_t.getId()] < mu) {
				System.out.println("found optimalShortestPath " );
				break;
			}
			
			//extract the min of both searches
			HeapItem<DiGraphNode<V, E>> item = queue_B.getMin();
			if (dist_F[queue_F.getMin().getValue().getId()] <= dist_B[queue_B.getMin().getValue().getId()]){
				item = queue_F.extractMin();
				top_s = item.getValue();
			}else {
				item = queue_B.extractMin();
				top_t = item.getValue();
			}
			
			DiGraphNode<V, E> u = item.getValue();
			
			//iterate throw the neighbors of u
			for (Iterator<DiGraphNode<V, E>> it = nit.getIterator(u); it.hasNext();) {
				DiGraphNode<V, E> v = it.next();

				weightOfArc = nit.getWeightOfCurrentArc(u, v);
				//discover the node in the forward search
				if (u == top_s) {
					discoverNode(u, v, queue_F, dist_F[u.getId()] + weightOfArc,true);
					
				}//discover the node in the backward search
				else {
					discoverNode(u, v, queue_B, dist_B[u.getId()] + weightOfArc,false);
				}
				//check whether the node got already investigated in both searches 
				//and if the new s-t-path is shorter than the length of the shortest s-t-path already found saved in mu
				if(stamps_B[v.getId()]  == currentStamp && stamps_F[v.getId()]  == currentStamp && this.mu > dist_F[v.getId()] + dist_B[v.getId()]) {
					//update mu
					this.mu = dist_F[v.getId()] + dist_B[v.getId()];
					this.via_node = v;
					
					}
			}
		}
		return this.mu;
	}

	private void discoverNode(DiGraphNode<V, E> curr, DiGraphNode<V, E> target, MinHeap<DiGraphNode<V, E>> queue,
			double alt, boolean Forward ) {
		if (Forward) {		
			//checks whether the neighbor of u got investigated before and if that is the case whether the new distance is shorter
			if (stamps_F[target.getId()] < currentStamp || alt < dist_F[target.getId()]) { 
				// update the distance and predecessor
				dist_F[target.getId()] = alt;
				pred_F[target.getId()] = curr;
				//checks whether the neighbor of u got investigated before and if that is the case whether neighbor of u is already in the queue
				if (stamps_F[target.getId()] < currentStamp || items[target.getId()] == null) {
					//put the neighbor of u in the queue
					items[target.getId()] = queue.insertItem(alt, target); 
				} else {
					//decrease the key if the neighbor of u is already in the queue
					queue.decreaseKey(items[target.getId()], alt);
				}
				//update: mark the neighbor of u as investigated
				stamps_F[target.getId()] = currentStamp;
			}
		}else {
			//checks whether the neighbor of u got investigated before and if that is the case whether the new distance is shorter
			if (stamps_B[target.getId()] < currentStamp || alt < dist_B[target.getId()]) {
				// update the distance and predecessor
				dist_B[target.getId()] = alt;
				pred_B[target.getId()] = curr;
				//checks whether the neighbor of u got investigated before and if that is the case whether neighbor of u is already in the queue
				if (stamps_B[target.getId()] < currentStamp || items[target.getId()] == null) {
					//put the neighbor of u in the queue
					items[target.getId()] = queue.insertItem(alt, target);
				} else {
					//decrease the key if the neighbor of u is already in the queue
					queue.decreaseKey(items[target.getId()], alt);
				}
				//update: mark the neighbor of u as investigated
				stamps_B[target.getId()] = currentStamp;
			}
		}
	}

	/**
	 * Assumes that the Dijkstra algorithm has been executed before. Returns the
	 * path of node to the target (stored in an ArrayList from start to target).
	 */

	public List<DiGraphNode<V, E>> getPath(DiGraphNode<V, E> target) {
		LinkedList<DiGraphNode<V, E>> path = new LinkedList<DiGraphNode<V, E>>();

		if (stamps_B[target.getId()] < currentStamp) {
			return new ArrayList<DiGraphNode<V, E>>();
		}

		DiGraphNode<V, E> current = this.via_node;
		while (current != null) {
			path.addFirst(current);
			current = pred_F[current.getId()];
		}
		current = pred_B[this.via_node.getId()];
		while (current != null) {
			path.addLast(current);
			current = pred_B[current.getId()];
		}
		return new ArrayList<DiGraphNode<V, E>>(path);
	}
/*
	public List<DiGraphArc<V, E>> getPathArcs(DiGraphNode<V, E> target) {
		LinkedList<DiGraphArc<V, E>> path = new LinkedList<DiGraphArc<V, E>>();

		if (stamps_B[target.getId()] < currentStamp) {
			return new ArrayList<DiGraphArc<V, E>>();
		}

		DiGraphNode<V, E> prev = null;
		DiGraphNode<V, E> current = target;
		while (current != null) {
			if (prev == null) {
				prev = current;
				current = pred[prev.getId()];
				continue;
			}
			path.addFirst(current.getFirstOutgoingArcTo(prev));
			prev = current;
			current = pred[prev.getId()];
		}

		return new ArrayList<DiGraphArc<V, E>>(path);
	}
*/
	/**
	 * Assumes that the dijkstra has been executed before. Returns the distance of
	 * <code>node</code> to the start node of the last run.
	 * 
	 * @param node
	 * @return distance of the node to the start node of the last run. If the node
	 *         is not reachable from the start node, then it returns
	 *         Double.MAX_VALUE.
	 */
	/*
	public double getDistance(DiGraphNode<V, E> node) {
		return stamps[node.getId()] < currentStamp ? Double.MAX_VALUE : dist[node.getId()];
	}
*/
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