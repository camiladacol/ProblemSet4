package ucu.edu.aed.tda.grafo;

import java.util.*;
import java.util.function.Consumer;

import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

public class GrafoNoDirigidoAlgortimos implements IUndirectedGraphAlgorithm {

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(IUndirectedGraph<V, D> graph) {

        IUndirectedGraph<V, D> resultado = new GrafoNoDirigido<>();
        for (V vertice : graph.vertices()) {
            resultado.agregarVertice(vertice);
        }

        List<Edge<V, D>> aristas = new ArrayList<>(graph.aristas());
        aristas.sort(Comparator.comparingDouble(a -> a.dato().getWeight()));

        int n = graph.cantidadDeVertices() - 1;
        int i = 0;

        while (i < n && !aristas.isEmpty()) {

            Edge<V, D> arista = aristas.remove(0);
            resultado.agregarArista(arista.source(), arista.target(), arista.dato());
            if (resultado.tieneCiclos()) {

                resultado.eliminarArista(resultado.construirComparable(arista.source()),
                        resultado.construirComparable(arista.target()));
            } else {
                i++;
            }
        }
        return resultado;
    }

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(IUndirectedGraph<V, D> graph, Comparable<V> source) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> Edge<V, D> searchMinEdge(IUndirectedGraph<V, D> graph, Collection<V> U, Collection<V> V) {
        Edge<V, D> minEdge = null;
        for (V v : U) {
            for (V u : V) {
                Edge<V, D> edge = graph.obtenerArista(v, u);
                if (edge != null) {
                    if (minEdge == null || edge.dato().getWeight() < minEdge.dato().getWeight()) {
                        minEdge = edge;
                    }
                }
            }
        }
        return minEdge;
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
