package controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import model.GitHubModel;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class IssueMenuController {
	@FXML
	private VBox issuesList;
	@FXML
	private Button openButton;
	@FXML
	private Button closedButton;
	
	private Main mainApp;
	private GitHubModel gitHubModel;
	private Repository repository;
	
	private List<Issue> issuesOpen;
	private List<Issue> issuesClosed;

	
	@FXML
	public void initialize(){
		openButton.setOnAction(new EventHandler<ActionEvent>() {
			 
			public void handle(ActionEvent event) {
				loadIssues(issuesOpen);
				openButton.setDefaultButton(true);
				closedButton.setDefaultButton(false);
			}
		});
		
		closedButton.setOnAction(new EventHandler<ActionEvent>() {
			 
			public void handle(ActionEvent event) {
				loadIssues(issuesClosed);
				openButton.setDefaultButton(false);
				closedButton.setDefaultButton(true);
			}
		});
	}
	
	public void setMainApp(Main mainApp){
		this.mainApp = mainApp;
		gitHubModel = mainApp.getGitHubModel();
	}

	public void setRepo(Repository repository) {
		this.repository=repository;
		issuesOpen = gitHubModel.getOpenIssues(repository);
		issuesClosed = gitHubModel.getClosedIssues(repository);
		loadIssues(issuesOpen);
	}
	

	
	private void loadIssues(List<Issue> issues){
		issuesList.getChildren().clear();
		for (Issue issue : issues) {
			final Issue finalIssue = issue;
			HBox hbox= new HBox();

			Button issueButton = new Button();
			issueButton.prefWidthProperty().bind(issuesList.widthProperty());
			hbox.prefWidthProperty().bind(issueButton.widthProperty().add(-27));
			
			Pane contentPane = new Pane();
			contentPane.setMinHeight(30);
			Label issueLabel = new Label("#"+issue.getNumber()+"  "+issue.getTitle());
			SimpleDateFormat format = new SimpleDateFormat("MMMMM dd");
			Label dateLabel = new Label("Opened on "+format.format(issue.getCreatedAt()));
			dateLabel.setStyle(" -fx-font-size: 10pt;"+
							   "-fx-text-fill: #e4e4e4;"+	
							   "-fx-padding: 20 30 0 15;"
					);
		    Region spacer = new Region();
		    HBox.setHgrow(spacer, Priority.ALWAYS);
			hbox.setSpacing(5);
			issueLabel.setStyle("-fx-text-fill: #e4e4e4;");
			hbox.getChildren().add(issueLabel);
			hbox.getChildren().add(spacer);
			contentPane.getChildren().add(hbox);
			contentPane.getChildren().add(dateLabel);
			Milestone milestone = issue.getMilestone();
			
			for (org.eclipse.egit.github.core.Label label : issue.getLabels()) {
				Label label2 = new Label(label.getName());
				label2.setMinWidth(30);
				label2.setStyle("-fx-background-color:#"+label.getColor()+";"+
								"-fx-background-radius:5;-fx-padding: 5 15 5 15;"
						
						);
				hbox.getChildren().add(label2);
			}
			if(milestone != null)
			{	
				Label label = new Label(milestone.getTitle());
				label.getStyleClass().add("item-title");
				label.setStyle("-fx-border-color:white;");
				hbox.getChildren().add(label);
			}
			issueButton.setGraphic(contentPane);
			issuesList.getChildren().add(issueButton);
			
			issueButton.setOnAction(new EventHandler<ActionEvent>() {
				 
				public void handle(ActionEvent event) {
					mainApp.loadIssue(repository, finalIssue);
				}
			});
		}
	}
	
	
	
}
