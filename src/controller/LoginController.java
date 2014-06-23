package controller;

import java.util.prefs.Preferences;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
	
	private final String PREF_USERNAME = "username";
	private final String PREF_PASSWORD = "password";
	private final Preferences prefs ;
	
	
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button loginButton;
	@FXML
	private CheckBox rememberBox;

	
	private Main mainApp;

	public LoginController() {
		prefs = Preferences.userNodeForPackage(application.Main.class);
	}
	
	@FXML
	private void initialize() {
		
		// Preference key name
		rememberBox.setSelected(true);

		String defaultValue = "";
		String username = prefs.get(PREF_USERNAME, defaultValue);
		String password = prefs.get(PREF_PASSWORD, defaultValue);
		
		usernameField.setText(username);
		passwordField.setText(password);

		
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {				
				String username = usernameField.getText();
				String password = passwordField.getText();
				if(rememberBox.isSelected()){
					prefs.put(PREF_USERNAME, username);
					prefs.put(PREF_PASSWORD, password);
				}
				
			
				LoginController.this.mainApp.getGitHubModel().connect(username, password);
				mainApp.writeNotification("Connection made.");
				mainApp.loadHomeView();
			}
		});
		
	
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}


	    
}
