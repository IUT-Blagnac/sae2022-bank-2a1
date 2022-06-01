package model.data;

public class Employe {

	public int idEmploye;
	public String nom, prenom, droitsAccess;
	public String login, motPasse;

	public int idAg;

	public Employe(int idEmploye, String nom, String prenom, String droitsAccess, String login, String motPasse,
			int idAg) {
		super();
		this.idEmploye = idEmploye;
		this.nom = nom;
		this.prenom = prenom;
		this.droitsAccess = droitsAccess;
		this.login = login;
		this.motPasse = motPasse;
		this.idAg = idAg;
	}

	public Employe(Employe e) {
		this(e.idEmploye, e.nom, e.prenom, e.droitsAccess, e.login, e.motPasse, e.idAg);
	}

	public Employe() {
		this(-1000, null, null, null, null, null, -1000);
	}

	@Override
	public String toString() {
		//return "Employe [idEmploye=" + this.idEmploye + ", nom=" + this.nom + ", prenom=" + this.prenom+ ", droitsAccess=" + this.droitsAccess + ", login=" + this.login + ", motPasse=" + this.motPasse+ ", idAg=" + this.idAg + "]";

		if (this.motPasse == null || this.login == null ) {
			return "[DESACTIVER]"+" "+this.nom+", "+this.prenom+", Droit access : "+this.droitsAccess+", Agence : "+this.idAg;
		}
		return "["+this.idEmploye+"]"+" "+this.nom+", "+this.prenom+", Droit access : "+this.droitsAccess+", login : "+this.login+", mdp : "+this.motPasse+", Agence : "+this.idAg;
	}

}
