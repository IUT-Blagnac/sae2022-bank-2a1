package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.ComptesManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.AccessClient;
import model.orm.AccessCompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class ClientsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private ClientsManagement cm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Client> olc;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ClientsManagement _cm, DailyBankState _dbstate) {
		this.cm = _cm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olc = FXCollections.observableArrayList();
		this.lvClients.setItems(this.olc);
		this.lvClients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvClients.getFocusModel().focus(-1);
		this.lvClients.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
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
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Client> lvClients;
	@FXML
	private Button btnDesactClient;
	@FXML
	private Button btnModifClient;
	@FXML
	private Button btnComptesClient;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Ferme la fenêtre a l'activation du button annuler
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Compare le texte remplie dans les champs nom et prenom avec le debut des nom et prenom
	 * des clients
	 */
	@FXML
	private void doRechercher() {
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

		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		ArrayList<Client> listeCli;
		listeCli = this.cm.getlisteComptes(numCompte, debutNom, debutPrenom);

		this.olc.clear();
		for (Client cli : listeCli) {
			this.olc.add(cli);
		}

		this.validateComponentState();
	}

	/**
	 * Ouvre une page permettant de gerer les comptes du client selectionné
	 */
	@FXML
	private void doComptesClient() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client client = this.olc.get(selectedIndice);
			this.cm.gererComptesClient(client);
		}
	}

	/**
	 * Ouvre une page permettant de modifier les informations du client selectionné
	 */
	@FXML
	private void doModifierClient() {

		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client cliMod = this.olc.get(selectedIndice);
			Client result = this.cm.modifierClient(cliMod);
			if (result != null) {
				this.olc.set(selectedIndice, result);
			}
		}
	}

	/**
	 * Ouvre une page permettant de désactiver un client si le solde de ses comptes est à 0
	 */
	@FXML
	private void doDesactiverClient() throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException, ManagementRuleViolation {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client cliMod = this.lvClients.getItems().get(selectedIndice);
			ComptesManagement cm = new ComptesManagement(this.primaryStage, this.dbs, cliMod);
			ArrayList<CompteCourant> listeCp = cm.getComptesDunClient();
			int taille = listeCp.size();
			boolean clotCpt = false;
			String mess = "";
			for(int i=0;i<taille;i++) {
				if(listeCp.get(i).estCloture.compareTo("N")==0) {
					mess+="Compte à cloturer : "+listeCp.get(i).idNumCompte+"\n";
					clotCpt = true;
				}
			}
			if(clotCpt) {
				Alert a = new Alert(AlertType.WARNING, "Ce client possède un ou plusieurs compte(s) non cloturé(s).\n"+mess);
				a.setTitle("Désactivation d'un client");
				a.showAndWait();
				return;
			}

			Alert a = new Alert(AlertType.CONFIRMATION, "Voulez vous vraiment désactiver le compte n°"+cliMod.idNumCli+" ?", ButtonType.YES, ButtonType.NO);
			a.setTitle("Désactivation d'un client");

			ButtonType result = a.showAndWait().orElse(ButtonType.NO);

			if (ButtonType.YES.equals(result)) {
				AccessClient ac = new AccessClient();
				ac.desactiverClient(cliMod);
				this.validateComponentState();
			}
		}
	}

	/**
	 * Ouvre une page permettant de créer un nouveau client
	 */
	@FXML
	private void doNouveauClient() {
		Client client;
		client = this.cm.nouveauClient();
		if (client != null) {
			this.olc.add(client);
		}
	}

	/**
	 * Rend activable les différents boutons si un compte est sectionné et actif
	 */
	private void validateComponentState() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client cliMod = this.lvClients.getItems().get(selectedIndice);
			if(cliMod.estInactif.compareTo("O")==0) {
				this.btnModifClient.setDisable(true);
				this.btnComptesClient.setDisable(true);
				this.btnDesactClient.setDisable(true);
			} else {
				this.btnModifClient.setDisable(false);
				this.btnComptesClient.setDisable(false);
				this.btnDesactClient.setDisable(false);
			}
		} else {
			this.btnModifClient.setDisable(true);
			this.btnComptesClient.setDisable(true);
			this.btnDesactClient.setDisable(true);
		}
	}
}
