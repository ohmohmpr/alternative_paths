package com.lbs.lbs.Base.routing;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphArc;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.Colored;
import com.lbs.lbs.Base.graph.types.ColoredNode;
import com.lbs.lbs.Base.graph.types.RoadGraph;
import com.lbs.lbs.Base.graph.types.WalkingData;
import com.lbs.lbs.Base.graph.types.multimodal.IsoEdge;
import com.lbs.lbs.Base.graph.types.multimodal.IsoVertex;
import com.lbs.lbs.Base.graph.types.multimodal.RoadNode;
import com.lbs.lbs.Base.io.GTFSLoader;
import com.lbs.lbs.Base.routing.Dijkstra.NodeIterator;
import com.lbs.lbs.Base.routing.Dijkstra.NodeVisitor;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MultiModalRouterBDV<E_iso extends IsoEdge, E_road extends WalkingData> implements Router<Point2D, E_road> {

	private int defaultTransferTime = 120;

	private int numNodesRoad;

	private DiGraph<IsoVertex, IsoEdge> routingGraph;
	private DiGraph<ColoredNode, E_road> coloredGraph;

	private Dijkstra<IsoVertex, IsoEdge> dijkstra;
	private NodeIterator<IsoVertex, IsoEdge> it;
	private NodeIterator<IsoVertex, IsoEdge> adj_it;
	private NodeVisitor<DiGraphNode<IsoVertex, IsoEdge>> visit;

	private long starttime;
	private DiGraphNode<ColoredNode, E_road> lastSource;

	Map<DiGraphNode<Point2D, E_road>, DiGraphNode<IsoVertex, IsoEdge>> road2routing;
	Map<DiGraphNode<IsoVertex, IsoEdge>, DiGraphNode<ColoredNode, E_road>> routing2color;

	// maps to dynamically add arcs to next transfer node
	private final HashMap<Integer, LinkedList<DiGraphNode<IsoVertex, IsoEdge>>> transferNodes;
	private final HashMap<Integer, Integer> transferTimes;

	Factory<E_iso, E_road> factory;

	/**
	 * Creates an instance of the <code>MultiModalRouter</code>. The input road
	 * graph (which remains unchanged) and the GTFS files in the
	 * <code>gtfsDirectory</code> get merged to a routing graph using the
	 * <code>factory</code>.
	 * 
	 * @param roadGraph     (previously read) road graph
	 * @param gtfsDirectory directory where the GTFS data is stored
	 * @param factory       Factory to convert graphs to routing graph
	 */
	public MultiModalRouterBDV(RoadGraph<Point2D, E_road> roadGraph, File gtfsDirectory, Factory<E_iso, E_road> factory) {
		this.factory = factory;
		numNodesRoad = roadGraph.n();
		initializeRoutingGraphWithRoadGraph(roadGraph);

		GTFSLoader loader = new GTFSLoader(routingGraph, defaultTransferTime);
		loader.loadGTFS(gtfsDirectory);

		transferNodes = loader.getTransferNodes();
		transferTimes = loader.getTransferTimes();

		dijkstra = new Dijkstra<>(routingGraph);
		it = new PublicTransportationIterator(transferNodes, transferTimes, dijkstra, defaultTransferTime);
	}

	/**
	 * Creates a copy of <code>roadGraph</code>, changing the node and edge data
	 * type to be suitable for the combined routing graph in the process.
	 * 
	 * Additionally, a copy of <code>roadGraph</code> is created, exchanging the
	 * node data to <code>ColoredNode</code> to later color the graph.
	 * 
	 * Due to the copy steps, the original <code>roadGraph</code> remains unchanged.
	 * 
	 * @param roadGraph previously loaded road graph
	 */
	private void initializeRoutingGraphWithRoadGraph(RoadGraph<Point2D, E_road> roadGraph) {
		if (factory == null)
			System.err.println("Factory not set");

		this.routingGraph = new DiGraph<>();
		this.coloredGraph = new DiGraph<>();

		// initialize maps to store relation between graphs
		road2routing = new HashMap<>();
		routing2color = new HashMap<>();

		DiGraphNode<IsoVertex, IsoEdge> routingGraphNode;
		DiGraphNode<ColoredNode, E_road> coloredGraphNode;
		for (DiGraphNode<Point2D, E_road> node : roadGraph.getNodes()) {
			routingGraphNode = routingGraph.addNode(new RoadNode(node.getNodeData()));
			coloredGraphNode = coloredGraph.addNode(new ColoredNode(node.getNodeData()));

			road2routing.put(node, routingGraphNode);
			routing2color.put(routingGraphNode, coloredGraphNode);
		}

		DiGraphNode<IsoVertex, IsoEdge> routingGraphSource, routingGraphTarget;
		DiGraphNode<ColoredNode, E_road> coloredGraphSource, coloredGraphTarget;
		for (DiGraphArc<Point2D, E_road> arc : roadGraph.getArcs()) {
			routingGraphSource = road2routing.get(arc.getSource());
			routingGraphTarget = road2routing.get(arc.getTarget());
			routingGraph.addArc(routingGraphSource, routingGraphTarget, factory.createIsoEdgeData(arc.getArcData()));

			coloredGraphSource = routing2color.get(routingGraphSource);
			coloredGraphTarget = routing2color.get(routingGraphTarget);
			coloredGraph.addArc(coloredGraphSource, coloredGraphTarget, factory.createEdgeData(arc.getArcData()));
		}
	}
	public void runReverse(DiGraphNode<Point2D, E_road> originalTarget, long maxTime) {
		DiGraphNode<IsoVertex, IsoEdge> target = road2routing.get(originalTarget);

		NodeIterator<IsoVertex, IsoEdge> it = new ReversePublicTransportationIterator(transferNodes, transferTimes,
				dijkstra, defaultTransferTime, starttime);
		adj_it = new ReverseGeofabrikRoadIterator<>(it);
		visit = new ReachabilityVisitor(starttime + maxTime, dijkstra);

		dijkstra.run(target, visit, adj_it);
		lastSource = routing2color.get(target);

		double dist;
		// road nodes are the first ones in the routing graph
		for (int i = 0; i < numNodesRoad; ++i) {

			dist = dijkstra.getDistance(routingGraph.getNode(i)) - starttime;

			ColoredNode nodeData = coloredGraph.getNode(i).getNodeData();

			if (dist <= maxTime) {
				nodeData.setReachability(Colored.REACHABLE, maxTime - dist);
			} else {
				nodeData.setReachability(Colored.UNREACHABLE, -1);
			}
		}
	}
	@Override
	public void setStarttime(long starttime) {
		this.starttime = starttime;
		this.dijkstra.setStarttime(starttime);
	}

	@Override
	public void run(DiGraphNode<Point2D, E_road> originalSource, long maxTime) {
		DiGraphNode<IsoVertex, IsoEdge> source = road2routing.get(originalSource);
		adj_it = new GeofabrikRoadIterator<>(it);
		visit = new ReachabilityVisitor(starttime + maxTime, dijkstra);

		dijkstra.run(source, visit, adj_it);
		lastSource = routing2color.get(source);

		double dist;
		// road nodes are the first ones in the routing graph
		for (int i = 0; i < numNodesRoad; ++i) {

			dist = dijkstra.getDistance(routingGraph.getNode(i)) - starttime;

			ColoredNode nodeData = coloredGraph.getNode(i).getNodeData();

			if (dist <= maxTime) {
				nodeData.setReachability(Colored.REACHABLE, maxTime - dist);
			} else {
				nodeData.setReachability(Colored.UNREACHABLE, -1);
			}
		}
	}


	/**
	 * Starts a routing query which is stopped once the <code>originalTarget</code>
	 * is reached.
	 * 
	 * @param originalSource source node in the road graph
	 * @param originalTarget target node in the road graph
	 * @return list of all nodes along the shortest path
	 */
	public List<DiGraphNode<IsoVertex, IsoEdge>> run(DiGraphNode<Point2D, E_road> originalSource,
			DiGraphNode<Point2D, E_road> originalTarget) {
		DiGraphNode<IsoVertex, IsoEdge> source = road2routing.get(originalSource);
		DiGraphNode<IsoVertex, IsoEdge> target = road2routing.get(originalTarget);
		adj_it = new GeofabrikRoadIterator<>(it);
		visit = new TargetVisitor(target.getId());

		dijkstra.run(source, visit, adj_it);

		return dijkstra.getPath(target);
	}

	@Override
	public DiGraphNode<ColoredNode, E_road> getLastSource() {
		return lastSource;
	}

	@Override
	public DiGraph<ColoredNode, E_road> getColoredGraph() {
		return coloredGraph;
	}

	@Override
	public final DiGraph<IsoVertex, IsoEdge> getRoutingGraph() {
		return routingGraph;
	}
}