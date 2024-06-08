

## initialization order
[order](https://stackoverflow.com/questions/2007666/in-what-order-do-static-instance-initializer-blocks-in-java-run)
1. public static class BasicAdjacentNodeIterator<V, E extends WeightedArcData> implements NodeIterator<V, E>
2. attributes.


length of this graph is 10000


```java
	private double starttime = 0;
	protected double dist[];
	>>>>>>>>>>>>>>>
	an array contains distance from source node to any node.
	is of type double, which is make sense.
	<<<<<<<<<<<<<<<
	protected double curr_dist = 0;
	protected int stamps[];
	protected HeapItem<DiGraphNode<V, E>> items[];
	public DiGraphNode<V, E> pred[];

	protected int currentStamp = 0;
```
	
6 attributes:
1. currentStamp = 
2. curr_dist =

stamps all zeros

## methods

### constructor methods
constructor need

```java
	@SuppressWarnings("unchecked")
	public Dijkstra(DiGraph<V, E> g, double startTime) {
		this.dist = new double[g.n()];
		this.stamps = new int[g.n()];
		this.items = new HeapItem[g.n()];
		this.pred = new DiGraphNode[g.n()];
		this.starttime = startTime;
	}
```
what are dist, stamps, items, pred, starttime?

### methods
1. public boolean run(DiGraphNode<V, E> source, NodeVisitor<DiGraphNode<V, E>> visitor, NodeIterator<V, E> nit)

```java
	currentStamp++;
	//
	dist[source.getId()] = starttime;
	//
	pred[source.getId()] = null;
	// 
	double weightOfArc; 
	// 
	MinHeap<DiGraphNode<V, E>> queue = new MinHeap<DiGraphNode<V, E>>();
	// this line creates a priority queue implemented by MinHeap.
	// think of MinHeap as a priority queue, No need to worry how it works for now.
	items[source.getId()] = queue.insertItem(starttime, source);
	// NOT SURE, items[source.getId()] is the root node of heap.
	stamps[source.getId()] = currentStamp;
	// 
	while (queue.size() > 0) {
	// if there is something thing in a queue.
		HeapItem<DiGraphNode<V, E>> item = queue.extractMin();
		// get a item with a minimum value, in the first while loop. It is a root node.
		DiGraphNode<V, E> u = item.getValue();
		// get a node u of type DiGraphNode<V, E>,. The first time I read, I am confused with this.
		curr_dist = item.getKey();
		if (!visitor.visit(u)) {
			return false;
		}
		>>>>>>>>>
		u - current node
		visitor - target
		visitor.visit(u) -> is the current node not the same as the target node?
		this typically returns True because they not the same.
		So, if (!visitor.visit(u)) always return false, except when they found.
		This is a terminate condition.
		break the while loop and return false, even though the number of queue is still greater than 0.
		<<<<<<<<<
		// NEED TO investigate NodeVisitor
		for (Iterator<DiGraphNode<V, E>> it = nit.getIterator(u); it.hasNext();) {
			DiGraphNode<V, E> v = it.next();
			weightOfArc = nit.getWeightOfCurrentArc(u, v);
			discoverNode(u, v, queue, dist[u.getId()] + weightOfArc);
		}
		// loop through NodeIterator
		// NEED TO investigate NodeIterator
		// Does this mean we are getting neighboring nodes of u node?
	}
	return true;
```
2. private void discoverNode(DiGraphNode<V, E> curr, DiGraphNode<V, E> target, MinHeap<DiGraphNode<V, E>> queue,
			double alt) {
// confusing

```java
		if (stamps[target.getId()] < currentStamp || alt < dist[target.getId()]) {
			dist[target.getId()] = alt;
			pred[target.getId()] = curr;
			if (stamps[target.getId()] < currentStamp || items[target.getId()] == null) {
				items[target.getId()] = queue.insertItem(alt, target);
			} else {
				queue.decreaseKey(items[target.getId()], alt);
			}
			stamps[target.getId()] = currentStamp;
		}
	}
```

