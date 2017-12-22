package persistanceManager.Table;

import java.util.ArrayList;

import persistanceManager.Annotation.Bean;
import persistanceManager.Annotation.BeansList;
import persistanceManager.Annotation.Ignore;

@Bean(	table = "etudiant",
		pK = "etudiantid",
		instancesList = "listEtudiants")
public class Etudiant {

	@Ignore
	public static final ArrayList<Etudiant> listEtudiants = new ArrayList<>();

	private static int etudiantid;
	private static String fname;
	private static String lname;
	private static int age;
	@BeansList
	private ArrayList<Inscriptions> inscriptions;

	public Etudiant(int etudiantid, String fname, String lname, int age) {
		this.etudiantid = etudiantid;
		this.fname = fname;
		this.lname = lname;
		this.age = age;
		listEtudiants.add(this);
	}

	public Etudiant() {
		listEtudiants.add(this);
	}

	@Override
	public String toString() {
		return etudiantid + " | " + fname + " | " + lname + " | " + age;
	}

	public ArrayList<Inscriptions> getInscription() {
		return inscriptions;
	}
	
	public void setInscription(ArrayList<Inscriptions> inscriptions){
		this.inscriptions = inscriptions;
	}

	public static int getEtudiantid() {
		return etudiantid;
	}

	public static String getFname() {
		return fname;
	}

	public static String getLname() {
		return lname;
	}

	public static int getAge() {
		return age;
	}
}
