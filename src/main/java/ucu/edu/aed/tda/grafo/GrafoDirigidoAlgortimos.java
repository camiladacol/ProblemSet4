package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashMap;

public class GrafoDirigidoAlgortimos implements IDirectedGraphAlgorithms{
    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        Set<V> visitados = new HashSet<>();
        Map<V, Double> costos = new HashMap<>();
        Set<V> vertices = grafo.vertices();

       V verticeOrigen= grafo.buscarVertice(source); // obtenemos el vertice de origen a partir del criterio de busqueda
        

        visitados.add(verticeOrigen);

        for(V v : vertices) {
            Edge<V, D> arista = grafo.obtenerArista(verticeOrigen, v);
            if (arista != null) {
                costos.put(v, arista.dato().getWeight());
            } else {
                costos.put(v, Double.POSITIVE_INFINITY); // si no hay una arista, se asigna un costo infinito
            }
        }

        costos.put(verticeOrigen, 0.0); // el costo del vertice de origen es 0
        visitados.add(verticeOrigen); // agregamos origen a visitados


        while (visitados.size() < vertices.size()) {
                V w = null;
                double menorCosto = Double.POSITIVE_INFINITY;

                // elegir el vertice NO visitado con menor costo
                for (V v : vertices) {

                    if (!visitados.contains(v)
                            && costos.get(v) < menorCosto) {

                        menorCosto = costos.get(v);
                        w = v;
                    }
                }

                // si no encontramos ninguno salimos
                if (w == null) {
                    break;
                }

                // agregamos w a visitados
                visitados.add(w);

                // recorremos sucesores de w
                Set<V> sucesores =
                        grafo.successors(grafo.construirComparable(w));

                for (V v : sucesores) {

                    Edge<V, D> arista = grafo.obtenerArista(w, v);

                    if (arista != null) {

                        double nuevoCosto =
                                costos.get(w)
                                + arista.dato().getWeight();
                        if (nuevoCosto < costos.get(v)) {
                            costos.put(v, nuevoCosto);
                        }
                    }
                }
            }

            return null;
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        return null;
    }

    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        return 0;
    }

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        return List.of();
    }

    @Override
    public <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {

    }

    @Override
    public <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {

    }

    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        return List.of();
    }
}
