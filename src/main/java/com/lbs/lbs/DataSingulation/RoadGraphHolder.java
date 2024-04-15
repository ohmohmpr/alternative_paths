package com.lbs.lbs.DataSingulation;

import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.types.RoadGraph;
import com.lbs.lbs.Base.graph.types.multimodal.GeofabrikData;
import com.lbs.lbs.Base.io.GeofabrikFactory;
import com.lbs.lbs.Base.io.ShapeFileReader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.strtree.ItemBoundable;
import org.locationtech.jts.index.strtree.ItemDistance;
import org.locationtech.jts.index.strtree.STRtree;
import org.springframework.core.io.ClassPathResource;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

public class RoadGraphHolder {
    static final String path;

    static {
        try {
            path = new ClassPathResource("static/").getFile().getPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RoadGraph<Point2D, GeofabrikData> roadGraph;
    private STRtree roadTree;
    private static RoadGraphHolder instance;

    public static RoadGraphHolder getInstance() {
        if(instance==null){
            System.out.println("graph reading");
            instance = getRoadGraphHolder();
        }
        return instance;
    }

    private static RoadGraphHolder getRoadGraphHolder(){
        RoadGraphHolder roadGraphHolder = new RoadGraphHolder();

        File roadShape = new File(path+"/road/bonnUTM.shp");
        RoadGraph<Point2D, GeofabrikData> roadGraph = null;
        try{
            GeofabrikFactory gff = new GeofabrikFactory();
            roadGraph = ShapeFileReader.importFromSHP(roadShape, gff);
            ShapeFileReader.reduceToBiggestComponent(roadGraph);
            roadGraphHolder.setRoadGraph(roadGraph);
        } catch (Exception e) {
            e.printStackTrace();
        }

        STRtree strTree = new STRtree();
        for (DiGraph.DiGraphNode<Point2D, GeofabrikData> point : roadGraph.getNodes()) {
            Point2D node = point.getNodeData();
            strTree.insert(new Envelope(node.getX(),node.getX(),node.getY(),node.getY()),point);
        }
        strTree.build();
        roadGraphHolder.setRoadTree(strTree);
        return roadGraphHolder;
    }
    public DiGraph.DiGraphNode<Point2D, GeofabrikData> findNearestPoint(Coordinate p) {
        /**
         * p is given coordinates
         * The Spatial Reference Identifier (SSID) of the given coordinates must be the same as the Road Graph Nodes.
         * This function finds the closest Graph Node to given coordinates in log(n) time complexity **/

        Coordinate queryPoint = new Coordinate(p.x, p.y);
        Envelope queryEnvelope = new Envelope(queryPoint);
        Object nearestObj = roadTree.nearestNeighbour(queryEnvelope, queryPoint, new PointDistance());

        if (nearestObj instanceof DiGraph.DiGraphNode) {
            return (DiGraph.DiGraphNode<Point2D, GeofabrikData>) nearestObj;
        }
        return null;
    }
    private class PointDistance implements ItemDistance {
        @Override
        public double distance(ItemBoundable itemBoundable1, ItemBoundable itemBoundable2) {
            Envelope env1 = (Envelope) itemBoundable1.getBounds();
            Envelope env2 = (Envelope) itemBoundable2.getBounds();

            // Envelope represents Point, so min and max values are same.
            double centerX1 = env1.getMaxX();
            double centerY1 = env1.getMaxY();
            double centerX2 = env2.getMaxX();
            double centerY2 = env2.getMaxY();

            // Calculate the Euclidean distance between the centers
            return Math.hypot(centerX1-centerX2,centerY1-centerY2);
        }
    }

    public RoadGraph<Point2D, GeofabrikData> getRoadGraph() {
        return roadGraph;
    }


    public static void setInstance(RoadGraphHolder instance) {
        RoadGraphHolder.instance = instance;
    }

    public void setRoadGraph(RoadGraph<Point2D, GeofabrikData> roadGraph) {
        this.roadGraph = roadGraph;
    }

    public void setRoadTree(STRtree roadTree) {
        this.roadTree = roadTree;
    }

    public STRtree getRoadTree() {
        return roadTree;
    }
}
