package persistanceManager.Table;

import java.util.ArrayList;

import persistanceManager.Annotation.*;

@Bean(	table = "inscription",
		pK = "inscriptionid",
		instancesList = "listInscriptions")
public class Inscriptions {

	@Ignore
	public static final ArrayList<Inscriptions> listInscriptions = new ArrayList<>();

	private int inscriptionid;
	private int etudiantid;
	private int coursid;
	@BeanInterne
	private Cours cours;
	@BeanInterne
	private Etudiant etudiant;

	public Inscriptions(int etudiantId, int coursId) {
		this.etudiantid = etudiantId;
		this.coursid = coursId;
		listInscriptions.add(this);
	}

	public Inscriptions() {
		listInscriptions.add(this);
	}

	@Override
	public String toString() {
		return inscriptionid + " | " + etudiantid + " | " + coursid;
	}

	public Cours getCourse() {
		return cours;
	}
	
	public Etudiant getStudent() {
		return etudiant;
	}
}
