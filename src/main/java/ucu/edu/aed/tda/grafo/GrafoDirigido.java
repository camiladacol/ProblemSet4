package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.edge.DirectedEdge;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.util.*;

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

    @Override
    public Set<V> vertices() {
        return Set.of();
    }

    @Override
    public Set<Edge<V, D>> aristas() {
        return Set.of();
    }

    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        return false;
    }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        return null;
    }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        return List.of();
    }

    @Override
    public boolean esConexo() {
        return false;
    }

    @Override
    public void vaciar() {

    }

    @Override
    public boolean tieneCiclos() {
        return false;
    }
}
