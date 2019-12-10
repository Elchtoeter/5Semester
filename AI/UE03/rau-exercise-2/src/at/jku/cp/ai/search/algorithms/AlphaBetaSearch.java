package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.AdversarialSearch;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.datastructures.Pair;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class AlphaBetaSearch implements AdversarialSearch {

    private BiPredicate<Integer, Node> searchLimitingPredicate;
    private Function<Node, Double> evalFunction;

    /**
     * To limit the extent of the search, this implementation should honor a
     * limiting predicate. The predicate returns 'true' as long as we are below the limit,
     * and 'false', if we exceed the limit.
     *
     * @param searchLimitingPredicate
     */
    public AlphaBetaSearch(BiPredicate<Integer, Node> searchLimitingPredicate) {
        this.searchLimitingPredicate = searchLimitingPredicate;
    }

    public Pair<Node, Double> search(Node start, Function<Node, Double> evalFunction) {
        this.evalFunction = evalFunction;

        Node result = start;
        double best = Double.NEGATIVE_INFINITY;

        for (Node node : start.adjacent()) {
            double val = min(node, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            if (val > best) {
                best = val;
                result = node;
            }
        }

        return new Pair<>(result, best);
    }

    private double max(Node current, int depth, double alpha, double beta) {
        if (current.isLeaf() || !searchLimitingPredicate.test(depth, current)) {
			return evalFunction.apply(current);
		}

        double best = alpha;
        double val;
        for (final Node node : current.adjacent()) {
            val = min(node, depth + 1, best, beta);
            if (val > best) {
                best = val;
                if (best >= beta) return best;
            }
        }
        return best;
    }

    private double min(Node current, int depth, double alpha, double beta) {
        if (current.isLeaf() || !searchLimitingPredicate.test(depth, current)) {
			return evalFunction.apply(current);
		}

        double best = beta;
        double val;
        for (final Node node : current.adjacent()) {
            val = max(node, depth + 1, alpha, best);
            if (val < best) {
                best = val;
                if (best <= alpha) return best;
            }
        }
        return best;
    }
}
