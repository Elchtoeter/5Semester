package at.jku.cp.ai.search.algorithms;

import java.util.function.BiPredicate;
import java.util.function.Function;

import at.jku.cp.ai.search.AdversarialSearch;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.datastructures.Pair;

public class MinMaxSearch implements AdversarialSearch {

	@SuppressWarnings("unused")
	private BiPredicate<Integer, Node> searchLimitingPredicate;
	private Function<Node,Double> evalFunction;

	/**
	 * To limit the extent of the search, this implementation should honor a
	 * limiting predicate. The predicate returns 'true' as long as we are below the limit,
	 * and 'false', if we exceed the limit.
	 *
	 * @param searchLimitingPredicate
	 */
	public MinMaxSearch(BiPredicate<Integer, Node> searchLimitingPredicate)
	{
		this.searchLimitingPredicate = searchLimitingPredicate;
	}

	public Pair<Node, Double> search(Node start, Function<Node, Double> evalFunction) {
		this.evalFunction = evalFunction;

		Node result = start;
		double best = Double.NEGATIVE_INFINITY;
		for (Node n : start.adjacent()) {
			final double val = min(n, 1);
			if (val > best) {
				best = val;
				result = n;
			}
		}
		return new Pair<>(result, best);
	}

	private double min(Node current, int depth) {
		if (current.isLeaf() || !searchLimitingPredicate.test(depth, current)) {
			return evalFunction.apply(current);
		}

		double val = Double.POSITIVE_INFINITY;
		for (Node n : current.adjacent()) {
			val = Math.min(val, max(n, depth+1));
		}
		return val;
	}

	private double max(Node current, int depth) {
		if (current.isLeaf() || !searchLimitingPredicate.test(depth, current)) {
			return evalFunction.apply(current);
		}

		double val = Double.NEGATIVE_INFINITY;
		for (Node n : current.adjacent()) {
			val = Math.max(val, min(n, depth+1));
		}
		return val;
	}
}
