package com.lbs.lbs.Service;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.multimodal.*;
import com.lbs.lbs.Base.routing.Dijkstra;
import com.lbs.lbs.Base.routing.MultiModalRouter;
import com.lbs.lbs.DataSingulation.MultiModalGraphHolder;
import com.lbs.lbs.DataSingulation.RoadGraphHolder;
import com.lbs.lbs.Entity.TransportPath;
import geotrellis.proj4.CRS;
import geotrellis.proj4.Transform;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ShortestPathService {

    public static List<Coordinate>  getShortestPath(double lat1,double lon1,double lat2, double lon2){

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

        /** Create Dijkstra shortest path class with your graph and run*/
            // Dijkstra class path is Base/routing/Dijkstra, check the construction parameters
            // and find how to run Dijkstra

        Dijkstra dj = new Dijkstra(graphHolder.getRoadGraph());
        dj.run(sourceNode,targetNode);
        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getPath(targetNode);
        List<Coordinate> returnList = new ArrayList<>();
        returnList.add(new Coordinate(lat1,lon1));
        /** get shortest path  and convert to coordinate List (List<Coordinate>)  As Latitude Longitude*/
        //
        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
            returnList.add(EN2LatLon(p.getNodeData().getX(),p.getNodeData().getY()));
        }
        returnList.add(new Coordinate(lat2,lon2));
        /** return shortest path */
        return returnList;




    }



    public static List<TransportPath> getMultiModalRoute(double lat1, double lon1, double lat2, double lon2,
                                                         long time) throws Exception {
        // Similar to getShortestPath convert the coordinates and find the nearest node in the road graph

        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);
        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);
        /**
         * As you want to include the public transportation, take a look at the class MultiModalGraphHolder
         * Use the methods from MultiModalGraphHolder and MultiModalRouter
         */
        
        MultiModalGraphHolder multiGraphHolder = MultiModalGraphHolder.getInstance();
        MultiModalRouter<GeofabrikData, GeofabrikData> router = multiGraphHolder.getMultiModalGraph();
        
        router.setStarttime(time);
        List<DiGraph.DiGraphNode<IsoVertex, IsoEdge>>  path = router.run(sourceNode,targetNode);

         // return the route using the method "multiModalPath2TransportPath"
//        for (DiGraph.DiGraphNode<IsoVertex, IsoEdge> p : path) {
//        	System.out.print(p);
//        }
        return multiModalPath2TransportPath(path);
    }

    /**
     * This Function Converts multiModalPath to TransportPath, because for Front-End
     */
    private static List<TransportPath> multiModalPath2TransportPath(List<DiGraph.DiGraphNode<IsoVertex, IsoEdge>> route) {
        List<TransportPath> transportPaths = new ArrayList<>();
        TransportPath transportPath = null;
        // loop through all elements in route
        for (DiGraph.DiGraphNode<IsoVertex, IsoEdge> r: route) {
            /**
             * The nodes from the MultiModalRoute can be one of four instances:
             * - Point2D, TransferNode, ArrivalNode or DepartureNode
             * Each of them needs to be handled a little different
             */
        	if (r.getNodeData() instanceof Point2D) {
                /**
                 *  If the node is a Point2D, convert the coordinates to LatLon
                 *  and create a new TransportPath with the condition "walk"
                 */
        		Point2D walkNode = (Point2D) r.getNodeData();
        		Coordinate c = EN2LatLon(walkNode.getX(), walkNode.getY());
        		transportPath = new TransportPath(c, null, null, null, "walk");
        	} else if (r.getNodeData() instanceof TransferNode){
                /**
                 *  If the node is a TransferNode, continue
                 */
        		continue;
        	} else if (r.getNodeData() instanceof ArrivalNode) {
                /**
                 *  If the node is a ArrivalNode, get the node in the node from the road network,
                 *  convert it to Point2D, convert the coordinates to LatLon and create a
                 *  new TransportPath with the condition "arrival"
                 */
        		ArrivalNode arrivalNode = (ArrivalNode) r.getNodeData();
        		Point2D point = (Point2D) arrivalNode.getNextStreetNode().getNodeData();
        		Coordinate c = EN2LatLon(point.getX(), point.getY());
        		transportPath = new TransportPath(c, arrivalNode.getTime(), arrivalNode.getName(), arrivalNode.getRoute_name(), "arrival");
        	} else if (r.getNodeData() instanceof DepartureNode) {
                /**
                 *  If the node is a DepartureNode, check if there are other nodes in the result list already
                 *  and if the last transportPath has the condition "arrival" -> If so, continue
                 */
        		TransportPath previousTransportPath = transportPaths.get(transportPaths.size() - 1);
        		if (previousTransportPath.getCondition().equals("arrival")) {
        			continue;
        		}
        		DepartureNode departureNode = (DepartureNode) r.getNodeData();
        		transportPath = new TransportPath(previousTransportPath.getCoordinate(), departureNode.getTime(), departureNode.getName(), departureNode.getTripId(), "departure");
        	}
            /**
             *  Get the node data and the last point from the result list and
             *  create a new TransportPath using the coordinates from the last point and the condition departure
             */
            // append the result list
            transportPaths.add(transportPath);
        }


    // return
		return transportPaths;
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
