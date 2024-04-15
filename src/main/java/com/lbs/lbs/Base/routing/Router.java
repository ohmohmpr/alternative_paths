package com.lbs.lbs.Base.routing;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.ColoredNode;
import com.lbs.lbs.Base.graph.types.WalkingData;
import com.lbs.lbs.Base.graph.types.multimodal.GeofabrikData;
import com.lbs.lbs.Base.graph.types.multimodal.IsoEdge;
import com.lbs.lbs.Base.graph.types.multimodal.IsoVertex;

/**
 * Classes implementing this interface can be used to query shortest paths.
 * 
 * @author Axel Forsch
 *
 * @param <V> type of node data in the graph
 * @param <E> type of edge data in the graph
 */
public interface Router<V, E extends WalkingData> {

	/**
	 * Starts a routing query which is stopped once the current time exceeds the
	 * given <code>time</code>. Can be used to get all nodes reachable in a certain
	 * time.
	 * 
	 * @param originalSource source node in the road graph
	 * @param time           at which query is stopped
	 */
	public void run(DiGraphNode<V, E> originalSource, long time);

	/**
	 * Returns a copy of the road graph, where node data is replaced by
	 * <code>ColoredNode</code>, storing the reachability of the node retrieved from
	 * the lust <code>run</code> call.
	 * 
	 * @return DiGraph with nodes colored according to reachability
	 */
	public DiGraph<ColoredNode, E> getColoredGraph();

	/**
	 * Returns the combined graph from road and public transportation data.
	 * 
	 * @return DiGraph applicable for routing
	 */
	public DiGraph<IsoVertex, IsoEdge> getRoutingGraph();

	/**
	 * Returns the (colored) node which was the source for the last
	 * <code>run</code>-query.
	 * 
	 * @return (colored) last query's source node
	 */
	public DiGraphNode<ColoredNode, E> getLastSource();

	/**
	 * Sets the start time of the query during the week in seconds after 1am on
	 * Monday morning.
	 * 
	 * @param starttime start time in seconds after Monday 1am
	 */
	public void setStarttime(long starttime);

	/**
	 * Factory used to transform edge data between road and routing graph.
	 * 
	 * @author Axel Forsch
	 *
	 * @param <E_iso>  type of edge data in routing graph
	 * @param <E_road> type of edge data in road graph
	 */
	public static interface Factory<E_iso, E_road> {
		E_road createEdgeData(E_road data);

		E_iso createIsoEdgeData(E_road data);
	}

	public static final Factory<GeofabrikData, GeofabrikData> GEOFABRIK_FACTORY = new Factory<>() {
		@Override
		public GeofabrikData createEdgeData(GeofabrikData data) {
			return new GeofabrikData(data);
		}

		@Override
		public GeofabrikData createIsoEdgeData(GeofabrikData data) {
			GeofabrikData isoData = new GeofabrikData(data);
			isoData.setValue(data.getValueAsTime());
			isoData.valueIsDistance(false);
			return isoData;
		}
	};
}
