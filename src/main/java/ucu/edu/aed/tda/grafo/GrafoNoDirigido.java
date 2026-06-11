package ucu.edu.aed.tda.grafo;

import java.util.*;

import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.UndirectedEdge;

public class GrafoNoDirigido<V, D> implements IUndirectedGraph<V, D> {

    private final Map<V, Set<Edge<V, D>>> adyacencias;

    public GrafoNoDirigido() {
        this.adyacencias = new HashMap<>();
    }

    @Override
    public boolean agregarVertice(V vertex) {
        if (vertex == null || adyacencias.containsKey(vertex)) {
            return false;
        }
        adyacencias.put(vertex, new HashSet<>());
        return true;
    }

    @Override
    public V buscarVertice(Comparable<V> criterio) {
        for (V vertice: adyacencias.keySet()) {
            if (criterio.compareTo(vertice) == 0) {
                return vertice;
            }
        }
        return null;
    }

    @Override
    public boolean agregarArista(V source, V target, D dato) {
        if (!adyacencias.containsKey(source) || !adyacencias.containsKey(target)){
            return false;
        }
        Edge<V, D> aristaDeIda = new UndirectedEdge(source, target, dato);
        Edge<V,D> aristaDeVuelta = new UndirectedEdge(target, source, dato);

        boolean ida = adyacencias.get(source).add(aristaDeIda);
        boolean vuelta = adyacencias.get(target).add(aristaDeVuelta);

        return ida || vuelta;
    }

    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        V aristaOrigen = buscarVertice(source);
        V aristaDestino = buscarVertice(target);
        if (aristaOrigen == null || aristaDestino == null) {
            return false;
        }

        boolean ida = adyacencias.get(source).contains(aristaOrigen);
        boolean vuelta = adyacencias.get(target).contains(aristaDestino);

        return ida || vuelta;
    }

    private boolean eliminarAristaUnidireccional(V desde, Comparable<V> haciaCriterio) {

        Set<Edge<V, D>> aristas = adyacencias.get(desde);

        Iterator<Edge<V, D>> iterator = aristas.iterator();

        boolean eliminado = false;

        while (iterator.hasNext()) {
            Edge<V, D> arista = iterator.next();
            if (haciaCriterio.compareTo(arista.target()) == 0) {
                iterator.remove();
                eliminado = true;
            }
        }

        return eliminado;
    }

    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V verticeAEliminar = buscarVertice(criteria);
        if (verticeAEliminar == null) {
            return false;
        }

        adyacencias.remove(verticeAEliminar);  // borramos el vértice y sus conexiones salientes

        // limpiamos las conexiones que los vecinos tenían hacia este vértice
        for (Set<Edge<V, D>> conexiones : adyacencias.values()) {
            conexiones.removeIf(arista -> arista.target().equals(verticeAEliminar));
        }
        return true;
    }

    @Override
    public Set<V> vertices() {
        return Collections.unmodifiableSet(adyacencias.keySet());
    }

    @Override
    public Set<Edge<V, D>> aristas() {
        Set<Edge<V, D>> setDeAristas = new HashSet<>();

        for (Set<Edge<V, D>> aristas : adyacencias.values()) {
            setDeAristas.addAll(aristas);
        }

        return setDeAristas;
    }

    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V origen = buscarVertice(sourceCriteria);

        if (origen == null) return false;

        for (Edge<V, D> a : adyacencias.get(origen)) {
            if (targetCriteria.compareTo(a.target()) == 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V origen = buscarVertice(sourceCriteria);

        if (origen == null) return null;

        for (Edge<V, D> a : adyacencias.get(origen)) {
            if (targetCriteria.compareTo(a.target()) == 0) {
                return a;
            }
        }
        return null;
    }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        V vertice = buscarVertice(verticeCriteria);

        if (vertice != null) {
            return List.copyOf(adyacencias.get(vertice));
        }

        return List.of();
    }

    @Override
    public boolean esConexo() {
        List<V> todosLosVertices = new ArrayList<>(vertices());

        if (todosLosVertices.isEmpty()) return true;

        Set<V> visitados = new HashSet<>();
        Queue<V> cola = new LinkedList<>();

        cola.add(todosLosVertices.get(0));
        visitados.add(todosLosVertices.get(0));

        while (!cola.isEmpty()) {
            V actual = cola.poll();
            for (Edge<V, D> arista : adyacencias(construirComparable(actual))) {
                if (!visitados.contains(arista.target())) {
                    visitados.add(arista.target());
                    cola.add(arista.target());
                }
            }
        }

        return visitados.size() == todosLosVertices.size();
    }

    @Override
    public void vaciar() {
        adyacencias.clear();
    }

    @Override
    public boolean tieneCiclos() {
        Set<V> visitados = new HashSet<>();

        for (V vertice : adyacencias.keySet()) {
            if (!visitados.contains(vertice)) {
                if (buscarCicloNoDirigido(vertice, null, visitados)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean buscarCicloNoDirigido(V actual, V padre, Set<V> visitados) {

        visitados.add(actual);

        for (Edge<V, D> arista : adyacencias.get(actual)) {
            V vecino = arista.target();

            // si el vecino no está visitado, continuamos la busqueda en profundidad recursivamente
            if (!visitados.contains(vecino)) {
                if (buscarCicloNoDirigido(vecino, actual, visitados)) {
                    return true;
                }
            }
            // si el vecino fue visitado y no es nodod de origen, se detecta una arista de retroceso. Existe un ciclo
            else if (!vecino.equals(padre)) {
                return true;
            }
        }
        return false;
    }
}
