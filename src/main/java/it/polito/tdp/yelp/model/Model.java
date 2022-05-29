package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private Graph<User, DefaultWeightedEdge> grafo;
	List<User> utenti;
	
	
	public Model() {
		
		dao = new YelpDao();
		
	}
	
	public String creaGrafo(int minRevisioni, int anno) {   // Devo passargli solo le variabili che inserisco dal FXML controller
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Creo Vertici
		
		this.utenti = dao.getUsersWhitReviews(minRevisioni);
		
		Graphs.addAllVertices(this.grafo, this.utenti);
		
		//Creo Archi
		
		for(User u1 : this.utenti) {
			for(User u2 : this.utenti) {
				if(!u1.equals(u2) && u1.getUserId().compareTo(u2.getUserId()) < 0) {
					int sim = dao.calcolaSimilarita(u1, u2, anno);
					if(sim>0) {
						Graphs.addEdge(this.grafo, u1, u2,anno);
					}
				}
			}
		}
		
		return "Grafo creato con : " + this.grafo.vertexSet().size() + " vertici e " + this.grafo.edgeSet().size() + " archi";
		
	}
	
	// PUNTO D
	
	public List<User> getUsers() {
		return this.utenti;
	}
	
	
	public List<User> utentiPiuSimili(User u) {
		
		int max = 0;
		
		for(DefaultWeightedEdge e : this.grafo.edgesOf(u)) {
			if(this.grafo.getEdgeWeight(e) > max) {
				max = (int)this.grafo.getEdgeWeight(e);
			}
		}
		
		List<User> result = new ArrayList<>();

		for(DefaultWeightedEdge e : this.grafo.edgesOf(u)) {
			if(this.grafo.getEdgeWeight(e) == max) {
				User u2 = Graphs.getOppositeVertex(this.grafo, e, u);
				result.add(u2);
			}
		}
		
		return result;
		
	}
	
	
}
