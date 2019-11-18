package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

// A* Search
public class ASTAR implements Search {
    @SuppressWarnings("unused")
    private Function<Node, Double> heuristic;
    @SuppressWarnings("unused")
    private Function<Node, Double> cost;

    public ASTAR(Function<Node, Double> heuristic, Function<Node, Double> cost) {
        this.heuristic = heuristic;
        this.cost = cost;
    }


    @Override
    public Node search(Node start, Predicate<Node> endPredicate) {
        // we use a StablePriorityQueue to have the nodes inserted according to their costs
        // ASTAR always expands the node with the lowest path and heuristic cost first and this data structure allows us to retrieve this node easily
        // The stable version of the priority queue guarantees that the order of same key value entries gets preserved
        // which allows us to have the same result every time.
        final StablePriorityQueue<HeuristicsAndCosts, Node> fringe = new StablePriorityQueue<>();
        fringe.add(new Pair<>(new HeuristicsAndCosts(heuristic.apply(start), cost.apply(start)), start));

		// A set provides us the necessary contains() function in O(1)
        final Set<Node> closedNodes = new HashSet<>();

        Pair<HeuristicsAndCosts, Node> curr;
        while (!fringe.isEmpty()) {
            curr = fringe.poll();
            if (endPredicate.test(curr.s)) {
				return curr.s;
			}
            closedNodes.add(curr.s);
            for (Node n : curr.s.adjacent()) {
                if (!closedNodes.contains(n)) {
                	// the cost of nodes is calculated as the cost of the current Node plus the sum of the predecessor nodes
					// as stored in the immediate predecessor
                    fringe.add(new Pair<>(new HeuristicsAndCosts(heuristic.apply(n), curr.f.costs + cost.apply(n)), n));
                }
            }
        }
        return null;
    }


    // we need to store both heuristics and cost totals as well as costs per node in order to properly calculate costs from start
    private static class HeuristicsAndCosts implements Comparable<HeuristicsAndCosts> {
    	final double costs;
    	final double total;
    	private HeuristicsAndCosts(double heuristics, double costs){
    		this.costs = costs;
    		this.total = costs + heuristics;
		}

		@Override
		public int compareTo(HeuristicsAndCosts o) {
			return Double.compare(this.total,o.total);
		}
	}
}
