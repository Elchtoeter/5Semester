package at.jku.cp.ai.search.algorithms;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

// Uniform Cost Search
public class UCS implements Search
{
	@SuppressWarnings("unused")
	private Function<Node, Double> cost;
	private Pair<Double, Node> temp;

	public UCS(Function<Node, Double> cost) {
		this.cost = cost;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate)
	{

if(endPredicate==null||cost==null) {
	return null;
}

final StablePriorityQueue<Double, Node> queue = new StablePriorityQueue<>();
final HashMap<Node,Double> visited = new HashMap<>();

		Pair<Double,Node> temp = new Pair<>(cost.apply(start),start);
		visited.put(temp.s, temp.f);
		while (temp != null){
			if(endPredicate.test(temp.s)){
				return temp.s;
			}

			for(Node x : temp.s.adjacent()){
				Double costTmp = temp.f + cost.apply(x);
				if(!visited.containsKey(x) || visited.get(x) > costTmp){
					queue.add(new Pair<>(costTmp, x));
					visited.put(x, costTmp);
				}
			}

			temp = queue.poll();
		}


		return null;
	}


}
