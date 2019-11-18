package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;


// Breadth-First search
public class BFS1 implements Search
{
	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		if (endPredicate.test(start)) return start;

		// Fast insert at back & remove at front
		final Queue<Node> fringe = new LinkedList<>();
		fringe.add(start);
		// ClosedList as set -> all operations in O(1)
		final Set<Node> closed = new HashSet<>();
		Node curr;

		do {
			// dequeue first node
			curr = fringe.poll();
			for (Node n : curr.adjacent()) {
				if (!closed.contains(n)) {
					if (endPredicate.test(n)) return n;
					// queue up node (at back)
					fringe.offer(n);
					// mark as visited but not a solution
					closed.add(n);
				}
			}
		} while (!fringe.isEmpty());
		return null;
	}
}