package com.lbs.lbs.Service;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.DiGraph.DiGraphNode;
import com.lbs.lbs.Base.graph.types.AlternativePaths;
import com.lbs.lbs.Base.graph.types.RoadGraph;
import com.lbs.lbs.Base.graph.types.multimodal.*;
import com.lbs.lbs.Base.io.GeofabrikFactory;
import com.lbs.lbs.Base.io.ShapeFileReader;
import com.lbs.lbs.Base.routing.Dijkstra;
import com.lbs.lbs.Base.routing.BiDijkstra;
import com.lbs.lbs.Base.routing.BDV;
import com.lbs.lbs.Base.routing.MultiModalRouter;
import com.lbs.lbs.Base.routing.Router;
import com.lbs.lbs.DataSingulation.MultiModalGraphHolder;
import com.lbs.lbs.DataSingulation.RoadGraphHolder;
import com.lbs.lbs.Entity.TransportPath;
import com.vividsolutions.jump.io.IllegalParametersException;
import geotrellis.proj4.CRS;
import geotrellis.proj4.Transform;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.awt.geom.Point2D;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ShortestPathService {

    public static List<Coordinate>  getShortestPath(double lat1,double lon1,double lat2, double lon2) throws Exception {

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

        Dijkstra<Point2D, GeofabrikData> dj = new Dijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
        
        dj.run(sourceNode,targetNode);
//        dj.run(targetNode,sourceNode);
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
    

    public static List<Coordinate>  getExploredNodes(double lat1,double lon1,double lat2, double lon2) throws Exception {

        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);

        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();

        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);

        Dijkstra<Point2D, GeofabrikData> dj = new Dijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
        double length = dj.run(sourceNode,targetNode);
//        System.out.println(length);
        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getExploredNodes(targetNode);

        List<Coordinate> returnList_nodes = new ArrayList<Coordinate>();

        returnList_nodes.add(new Coordinate(lat1,lon1));
        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
        	if (p != null) {
                returnList_nodes.add(EN2LatLon(p.getNodeData().getX(), p.getNodeData().getY()));
        	}
        }
        returnList_nodes.add(new Coordinate(lat2,lon2));
        
        return returnList_nodes;
    }

    public static List<Coordinate>  getShortestPathBiDi(double lat1,double lon1,double lat2, double lon2) throws Exception {

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

        BiDijkstra<Point2D, GeofabrikData> dj = new BiDijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
        
        dj.run(sourceNode,targetNode);
//        dj.run(targetNode,sourceNode);
        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getPath();
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
    

    public static List<Coordinate>  getExploredNodesBiDi(double lat1,double lon1,double lat2, double lon2) throws Exception {

        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);

        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();

        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);

        BiDijkstra<Point2D, GeofabrikData> dj = new BiDijkstra<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
        double length = dj.run(sourceNode,targetNode);
//        System.out.println(length);
        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getExploredNodes(targetNode);

        List<Coordinate> returnList_nodes = new ArrayList<Coordinate>();

        returnList_nodes.add(new Coordinate(lat1,lon1));
        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
        	if (p != null) {
                returnList_nodes.add(EN2LatLon(p.getNodeData().getX(), p.getNodeData().getY()));
        	}
        }
        returnList_nodes.add(new Coordinate(lat2,lon2));
        
        return returnList_nodes;
    }
    

    public static List<List<Coordinate>>  getAlternativeRoutesBDV(double lat1,double lon1,double lat2, double lon2) throws Exception {

        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);

        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();
        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);

        BDV<Point2D, GeofabrikData> dj = new BDV<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
        
        dj.run(sourceNode,targetNode);
//        dj.run(targetNode,sourceNode);
        List<AlternativePaths<Point2D, GeofabrikData>> ALTpaths = dj.getPaths();
        System.out.println("size = " + ALTpaths.size());

        // --> The shortest path
        AlternativePaths<Point2D, GeofabrikData> ALTpath1 = ALTpaths.get(0);
        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path1 = ALTpath1.path;
        
        List<Coordinate> returnList1 = new ArrayList<>();
        returnList1.add(new Coordinate(lat1,lon1));
        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path1){
            returnList1.add(EN2LatLon(p.getNodeData().getX(),p.getNodeData().getY()));
        }
        returnList1.add(new Coordinate(lat2,lon2));
        
        
        // --> The between shortest and longest of shortest paths.
        AlternativePaths<Point2D, GeofabrikData> ALTpath2 = ALTpaths.get((int) (ALTpaths.size()/2));
        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path2 = ALTpath2.path;
        
        List<Coordinate> returnList2 = new ArrayList<>();
        returnList2.add(new Coordinate(lat1,lon1));
        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path2){
            returnList2.add(EN2LatLon(p.getNodeData().getX(),p.getNodeData().getY()));
        }
        returnList2.add(new Coordinate(lat2,lon2));

        
        // --> The longest shortest path
        AlternativePaths<Point2D, GeofabrikData> ALTpath3 = ALTpaths.get(ALTpaths.size() - 1);
        System.out.println("ALTpaths.size() - 1 = " + (ALTpaths.size() - 1));
        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path3 = ALTpath3.path;
        
        List<Coordinate> returnList3 = new ArrayList<>();
        returnList3.add(new Coordinate(lat1,lon1));
        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path3){
            returnList3.add(EN2LatLon(p.getNodeData().getX(),p.getNodeData().getY()));
        }
        returnList3.add(new Coordinate(lat2,lon2));

        List<List<Coordinate>> returnListAltnative = new ArrayList<List<Coordinate>>();
        returnListAltnative.add(returnList1);
        returnListAltnative.add(returnList2);
        returnListAltnative.add(returnList3);
        
        return returnListAltnative;
    }
    

    public static List<Coordinate>  getExploredNodesBDV(double lat1,double lon1,double lat2, double lon2) throws Exception {

        Coordinate source = LatLon2EN(lat1, lon1);
        Coordinate target = LatLon2EN(lat2, lon2);

        RoadGraphHolder graphHolder = RoadGraphHolder.getInstance();

        DiGraph.DiGraphNode<Point2D, GeofabrikData> sourceNode = graphHolder.findNearestPoint(source);
        DiGraph.DiGraphNode<Point2D, GeofabrikData> targetNode = graphHolder.findNearestPoint(target);

        BDV<Point2D, GeofabrikData> dj = new BDV<Point2D, GeofabrikData>(graphHolder.getRoadGraph());
        double length = dj.run(sourceNode,targetNode);
//        System.out.println(length);
        List<DiGraph.DiGraphNode<Point2D, GeofabrikData>> path = dj.getExploredNodes(targetNode);

        List<Coordinate> returnList_nodes = new ArrayList<Coordinate>();

        returnList_nodes.add(new Coordinate(lat1,lon1));
        for(DiGraph.DiGraphNode<Point2D, GeofabrikData> p : path){
        	if (p != null) {
                returnList_nodes.add(EN2LatLon(p.getNodeData().getX(), p.getNodeData().getY()));
        	}
        }
        returnList_nodes.add(new Coordinate(lat2,lon2));
        
        return returnList_nodes;
    }

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
                                                             long time){

        return null;
    }

    public static Coordinate LatLon2EN (double lat, double lon){
        Coordinate coordinate = new Coordinate();
        Date time = new Date();
        TransportPath transportPath = new TransportPath(coordinate, time, "name","routeName","condition");
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
