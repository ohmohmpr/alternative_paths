package com.lbs.lbs.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lbs.lbs.Entity.TransportPath;
import com.lbs.lbs.Service.ShortestPathService;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@CrossOrigin
@RequestMapping("/ex1")
public class ShortestPathController {

    @GetMapping("/static")
    public ResponseEntity<List<Coordinate>> staticShortestPathController(){


        String json = "[{\"x\":50.7256469,\"y\":7.0914916,\"z\":\"NaN\"},{\"x\":50.725741001091336,\"y\":7.091710999974367,\"z\":\"NaN\"},{\"x\":50.72587350109135,\"y\":7.091867499974368,\"z\":\"NaN\"},{\"x\":50.72620890109136,\"y\":7.092264099974375,\"z\":\"NaN\"},{\"x\":50.726312601091344,\"y\":7.092386799974377,\"z\":\"NaN\"},{\"x\":50.726361101091356,\"y\":7.092439499974376,\"z\":\"NaN\"},{\"x\":50.72640630109136,\"y\":7.092485799974378,\"z\":\"NaN\"},{\"x\":50.72642160109135,\"y\":7.092499399974377,\"z\":\"NaN\"},{\"x\":50.726551201091354,\"y\":7.092616799974379,\"z\":\"NaN\"},{\"x\":50.72660860109134,\"y\":7.09266649997438,\"z\":\"NaN\"},{\"x\":50.72674390109137,\"y\":7.092783499974381,\"z\":\"NaN\"},{\"x\":50.726971401091355,\"y\":7.092987199974383,\"z\":\"NaN\"},{\"x\":50.727286901091375,\"y\":7.093282799974387,\"z\":\"NaN\"},{\"x\":50.72830760109139,\"y\":7.094225099974398,\"z\":\"NaN\"},{\"x\":50.72882890109139,\"y\":7.094714999974404,\"z\":\"NaN\"},{\"x\":50.7289704010914,\"y\":7.094847499974405,\"z\":\"NaN\"},{\"x\":50.7290744010914,\"y\":7.094918499974407,\"z\":\"NaN\"},{\"x\":50.729189201091394,\"y\":7.0949805999744076,\"z\":\"NaN\"},{\"x\":50.7292942010914,\"y\":7.095011299974408,\"z\":\"NaN\"},{\"x\":50.729365301091406,\"y\":7.095029999974407,\"z\":\"NaN\"},{\"x\":50.7295385010914,\"y\":7.095034199974407,\"z\":\"NaN\"},{\"x\":50.72978430109139,\"y\":7.095022199974407,\"z\":\"NaN\"},{\"x\":50.7301437010914,\"y\":7.0950045999744065,\"z\":\"NaN\"},{\"x\":50.7302318010914,\"y\":7.095000199974406,\"z\":\"NaN\"},{\"x\":50.73030290109139,\"y\":7.094995299974405,\"z\":\"NaN\"},{\"x\":50.73036500109139,\"y\":7.094994299974405,\"z\":\"NaN\"},{\"x\":50.73043550109139,\"y\":7.094989899974404,\"z\":\"NaN\"},{\"x\":50.730505901091384,\"y\":7.094985199974405,\"z\":\"NaN\"},{\"x\":50.73197050109141,\"y\":7.094896999974402,\"z\":\"NaN\"},{\"x\":50.73215660109141,\"y\":7.094961199974403,\"z\":\"NaN\"},{\"x\":50.73232260109141,\"y\":7.0949721999744035,\"z\":\"NaN\"},{\"x\":50.7324105010914,\"y\":7.094974699974402,\"z\":\"NaN\"},{\"x\":50.7324798010914,\"y\":7.0949540999744025,\"z\":\"NaN\"},{\"x\":50.7325593010914,\"y\":7.094908799974402,\"z\":\"NaN\"},{\"x\":50.7326528010914,\"y\":7.094813899974399,\"z\":\"NaN\"},{\"x\":50.73270560109141,\"y\":7.095025199974404,\"z\":\"NaN\"},{\"x\":50.73274630109141,\"y\":7.095000399974402,\"z\":\"NaN\"},{\"x\":50.732804901091406,\"y\":7.094980899974402,\"z\":\"NaN\"},{\"x\":50.7328340010914,\"y\":7.0949999999744024,\"z\":\"NaN\"},{\"x\":50.7328592010914,\"y\":7.0949901999744025,\"z\":\"NaN\"},{\"x\":50.73287300109142,\"y\":7.094948299974401,\"z\":\"NaN\"},{\"x\":50.732945801091404,\"y\":7.0950402999744036,\"z\":\"NaN\"},{\"x\":50.73315610109141,\"y\":7.0947730999744,\"z\":\"NaN\"},{\"x\":50.7332013010914,\"y\":7.094637199974398,\"z\":\"NaN\"},{\"x\":50.73321540109139,\"y\":7.0946501999743985,\"z\":\"NaN\"},{\"x\":50.733243001091395,\"y\":7.0947778999744004,\"z\":\"NaN\"},{\"x\":50.73324550109141,\"y\":7.0947891999744,\"z\":\"NaN\"},{\"x\":50.73325570109141,\"y\":7.094820799974401,\"z\":\"NaN\"},{\"x\":50.7332607010914,\"y\":7.0948338999744,\"z\":\"NaN\"},{\"x\":50.733269001091394,\"y\":7.094853699974401,\"z\":\"NaN\"},{\"x\":50.7332925010914,\"y\":7.0948881999744,\"z\":\"NaN\"},{\"x\":50.73331950109141,\"y\":7.094920199974401,\"z\":\"NaN\"},{\"x\":50.73336650109139,\"y\":7.094927899974401,\"z\":\"NaN\"},{\"x\":50.733362501091406,\"y\":7.095417199974407,\"z\":\"NaN\"},{\"x\":50.73335250109141,\"y\":7.095508299974409,\"z\":\"NaN\"},{\"x\":50.73350540109141,\"y\":7.095580999974409,\"z\":\"NaN\"},{\"x\":50.73396320109142,\"y\":7.095816399974412,\"z\":\"NaN\"},{\"x\":50.73393800109144,\"y\":7.095931599974414,\"z\":\"NaN\"},{\"x\":50.73375670109144,\"y\":7.096738599974425,\"z\":\"NaN\"},{\"x\":50.73372400109144,\"y\":7.0968844999744265,\"z\":\"NaN\"},{\"x\":50.73370170109143,\"y\":7.096983699974429,\"z\":\"NaN\"},{\"x\":50.733930401091435,\"y\":7.09716769997443,\"z\":\"NaN\"},{\"x\":50.73403660109145,\"y\":7.097261099974429,\"z\":\"NaN\"},{\"x\":50.73405290109144,\"y\":7.097209599974431,\"z\":\"NaN\"},{\"x\":50.73454390109144,\"y\":7.097716399974438,\"z\":\"NaN\"},{\"x\":50.73511900109146,\"y\":7.098496799974446,\"z\":\"NaN\"},{\"x\":50.73520090109147,\"y\":7.098362099974444,\"z\":\"NaN\"},{\"x\":50.735235201091456,\"y\":7.0984687999744445,\"z\":\"NaN\"},{\"x\":50.73525650109146,\"y\":7.098517499974444,\"z\":\"NaN\"},{\"x\":50.73527270109146,\"y\":7.098549099974446,\"z\":\"NaN\"},{\"x\":50.73527930109146,\"y\":7.098557699974448,\"z\":\"NaN\"},{\"x\":50.73528330109145,\"y\":7.098562899974447,\"z\":\"NaN\"},{\"x\":50.73542710109146,\"y\":7.098422199974445,\"z\":\"NaN\"},{\"x\":50.73562900109146,\"y\":7.098279199974442,\"z\":\"NaN\"},{\"x\":50.73579200109147,\"y\":7.098270699974441,\"z\":\"NaN\"},{\"x\":50.73570430109147,\"y\":7.0988813999744504,\"z\":\"NaN\"},{\"x\":50.73561350109147,\"y\":7.099022799974452,\"z\":\"NaN\"},{\"x\":50.73553890109147,\"y\":7.099138199974454,\"z\":\"NaN\"},{\"x\":50.735444301091476,\"y\":7.099283499974455,\"z\":\"NaN\"},{\"x\":50.735383401091475,\"y\":7.099383899974457,\"z\":\"NaN\"},{\"x\":50.73532320109148,\"y\":7.099480199974459,\"z\":\"NaN\"},{\"x\":50.7353521,\"y\":7.099526,\"z\":\"NaN\"}] ";

        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Coordinate> coordinates = mapper.readValue(json, new TypeReference<List<Coordinate>>(){});
            return new ResponseEntity(coordinates, HttpStatus.OK);
        } catch (JsonMappingException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);

        } catch (JsonProcessingException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    @GetMapping("/shortestpath")
    public ResponseEntity<List<Coordinate>> simpleShortestPathController(@RequestParam double lat1,@RequestParam double lon1,@RequestParam double lat2,@RequestParam double lon2){
        /**
         * This Function's inputs are latitude and longitude, represents source and target locations
         * returns list of the shortest path coordinates as latitude and longitude*/

        try {
            List<Coordinate> path = ShortestPathService.getShortestPath(lat1, lon1, lat2, lon2);
            return new ResponseEntity(path, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }

    }
    

    @GetMapping("/explorednodes")
    public ResponseEntity<List<Coordinate>> exploredNodesController(@RequestParam double lat1,@RequestParam double lon1,@RequestParam double lat2,@RequestParam double lon2){
        try {
            List<Coordinate> path = ShortestPathService.getExploredNodes(lat1, lon1, lat2, lon2);
        	System.out.println(HttpStatus.OK);
            return new ResponseEntity(path, HttpStatus.OK);
        } catch (Exception e){
        	System.out.println(e);
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }


    @GetMapping("/shortestpathbidi")
    public ResponseEntity<List<Coordinate>> simpleShortestPathBiDiController(@RequestParam double lat1,@RequestParam double lon1,@RequestParam double lat2,@RequestParam double lon2){
        /**
         * This Function's inputs are latitude and longitude, represents source and target locations
         * returns list of the shortest path coordinates as latitude and longitude*/
        try {
            List<Coordinate> path = ShortestPathService.getShortestPathBiDi(lat1, lon1, lat2, lon2);
            return new ResponseEntity(path, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    

    @GetMapping("/explorednodesbidi")
    public ResponseEntity<List<Coordinate>> getExploredNodesBiDi(@RequestParam double lat1,@RequestParam double lon1,@RequestParam double lat2,@RequestParam double lon2){
        try {
            List<Coordinate> path = ShortestPathService.getExploredNodesBiDi(lat1, lon1, lat2, lon2);
        	System.out.println(HttpStatus.OK);
            return new ResponseEntity(path, HttpStatus.OK);
        } catch (Exception e){
        	System.out.println(e);
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

//    @GetMapping("/shortestpathbdv")
//    public ResponseEntity<List<Coordinate>> simpleShortestPathBDVController(@RequestParam double lat1,@RequestParam double lon1,@RequestParam double lat2,@RequestParam double lon2){
//        /**
//         * This Function's inputs are latitude and longitude, represents source and target locations
//         * returns list of the shortest path coordinates as latitude and longitude*/
//
//        try {
//            List<Coordinate> path = ShortestPathService.getShortestPathBDV(lat1, lon1, lat2, lon2);
//            return new ResponseEntity(path, HttpStatus.OK);
//        } catch (Exception e){
//            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
//        }
//
//    }

    @GetMapping("/alternativebdv")
    public ResponseEntity<List<List<TransportPath>>> simpleShortestPathBDVController(@RequestParam double lat1,@RequestParam double lon1,@RequestParam double lat2,@RequestParam double lon2){
        /**
         * This Function's inputs are latitude and longitude, represents source and target locations
         * returns list of the shortest path coordinates as latitude and longitude*/

        try {
            List<List<Coordinate>> alt = ShortestPathService.getAlternativeRoutesBDV(lat1, lon1, lat2, lon2);
            System.out.println(alt.size());
            return new ResponseEntity(alt, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @GetMapping("/explorednodesbdv")
    public ResponseEntity<List<Coordinate>> getExploredNodesBDV(@RequestParam double lat1,@RequestParam double lon1,@RequestParam double lat2,@RequestParam double lon2){
        try {
            List<Coordinate> path = ShortestPathService.getExploredNodesBDV(lat1, lon1, lat2, lon2);
        	System.out.println(HttpStatus.OK);
            return new ResponseEntity(path, HttpStatus.OK);
        } catch (Exception e){
        	System.out.println(e);
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/multimodalroute")
    public ResponseEntity<List<Coordinate>> multiModalRouteController(@RequestParam double lat1,
                                                                      @RequestParam double lon1,
                                                                      @RequestParam double lat2,
                                                                      @RequestParam double lon2){
        /**
         * This Function's inputs are latitude and longitude, represents source and target locations
         * returns list of shortest path coordinates as latitude and longitude*/
        long time = 36000;
        try {
            List<TransportPath> path = ShortestPathService.getMultiModalRoute(lat1, lon1, lat2, lon2,time);

            return new ResponseEntity(path, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }

    }
    @GetMapping("/alternative")
    public ResponseEntity<List<List<TransportPath>>> getAlternativeRoutes(@RequestParam double lat1,
                                                                          @RequestParam double lon1,
                                                                          @RequestParam double lat2,
                                                                          @RequestParam double lon2) throws Exception {


        try {
            List<List<TransportPath>> alternatives = ShortestPathService.getAlternativeRoutes(lat1, lon1, lat2,lon2,36000);

            return new ResponseEntity(alternatives, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
