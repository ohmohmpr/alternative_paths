package com.lbs.lbs.Service;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.types.AlternativePaths;
import com.lbs.lbs.Base.graph.types.multimodal.*;
import com.lbs.lbs.Base.routing.BiDijkstra;
import com.lbs.lbs.Base.routing.BDV;
import com.lbs.lbs.Base.routing.MultiModalRouter;
import com.lbs.lbs.DataSingulation.MultiModalGraphHolder;
import com.lbs.lbs.DataSingulation.RoadGraphHolder;
import com.lbs.lbs.Entity.TransportPath;
import geotrellis.proj4.CRS;
import geotrellis.proj4.Transform;

import org.apache.commons.lang3.tuple.Triple;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShortestPathService {

//    public static List<Coordinate>  getShortestPath(double lat1,double lon1,double lat2, double lon2) throws Exception {
//
//        /** Transform lat-lon to East-North */
//            // You should use LatLon2EN to transform it.
//        Coordinate source = LatLon2EN(lat1, lon1);
//        Coordinate target = LatLon2EN(lat2, lon2);
//
//        /** Get Graph Holder from Road Graph Holder */
//            // Hint : RoadGraphHolder.getInstance() returns graph holder
//        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
//        /** Find The Nearest nodes in the graph */
//            // Hint : RoadGraphHolder has function to find nearest Node 'findNearestPoint(Coordinate p)'
//        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
//        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);
//
//        /** Create Dijkstra shortest path class with your graph and run*/
//            // Dijkstra class path is Base/routing/Dijkstra, check the construction parameters
//            // and find how to run Dijkstra
//
//        Dijkstra<Point2D, GeofabrikData> dj = new Dijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
//        
//        dj.run(sourceNode,targetNode);
////        dj.run(targetNode,sourceNode);
//        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getPath(targetNode);
//        List<Coordinate> returnList = new ArrayList<>();
//        returnList.add(new Coordinate(lat1,lon1));
//        /** get shortest path  and convert to coordinate List (List<Coordinate>)  As Latitude Longitude*/
//        //
//        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
//            returnList.add(EN2LatLon(p.getNodeData().getX(),p.getNodeData().getY()));
//        }
//        returnList.add(new Coordinate(lat2,lon2));
//        /** return shortest path */
//        return returnList;
//    }
    

//    public static List<Coordinate>  getExploredNodes(double lat1,double lon1,double lat2, double lon2) throws Exception {
//
//        Coordinate source = LatLon2EN(lat1, lon1);
//        Coordinate target = LatLon2EN(lat2, lon2);
//
//        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
//
//        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
//        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);
//
//        Dijkstra<Point2D, GeofabrikData> dj = new Dijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
//        dj.run(sourceNode,targetNode);
////        System.out.println(length);
//        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getExploredNodes(targetNode);
//
//        List<Coordinate> returnList_nodes = new ArrayList<Coordinate>();
//
//        returnList_nodes.add(new Coordinate(lat1,lon1));
//        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
//        	if (p != null) {
//                returnList_nodes.add(EN2LatLon(p.getNodeData().getX(), p.getNodeData().getY()));
//        	}
//        }
//        returnList_nodes.add(new Coordinate(lat2,lon2));
//        
//        return returnList_nodes;
//    }

    public static Triple<Double, Double, List<Coordinate>>  getShortestPathBiDi(double lat1,double lon1,double lat2, double lon2) throws Exception {

        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);

        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);

        BiDijkstra<Point2D, GeofabrikData> dj = new BiDijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
        dj.run(sourceNode,targetNode);
        double length = dj.shortestPathLength;

        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getPath();
        List<Coordinate> returnList = new ArrayList<>();
        
        returnList.add(new Coordinate(lat1,lon1));
        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
            returnList.add(EN2LatLon(p.getNodeData().getX(),p.getNodeData().getY()));
        }
        returnList.add(new Coordinate(lat2,lon2));
        /** return shortest path */
        
        return Triple.of(length, null, returnList);
    }
    

//    public static List<Coordinate>  getExploredNodesBiDi(double lat1,double lon1,double lat2, double lon2) throws Exception {
//
//        Coordinate source = LatLon2EN(lat1, lon1);
//        Coordinate target = LatLon2EN(lat2, lon2);
//
//        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
//
//        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
//        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);
//
//        BiDijkstra<Point2D, GeofabrikData> dj = new BiDijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
//        dj.run(sourceNode,targetNode);
//
//        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getExploredNodes(targetNode);
//
//        List<Coordinate> returnList_nodes = new ArrayList<Coordinate>();
//
//        returnList_nodes.add(new Coordinate(lat1,lon1));
//        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
//        	if (p != null) {
//                returnList_nodes.add(EN2LatLon(p.getNodeData().getX(), p.getNodeData().getY()));
//        	}
//        }
//        returnList_nodes.add(new Coordinate(lat2,lon2));
//        
//        return returnList_nodes;
//    }
    

    public static List< Triple<Double,Double,List<Coordinate>> >  getAlternativeRoutesBDV(double lat1,double lon1,double lat2, double lon2, 
    		int numPaths, double limSharing, double localOpt, double UBS) throws Exception {

        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);

        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);

        BDV<Point2D, GeofabrikData> dj = new BDV<Point2D, GeofabrikData>(graphHolder.getRoadGraph(), numPaths, limSharing, localOpt, UBS);
        
        dj.run(sourceNode,targetNode);

        List<Triple<Double, Double, AlternativePaths<Point2D, GeofabrikData>>> ALTpaths = dj.getPaths();

    	List< Triple<Double,Double,List<Coordinate>> > returnListAltnative = new ArrayList< Triple<Double,Double,List< Coordinate>> >();
    	for (Triple<Double, Double, AlternativePaths<Point2D, GeofabrikData>> alt : ALTpaths) {
            List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = alt.getRight().path;
            
            List<Coordinate> returnList = new ArrayList<>();
            returnList.add(new Coordinate(lat1,lon1));
	            for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
	                returnList.add(EN2LatLon(p.getNodeData().getX(),p.getNodeData().getY()));
	            }
            returnList.add(new Coordinate(lat2,lon2));
            returnListAltnative.add( Triple.of(alt.getLeft(), alt.getMiddle(), returnList) );
		}
    	
        return returnListAltnative;
        
    }
    

//    public static List<Coordinate>  getExploredNodesBDV(double lat1,double lon1,double lat2, double lon2) throws Exception {
//
//        Coordinate source = LatLon2EN(lat1, lon1);
//        Coordinate target = LatLon2EN(lat2, lon2);
//
//        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
//
//        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
//        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);
//
//        BDV<Point2D, GeofabrikData> dj = new BDV<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
//        dj.run(sourceNode,targetNode);
//
//        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getExploredNodes(targetNode);
//
//        List<Coordinate> returnList_nodes = new ArrayList<Coordinate>();
//
//        returnList_nodes.add(new Coordinate(lat1,lon1));
//        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
//        	if (p != null) {
//                returnList_nodes.add(EN2LatLon(p.getNodeData().getX(), p.getNodeData().getY()));
//        	}
//        }
//        returnList_nodes.add(new Coordinate(lat2,lon2));
//        
//        return returnList_nodes;
//    }

    public static List<TransportPath> getMultiModalRoute(double lat1, double lon1, double lat2, double lon2,
                                                         long time) throws Exception {
        // Similar to getShortestPath convert the coordinates and find the nearest node in the road graph
        /** Transform lat-lon to East-North */
        // You should use LatLon2EN to transform it.
        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);

        /** Get Graph Holder from Road Graph Holder */
        // Hint : RoadGraphHolder.getInstance() returns graph holder
        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
        /** Find The Nearest nodes in the graph */
        // Hint : RoadGraphHolder has function to find nearest Node 'findNearestPoint(Coordinate p)'
        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);
        /**
         * As you want to include the public transportation, take a look at the class MultiModalGraphHolder
         * Use the methods from MultiModalGraphHolder and MultiModalRouter
         */
        MultiModalGraphHolder mGraphHolder = MultiModalGraphHolder.getInstance();
        MultiModalRouter<GeofabrikData, GeofabrikData> router = mGraphHolder.getMultiModalGraph();
        router.setStarttime(time);

        List<DiGraph.DiGraphNode<IsoVertex, IsoEdge>> path = router.run(sourceNode, targetNode);

        // return the route using the method "multiModalPath2TransportPath"
        return multiModalPath2TransportPath(path);
    }

    /**
     * This Function Converts multiModalPath to TransportPath, because for Front-End
     */
    private static List<TransportPath> multiModalPath2TransportPath(List<DiGraph.DiGraphNode<IsoVertex, IsoEdge>> route) {
        List<TransportPath> transportPaths = new ArrayList<>();
        TransportPath transportPath = null;
        // loop trough all elements in route
        for (DiGraph.DiGraphNode<IsoVertex, IsoEdge> r : route) {
            /**
             * The nodes from the MultiModalRoute can be one of four instances:
             * - Point2D, TransferNode, ArrivalNode or DepartureNode
             * Each of them needs to be handled a little different
             */
            if(r.getNodeData() instanceof Point2D) {
                /**
                 *  If the node is a Point2D, convert the coordinates to LatLon
                 *  and create a new TransportPath with the condition "walk"
                 */
                Point2D walkNode = (Point2D) r.getNodeData();
                Coordinate c = EN2LatLon(walkNode.getX(),walkNode.getY());
                transportPath = new TransportPath(c,null,null,null,"walk");
            }
            /**
             *  If the node is a TransferNode, continue
             */
            else if(r.getNodeData() instanceof TransferNode){
                continue;
            }
            /**
             *  If the node is a ArrivalNode, get the node in the node from the road network,
             *  convert it to Point2D, convert the coordinates to LatLon and create a
             *  new TransportPath with the condition "arrival"
             */
            else if (r.getNodeData() instanceof ArrivalNode) {
                ArrivalNode arrivalNode = (ArrivalNode) r.getNodeData();
                Point2D point = (Point2D) arrivalNode.getNextStreetNode().getNodeData();
                Coordinate c = EN2LatLon(point.getX(),point.getY());
                transportPath =  new TransportPath(c,arrivalNode.getTime(),arrivalNode.getName(),arrivalNode.getRoute_name(),"arrival");
            }
            /**
             *  If the node is a DepartureNode, check if there are other nodes in the result list already
             *  and if the last transportPath has the condition "arrival" -> If so, continue
             */
            else if (r.getNodeData() instanceof DepartureNode) {
                /**
                 *  Get the node data and the last point from the result list and
                 *  create a new TransportPath using the coordinates from the last point and the condition departure
                 */
                TransportPath previousTransportPath = transportPaths.get(transportPaths.size() - 1);
                if (previousTransportPath.getCondition().equals("arrival")) {
                    continue;
                }
                DepartureNode departureNode = (DepartureNode) r.getNodeData();
                transportPath = new TransportPath(previousTransportPath.getCoordinate(),departureNode.getTime(),departureNode.getName(), departureNode.getTripId(), "departure");
            }
            // append the result list
            transportPaths.add(transportPath);
        }
		return transportPaths;
    }

    public static List<List<TransportPath>> getAlternativeRoutes(double lat1, double lon1, double lat2, double lon2,
                                                             long time) throws Exception{

        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);

        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();

        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);

        MultiModalGraphHolder mGraphHolder = MultiModalGraphHolder.getInstance();
        MultiModalRouter<GeofabrikData, GeofabrikData> router = mGraphHolder.getMultiModalGraph();
        router.setStarttime(time);

        List<DiGraph.DiGraphNode<IsoVertex, IsoEdge>> path = router.run(sourceNode, targetNode);

        List<List<TransportPath>> ListListTransportPath = new ArrayList<List<TransportPath>>();
        ListListTransportPath.add(multiModalPath2TransportPath(path));
        
        return ListListTransportPath;

    }

    public static Coordinate LatLon2EN (double lat, double lon){
        CRS epsg3044 = CRS.fromEpsgCode(3044);
        CRS wgs84 = CRS.fromEpsgCode(4326);
        var fromWgs84 = Transform.apply(wgs84, epsg3044);
        Tuple2<Object, Object> latlon2EN = fromWgs84.apply(lon  , lat);
        
        return new Coordinate((double) latlon2EN._1(),(double) latlon2EN._2());
    }

    public static Coordinate EN2LatLon (double x,double y){
        CRS epsg3044 = CRS.fromEpsgCode(3044);
        CRS wgs84 = CRS.fromEpsgCode(4326);
        var toWgs84 = Transform.apply(epsg3044, wgs84);
        Tuple2<Object, Object> EN2LatLon = toWgs84.apply(x,y);

        return new Coordinate((double)EN2LatLon._2(),(double)EN2LatLon._1());
    }
}
