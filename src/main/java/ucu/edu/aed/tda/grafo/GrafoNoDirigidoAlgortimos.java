package ucu.edu.aed.tda.grafo;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

public class GrafoNoDirigidoAlgortimos implements IUndirectedGraphAlgorithm {
    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(IUndirectedGraph<V, D> graph) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(IUndirectedGraph<V, D> graph, Comparable<V> source) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> Edge<V, D> searchMinEdge(IUndirectedGraph<V, D> graph, Collection<V> U, Collection<V> V) {
        return null;
    }

    @Override
    public <V, D> void bea(IUndirectedGraph<V, D> graph, Consumer<V> consumer) {

        Set<V> visitados = new HashSet<>();
        Queue<V> cola = new LinkedList<>();

        for (V v : graph.vertices()) {

            if (!visitados.contains(v)) { //Si el vertice todavia no fue visitado, lo agregamos a la cola y al conjunto de visitados

                cola.add(v);
                visitados.add(v);

                while (!cola.isEmpty()) { //Mientras existan vertices pendientes en la cola, se desencolan y se procesan sus adyacencias

                    V x = cola.remove();

                    consumer.accept(x);

                    for (Edge<V, D> arista :
                            graph.adyacencias(graph.construirComparable(x))) { //recorre las adyacencias del vertice x, y si encuentra un vertice y que no ha sido visitado, lo agrega a la cola y al conjunto de visitados

                        V y = arista.target();

                        if (!visitados.contains(y)) {

                            cola.add(y);
                            visitados.add(y);
                        }
                    }
                }
            }
        }
    }
}
