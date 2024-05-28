







### create multi-modal graph holder method

```java
    private static MultiModalGraphHolder getMultiModalGraphHolder() throws Exception {
        try	{
			File gtfsDir = new File(path + "/gtfs/");
			// load gtfs file, interesting.

			RoadGraph<Point2D, GeofabrikData> roadGraph = RoadGraphHolder.getInstance().getRoadGraph();
			// get the existing roadGraphHolder (static)
			
			MultiModalRouter<GeofabrikData, GeofabrikData> router = new MultiModalRouter<>(roadGraph, gtfsDir,
			        Router.GEOFABRIK_FACTORY);
			// NEED TO INVESTIGATE MultiModalRouter

			MultiModalGraphHolder multiModalGraphHolder = new MultiModalGraphHolder();
			// constructor not sure which lines.
			
			multiModalGraphHolder.setMultiModalGraph(router);
			// self-setter method
			
			return multiModalGraphHolder;
        }	catch (Exception e){
			throw e;
        }
    }
```

### world-getter method

```java
    public MultiModalRouter<GeofabrikData, GeofabrikData> getMultiModalGraph() {
        return multiModalGraph;
    }
```
### world-setter method

```java
    public static void setInstance(MultiModalGraphHolder instance) {
        MultiModalGraphHolder.instance = instance;
    }
```

### self-setter method

```java
    public void setMultiModalGraph(MultiModalRouter<GeofabrikData, GeofabrikData> multiModalGraph) {
        this.multiModalGraph = multiModalGraph;
    }
```