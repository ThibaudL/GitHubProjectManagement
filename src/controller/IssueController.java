package controller;

import java.text.SimpleDateFormat;
import java.util.List;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.GitHubModel;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;

import customContainers.EditableMardownViewer;
import application.Main;

public class IssueController {

	private Main mainApp;
	private GitHubModel GHmodel;
	private Issue issue;
	private List<Comment> comments;
	private Repository repository;
	
	@FXML
	private Label titleLabel;
	@FXML
	private Label informationLabel;
	@FXML
	private AnchorPane contentPane;
	@FXML
	private VBox commentBox;
	
	private EditableMardownViewer contentMarkdown;
	
	@FXML
	public void initialize(){
		
	}
	
	public void setMainApp(Main main) {
		this.mainApp = main;
		this.GHmodel = main.getGitHubModel();
		
		contentMarkdown = new EditableMardownViewer(GHmodel);
		contentPane.getChildren().add(contentMarkdown);
		
		AnchorPane.setBottomAnchor(contentMarkdown, (double) 0);
		AnchorPane.setTopAnchor(contentMarkdown, (double) 0);
		AnchorPane.setLeftAnchor(contentMarkdown, (double) 0);
		AnchorPane.setRightAnchor(contentMarkdown, (double) 0);

		
		 
		Stage stage = mainApp.getPrimaryStage();
		Scene scene = stage.getScene();

		scene.heightProperty().addListener(new ChangeListener<Number>() {
		    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		    	contentPane.setPrefHeight(newSceneHeight.doubleValue()*0.4);
				contentPane.setMinHeight(mainApp.getPrimaryStage().getScene().getHeight()*0.4);

		    }
		});
		scene.widthProperty().addListener(new ChangeListener<Number>() {
		    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
		    	contentPane.setPrefWidth(newSceneWidth.doubleValue()*0.6);
				contentPane.setMinWidth(mainApp.getPrimaryStage().getScene().getWidth()*0.6);
		    }
		});
		
		contentPane.setPrefWidth(mainApp.getPrimaryStage().getScene().getWidth()*0.6);
		contentPane.setPrefHeight(mainApp.getPrimaryStage().getScene().getHeight()*0.4);
		
		contentPane.setMinWidth(mainApp.getPrimaryStage().getScene().getWidth()*0.6);
		contentPane.setMinHeight(mainApp.getPrimaryStage().getScene().getHeight()*0.4);


	}

	public void setIssueAndRepository(Repository repository,Issue issue) {
		this.repository = repository;
		this.issue = issue;
		
		titleLabel.setText(issue.getTitle());
		if(issue.getState().compareTo("open") == 0){
			this.titleLabel.setStyle("-fx-text-fill: palegreen;");
		}else{
			this.titleLabel.setStyle("-fx-text-fill: red;");
		}
		
		contentMarkdown.setRepository(repository);
		contentMarkdown.setIssue(issue);

		comments = GHmodel.getComments(repository, issue.getNumber());
		for (Comment com : comments) {
			EditableMardownViewer commentViewer = new EditableMardownViewer(GHmodel);
			commentBox.getChildren().add(commentViewer);
			commentViewer.setComment(com);
			commentViewer.setRepository(repository);
			if(com.getUser().getLogin().compareTo(GHmodel.connectedUser())!=0){
				commentViewer.hideRemoveButton();
			}
			commentViewer.setMinHeight(200);

		}
		contentMarkdown.hideRemoveButton();
		
	}
	
	

}
