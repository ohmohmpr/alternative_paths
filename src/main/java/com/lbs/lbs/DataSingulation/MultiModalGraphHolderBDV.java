package com.lbs.lbs.DataSingulation;

import com.lbs.lbs.Base.graph.types.RoadGraph;
import com.lbs.lbs.Base.graph.types.multimodal.GeofabrikData;
import com.lbs.lbs.Base.routing.MultiModalRouterBDV;
import com.lbs.lbs.Base.routing.Router;
import org.springframework.core.io.ClassPathResource;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

public class MultiModalGraphHolderBDV {
    static final String path;

    static {
        try {
            path = new ClassPathResource("static/").getFile().getPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MultiModalRouterBDV<GeofabrikData, GeofabrikData> multiModalGraphBDV;
    private static MultiModalGraphHolderBDV instance;

    public static MultiModalGraphHolderBDV getInstance() throws Exception {

        if(instance == null){
            try {
                instance = getMultiModalGraphHolder();
            } catch (Exception e){
                throw e;
            }
        }
        return instance;
    }
    private static MultiModalGraphHolderBDV getMultiModalGraphHolder() throws Exception {
        try{
	        File gtfsDir = new File(path + "/gtfs/");

	        RoadGraph<Point2D, GeofabrikData> roadGraph = RoadGraphHolder.getInstance().getRoadGraph();
	
	        MultiModalRouterBDV<GeofabrikData, GeofabrikData> router = new MultiModalRouterBDV<>(roadGraph, gtfsDir,
	                Router.GEOFABRIK_FACTORY);
	        MultiModalGraphHolderBDV multiModalGraphHolder = new MultiModalGraphHolderBDV();
	        multiModalGraphHolder.setMultiModalGraph(router);
	        return multiModalGraphHolder;
        } catch (Exception e){
            throw e;
        }
    }

    public MultiModalRouterBDV<GeofabrikData, GeofabrikData> getMultiModalGraph() {
        return multiModalGraphBDV;
    }

    public static void setInstance(MultiModalGraphHolderBDV instance) {
        MultiModalGraphHolderBDV.instance = instance;
    }

    public void setMultiModalGraph(MultiModalRouterBDV<GeofabrikData, GeofabrikData> multiModalGraphBDV) {
        this.multiModalGraphBDV = multiModalGraphBDV;
    }
}
