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

        //Convertimos los vértices a una lista para poder accederlos por índice.
        //Floyd trabaja con matrices, por lo que necesitamos asociar cada vértice a una posición dentro de la matriz.

        List<V> vertices = new ArrayList<>(grafo.vertices());
        int n = vertices.size();
        double INF = Double.MAX_VALUE / 100;
        double[][] costos = new double[n][n]; //Matriz de costos minimos
        int[][] siguiente = new int[n][n];

        Map<V, Integer> posiciones = new HashMap<>();

        //Inicializamos las matrices. Asumimos que ningún vértice está conectado con otro.
        //La diagonal principal vale 0 porque el costo de ir a sí mismo es cero.
        for (int i = 0; i < n; i++) {
            posiciones.put(vertices.get(i), i);
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                costos[i][j] = INF;
                siguiente[i][j] = -1;
            }

            costos[i][i] = 0;
            siguiente[i][i] = i;
        }

        for (Edge<V, D> arista : grafo.aristas()) {
            int origen = posiciones.get(arista.source());
            int destino = posiciones.get(arista.target());

            costos[origen][destino] = arista.dato().getWeight();
            siguiente[origen][destino] = destino;
        }

        for (int k = 0; k < n; k++) { //k representa el vértice intermedio que estamos permitiendo usar
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (costos[i][k] != INF && costos[k][j] != INF) { //Verificamos que existan los caminos

                        double nuevoCosto = costos[i][k] + costos[k][j];

                        if (nuevoCosto < costos[i][j]) {
                            costos[i][j] = nuevoCosto;
                            siguiente[i][j] = siguiente[i][k];
                        }
                    }
                }
            }
        }

        return new IFloydWarshallResult<V>() {

            @Override
            public List<V> getPath(V source, V target) {
                Integer origen = posiciones.get(source);
                Integer destino = posiciones.get(target);

                if (origen == null || destino == null) {
                    return List.of();
                }

                if (siguiente[origen][destino] == -1) {
                    return List.of();
                }

                List<V> camino = new ArrayList<>();
                int actual = origen;

                camino.add(vertices.get(actual));

                while (actual != destino) {
                    actual = siguiente[actual][destino];

                    if (actual == -1) {
                        return List.of();
                    }

                    camino.add(vertices.get(actual));
                }

                return camino;
            }

            @Override
            public double getCost(V source, V target) {
                Integer origen = posiciones.get(source);
                Integer destino = posiciones.get(target);

                if (origen == null || destino == null) {
                    return INF;
                }
                // Floyd deja guardados los costos mínimos en la matriz costos[][]
                return costos[origen][destino];
            }

            @Override
            public boolean connected(V source, V target) {
                //Si el costo sigue siendo INF, significa que no existe camino.
                return getCost(source, target) != INF;
            }
        };
    }
    //Warshall no calcula costos mínimos.
    // conectado[i][j] = true / existe camino
    // conectado[i][j] = false / no existe camino

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {

        List<V> vertices = new ArrayList<>(grafo.vertices());
        int n = vertices.size();

        boolean[][] conectado = new boolean[n][n];
        int[][] siguiente = new int[n][n];

        Map<V, Integer> posiciones = new HashMap<>();

        for (int i = 0; i < n; i++) {
            posiciones.put(vertices.get(i), i);
        }

        for (int i = 0; i < n; i++) {
            Arrays.fill(siguiente[i], -1);

            conectado[i][i] = true;
            siguiente[i][i] = i;
        }

        for (Edge<V, D> arista : grafo.aristas()) {
            int origen = posiciones.get(arista.source());
            int destino = posiciones.get(arista.target());

            conectado[origen][destino] = true;
            siguiente[origen][destino] = destino;
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (!conectado[i][j] && conectado[i][k] && conectado[k][j]) {
                        conectado[i][j] = true;
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }

        return new IFloydWarshallResult<V>() {

            @Override
            public List<V> getPath(V source, V target) {
                Integer origen = posiciones.get(source);
                Integer destino = posiciones.get(target);

                if (origen == null || destino == null) {
                    return List.of();
                }

                if (!conectado[origen][destino]) {
                    return List.of();
                }

                List<V> camino = new ArrayList<>();
                int actual = origen;

                camino.add(vertices.get(actual));

                while (actual != destino) {
                    actual = siguiente[actual][destino];

                    if (actual == -1) {
                        return List.of();
                    }

                    camino.add(vertices.get(actual));
                }

                return camino;
            }

            @Override
            public double getCost(V source, V target) {
                return connected(source, target) ? 1 : 0;
            }

            @Override
            public boolean connected(V source, V target) {
                Integer origen = posiciones.get(source);
                Integer destino = posiciones.get(target);

                if (origen == null || destino == null) {
                    return false;
                }

                return conectado[origen][destino];
            }
        };
    }

    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {

        if (grafo.vertices().isEmpty()) {
            return null;
        }

        V centro = null;
        double excentriciadMinima = Double.MAX_VALUE;

        // Calculamos la excentricidad de cada vértice
        for (V nodo : grafo.vertices()) {
            double excentrididad = obtenerExcentricidad(grafo, (Comparable<V>) nodo);

            if (excentrididad < excentriciadMinima) {
                excentriciadMinima = excentrididad;
                centro = nodo;
            }
        }
    return centro;
    }

    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        V origen = grafo.buscarVertice(vertexCriteria);
        if (origen == null)
        {
            return 0;
        }

        IFloydWarshallResult<V> resultado = this.floyd(grafo);
        double costoMaximo = 0;
        for (V destino: grafo.vertices()) {
            if (!origen.equals(destino)) {
                double costo = resultado.getCost(destino, origen);
                if(costo> costoMaximo){
                    costoMaximo = costo;
                }
            }
        }
        return costoMaximo;
    }

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        List<Path<V>> resultado = new ArrayList<>();
        List<V> caminoActual = new ArrayList<>();

        V verticeInicial = grafo.buscarVertice(source);
        if (verticeInicial == null) return resultado;

        caminoActual.add(verticeInicial);
        dfs(verticeInicial, target, grafo, caminoActual, resultado, 0);

        return resultado;
    }

    private <V, D extends WeightedEdge> void dfs(V actual, Comparable<V> target, IGraph<V, D> grafo, List<V> caminoActual, List<Path<V>> resultado, double costoActual) {
        if (target.compareTo(actual) == 0 && caminoActual.size() > 1) {
            resultado.add(new Path<>(new ArrayList<>(caminoActual), costoActual));
            return;
        }

        for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(actual))) {
            V vecino = arista.target();
            if (!caminoActual.contains(vecino)) {
                caminoActual.add(vecino);
                dfs(vecino, target, grafo, caminoActual, resultado, costoActual + arista.dato().getWeight());
                caminoActual.remove(caminoActual.size() - 1);
            }
        }
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
