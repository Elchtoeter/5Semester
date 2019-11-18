package at.jku.cp.ai.search.algorithms;

import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

// Iterative Deepening Search
public class IDS implements Search
{
	@SuppressWarnings("unused")
	private int limit;

	public IDS(int limit)
	{
		this.limit = limit;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		Node result = null;
		for (int depth = 0; depth <= limit && result == null; depth++) {
			result = new DLDFS(depth).search(start, endPredicate);
		}
		return result;
	}

}
