package application;

import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;

public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;

	/**
	 * Retourne l'employe actif sur l'application
	 * 
	 * @return Employe getEmpAct
	 */
	public Employe getEmpAct() {
		return this.empAct;
	}
	
	/**
	 * Modifie l'employe actif sur l'application
	 * 
	 * @param employeActif L'emploie actif sur l'application
	 */
	public void setEmpAct(Employe employeActif) {
		this.empAct = employeActif;
	}
	
	/**
	 * Retourne l'agence bancaire actif sur l'application
	 * 
	 * @return AgenceBancaire agAct
	 */
	public AgenceBancaire getAgAct() {
		return this.agAct;
	}
	
	/**
	 * Modifie l'agence bancaire actif sur l'application
	 * 
	 * @param agenceActive l'agence bancaire actif sur l'application
	 */
	public void setAgAct(AgenceBancaire agenceActive) {
		this.agAct = agenceActive;
	}
	/** Regarde si l'employe est un chef d'agence
	 * 
	 * @return boolean 
	 */
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}
	/** Modifie le role de l'employe en tant que chef d'agence
	 * 
	 * @param isChefDAgence IN boolean qui dit si l'employe est chef d'agence
	 */
	public void setChefDAgence(boolean isChefDAgence) {
		this.isChefDAgence = isChefDAgence;
	}
	/** Modifie le role de l'employe en tant que chef d'agence s'il a les droits d'accès
	 * 
	 * @param droitsAccess IN boolean 
	 */
	public void setChefDAgence(String droitsAccess) {
		this.isChefDAgence = false;

		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.isChefDAgence = true;
		}
	}
}
