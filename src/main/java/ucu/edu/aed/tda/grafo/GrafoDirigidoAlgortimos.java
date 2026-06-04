package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.*;
import java.util.function.Consumer;

public class GrafoDirigidoAlgortimos implements IDirectedGraphAlgorithms{
    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
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
