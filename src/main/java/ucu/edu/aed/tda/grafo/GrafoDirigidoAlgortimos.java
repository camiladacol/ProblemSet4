package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.*;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
        V nodoInicial = grafo.buscarVertice(sourceCriteria);
        if (nodoInicial == null) {
            return; // Si el nodo de origen no existe, terminamos
        }
        Set <V> visitados = new HashSet<>();

        busquedaEnProfundidad(nodoInicial, visitados, grafo, consumer);
    }

    private <V, D> void busquedaEnProfundidad(V vertice, Set<V> visitados, IGraph<V, D> grafo, Consumer<V> consumer) {
        if  (visitados.contains(vertice)) {
            return;
        }
        visitados.add(vertice);
        consumer.accept(vertice);

        Comparable<V> verticesComparable = (Comparable<V>) vertice;
        for (Edge<V,D> arista : grafo.adyacencias(verticesComparable)) {
            V w = arista.target();
            busquedaEnProfundidad(w,visitados,grafo,consumer);
        }
    }

    @Override
    public <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        V origen = grafo.buscarVertice(sourceCriteria);

        if (origen == null) {
            return;
        }
        Set<V> visitados = new HashSet<>();
        Queue<V> cola = new LinkedList<>();
        visitados.add(origen);
        cola.offer(origen);

        while (!cola.isEmpty()) {
            V actual = cola.poll();
            consumer.accept(actual);

            for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(actual))) {
                V vecino = arista.target();

                if (visitados.add(vecino)) {
                    cola.offer(vecino);
                }
            }
        }
    }

    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        Set<V> visitados = new HashSet<>(); // set de visitados

        LinkedList<V> listaResultado = new LinkedList<>(); // la lista donde vamos a meter el resultado

        // Recorremos todos los vértices del grafo por si alguno esta desconectado
        for (V nodo : grafo.vertices()) {
            clasificacionTopologicaAux(nodo, visitados, listaResultado, grafo);
        }

        return new ArrayList<>(listaResultado);
    }

    private <V, D> void clasificacionTopologicaAux(V nodo, Set<V> visitados, LinkedList<V> lista, IDirectedIGraph<V, D> grafo) {

        // Si nodo no está en visitados
        if (!visitados.contains(nodo)) {

            // agregamos nodo a visitados
            visitados.add(nodo);

            Comparable<V> criterioActual = (Comparable<V>) nodo;
            for (Edge<V, D> arista : grafo.adyacencias(criterioActual)) {
                V w = arista.target();

                // Llamada recursiva
                clasificacionTopologicaAux(w, visitados, lista, grafo);
            }
            lista.addFirst(nodo);
        }
    }
}
