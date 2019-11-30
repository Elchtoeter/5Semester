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
	public MinMaxSearch(BiPredicate<Integer, Node> slp)
	{
		this.searchLimitingPredicate = slp;
	}

	public Pair<Node, Double> search(Node start, Function<Node, Double> evalFunction) {
		this.evalFunction = evalFunction;

		Node result = start;
		double best = Double.NEGATIVE_INFINITY;
		for (final Node n : start.adjacent()) {
			final double val = min(n, 1);
			if (val > best) {
				best = val;
				result = n;
			}
		}
		return new Pair<Node, Double>(result, best);
	}

	private double min(Node current, int depth) {
		if (!searchLimitingPredicate.test(depth, current) || current.isLeaf())
			return evalFunction.apply(current);

		double val = Double.NEGATIVE_INFINITY;
		for (final Node n : current.adjacent()) {
			val = Math.max(val, max(n, depth + 1));
		}
		return val;
	}

	private double max(Node current, int depth) {
		if (!searchLimitingPredicate.test(depth, current) || current.isLeaf())
			return evalFunction.apply(current);

		double val = Double.POSITIVE_INFINITY;
		for (final Node n : current.adjacent()) {
			val = Math.min(val, min(n, depth + 1));
		}
		return val;
	}
}
