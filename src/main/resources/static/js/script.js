//var map;
// var coord1Input = document.getElementById("coord1");
// var coord2Input = document.getElementById("coord2");
// var marker1;
// var marker2;
// var line;
// var exploredNodes;
// var groupName = "lbsproject-ohm"
// let counter = 0;
//// var server = "https://geonet.igg.uni-bonn.de";
// var server = "http://localhost:8080";
// var markerLayer1;
// var markerLayer2;
//const targetImgRes = fetch("https://cdn4.iconfinder.com/data/icons/small-n-flat/24/map-marker-1024.png");
// var leftBottom =  ol.proj.transform([7.004813999686911, 50.67771640948173], "EPSG:4326", "EPSG:3857");
// var rightTop = ol.proj.transform([7.19776199427912, 50.768218129933224], "EPSG:4326", "EPSG:3857");
// var minx = leftBottom[0];
// var miny = leftBottom[1];
// var maxx = rightTop[0];
// var maxy = rightTop[1];
////var oldLineList = [];
// let routeMethod = "shortestpath";
//
// tile_layer = new ol.layer.Tile({ source: new ol.source.OSM() });
// var oldZoom = 2;
// var walkLineStyle = new ol.style.Style({
//     stroke: new ol.style.Stroke({
//         color: '#0080ff',
//         width: 4,
//         opacity: 1,
//         lineDash: [.1, 7]
//     })
// });
// var busLineStyle = new ol.style.Style({
//     stroke: new ol.style.Stroke({
//         color: '#ec0909',
//         width: 4,
//         opacity: 1
//     })
// });
// var tramLineStyle = new ol.style.Style({
//     stroke: new ol.style.Stroke({
//         color: '#40ff00',
//         width: 4,
//         opacity: 1
//     })
// });
//var departurePointStyle = new ol.style.Style({
//    stroke: new ol.style.Stroke({
//        color: '#a4a4a4',
//        width: 10,
//        opacity: 1
//    })
//});
//var arrivalPointStyle = new ol.style.Style({
//    stroke: new ol.style.Stroke({
//        color: '#b93434',
//        width: 10,
//        opacity: 1
//    })
//});
//
// var map = new ol.Map({
//	target: 'map',
//	layers: [
//		tile_layer
//	],
//	view: new ol.View({
//		center: ol.proj.fromLonLat([(minx+maxx)/2, (maxy+miny)/2]),
//		zoom: oldZoom,
//		maxZoom: 20,
//		minZoom: 2,
//		extent: [minx, miny, maxx, maxy],
//	})
//});
//marker1 = new ol.Feature({
//  geometry: new ol.geom.Point(1,1)
//});
//
//const marker1Icon =
//  new ol.style.Style({
//    image: new ol.style.Icon({
//      crossOrigin: 'anonymous',
//      src: 'https://cdn1.iconfinder.com/data/icons/web-55/32/web_1-1024.png',
//      scale: "0.03"
//    }),
//});
//
//marker1.setStyle(marker1Icon);
//
//marker2 = new ol.Feature({
//  geometry: new ol.geom.Point(1,1)
//});
//const marker2Icon = 
//  new ol.style.Style({
//    image: new ol.style.Icon({
//      crossOrigin: 'anonymous',
//      src: 'https://cdn4.iconfinder.com/data/icons/twitter-29/512/157_Twitter_Location_Map-1024.png',
//      scale: "0.04"
//    }),
//  });
//marker2.setStyle(marker2Icon);
//
//map.on("click", function (e) {
//	  if (line) {
//	      map.removeLayer(line);
//	  }
////	  if (oldLineList.length>0){
////	      oldLineList.forEach(item => map.removeLayer(item));
////	      oldLineList = [];
////	  }
//	  var position = ol.proj.toLonLat(e.coordinate);
//	
//	  if (counter%2 === 0) {
//	    map.removeLayer(markerLayer2)
//	    marker1.getGeometry().setCoordinates(e.coordinate);
//	    markerLayer1 = new ol.layer.Vector({
//	      source: new ol.source.Vector({
//	        features: [marker1]
//	      })
//	    });
//	
//	    map.addLayer(markerLayer1);
//	    coord1Input.value = position[0].toFixed(7) + "," + position[1].toFixed(7);
//	    counter++;
//	  } else if (counter%2 === 1) {
//	    marker2.getGeometry().setCoordinates(e.coordinate);
//	      markerLayer2 = new ol.layer.Vector({
//	      source: new ol.source.Vector({
//	        features: [marker2]
//	      })
//	    });
//	    map.addLayer(markerLayer2);
//	    coord2Input.value = position[0].toFixed(7) + "," + position[1].toFixed(7);
//	    counter++;
//	  }
//	}
//);
//
//  function findShortestPath() {
//
//    var coord1 = coord1Input.value.split(",");
//    var coord2 = coord2Input.value.split(",");
//    var lat1 = parseFloat(coord1[1]);
//    var lon1 = parseFloat(coord1[0]);
//    var lat2 = parseFloat(coord2[1]);
//    var lon2 = parseFloat(coord2[0]);
//
//
//
//
//  	// get shortest
////      const url = `${server}/${groupName}/ex1/${routeMethod}bidi?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;
////      const url_explorednodes = `${server}/${groupName}/ex1/explorednodesbidi?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;
//
//  	// get alternatives
//      const url = `${server}/${groupName}/ex1/alternativebdv?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;
////      const url_explorednodes = `${server}/${groupName}/ex1/explorednodesbdv?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;
//
//	document.getElementById("overlay").style.display = "flex";
//
//    fetch(url)
//            .then(response => {
//              if (response.ok) {
//                return response.json();
//              } else {
//                throw new Error('Connection is unsuccessful.');
//              }
//            })
//            .then(data => {
//                if (routeMethod != "multimodalroute") {
//                    drawPath(data[0],'#0080ff');
//                    drawPath(data[1],"#FF0000");
//                    drawPath(data[2],"#008000");
//                    console.log(data);
//				  document.getElementById("overlay").style.display = "none";
//                } else {
//                    drawMultiModalPath(data)
//                }
//
//            })
//            .catch(error => {
//				console.log(error);
//            });
//            
//           
////    fetch(url_explorednodes)
////            .then(response => {
////              if (response.ok) {
////                return response.json();
////              } else {
////                throw new Error('Connection is unsuccessful.');
////              }
////            })
////            .then(data => {
////                if (routeMethod != "multimodalroute") {
////                    drawExploredNodes(data);
////                } else {
////                    drawMultiModalPath(data)
////                }
////            })
////            .catch(error => {
////				console.log(error)
////            });
//  }
//  
//  
//  
//  
//  
//  
//  function drawPath(path,colorCode) {
////    if (line) {
////      map.removeLayer(line);
////    }
////    if (oldLineList.length>0) {
////        oldLineList.forEach(item => map.removeLayer(item));
////        oldLineList = [];
////    }
//    var points = [];
//    for (var i = 0; i < path.length; i++) {
//      var coord = path[i];
//        var point = ol.proj.fromLonLat([coord.y, coord.x]);
//        points.push(point);
//    }
//
//
//    var lineString = new ol.geom.LineString(points);
//    var lineFeature = new ol.Feature({
//      geometry: lineString
//    });
//
//    var lineStyle = new ol.style.Style({
//      stroke: new ol.style.Stroke({
//        color: colorCode,
//        width: 4,
//        opacity: 1,
//        lineDash: [.1, 7]
//      })
//    });
//    lineFeature.setStyle(lineStyle);
//
//    var vectorSource = new ol.source.Vector({
//      features: [lineFeature]
//    });
//
//    line = new ol.layer.Vector({
//      source: vectorSource
//    });
//    map.addLayer(line);
//  }
//
//
//let ptCount =0;
//function drawMultiModalPath(path) {
//    // Önceki katmanları temizle
////    oldLineList.forEach(item => map.removeLayer(item));
////    oldLineList = [];
//    if (line) {
//        map.removeLayer(line);
//    }
//
//    var path_list = [];
//    var path_ids = [];
//    var points = [];
//
//    var startCoord = path[0].coordinate;
//    var startPoint = ol.proj.fromLonLat([startCoord.y, startCoord.x]);
//    path_ids.push(path[0].condition);
//    points.push(startPoint);
//
//    for (var i = 1; i < path.length; i++) {
//        var pth = path[i];
//        var coord = pth.coordinate;
//        var point = ol.proj.fromLonLat([coord.y, coord.x]);
//
//        if (path[i - 1].condition != pth.condition && pth.condition != "arrival") {
//            path_list.push(points);
//            points = [];
//            path_ids.push(pth.condition);
//        }
//
//        if (pth.condition == "departure" || pth.condition == "arrival") {
//            var pointFeature = new ol.Feature({
//                geometry: new ol.geom.Point(point)
//            });
//            var vectorSource = new ol.source.Vector({
//                features: [pointFeature]
//            });
//            var pointLayer = new ol.layer.Vector({
//                source: vectorSource
//            });
//            pointLayer.setStyle(pth.condition == "departure" ? departurePointStyle : arrivalPointStyle);
//            map.addLayer(pointLayer);
////            oldLineList.push(pointLayer);
//        }
//
//        points.push(point);
//    }
//    path_list.push(points);
//
//    // draw path
//    for (var i = 0; i < path_ids.length; i++) {
//        var lineString = new ol.geom.LineString(path_list[i]);
//        var lineFeature = new ol.Feature({
//            geometry: lineString
//        });
//        lineFeature.setStyle(path_ids[i] == "walk" ? walkLineStyle : (ptCount%2 ==0 ? tramLineStyle : busLineStyle));
//        if(path_ids[i]!= "walk"){
//            ptCount +=1;
//        }
//
//
//        var vectorSource = new ol.source.Vector({
//            features: [lineFeature]
//        });
//        line = new ol.layer.Vector({
//            source: vectorSource
//        });
//        map.addLayer(line);
////        oldLineList.push(line);
//    }
//}
//
//  function drawExploredNodes(path) {
//    if (exploredNodes) {
//      map.removeLayer(exploredNodes);
//    }
////    if (oldLineList.length>0){
////        oldLineList.forEach(item => map.removeLayer(item));
////        oldLineList = [];
////    }
//    var points = [];
//    for (var i = 0; i < path.length; i++) {
//      var coord = path[i];
//        var point = ol.proj.fromLonLat([coord.y, coord.x]);
//        points.push(point);
//    }
//
//	
//	marker3 = new ol.Feature({
//	  geometry: new ol.geom.MultiPoint(points)
//	});
//
//	const marker3Style =
//	  new ol.style.Style({
//		image: new ol.style.Circle({
//		      radius: 4,
//		      stroke: new ol.style.Stroke({
//		        color: '#0000ff'
//		      }),
////		      fill: new ol.style.Fill({
////		        color: '#00ff00'
////		      })
//		    }),
//	});
//
//	try {
//	  marker3.setStyle(marker3Style);
//	  console.log("done");
//	}
//	catch(err) {
//	  console.log(err.message)
//	}
//	
//    marker3.getGeometry().setCoordinates(points);
//    exploredNodes = new ol.layer.Vector({
//      source: new ol.source.Vector({
//        features: [marker3]
//      })
//    });
//    map.addLayer(exploredNodes);
//  }
// document.getElementById('toggleBtn').addEventListener('change', function(e) {
//     routeMethod = e.target.checked ?  "multimodalroute" : "shortestpath";
//     console.log(routeMethod);
//
// } );

/**** old line array to clear layers ***/
const oldLineArr = [];

/**** UTIL METHODS ******/
const getRandomHexColorStyle = () => {

    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    const createdStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: color,
            width: 4,
            opacity: 1
        })
    });
    return createdStyle;
};

const addLayer = (layer, addToOldLineArr = true) => {
    MAP.addLayer(layer);
    if (addToOldLineArr) {
        oldLineArr.push(layer);
    }
};

const clearLayer = (layer) => {
    MAP.removeLayer(layer);
};

const createLayerVector = (features) => {
    return new ol.layer.Vector({
        source: new ol.source.Vector({
            features: features
        })
    });
};

const clearOldLayers = () => {
    if (oldLineArr.length > 0) {
        oldLineArr.forEach(item => clearLayer(item));
        oldLineArr.splice(0, oldLineArr.length);
    }
};
/************* --------------------- *************/

/**** CONSTANTS ******/
const GROUP_NAME = "lbsproject-ohm";
const BASE_URL = "http://localhost:8080";
// const BASE_URL = "https://geonet.igg.uni-bonn.de";
const LEFT_BOTTOM = ol.proj.transform([7.004813999686911, 50.67771640948173], "EPSG:4326", "EPSG:3857");
const RIGHT_TOP = ol.proj.transform([7.19776199427912, 50.768218129933224], "EPSG:4326", "EPSG:3857");
/**** -------- *****/



/**** MIN_MAX_COORDS *****/
const MIN_X = LEFT_BOTTOM[0];
const MIN_Y = LEFT_BOTTOM[1];
const MAX_X = RIGHT_TOP[0];
const MAX_Y = RIGHT_TOP[1];
/**** -------- *****/


/****** MARKER ICONS ******/
const FIRST_MARKER_ICON = new ol.style.Style({
    image: new ol.style.Icon({
        crossOrigin: 'anonymous',
        src: 'https://cdn1.iconfinder.com/data/icons/web-55/32/web_1-1024.png',
        scale: "0.03"
    }),
});

const SECOND_MARKER_ICON = new ol.style.Style({
    image: new ol.style.Icon({
        crossOrigin: 'anonymous',
        src: 'https://cdn4.iconfinder.com/data/icons/twitter-29/512/157_Twitter_Location_Map-1024.png',
        scale: "0.04"
    }),
});
/**** -------- *****/

/****** MARKERS ******/
const FIRST_MARKER = new ol.Feature({
    geometry: new ol.geom.Point(1,1)
});
FIRST_MARKER.setStyle(FIRST_MARKER_ICON);

const SECOND_MARKER = new ol.Feature({
    geometry: new ol.geom.Point(1,1)
});
SECOND_MARKER.setStyle(SECOND_MARKER_ICON);
/**** -------- *****/


/***** INPUT ELEMENTS *****/
const firstCoordInput = document.getElementById("coord1");
const secondCoordInput = document.getElementById("coord2");
/**** -------- *****/


/****** LAYERS ******/
// BUG:: if put const here code doesn't work;
TILE_LAYER = new ol.layer.Tile({ source: new ol.source.OSM()});
//const TILE_LAYER = new ol.layer.Tile({ source: new ol.source.OSM()});
/**** -------- *****/


/**** MAP ****/
const MAP = new ol.Map({
    target: 'map',
    layers: [
        TILE_LAYER
    ],
    view: new ol.View({
        center: ol.proj.fromLonLat([(MIN_X + MAX_X)/2, (MAX_Y + MIN_Y)/2]),
        zoom: 2,
        maxZoom: 20,
        minZoom: 2,
        extent: [MIN_X, MIN_Y, MAX_X, MAX_Y],
    })
});
/**** -------- *****/

/***** PRESET STYLE *****/
var WALK_LINE_STYLE = new ol.style.Style({
    stroke: new ol.style.Stroke({
        color: '#0080ff',
        width: 4,
        opacity: 1,
        lineDash: [.1, 7]
    })
});
/**** -------- *****/

/***** VARIABLES *****/
let counter = 0;
let firstMarkerLayer;
let secondMarkerLayer;
let routeMethod = "shortestpath";
let pathMethod = "singlepath";
let ptCount = 0;
/**** -------- *****/



function drawPath(path, colorCode="") {

	if (!colorCode == "") {
		WALK_LINE_STYLE = new ol.style.Style({
		    stroke: new ol.style.Stroke({
		        color: colorCode,
		        width: 4,
		        opacity: 1,
		        lineDash: [.1, 7]
		    })
		});
	} else {
		clearOldLayers();
		WALK_LINE_STYLE = new ol.style.Style({
		    stroke: new ol.style.Stroke({
		        color: '#0080ff',
		        width: 4,
		        opacity: 1,
		        lineDash: [.1, 7]
		    })
		});
	}
    	
    const points = [];

    for (let i = 0; i < path.length; i++) {
        let coord = path[i];
        let point = ol.proj.fromLonLat([coord.y, coord.x]);
        
        points.push(point);
    }

    let lineString = new ol.geom.LineString(points);
    let lineFeature = new ol.Feature({
        geometry: lineString
    });

    lineFeature.setStyle(WALK_LINE_STYLE);
    line = createLayerVector([lineFeature]);
    addLayer(line);
}


function drawMultiModalPath(path) {

    const path_list = [];
    const path_ids = [];
    let points = [];

    let startCoord = path[0].coordinate;
    let startPoint = ol.proj.fromLonLat([startCoord.y, startCoord.x]);
    path_ids.push(path[0].condition);
    points.push(startPoint);

    for (let i = 1; i < path.length; i++) {
        let pth = path[i];
        let coord = pth.coordinate;
        let point = ol.proj.fromLonLat([coord.y, coord.x]);

        if (path[i - 1].condition !== pth.condition && pth.condition !== "arrival") {
            path_list.push(points);
            points = [];
            path_ids.push(pth.condition);
        }

        if (pth.condition === "departure" || pth.condition === "arrival") {
            let pointFeature = new ol.Feature({
                geometry: new ol.geom.Point(point)
            });
            const pointLayer = createLayerVector([pointFeature]);
            pointLayer.setStyle(getRandomHexColorStyle());
            addLayer(pointLayer);
        }

        points.push(point);
    }
    path_list.push(points);

    // draw path
    for (let i = 0; i < path_ids.length; i++) {
        let lineString = new ol.geom.LineString(path_list[i]);
        let lineFeature = new ol.Feature({
            geometry: lineString
        });
        lineFeature.setStyle(path_ids[i] == "walk" ? WALK_LINE_STYLE : getRandomHexColorStyle());
        if(path_ids[i]!= "walk"){
            ptCount +=1;
        }
        const line = createLayerVector([lineFeature]);
        addLayer(line);
    }
}

/**** EVENT LISTENERS ****/
document.getElementById('toggleBtn').addEventListener('change', function(e) {
    routeMethod = e.target.checked ?  "multimodalroute" : "shortestpath";
});

document.getElementById('alternateBtn').addEventListener('change', function(e) {
    pathMethod = e.target.checked ?  "alternativepath" : "singlepath";
});


MAP.on("click", function (e) {
    clearOldLayers();

    const position = ol.proj.toLonLat(e.coordinate);

    if (counter%2 === 0) {
        clearLayer(secondMarkerLayer);
        FIRST_MARKER.getGeometry().setCoordinates(e.coordinate);
        firstMarkerLayer = createLayerVector([FIRST_MARKER]);
        addLayer(firstMarkerLayer, false);
        firstCoordInput.value = position[0].toFixed(7) + "," + position[1].toFixed(7);
        counter++;
    } else if (counter%2 === 1) {
        SECOND_MARKER.getGeometry().setCoordinates(e.coordinate);
        secondMarkerLayer = createLayerVector([SECOND_MARKER]);
        addLayer(secondMarkerLayer, false);
        secondCoordInput.value = position[0].toFixed(7) + "," + position[1].toFixed(7);
        counter++;
    }
});
/************* --------------------- *************/


/**** METHODS ******/
async function findShortestPath() {

    clearOldLayers();
    const [lon1, lat1] = [parseFloat(firstCoordInput.value.split(",")[0]), parseFloat(firstCoordInput.value.split(",")[1])];
    const [lon2, lat2] = [parseFloat(secondCoordInput.value.split(",")[0]), parseFloat(secondCoordInput.value.split(",")[1])];

//	const url = `${server}/${groupName}/ex1/alternativebdv?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;

    let url = `${BASE_URL}/${GROUP_NAME}/ex1/${pathMethod === "singlepath" ? routeMethod : 'alternativebdv'}?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;
    const res = await fetch(url);
    const data = await res.json();

    if (routeMethod !== "multimodalroute" && pathMethod === "singlepath") {
        drawPath(data);
    } else if (routeMethod === "multimodalroute" && pathMethod === "singlepath") {
        clearOldLayers();
        drawMultiModalPath(data)
    } else if (routeMethod !== "multimodalroute" && pathMethod === "alternativepath") {
        clearOldLayers();
		drawPath(data[0],"#0080ff");
		drawPath(data[1],"#FF0000");
		drawPath(data[2],"#008000");
//        data.forEach(pathData => drawMultiModalPath(pathData, true));
    }else{
        clearOldLayers();
        data.forEach(pathData => drawMultiModalPath(pathData, true));
    }
    
}
/************* --------------------- *************/





