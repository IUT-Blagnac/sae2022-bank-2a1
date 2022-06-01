package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.EmployesManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

public class EmployesManagementController implements Initializable {

	private DailyBankState dbs;
	private EmployesManagement em;

	private Stage primaryStage;

	private ObservableList<Employe> ole;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * Initialise le contexte de la fen�tre
	 * 
	 * @param _primaryStage La fen�tre parent de la fen�tre actuelle
	 * @param _em L'objet permettant de g�rer les employ�s
	 * @param _dbstate L'�tat de la banque actuel
	 * 
	 * (Antoine MAZEAU)
	 */
	public void initContext(Stage _primaryStage, EmployesManagement _em, DailyBankState _dbstate) {
		this.em = _em;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/**
	 * Configure l'affichage de la ListView des employ� ainsi que l'ObservableList qui les stocks
	 * 
	 * (Antoine MAZEAU)
	 */
	public void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.ole = FXCollections.observableArrayList();
		this.lvEmploye.setItems(this.ole);
		this.lvEmploye.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvEmploye.getFocusModel().focus(-1);
		this.lvEmploye.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	/**
	 * Réalise l'événement et le consomme
	 * 
	 * @param e L'événement à réaliser
	 * @return null
	 * 
	 * (Antoine MAZEAU)
	 */
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}


	/**
	 * Affiche la fenétre jusqu'à ce quelle ce ferme
	 * 
	 * (Antoine MAZEAU)
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	@FXML
	private ListView<Employe> lvEmploye;

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private Button btnModifEmploye;
	@FXML
	private Button btnDesactEmploye;
	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;

	/**
	 * Récupére les informations de recherche des champs de texte de l'affichage pour les envoyés à une fonction qui fait appelle
	 * à une autre fonction qui recherche dans les BD les employés correspondant
	 * 
	 * (Antoine MAZEAU)
	 */
	@FXML
	private void doRecherche() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtNum.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numCompte = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numCompte != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}


		ArrayList<Employe> listeEmploye = new ArrayList<Employe>();
		listeEmploye = this.em.getListeEmploye(numCompte, debutNom, debutPrenom,this.dbs.getEmpAct().idAg, this.dbs.getEmpAct());

		this.ole.clear();
		for (Employe cli : listeEmploye) {
			this.ole.add(cli);
		}


	}

	/**
	 * Récupére l'employé sélectionner via le curseur et l'envoie a une fonction qui va ouvrir une fenétre permettant de modifier ses informations
	 * 
	 * (Antoine MAZEAU)
	 */
	@FXML
	private void doModifierEmpl() {
		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe emplMod = this.ole.get(selectedIndice);
			Employe result = this.em.modifierEmploye(emplMod);
			if (result != null) {
				this.ole.set(selectedIndice, result);
			}
		}
	}

	/**
	 * Récupére l'employé sélectionner via le curseur et l'envoie a une fonction qui va le supprimer.
	 * 
	 * (Antoine MAZEAU)
	 */
	@FXML
	private void doSupprimerEmploye() {

		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe emplMod = this.ole.get(selectedIndice);
			Employe result = this.em.supprimerEmploye(emplMod);
			if (result != null) {
				this.doRecherche();
			}
		}
	}

	/**
	 * Appel une fonction qui va ouvrir une fenêtre de création d'un nouvel employé
	 * 
	 * (Antoine MAZEAU)
	 */
	@FXML
	private void doNouveauEmploye() {
		Employe employe;
		employe = this.em.nouveauEmploye();
		if (employe != null) {
			this.ole.add(employe);
		}
	}

	/**
	 * Valide l'état actuel du composant 
	 * Permet de bloquer/débloquer les boutons en fonction de si un employé est selectionné ou pas
	 * 
	 * (Antoine MAZEAU)
	 */
	private void validateComponentState() {

		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe e = this.lvEmploye.getItems().get(selectedIndice);
			if(e.motPasse == null || e.login == null) {
				this.btnModifEmploye.setDisable(true);
				this.btnDesactEmploye.setDisable(true);
			} else {
				this.btnModifEmploye.setDisable(false);
				this.btnDesactEmploye.setDisable(false);
			}
		} else {
			this.btnModifEmploye.setDisable(true);
			this.btnDesactEmploye.setDisable(true);
		}
	}
}