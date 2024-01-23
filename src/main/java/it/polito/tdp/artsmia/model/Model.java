package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<ArtObject, DefaultWeightedEdge> graph;
	private List<ArtObject> allNodes;
	private Map<Integer, ArtObject> idMap;
	ArtsmiaDAO dao;
	
	public Model() {
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.allNodes = new ArrayList<>();
		this.dao = new ArtsmiaDAO();
		this.idMap = new HashMap<>();
	}
	
	public void loadNodes() {
		if(this.allNodes.isEmpty()) {
			this.allNodes = this.dao.listObjects();
		}
		
		if(this.idMap.isEmpty()) {
			for(ArtObject a : allNodes) {
				this.idMap.put(a.getId(), a);
			}
		}
	}
	
	public void creaGrafo() {
		loadNodes();
		
		Graphs.addAllVertices(this.graph, allNodes);
		
//		metodo utile su database piccoli
//		for(ArtObject a1 : this.allNodes) {
//			for(ArtObject a2 : this.allNodes) {
//				int peso = this.dao.getWeight(a1.getId(), a2.getId());
//				Graphs.addEdgeWithVertices(this.graph, a1, a2, peso);
//			}
//		}
		
//		metodo utile su database grandi
		List<edgeModel> allEdges = this.dao.getAllWeights(idMap);
		for(edgeModel e : allEdges) {
			Graphs.addEdgeWithVertices(this.graph, e.getSource(), e.getTarget(), e.getPeso());
		}
		
		System.out.println("Il grafo contiene " + this.graph.vertexSet().size() + " vertici.");
		System.out.println("Il grafo contiene " + this.graph.edgeSet().size() + " archi.");

	}
	
	public boolean isIdInGraph(Integer objId) {
		if(this.idMap.get(objId) != null) {
			return true;
		}
		return false;
	}
	
	public Integer calcolaConnessa(Integer objId) {
		//crea un iteratore che esplora il grafo in profondità e ritorna un set di nodi
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> iterator = new DepthFirstIterator<>(this.graph ,this.idMap.get(objId));
		
		List<ArtObject> compConnessa = new ArrayList<>();
		while(iterator.hasNext()) {
		    compConnessa.add(iterator.next());
		}
		
		//secondo metodo che fa la stessa cosa delle righe sopra scritte
		ConnectivityInspector<ArtObject, DefaultWeightedEdge> inspector = new ConnectivityInspector<>(this.graph);
		Set<ArtObject> setConnesso = inspector.connectedSetOf(this.idMap.get(objId));
		
//		return compConnessa.size();
		return setConnesso.size();
	}

}
