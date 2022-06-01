package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmployesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.AccessEmploye;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployesManagement {
	private Stage primaryStage;
	private DailyBankState dbs;
	private EmployesManagementController cme;

	/** 
	 * Permet d'initiliser la fenetre qui permet de g�rer les employ�, la fenetre s'ouvre sur la fenetre parent 
	 *  + initialise l'objet dbs avec le DailikyBankState actuel de l'application
	 * 
	 * @param _parentStage la fenetre parent
	 * @param _dbstate DailikyBankState actuel de l'application
	 * 
	 * (Antoine MAZEAU)
	 */
	public EmployesManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("employesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des employes");
			this.primaryStage.setResizable(false);

			this.cme = loader.getController();
			this.cme.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			System.out.println("erreur");
			e.printStackTrace();
		}
	}

	/** 
	 * Fait appel � une fonction qui r�cup�re la liste des employ�s correspondant aux crit�res de recherche (id, nom, pr�nom, idAg)
	 * + envoie l'employer connecter a la fonction pour qu'il soit ignorer lors des recherches des employ�s
	 * Si aucun crit�re renseigner, tout les employ�s seront affich�
	 * 
	 * @param _idEmpl l'id de l'employ� rechercher
	 * @param _debNom le nom/d�but du nom de l'employ� rechercher
	 * @param _debPrenom le pr�nom/d�but du pr�nom de l'employ� rechercher
	 * @param _idAg l'id de l'agence de l'employ� rechercher
	 * @param _employeConnecter l'employ� connecter
	 * @return la liste des employ�s trouv�s
	 * 
	 * (Antoine MAZEAU)
	 */
	public ArrayList<Employe> getListeEmploye(int _idEmpl, String _debNom, String _debPrenom,int _idAg, Employe _employeConnecter){
		ArrayList<Employe> listeEmploye = new ArrayList<Employe>();

		try {
			AccessEmploye ae = new AccessEmploye();
			listeEmploye = ae.getEmployes(_idEmpl,_debNom,_debPrenom,_idAg, _employeConnecter);
		} catch (DatabaseConnexionException e) {
			e.printStackTrace();
		}

		return listeEmploye;
	}

	
	/**
	 * Appel une fonction permettant d'afficher la fen�tre
	 */
	public void doEmployesManagementDialog() {
		this.cme.displayDialog();
	}

	/**
	 * Modiefie les informations d'un employ� en faisant appel a une autre fonction et le renvoie
	 * 
	 * @return l'employ� cr�e
	 * 
	 * (Antoine MAZEAU)
	 */
	public Employe modifierEmploye(Employe empl) {
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dbs);
		Employe result = eep.doClientEditorDialog(empl, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessEmploye ae = new AccessEmploye();
				ae.updateEmploye(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				result = null;

			}
		}
		return result;
	}
	
	/**
	 * Cr�er un nouveau employ� en faisant appel a une autre fonction et le renvoie
	 * 
	 * @return l'employ� cr�e
	 * 
	 * (Antoine MAZEAU)
	 */
	public Employe nouveauEmploye() {
		Employe employe;
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dbs);
		employe = eep.doClientEditorDialog(null, EditionMode.CREATION);
		if (employe != null) {
			try {
				AccessEmploye ae = new AccessEmploye();
				ae.insertEmploye(employe);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		}
		return employe;
	}
	
	/**
	 * Supprime un employ� en faisant appel a une autre fonction et le renvoie
	 * 
	 * @param empl L'employ� � supprimer
	 * 
	 * @return l'employ� cr�e
	 * 
	 * (Antoine MAZEAU)
	 */
	public Employe supprimerEmploye(Employe empl) {
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dbs);
		Employe result = eep.doClientEditorDialog(empl, EditionMode.SUPPRESSION);
		if (result != null) {
			try {
				AccessEmploye ae = new AccessEmploye();
				ae.deleteEmploye(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				result = null;

			}
		}
		return result;
	}
}
