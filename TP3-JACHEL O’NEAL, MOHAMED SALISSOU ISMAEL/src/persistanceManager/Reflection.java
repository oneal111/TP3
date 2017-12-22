package persistanceManager;

import com.google.inject.Guice;
import com.google.inject.Injector;

import persistanceManager.Table.*;
import persistanceManager.Annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reflection {

	private static Reflection inst = null;
	private static final String dbName = "postgres";
	private static final String user = "postgres";	
	private static final String pass = "lol";	
	private PersistanceManager connection = null;	
	
	private Reflection() {
		connection = new PersistanceManager(dbName, user, pass);
		connection.connect();	
	}

	public static final Reflection getInstance() {	//Reflection n'a qu'une inst
		if (inst == null) {
			inst = new Reflection();
		}
		return inst;
	}

	public PersistanceManager getDB() {
		return connection;
	}
	public void Retrieve() {
		try {
			System.out.println("\n-*********-Affichage -*********-\n");
			ArrayList<Etudiant> etudiants = connection.retrieve(Etudiant.class, "SELECT * FROM etudiant");
			for (Etudiant e : etudiants) {
				
				System.out.println(e);
				for (Inscriptions r : e.getInscription()) {
					System.out.println("\t"+r+"\n\t\t"+r.getCourse());
				}
				
			}
			System.out.println("\n-------------------------\n");
			ArrayList<Cours> cours = connection.retrieve(Cours.class, "SELECT * FROM cours");

			for (Cours c : cours) {
				System.out.println(c);
			}
			System.out.println("\n-------------------------\n");
			ArrayList<Inscriptions> inscriptions = connection.retrieve(Inscriptions.class, "SELECT * FROM inscription");

			for (Inscriptions r : inscriptions) {
				System.out.println(r);
				System.out.println("    " + r.getStudent());
				System.out.println("\t" + r.getCourse());
				
			}
			System.out.println("____________________________________________________\n");

		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
	}

	@Transactional
	public void insertion() throws PersistentException, SQLException, IllegalArgumentException, IllegalAccessException {
		
		Etudiant etudiant = new Etudiant(106, "Pomme", "Poire", 25);
		connection.insert(etudiant);
		
	}

	public static void main(String[] args) {
		getInstance().Retrieve();

		try {
			inst.connection.getConnection();
			inst.insertion();
		} catch (PersistentException | SQLException | IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();			
			} finally {
				getInstance().connection.close();	
			}
		}
}