package ucu.edu.aed.tda.grafo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ucu.edu.aed.tda.grafo.model.edge.DirectedEdge;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

public class GrafoDirigido<V, D> implements IDirectedIGraph<V, D>{

    public GrafoDirigido(){
        this.adyacencias = new HashMap<>();
    }

    private final Map<V, Set<Edge<V, D>>> adyacencias;

    @Override
    public Set<V> successors(Comparable<V> criteria) {
        return Set.of();
    }

    @Override
    public Set<V> predecessors(Comparable<V> criteria) {
        return Set.of();
    }

    @Override
    public boolean agregarVertice(V vertex) {
        if (vertex == null) {
            return false;
        }
        if (adyacencias.containsKey(vertex)) {
            return false;
        }
        adyacencias.put(vertex, new HashSet<>()); // iniciamos el conjunto de aristas para el nuevo vertice
        return true; // deveuvle true si el vertice se agregó
    }

    @Override
    public V buscarVertice(Comparable<V> criterio) {
       for (V v : adyacencias.keySet()) { // recorre todos los vertices del mapa de adyacencias
           if (criterio.compareTo(v) == 0) {
               return v; // retorna el vertice si coincide segun el criterio
           }
       }
       return null;
    }

    @Override
    public boolean agregarArista(V source, V target, D dato) {

        // verifica la existencia de amsbos vertices en el grafo
        if (!adyacencias.containsKey(source)) {
            return false;
        }

        if (!adyacencias.containsKey(target)) {
            return false;
        }

        // creamos la nueva arista con sentido
        Edge <V,D> aristaNueva = new DirectedEdge<>(source, target, dato);

        // conjunto de aristas que parten del vertice de origen
        Set<Edge<V, D>> conexionesSource = adyacencias.get(source);

        if (conexionesSource.add(aristaNueva)) { // .add devuvelve false si el elemtno ya exise en el conjutno
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {

        // intentamos encontrar el vertice de origen usando el criterio
      V origen = buscarVertice(source);
      if (origen == null) {
          return false;
      }

      // Obtiene el conjutno de aristas del vertice de origen
      Set<Edge<V, D>> aristas = adyacencias.get(origen);
      boolean eliminado = false;

      // usamos iterator para poder remover elemntos de forma segura mientras recorremos la coleccion
      Iterator<Edge<V, D>> iterator = aristas.iterator();
      while (iterator.hasNext()) {
          Edge<V, D> arista = iterator.next();

          // si el destino de la arista actual coincide con el criterio buscado, esta se elimina
          if(target.compareTo(arista.target()) == 0)
          {
              iterator.remove(); // elimina a travez del iterator (segura)
              eliminado = true; // nos dice que al menos un elemento fue elikmiando

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

        // eliminamos el vertice del mapa de adyacencias (con esto borramos el vertice y las aristas salientes)
        adyacencias.remove(verticeAEliminar);


        // recorremos el grafo para eloiminar las aristas entrantes hacia el vertice
        for (V v : adyacencias.keySet()) {
            Set<Edge<V, D>> conexiones = adyacencias.get(v);
            // usamos iterador para poder eliminar de una forma segurda
            Iterator<Edge<V, D>> iterator = conexiones.iterator();
            while (iterator.hasNext()) {
                Edge<V, D> arista = iterator.next();

                // si la arista apunta al certice a eliminar la borramos
                if (arista.target().equals(verticeAEliminar)) {
                    iterator.remove();
                }
            }

        }
        return true; // si el vertice fue encontrado y eliminado retormanos true
    }

    /**
     * Conjunto de vértices (preferible devolver vista inmodificable).
     */
    @Override
    public Set<V> vertices() {
        return adyacencias.keySet();
    }

    /**
     * Conjunto de aristas (preferible vista inmodificable).
     */
    @Override
    public Set<Edge<V, D>> aristas() {
        Set<Edge<V, D>> setDeAristas = new HashSet<>(); //inicias un set de aristas vacio
        for (V v : adyacencias.keySet()) { //recorres todos los vertices
            Set<Edge<V, D>> aristas = adyacencias.get(v); //para cada vertice obtenes sus aristas
            setDeAristas.addAll(aristas); //agregas las aristas al set
        }
        return setDeAristas;
    }

    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        for (V v : vertices()) { //recorro todos los vertices
            if (sourceCriteria.compareTo(v) == 0) { //veo si existe el origen
                Set<Edge<V, D>> aristas = adyacencias.get(v); //obetnemos aristas del vertice
                for (Edge<V, D> a : aristas) {
                    if (targetCriteria.compareTo(a.target()) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        for (V v : vertices()) {
            if (sourceCriteria.compareTo(v) == 0) {
                Set<Edge<V, D>> aristas = adyacencias.get(v);
                for (Edge<V, D> a : aristas) {
                    if (targetCriteria.compareTo(a.target()) == 0) {
                        return a;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        for (V v : vertices()) {
            if(verticeCriteria.compareTo(v) == 0){
                Set<Edge<V, D>> aristas = adyacencias.get(v);
                return List.copyOf(aristas);
            }
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
            for (Edge<V, D> arista : adyacencias.get(actual)) {
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

        for (V v : adyacencias.keySet()) {
            if (buscarCiclo(v, visitados, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    private boolean buscarCiclo (V v, Set<V> visitados, Set<V> caminoActual) {
        if (caminoActual.contains(v)) {
            return true;
        }
        if  (visitados.contains(v)) {
            return false;
        }
        visitados.add(v);
        caminoActual.add(v);

        for (Edge<V, D> arista : adyacencias.get(v)) {
            if (buscarCiclo(arista.target(), visitados, caminoActual)) {
                return true;
            }
        }
        caminoActual.remove(v);
        return false;
    }
}
