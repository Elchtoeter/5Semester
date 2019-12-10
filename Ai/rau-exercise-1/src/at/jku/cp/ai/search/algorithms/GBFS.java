package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

// Greedy Best-First Search
public class GBFS implements Search
{
	@SuppressWarnings("unused")
	private Function<Node, Double> heuristic;

	public GBFS(Function<Node, Double> heuristic) {
		this.heuristic = heuristic;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		// we use a StablePriorityQueue to have the nodes inserted according to their costs
		// UCS always expands the node with the lowest path cost first and this data structure allows us to retrieve this node easily
		// The stable version of the priority queue guarantees that the order of same key value entries gets preserved
		// which allows us to have the same result every time.
		final StablePriorityQueue<Double, Node> fringe = new StablePriorityQueue<>();
		fringe.add(new Pair<>(heuristic.apply(start), start));
		// A set provides us the necessary contains() function on O(1)
		final Set<Node> closedNodes = new HashSet<>();
		closedNodes.add(start);
		while(!fringe.isEmpty()) {
			Node current = fringe.poll().s;
			if (endPredicate.test(current)) {
				return current;
			}
			for (Node n : current.adjacent()) {
				if (!closedNodes.contains(n)) {
					// if current Node isn't the target we add the adjecent nodes in order of the value of their heuristic function.
					fringe.add(new Pair<>(heuristic.apply(n), n));
					closedNodes.add(n);
				}
			}
		}
		return null;
	}
}
