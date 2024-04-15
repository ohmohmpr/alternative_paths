var map;
 var coord1Input = document.getElementById("coord1");
 var coord2Input = document.getElementById("coord2");
 var marker1;
 var marker2;
 var line;
 var groupName = "group1"
 let counter = 0;
 //var server = "geonet.igg.uni-bonn.de";
 var server = "localhost:8080";
 var markerLayer1;
 var markerLayer2;

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
         color: '#000000',
         width: 4,
         opacity: 1
     })
 });
 var tramLineStyle = new ol.style.Style({
     stroke: new ol.style.Stroke({
         color: '#06dfe7',
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
      src: 'assets/source.png',
      scale: "0.08"
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
      src: 'assets/target.png',
      scale: "0.08"
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




  
      const url = `http://${server}/${groupName}/ex1/${routeMethod}?lat1=${lat1}&lon1=${lon1}&lat2=${lat2}&lon2=${lon2}`;

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
function drawMultiModalPath(path) {
    // Önceki katmanları temizle
    oldLineList.forEach(item => map.removeLayer(item));
    oldLineList = [];
    if (line) {
        map.removeLayer(line);
    }

    // Yol listesi, yol id'leri ve noktaları tanımla
    var path_list = [];
    var path_ids = [];
    var points = [];

    // Başlangıç noktasını al
    var startCoord = path[0].coordinate;
    var startPoint = ol.proj.fromLonLat([startCoord.y, startCoord.x]);
    path_ids.push(path[0].condition);
    points.push(startPoint);

    // Yol boyunca döngü
    for (var i = 1; i < path.length; i++) {
        var pth = path[i];
        var coord = pth.coordinate;
        var point = ol.proj.fromLonLat([coord.y, coord.x]);

        // Yol koşulları değiştiğinde yeni bir yolu başlat
        if (path[i - 1].condition != pth.condition && pth.condition != "arrival") {
            path_list.push(points);
            points = [];
            path_ids.push(pth.condition);
        }

        // Noktaları işle
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

    // Yolları çiz
    let flag = false;
    for (var i = 0; i < path_ids.length; i++) {
        var lineString = new ol.geom.LineString(path_list[i]);
        var lineFeature = new ol.Feature({
            geometry: lineString
        });
        lineFeature.setStyle(path_ids[i] == "walk" ? walkLineStyle : (flag ? tramLineStyle : busLineStyle));
        if(!flag){
            flag = !flag;
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
 /*function drawMultiModalPath(path) {
      if (oldLineList.length>0){
          oldLineList.forEach(item => map.removeLayer(item));
          oldLineList = [];
      }

     if (line) {
         map.removeLayer(line);
     }
     console.log(path[10])
     var path_list = []
     var path_ids = [];
     var points = [];



     var coord = path[0].coordinate;
     var point = ol.proj.fromLonLat([coord.y, coord.x]);
     path_ids.push(path[0].condition)
     points.push(point);

     for (var i = 1; i < path.length; i++) {
         var pth = path[i];
         if(path[i-1].condition != pth.condition && pth.condition != "arrival"){
             path_list.push(points)
             points  = []
             path_ids.push(pth.condition)
         }


         var coord = pth.coordinate;
         var point = ol.proj.fromLonLat([coord.y, coord.x]);
         console.log(pth);
         if(pth.condition == "departure"){
             console.log(point);
             var pointFeature = new ol.Feature({
                 geometry: point
             });
             var vectorSource = new ol.source.Vector({
                 features: [pointFeature]
             });
             pointLayer = new ol.layer.Vector({
                 source: vectorSource
             });
             pointLayer.setStyle(departurePointStyle)
             map.addLayer(pointLayer);
             oldLineList.push(pointLayer);
         }
         if(pth.condition == "arrival"){
             console.log(point);
             var pointFeature = new ol.Feature({
                 geometry: point
             });
             var vectorSource = new ol.source.Vector({
                 features: [pointFeature]
             });
             pointLayer = new ol.layer.Vector({
                 source: vectorSource
             });
             pointLayer.setStyle(arrivalPointStyle)
             map.addLayer(pointLayer);
             oldLineList.push(pointLayer);
         }
         points.push(point);
     }
     path_list.push(points);
     console.log(path_ids);
     let flag = false;
     for(var i = 0;i<path_ids.length;i++){
         var lineString = new ol.geom.LineString(path_list[i]);
         var lineFeature = new ol.Feature({
             geometry: lineString
         });
         if(path_ids[i] == "walk"){
             lineFeature.setStyle(walkLineStyle)
         }
         else{

             if(flag){
                 lineFeature.setStyle(tramLineStyle)
                 flag = false;
             }else{
                lineFeature.setStyle(busLineStyle)
                 flag = true;
             }

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
*/


 document.getElementById('toggleBtn').addEventListener('change', function(e) {
     routeMethod = e.target.checked ?  "multimodalroute" : "shortestpathjgrapht";
     console.log(routeMethod);

 } );


 // run static path
const url = `http://${server}/${groupName}/ex1/static`;

fetch(url)
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Connection is unsuccessful.');
        }
    })
    .then(data => {
        drawPath(data);

    })
    .catch(error => {
    });
