package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmployeEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;

public class EmployeEditorPane {
	private Stage primaryStage;
	private EmployeEditorPaneController eepc;
	
	/** 
	 * Permet d'initiliser la fenetre qui permet de g�rer les employ�, la fenetre s'ouvre sur la fenetre parent 
	 *  + initialise l'objet dbs avec le DailikyBankState (�tat actuel de la banque) de l'application
	 * 
	 * @param _parentStage la fenetre parent
	 * @param _dbstate DailikyBankState actuel de l'application
	 * 
	 * (Antoine MAZEAU)
	 */
	public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("employeeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un employ�");
			this.primaryStage.setResizable(false);

			this.eepc = loader.getController();
			this.eepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Fait a une appel a une fonction qui permet d'afficher la fen�tre initialiser. Cette fenetre permet de modifier un employ�.
	 * En fonction de quel cas la fen�tre est ouverte, elle sera diff�rente :
	 * 	- modification : les informations actuelle de l'employ� seront pr�-remplies dans les cases
	 *  - cr�ation : aucune case ne vas �tre remplie
	 *  - suppresion : une nouvelle fen�tre de confirmation de suppresion va s'ouvrir
	 *  
	 * 
	 * @param employe l'employe qui est appeler
	 * @param em le mode d'�dition (modification, cr�ation ou suppresion)
	 * @return l'employer qui a subit les modification
	 * 
	 * (Antoine MAZEAU)
	 */
	public Employe doClientEditorDialog(Employe employe, EditionMode em) {
		return this.eepc.displayDialog(employe, em);
	}
}
