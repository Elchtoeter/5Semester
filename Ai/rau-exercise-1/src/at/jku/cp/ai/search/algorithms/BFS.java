package at.jku.cp.ai.search.algorithms;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;


// Breadth-First search
public class BFS implements Search {


    @Override
    public Node search(Node start, Predicate<Node> endPredicate) {
        final Queue<Node> queue = new LinkedList<>();
        final HashSet<Node> visited = new HashSet<>();

        if (endPredicate == null) {
            return null;
        }

        Node tmp;
        visited.add(tmp = start);
        while (tmp != null) {
            if (endPredicate.test(tmp)) {
                return tmp;
            }

            for (Node x : tmp.adjacent()) {
                if (!visited.contains(x)) {
                    queue.add(x);
                    visited.add(x);
                }
            }

            tmp = queue.poll();
        }

        return null;


    }
}
