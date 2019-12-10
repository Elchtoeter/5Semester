package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.StackWithFastContains;

// Depth-Limited Depth-First Search
public class DLDFS implements Search {
	// since we used a recursive implementation we used only a set for current Path avoidance.
	private Set<Node> currentPath;
	private int limit;

	public DLDFS(int limit) {
		this.limit = limit;
		this.currentPath = new HashSet<>();
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		currentPath.clear();
		return dls(start, endPredicate, limit);
	}

	private Node dls(Node root, Predicate<Node> endPredicate, int currLimit) {
		if (endPredicate.test(root)) {
			return root;
		}
		currentPath.add(root);
		if (currLimit > 0) {
			for (Node n : root.adjacent()) {
				if (!currentPath.contains(n)) {
					final Node result = dls(n, endPredicate, currLimit - 1);
					if (result != null) {
						return result;
					}
				}
			}
		}
		currentPath.remove(root);
		return null;
	}
}