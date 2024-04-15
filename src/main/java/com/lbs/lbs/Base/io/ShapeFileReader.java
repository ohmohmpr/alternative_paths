package com.lbs.lbs.Base.io;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.GeometricGraph;
import com.lbs.lbs.Base.graph.GraphSearch;
import com.lbs.lbs.Base.graph.GraphSearch.BFSQueue;
import com.lbs.lbs.Base.graph.types.WeightedArcData;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.IllegalParametersException;
import com.vividsolutions.jump.io.ShapefileReader;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ShapeFileReader {

	public static <G extends GeometricGraph<V, E>, V extends Point2D, E extends WeightedArcData> G importFromSHP(
			File path, Factory<G, V, E> factory) throws IllegalParametersException, Exception {
		ShapefileReader shpIn = new ShapefileReader();
		DriverProperties dp = new DriverProperties(path.getAbsolutePath());
		FeatureCollection fc = shpIn.read(dp);

		G g = factory.getGraph(fc);

		Iterator<?> it = fc.iterator();
		while (it.hasNext()) {
			Feature f = (Feature) it.next();

			if (!factory.includeFeature(f))
				continue;

			Geometry geom = f.getGeometry();
			if (geom instanceof LineString) {
				processLineString((LineString) geom, f, g, factory);
			} else if (geom instanceof MultiLineString) {
				Geometry subgeom;
				for (int i = 0; i < geom.getNumGeometries(); ++i) {
					subgeom = geom.getGeometryN(i);
					if (subgeom instanceof LineString) {
						processLineString((LineString) subgeom, f, g, factory);
					} else {
						System.err.println("unable to handle geometry of type " + subgeom.getGeometryType());
						return null;
					}
				}
			} else {
				System.err.println("unable to handle geometry of type " + geom.getGeometryType());
				return null;
			}
		}
		return g;
	}

	private static <G extends GeometricGraph<V, E>, V extends Point2D, E extends WeightedArcData> void processLineString(
			LineString geom, Feature f, G g, Factory<G, V, E> factory) {
		int oneway = factory.getOneway(f);

		Coordinate[] c = geom.getCoordinates();
		DiGraphNode<V, E> q = g.getDiGraphNode(c[0].x, c[0].y);
		if (q == null) {
			q = g.addNode(factory.createNodeData(c[0].x, c[0].y));
		}
		double distance;
		for (int i = 1; i < c.length; ++i) {
			DiGraphNode<V, E> p = q;
			q = g.getDiGraphNode(c[i].x, c[i].y);
			if (q == null) {
				q = g.addNode(factory.createNodeData(c[i].x, c[i].y));
			}
			distance = c[i - 1].distance(c[i]);
			if (oneway >= 0) // both directions or just forward
				if (p.getFirstOutgoingArcTo(q) == null)
					g.addArc(p, q, factory.createArcData(distance, f));
			if (oneway <= 0) // both directions or just backwards
				if (q.getFirstOutgoingArcTo(p) == null)
					g.addArc(q, p, factory.createArcData(distance, f));
		}
	}

	public static <G extends GeometricGraph<V, E>, V extends Point2D, E extends WeightedArcData> void reduceToBiggestComponent(
			G g) {
		GraphSearch<V, E> searcher = new GraphSearch<>(g);
		BFSQueue<V, E> queue = new BFSQueue<>();

		ArrayList<ArrayList<Integer>> result = searcher.findAllComponents(queue);
		ArrayList<Integer> biggestList = new ArrayList<>();
		for (ArrayList<Integer> a : result) {
			if (a.size() > biggestList.size()) {
				biggestList = a;
			}
		}
		System.out.println("Connected components found: " + result.size());
		System.out.println("Size of biggest component: " + biggestList.size());

		HashSet<DiGraphNode<V, E>> nodesToBeRemoved = new HashSet<>(g.getNodes());
		for (int i : biggestList) {
			nodesToBeRemoved.remove(g.getNode(i));
		}
		g.removeNodes(nodesToBeRemoved);
		g.updateIDs();
	}

	/**
	 * Defines how input data should be converted to graph types.
	 * 
	 * @author Axel Forsch
	 *
	 * @param <G> type of graph
	 * @param <V> type of node data
	 * @param <E> type of edge data
	 */
	public interface Factory<G extends DiGraph<V, E>, V, E extends WeightedArcData> {

		public static byte FORWARD = 1;
		public static byte BIDIRECTIONAL = 0;
		public static byte BACKWARD = -1;

		/**
		 * Whether to include the given feature in the graph.
		 * 
		 * @param f feature to decide on
		 * @return include feature?
		 */
		boolean includeFeature(Feature f);

		/**
		 * Returns if the given feature is bidirectional, can only be used forward or
		 * can only used backwards.
		 * 
		 * @param f feature to decide on
		 * @return {@link Factory#BIDIRECTIONAL}, {@link Factory#FORWARD} or
		 *         {@link Factory#BACKWARD}
		 */
		byte getOneway(Feature f);

		/**
		 * Creates a graph given all the features.
		 * 
		 * @param fc collection of all features
		 * @return graph
		 */
		G getGraph(FeatureCollection fc);

		/**
		 * Creates edge data given the feature and the geometric length of the feature.
		 * 
		 * @param distance geometric length of the feature
		 * @param f        given feature
		 * @return edge data
		 */
		E createArcData(double distance, Feature f);

		/**
		 * Creates node data given the x- and y-coordinates of the node.
		 * 
		 * @param x x-coordinate
		 * @param y y-coordinate
		 * @return node data
		 */
		V createNodeData(double x, double y);
	}
}