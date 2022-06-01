package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

public class EmployeEditorPaneController implements Initializable {


	private DailyBankState dbs;

	private Stage primaryStage;

	private Employe employeEdite;
	private EditionMode em;
	private Employe employeResult;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Initialise le contexte de la fenetre
	 * 
	 * @param _primaryStage la fenetre principale
	 * @param _dbstate l'�tat de la banque actuel
	 * 
	 * v
	 */
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/**
	 * Configure l'option qui permet de fermer la fenetre
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * Affiche la fen�tre d'alt�ration d'un employ� initialiser pr�c�demment.
	 * Cette fenetre sera diff�rente en fonction de quel mode d'�dition elle est ouvert.
	 * 
	 * @param employe l'employ� � modifier
	 * @param mode le mode d'�dition (modification, cr�ation ou suppresion)
	 * @return un nouvel employ� null pour l'instant, qui sera l'employ� modifier
	 * 
	 * (Antoine MAZEAU)
	 */
	public Employe displayDialog(Employe employe, EditionMode mode) {
		this.em = mode;
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "", this.dbs.getEmpAct().idAg);
		} else {
			this.employeEdite = new Employe(employe);
		}
		this.employeResult = null;
		switch (mode) {
		case CREATION:
			this.txtIdEmpl.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLogin.setDisable(false);
			this.txtMotPasse.setDisable(false);
			if(this.employeEdite.droitsAccess.compareTo("chefAgence")==0) {
				this.rbChefAgence.setSelected(true);
				this.rbGuichetier.setSelected(false);
			} else {
				this.rbChefAgence.setSelected(false);
				this.rbGuichetier.setSelected(true);
			}
			this.lblMessage.setText("Informations sur le nouveau employ�");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.txtIdEmpl.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLogin.setDisable(false);
			this.txtMotPasse.setDisable(false);
			if(this.employeEdite.droitsAccess.compareTo("chefAgence")==0) {
				this.rbChefAgence.setSelected(true);
				this.rbGuichetier.setSelected(false);
			} else {
				this.rbChefAgence.setSelected(false);
				this.rbGuichetier.setSelected(true);
			}
			this.lblMessage.setText("Informations sur le nouveau employ�");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			this.employeResult = this.employeEdite;
			return this.employeResult;
		}

		this.txtIdEmpl.setText("" + this.employeEdite.idEmploye);
		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
		this.txtLogin.setText(this.employeEdite.login);
		this.txtMotPasse.setText(this.employeEdite.motPasse);

		this.employeResult = null;

		this.primaryStage.showAndWait();
		return this.employeResult;
	}


	/**
	 * R�alise l'�v�ment e et le consomme
	 * 
	 * @param e L'�v�nement � r�aliser
	 * @return null
	 * 
	 * (Antoine MAZEAU)
	 */
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}


	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdEmpl;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtLogin;
	@FXML
	private TextField txtMotPasse;
	@FXML
	private RadioButton rbChefAgence;
	@FXML
	private RadioButton rbGuichetier;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	@FXML
	private void doCancel() {
		this.employeResult = null;
		this.primaryStage.close();
	}

	
	/**
	 * Configure l'employ� modifier avec les changement effectuer
	 * 
	 * (Antoine MAZEAU)
	 */
	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResult = this.employeEdite;
			this.primaryStage.close();
			break;
		}
	}

	/**
	 * V�rfie si les champs de saisie sont correctemment remplies
	 * 
	 * @return oui s'ils le sont et non sinon
	 * 
	 * (Antoine MAZEAU)
	 */
	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.login = this.txtLogin.getText().trim();
		this.employeEdite.motPasse = this.txtMotPasse.getText().trim();
		if(this.rbChefAgence.isSelected()) {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_CHEF;
		} else {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_GUICHETIER;
		}

		if (this.employeEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.employeEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}
		if (this.employeEdite.login.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.employeEdite.motPasse.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mot de passe ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}


		return true; 
	}

}
