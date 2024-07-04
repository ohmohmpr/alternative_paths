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
var numPaths = 3;
var limitedSharing = 0.80;
var localOptimality = 0.25;
var UBS = 0.25;
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
let routeMethod = "shortestpathbidi"; // shortestpath, shortestpathbidi
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
    routeMethod = e.target.checked ?  "multimodalroute" : "shortestpathbidi";
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
async function findShortestPath(lon1=null, lat1=null, lon2=null, lat2=null, id=null) {

    let startTime = performance.now();
      
	validateBDV();
    clearOldLayers();
    if (lon1 === null && lat1 === null && lon2 === null && lat2 === null){
	    [lon1, lat1] = [parseFloat(firstCoordInput.value.split(",")[0]), parseFloat(firstCoordInput.value.split(",")[1])];
	    [lon2, lat2] = [parseFloat(secondCoordInput.value.split(",")[0]), parseFloat(secondCoordInput.value.split(",")[1])];
	} else {
		if (!firstMarkerLayer || !secondMarkerLayer) {
			plot_markers(lon1, lat1, lon2, lat2);
		}
		clearLayer(firstMarkerLayer);
		clearLayer(secondMarkerLayer);
	}

	if (pathMethod === "alternativepath") {
	   url = `${BASE_URL}/${GROUP_NAME}/ex1/${pathMethod}-${routeMethod}?`+
	   		`lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`+
	   		`&numPaths=${numPaths}&limitedSharing=${limitedSharing}`+
	   		`&localOptimality=${localOptimality}&UBS=${UBS}`;
	} else {
	   url = `${BASE_URL}/${GROUP_NAME}/ex1/${pathMethod}-${routeMethod}?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;
	}

	console.log(url);
    document.getElementById("overlay").style.display = "flex";
    const res = await fetch(url);
    const data = await res.json();
    
    console.log(data)

    if (routeMethod !== "multimodalroute" && pathMethod === "singlepath") {
        drawPath(data);
    } else if (routeMethod === "multimodalroute" && pathMethod === "singlepath") {
        clearOldLayers();
        drawMultiModalPath(data)
    } else if (routeMethod !== "multimodalroute" && pathMethod === "alternativepath") {
        clearOldLayers();
        colorCode = ["#0080ff", "#FF0000", "#008000", "#ffA500"];
        i_c = 0;
        data.forEach(pathData =>  {
			drawPath(pathData,colorCode[i_c%colorCode.length])
			i_c = i_c + 1;
		});
//        data.forEach(pathData => drawMultiModalPath(pathData, true));
    }else{
        clearOldLayers();
        data.forEach(pathData => drawMultiModalPath(pathData, true));
    }

	plot_markers(lon1, lat1, lon2, lat2);
	changeColor(id);
	
    document.getElementById("center").style.display = "block";
    document.getElementById("overlay").style.display = "none";
    
	let endTime = performance.now();
	let timeElapsed = endTime - startTime;
	showResults(pathMethod, data, timeElapsed/1000);

}
/************* --------------------- *************/

/**** UTILS ******/
function showResults(pathMethod, data, timeElapsed) {
	var rst = document.getElementById('center');
	
	rst.innerHTML = "";
	if (pathMethod === "singlepath") {
		var numberPaths = 1;
		rst.innerHTML += 
				`<p> <b>${numberPaths}</b> path has been found.</p>\
				<p> Time used: <b>${timeElapsed.toFixed(3)}</b> seconds.</p>\
				<br>`;
	}
	else {
		var numberPaths = data.length;
		rst.innerHTML += 
				`<p> <b>${numberPaths}</b> paths have been found.</p>\
				<p> Time used: <b>${timeElapsed.toFixed(3)}</b> seconds.</p>\
				<br>`;
		for (let i = 0; i < numberPaths; i++) {
			rst.innerHTML += 
				`<p> <b>${numberPaths}</b> paths have been found.</p>\
				<p> Time used: <b>${timeElapsed.toFixed(3)}</b> seconds.</p>\
				<br>`;
		}
	}



}

function plot_markers(lon1, lat1, lon2, lat2) {
	
    MAP.setView(new ol.View({
	    center: ol.proj.fromLonLat([(lon1 + lon2)/2, (lat1 + lat2)/2]),
	    zoom: 2,
	    maxZoom: 20,
	    minZoom: 2,
	    extent: [MIN_X, MIN_Y, MAX_X, MAX_Y],
	}));
    
	pos_start = ol.proj.transform([lon1, lat1], "EPSG:4326", "EPSG:3857");
	pos_end = ol.proj.transform([lon2, lat2], "EPSG:4326", "EPSG:3857");
    FIRST_MARKER.getGeometry().setCoordinates(pos_start);
    firstMarkerLayer = createLayerVector([FIRST_MARKER]);
    addLayer(firstMarkerLayer);
    SECOND_MARKER.getGeometry().setCoordinates(pos_end);
    secondMarkerLayer = createLayerVector([SECOND_MARKER]);
    addLayer(secondMarkerLayer);
}

function changeColor(id) {

  if (id !== null) {
	  document.getElementById("query1").style.color = "#0a55a1";
	  document.getElementById("query2").style.color = "#0a55a1";
	  document.getElementById("query3").style.color = "#0a55a1";
	  document.getElementById("query4").style.color = "#0a55a1";
	  document.getElementById("query5").style.color = "#0a55a1";
	  document.getElementById("query6").style.color = "#0a55a1";
	  document.getElementById("query7").style.color = "#0a55a1";
	  document.getElementById("query8").style.color = "#0a55a1";
	  document.getElementById("query9").style.color = "#0a55a1";
	  document.getElementById(id).style.color = "red";
  }

}

function validateBDV() {

	if (document.getElementById("numPaths").value == "") {
		document.getElementById("numPaths").value = 3;
	}
	numPaths = document.getElementById("numPaths").value;
	
	if (document.getElementById("limitedSharing").value == "") {
		document.getElementById("limitedSharing").value = 0.8;
	}
	limitedSharing = document.getElementById("limitedSharing").value;
	
	if (document.getElementById("localOptimality").value == "") {
		document.getElementById("localOptimality").value = 0.25;
	}
	localOptimality = document.getElementById("localOptimality").value;
	
	if (document.getElementById("UBS").value == "") {
		document.getElementById("UBS").value = 0.25;
	}
	UBS = document.getElementById("UBS").value;
 }

function checkNumber(id, min=0, max=1) {
	  let number = document.getElementById(id).value;
	  if (number>max || number<min)
		  document.getElementById(id).value = "";
}

function toggleResult() {
	var form = document.getElementById("center");
	form.style.display = form.style.display === "block" ? "none" : "block";
	var legend = document.getElementById("legend");
	legend.style.display = form.style.display === "none" ? "block" : "none";
}
/************* --------------------- *************/















// var exploredNodes;
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