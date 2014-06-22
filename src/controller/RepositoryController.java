package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import application.Main;

public class RepositoryController {
	private Main mainApp;
	

	public RepositoryController() {
	}
	
	@FXML
	private void initialize() {

	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}
}
