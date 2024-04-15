package com.lbs.lbs.Service;

import geotrellis.proj4.CRS;
import geotrellis.proj4.Transform;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.util.List;

@Service
public class ShortestPathService {

    public static List<Coordinate>  getShortestPath(double lat1,double lon1,double lat2, double lon2){

        /** Transform lat-lon to East-North */
            // You should use LatLon2EN to transform it.

        /** Get Graph Holder from Road Graph Holder */
            // Hint : RoadGraphHolder.getInstance() returns graph holder

        /** Find The Nearest nodes in the graph */
            // Hint : RoadGraphHolder has function to find nearest Node 'findNearestPoint(Coordinate p)'

        /** Create Dijkstra shortest path class with your graph and run*/
            // Dijkstra class path is Base/routing/Dijkstra, check the construction parameters
            // and find how to run Dijkstra

        /** get shortest path  and convert to coordinate List (List<Coordinate>)  As Latitude Longitude*/
            //

        /** return shortest path */

        return  null;
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
