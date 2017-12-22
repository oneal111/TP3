package persistanceManager.Table;

import java.util.ArrayList;

import persistanceManager.Annotation.*;

@Bean(	table = "cours",
		pK = "coursid",
		instancesList = "listCours")
public class Cours {

	@Ignore
	public static final ArrayList<Cours> listCours = new ArrayList<>();

	private int coursid;
	private String name;
	private String sigle;
	private String description;
	@BeansList
	private ArrayList<Inscriptions> inscriptions;

	public Cours(String name, String sigle, String description) {
		this.name = name;
		this.sigle = sigle;
		this.description = description;
		listCours.add(this);
	}

	public Cours() {
		listCours.add(this);
	}

	@Override
	public String toString() {
		return coursid + " | " + name + " | " + sigle + " | " + description;
	}

	public ArrayList<Inscriptions> getRegistrations() {
		return inscriptions;
	}
	
	public void setRegistrations(ArrayList<Inscriptions> inscriptions){
		this.inscriptions = inscriptions;
	}
}
