package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

// Uniform Cost Search
public class UCS1 implements Search
{
	@SuppressWarnings("unused")
	private Function<Node, Double> cost;

	public UCS1(Function<Node, Double> cost) {
		this.cost = cost;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		if (endPredicate.test(start)) return start;


		// we use a StablePriorityQueue to have the nodes inserted according to their costs
		// UCS always expands the node with the lowest path cost first and this data structure allows us to retrieve this node easily
		// The stable version of the priority queue guarantees that the order of same key value entries gets preserved
		// which allows us to have the same result every time.

		final StablePriorityQueue<Double, Node> fringe = new StablePriorityQueue<>();

		fringe.add(new Pair<>(cost.apply(start), start));

		// A set provides us the necessary contains() function on O(1)
		final Set<Node> visitedNodes = new HashSet<>();

		// For sorting the node has its costs paired
		Pair<Double, Node> curr;

		do {
			curr = fringe.poll();
			visitedNodes.add(curr.s);
			for (Node n : curr.s.adjacent()) {
				if (!visitedNodes.contains(n)) {
					if (endPredicate.test(n)) {
						return n;
					}
					fringe.offer(new Pair<>(curr.f + cost.apply(n), n)); //
					visitedNodes.add(n);
				}
			}
		} while (!fringe.isEmpty());
		return null;
	}
}
