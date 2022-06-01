package application.view;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

public class CompteEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode em;
	private Client clientDuCompte;
	private CompteCourant compteEdite;
	private CompteCourant compteResult;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		//this.txtDecAutorise.focusedProperty().addListener((t, o, n) -> this.focusDecouvert(t, o, n));
		//this.txtSolde.focusedProperty().addListener((t, o, n) -> this.focusSolde(t, o, n));
	}

	public CompteCourant displayDialog(Client client, CompteCourant cpte, EditionMode mode) {
		this.clientDuCompte = client;
		this.em = mode;
		if (cpte == null) {
			this.compteEdite = new CompteCourant(0, 200, 0, "N", this.clientDuCompte.idNumCli);
		} else {
			this.compteEdite = new CompteCourant(cpte);
		}
		this.compteResult = null;
		this.txtIdclient.setDisable(true);
		this.txtIdAgence.setDisable(true);
		this.txtIdNumCompte.setDisable(true);
		switch (mode) {
		case CREATION:
			this.txtDecAutorise.setDisable(false);
			this.txtSolde.setDisable(false);
			this.lblMessage.setText("Informations sur le nouveau compte");
			this.lblSolde.setText("Solde (premier dépôt)");
			this.btnOk.setText("Ajouter");
			this.btnCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.txtDecAutorise.setDisable(false);
			this.txtSolde.setDisable(false);
			this.lblMessage.setText("Informations sur le nouveau compte");
			this.lblSolde.setText("Solde");
			this.btnOk.setText("Modifier");
			this.btnCancel.setText("Annuler");
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}

		// initialisation du contenu des champs
		this.txtIdclient.setText("" + this.compteEdite.idNumCli);
		this.txtIdNumCompte.setText("" + this.compteEdite.idNumCompte);
		this.txtIdAgence.setText("" + this.dbs.getEmpAct().idAg);
		this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
		this.txtSolde.setText(""+this.compteEdite.solde);

		this.compteResult = null;
		this.primaryStage.showAndWait();
		return this.compteResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private Label lblSolde;
	@FXML
	private TextField txtIdclient;
	@FXML
	private TextField txtIdAgence;
	@FXML
	private TextField txtIdNumCompte;
	@FXML
	private TextField txtDecAutorise;
	@FXML
	private TextField txtSolde;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Annule la procédure d'édition de compte
	 */
	@FXML
	private void doCancel() {
		this.compteResult = null;
		this.primaryStage.close();
	}

	/**
	 * Ajoute un compte à la liste des comptes de l'utilisateur
	 */
	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				if(Double.parseDouble(this.txtSolde.getText().trim())<-Integer.parseInt(this.txtDecAutorise.getText().trim())) {
					AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le solde doit être supérieur au débit autorisé",
							AlertType.WARNING);
				} else if(Double.parseDouble(this.txtSolde.getText().trim())<50) {
					AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le solde à la création d'un compte doit être au minimum de 50",
							AlertType.WARNING);
				} else {
					this.compteResult = this.compteEdite;
					this.primaryStage.close();
				}
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				if(Double.parseDouble(this.txtSolde.getText().trim())<-Integer.parseInt(this.txtDecAutorise.getText().trim())) {
					AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le solde doit être supérieur au débit autorisé",
							AlertType.WARNING);
				} else {
					this.compteResult = this.compteEdite;
					this.primaryStage.close();
				}
			}
		}
	}

	/**
	 * Regarde si la saisie des champs d'édition du client sont valides
	 * 
	 * @return boolean
	 */
	private boolean isSaisieValide() {
		if (this.txtDecAutorise.getText().isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le débit autorisé ne doit pas être vide",
					AlertType.WARNING);
			this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
			this.txtDecAutorise.setStyle("-fx-text-fill:red; -fx-border-color : red;");
			this.txtDecAutorise.requestFocus();
			return false;
		}
		this.txtDecAutorise.setStyle("-fx-text-fill:black; -fx-border-color : none;");
		if (this.txtSolde.getText().isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le solde ne doit pas être vide",
					AlertType.WARNING);
			this.txtSolde.setText(""+this.compteEdite.solde);
			this.txtSolde.setStyle("-fx-text-fill:red; -fx-border-color : red;");
			this.txtSolde.requestFocus();
			return false;
		}
		this.txtSolde.setStyle("-fx-text-fill:black; -fx-border-color : none;");
		try {
			this.compteEdite.debitAutorise = Integer.parseInt(this.txtDecAutorise.getText().trim());
		} catch (Exception e) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le débit autorisé doit être un entier",
					AlertType.WARNING);
			this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
			this.txtDecAutorise.setStyle("-fx-text-fill:red; -fx-border-color : red;");
			this.txtDecAutorise.requestFocus();
			return false;
		}
		this.txtDecAutorise.setStyle("-fx-text-fill:black; -fx-border-color : none;");
		try {
			this.compteEdite.solde = Double.parseDouble(this.txtSolde.getText().trim());
		} catch (Exception e) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le solde doit être un réel",
					AlertType.WARNING);
			this.txtSolde.setText(""+this.compteEdite.solde);
			this.txtSolde.setStyle("-fx-text-fill:red; -fx-border-color : red;");
			this.txtSolde.requestFocus();
			return false;
		}
		this.txtSolde.setStyle("-fx-text-fill:black; -fx-border-color : none;");
		return true;
	}
}
