package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.tools.CategorieOperation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.AccessCompteCourant;
import model.orm.AccessOperation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class ComptesManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private ComptesManagement cm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> olCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cm = _cm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.olCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Permet de retourner à la page de gestion des clients
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Affiche la fenetre des opérations du compte selectionner a l'activation du bouton
	 */
	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			this.cm.gererOperations(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}

	/**
	 * Permet la modification d'un compte
	 * @throws DatabaseConnexionException 
	 * @throws DataAccessException 
	 * @throws RowNotFoundOrTooManyRowsException 
	 * @throws ManagementRuleViolation 
	 */
	@FXML
	private void doModifierCompte() throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException, ManagementRuleViolation {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cp = this.olCompteCourant.get(selectedIndice);
			double acSolde = cp.solde;
			CompteCourant result = this.cm.modifierCompte(cp);
			if (result != null) {
				AccessOperation ao = new AccessOperation();
				if(acSolde>result.solde) {
					ao.insertDebit(result.idNumCompte, (acSolde-result.solde), "Retrait Espèces");
				} else {
					ao.insertDebit(result.idNumCompte, (result.solde-acSolde), "Dépôt Espèces");
				}
				this.olCompteCourant.set(selectedIndice, result);
			}
		}
	}

	/**
	 * Permet la supression d'un compte si et seulement si le solde vaut 0
	 */
	@FXML
	private void doSupprimerCompte() throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException, ManagementRuleViolation {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cp = this.lvComptes.getItems().get(selectedIndice);
			if(cp.solde==0) {
				Alert a = new Alert(AlertType.CONFIRMATION, "Voulez vous vraiment supprimer le compte n°"+cp.idNumCompte+" ?", ButtonType.YES, ButtonType.NO);
				a.setTitle("Suppression d'un compte");

				ButtonType result = a.showAndWait().orElse(ButtonType.NO);

				if (ButtonType.YES.equals(result)) {
					AccessCompteCourant ac = new AccessCompteCourant();
					ac.cloturerCompteCourant(cp);
					this.validateComponentState();
				}
			} else {
				if(cp.solde>0) {
					Alert a = new Alert(AlertType.WARNING, "Ce compte contient actuellement "+cp.solde+" euros.\nVotre solde doit être de 0 avant de pouvoir cloturer le compte.");
					a.setTitle("Suppression d'un compte");
					a.showAndWait();
				} else {
					Alert a = new Alert(AlertType.WARNING, "Ce compte contient actuellement un solde négatif ("+cp.solde+") euros.\nVotre solde doit être de 0 avant de pouvoir cloturer le compte.");
					a.setTitle("Suppression d'un compte");
					a.showAndWait();
				}
			}
		}
	}

	/**
	 * Permet la création d'un compte, ouvre la fenetre de création 
	 */
	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cm.creerCompte();
		if (compte != null) {
			this.olCompteCourant.add(compte);
		}
	}

	private void loadList () {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cm.getComptesDunClient();
		this.olCompteCourant.clear();
		for (CompteCourant co : listeCpt) {
			this.olCompteCourant.add(co);
		}
	}

	/**
	 * Rend activable les différents boutons si un compte est selectionné et non cloturé
	 */
	private void validateComponentState() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cp = this.lvComptes.getItems().get(selectedIndice);
			if(cp.estCloture.compareTo("O")==0) {
				this.btnVoirOpes.setDisable(true);
				this.btnSupprCompte.setDisable(true);
				this.btnModifierCompte.setDisable(true);
			} else {
				this.btnSupprCompte.setDisable(false);
				this.btnVoirOpes.setDisable(false);
				this.btnModifierCompte.setDisable(false);
			}
		} else {
			this.btnSupprCompte.setDisable(true);
			this.btnVoirOpes.setDisable(true);
			this.btnModifierCompte.setDisable(true);
		}
	}
}
