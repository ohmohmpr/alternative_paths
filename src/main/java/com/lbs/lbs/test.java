package com.lbs.lbs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.lbs.Base.graph.DiGraph;
import com.lbs.lbs.Base.graph.types.multimodal.ArrivalNode;
import com.lbs.lbs.Base.graph.types.multimodal.DepartureNode;
import com.lbs.lbs.Base.graph.types.multimodal.IsoEdge;
import com.lbs.lbs.Base.graph.types.multimodal.IsoVertex;
import com.lbs.lbs.Entity.TransportPath;
import com.lbs.lbs.Service.ShortestPathService;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) throws Exception {

        double lat1 = 50.7426687;
        double lon1 = 7.0476567;

        double lat2 = 50.7371319;
        double lon2 = 7.1523725;

        List<TransportPath> f = ShortestPathService.getMultiModalRoute(lat1, lon1, lat2, lon2, 36000);
        for (TransportPath p : f ){
            System.out.println(p);
        }


    }

}
