var map;
 var coord1Input = document.getElementById("coord1");
 var coord2Input = document.getElementById("coord2");
 var marker1;
 var marker2;
 var line;
 var groupName = "lbsproject-ohm"
 let counter = 0;
 //var server = "https://geonet.igg.uni-bonn.de";
 var server = "http://localhost:8080";
 var markerLayer1;
 var markerLayer2;
const targetImgRes = fetch("https://cdn4.iconfinder.com/data/icons/small-n-flat/24/map-marker-1024.png");
 var leftBottom =  ol.proj.transform([7.004813999686911, 50.67771640948173], "EPSG:4326", "EPSG:3857");
 var rightTop = ol.proj.transform([7.19776199427912, 50.768218129933224], "EPSG:4326", "EPSG:3857");
 var minx = leftBottom[0];
 var miny = leftBottom[1];
 var maxx = rightTop[0];
 var maxy = rightTop[1];
var oldLineList = [];
 let routeMethod = "shortestpath";

 tile_layer = new ol.layer.Tile({ source: new ol.source.OSM() });
 var oldZoom = 2;
 var walkLineStyle = new ol.style.Style({
     stroke: new ol.style.Stroke({
         color: '#0080ff',
         width: 4,
         opacity: 1,
         lineDash: [.1, 7]
     })
 });
 var busLineStyle = new ol.style.Style({
     stroke: new ol.style.Stroke({
         color: '#ec0909',
         width: 4,
         opacity: 1
     })
 });
 var tramLineStyle = new ol.style.Style({
     stroke: new ol.style.Stroke({
         color: '#40ff00',
         width: 4,
         opacity: 1
     })
 });
var departurePointStyle = new ol.style.Style({
    stroke: new ol.style.Stroke({
        color: '#a4a4a4',
        width: 10,
        opacity: 1
    })
});
var arrivalPointStyle = new ol.style.Style({
    stroke: new ol.style.Stroke({
        color: '#b93434',
        width: 10,
        opacity: 1
    })
});

 var map = new ol.Map({
	target: 'map',
	layers: [
		tile_layer
	],
	view: new ol.View({
		center: ol.proj.fromLonLat([(minx+maxx)/2, (maxy+miny)/2]),
		zoom: oldZoom,
		maxZoom: 20,
		minZoom: 2,
		extent: [minx, miny, maxx, maxy],
	})
});
marker1 = new ol.Feature({
  geometry: new ol.geom.Point(1,1)
});

const marker1Icon =
  new ol.style.Style({
    image: new ol.style.Icon({
      crossOrigin: 'anonymous',
      src: 'https://cdn1.iconfinder.com/data/icons/web-55/32/web_1-1024.png',
      scale: "0.03"
    }),
});

marker1.setStyle(marker1Icon);

marker2 = new ol.Feature({
  geometry: new ol.geom.Point(1,1)
});
const marker2Icon = 
  new ol.style.Style({
    image: new ol.style.Icon({
      crossOrigin: 'anonymous',
      src: 'https://cdn4.iconfinder.com/data/icons/twitter-29/512/157_Twitter_Location_Map-1024.png',
      scale: "0.04"
    }),
  });
marker2.setStyle(marker2Icon);
map.on("click", function (e) {
      if (line) {
          map.removeLayer(line);
      }
      if (oldLineList.length>0){
          oldLineList.forEach(item => map.removeLayer(item));
          oldLineList = [];
      }
      var position = ol.proj.toLonLat(e.coordinate);

      if (counter%2 === 0) {
        map.removeLayer(markerLayer2)
        marker1.getGeometry().setCoordinates(e.coordinate);
        markerLayer1 = new ol.layer.Vector({
          source: new ol.source.Vector({
            features: [marker1]
          })
        });

        map.addLayer(markerLayer1);
        coord1Input.value = position[0].toFixed(7) + "," + position[1].toFixed(7);
        counter++;
      } else if (counter%2 === 1) {
        marker2.getGeometry().setCoordinates(e.coordinate);
          markerLayer2 = new ol.layer.Vector({
          source: new ol.source.Vector({
            features: [marker2]
          })
        });
        map.addLayer(markerLayer2);
        coord2Input.value = position[0].toFixed(7) + "," + position[1].toFixed(7);
        counter++;
      }
    });

  function findShortestPath() {

    var coord1 = coord1Input.value.split(",");
    var coord2 = coord2Input.value.split(",");
    var lat1 = parseFloat(coord1[1]);
    var lon1 = parseFloat(coord1[0]);
    var lat2 = parseFloat(coord2[1]);
    var lon2 = parseFloat(coord2[0]);




  
      const url = `${server}/${groupName}/ex1/${routeMethod}?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;

    fetch(url)
            .then(response => {
              if (response.ok) {
                return response.json();
              } else {
                throw new Error('Connection is unsuccessful.');
              }
            })
            .then(data => {
                if (routeMethod != "multimodalroute") {
                    drawPath(data);
                } else {
                    drawMultiModalPath(data)
                }

            })
            .catch(error => {
            });
  }
  function drawPath(path) {
    if (line) {
      map.removeLayer(line);
    }
    if (oldLineList.length>0){
        oldLineList.forEach(item => map.removeLayer(item));
        oldLineList = [];
    }
    var points = [];
    for (var i = 0; i < path.length; i++) {
      var coord = path[i];
        var point = ol.proj.fromLonLat([coord.y, coord.x]);
        points.push(point);
    }


    var lineString = new ol.geom.LineString(points);
    var lineFeature = new ol.Feature({
      geometry: lineString
    });

    var lineStyle = new ol.style.Style({
      stroke: new ol.style.Stroke({
        color: '#0080ff',
        width: 4,
        opacity: 1,
        lineDash: [.1, 7]
      })
    });
    lineFeature.setStyle(lineStyle);

    var vectorSource = new ol.source.Vector({
      features: [lineFeature]
    });

    line = new ol.layer.Vector({
      source: vectorSource
    });
    map.addLayer(line);
  }
let ptCount =0;
function drawMultiModalPath(path) {
    // Önceki katmanları temizle
    oldLineList.forEach(item => map.removeLayer(item));
    oldLineList = [];
    if (line) {
        map.removeLayer(line);
    }

    var path_list = [];
    var path_ids = [];
    var points = [];

    var startCoord = path[0].coordinate;
    var startPoint = ol.proj.fromLonLat([startCoord.y, startCoord.x]);
    path_ids.push(path[0].condition);
    points.push(startPoint);

    for (var i = 1; i < path.length; i++) {
        var pth = path[i];
        var coord = pth.coordinate;
        var point = ol.proj.fromLonLat([coord.y, coord.x]);

        if (path[i - 1].condition != pth.condition && pth.condition != "arrival") {
            path_list.push(points);
            points = [];
            path_ids.push(pth.condition);
        }

        if (pth.condition == "departure" || pth.condition == "arrival") {
            var pointFeature = new ol.Feature({
                geometry: new ol.geom.Point(point)
            });
            var vectorSource = new ol.source.Vector({
                features: [pointFeature]
            });
            var pointLayer = new ol.layer.Vector({
                source: vectorSource
            });
            pointLayer.setStyle(pth.condition == "departure" ? departurePointStyle : arrivalPointStyle);
            map.addLayer(pointLayer);
            oldLineList.push(pointLayer);
        }

        points.push(point);
    }
    path_list.push(points);

    // draw path
    for (var i = 0; i < path_ids.length; i++) {
        var lineString = new ol.geom.LineString(path_list[i]);
        var lineFeature = new ol.Feature({
            geometry: lineString
        });
        lineFeature.setStyle(path_ids[i] == "walk" ? walkLineStyle : (ptCount%2 ==0 ? tramLineStyle : busLineStyle));
        if(path_ids[i]!= "walk"){
            ptCount +=1;
        }


        var vectorSource = new ol.source.Vector({
            features: [lineFeature]
        });
        line = new ol.layer.Vector({
            source: vectorSource
        });
        map.addLayer(line);
        oldLineList.push(line);
    }
}

 document.getElementById('toggleBtn').addEventListener('change', function(e) {
     routeMethod = e.target.checked ?  "multimodalroute" : "shortestpath";
     console.log(routeMethod);

 } );



