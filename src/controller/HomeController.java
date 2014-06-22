package controller;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import application.Main;

public class HomeController {
	private Main mainApp;
	
	@FXML
	private Label nameLabel;
	@FXML
	private AnchorPane mainPane;
	
	@FXML
	private VBox followingBox;
	@FXML
	private VBox followersBox;

	public HomeController() {
	}
	
	@FXML
	private void initialize() {
		

	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		this.mainApp.setName(mainApp.getGitHubController().getConnectedUserName());
		this.mainApp.setUserImage(mainApp.getGitHubController().getConnectedUserImage());
		
		
		
		for (User usr : mainApp.getGitHubController().getFollowing()){
			final User userAction = usr;
			EventHandler<ActionEvent> clicEvent = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					HomeController.this.mainApp.loadWebPage(userAction.getHtmlUrl());
				}
			};
			addBoxButtonWithImage(usr.getLogin(),new Image(usr.getAvatarUrl(),50,50,true,true),followingBox,clicEvent);
		}
		
		for (User usr : mainApp.getGitHubController().getFollowers()){
			final User userAction = usr;
			EventHandler<ActionEvent> clicEvent = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					HomeController.this.mainApp.loadWebPage(userAction.getHtmlUrl());
				}
			};
			addBoxButtonWithImage(usr.getLogin(),new Image(usr.getAvatarUrl(),50,50,true,true),followersBox,clicEvent);
		}
		
	}
	
	private Button createLeftButton(String name,EventHandler<ActionEvent> event){
		Button butt = new Button();
		butt.setId("repositoryButton");
		butt.setPrefWidth(270);
		butt.setPrefHeight(50);
		butt.setText(name);
		
		butt.setOnAction(event);

		return butt;
	}
	
	private void addBoxButtonWithImage(String name,Image img,VBox dest,EventHandler<ActionEvent> event){
		HBox hb = new HBox();
		dest.getChildren().add(hb);
		hb.getChildren().add(new ImageView(img));
		hb.getChildren().add(createLeftButton(name,event));
	}
}