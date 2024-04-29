package com.lbs.lbs.DataSingulation;

import com.lbs.lbs.Base.graph.types.RoadGraph;
import com.lbs.lbs.Base.graph.types.multimodal.GeofabrikData;
import com.lbs.lbs.Base.routing.MultiModalRouter;
import com.lbs.lbs.Base.routing.Router;
import org.springframework.core.io.ClassPathResource;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

public class MultiModalGraphHolder {
    static final String path;

    static {
        try {
            path = new ClassPathResource("static/").getFile().getPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private MultiModalRouter<GeofabrikData, GeofabrikData> multiModalGraph;
    private static MultiModalGraphHolder instance;

    public static MultiModalGraphHolder getInstance() {

        if(instance == null){
            try{

                instance = getMultiModalGraphHolder();
            }catch (Exception e){
                System.out.printf("Failed to Create Multimodal Graph : " +e.getMessage());
            }
        }
        return instance;
    }
    private static MultiModalGraphHolder getMultiModalGraphHolder() throws Exception {

        File gtfsDir = new File(path + "/gtfs/");


        RoadGraph<Point2D, GeofabrikData> roadGraph = RoadGraphHolder.getInstance().getRoadGraph();

        MultiModalRouter<GeofabrikData, GeofabrikData> router = new MultiModalRouter<>(roadGraph, gtfsDir,
                Router.GEOFABRIK_FACTORY);
        MultiModalGraphHolder multiModalGraphHolder = new MultiModalGraphHolder();
        multiModalGraphHolder.setMultiModalGraph(router);
        return multiModalGraphHolder;
    }

    public MultiModalRouter<GeofabrikData, GeofabrikData> getMultiModalGraph() {
        return multiModalGraph;
    }

    public static void setInstance(MultiModalGraphHolder instance) {
        MultiModalGraphHolder.instance = instance;
    }

    public void setMultiModalGraph(MultiModalRouter<GeofabrikData, GeofabrikData> multiModalGraph) {
        this.multiModalGraph = multiModalGraph;
    }
}
