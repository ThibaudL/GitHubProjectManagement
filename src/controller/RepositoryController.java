package controller;

import java.util.List;

import model.GitHubModel;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import application.Main;

public class RepositoryController {
	private Main mainApp;
	private Repository repository;
	private GitHubModel githubModel;
	
	private List<Issue> openIssues;
	private List<Issue> closedIssues;
	private List<Milestone> milestones;

	
	@FXML
	private Label openIssuesLabel;
	@FXML
	private Label closedIssuesLabel;
	@FXML 
	private ProgressBar issuesAdvancement;
	@FXML
	private AnchorPane mainPane;
	@FXML
	private Button issuesButton;

	public RepositoryController() {
	}
	
	@FXML
	private void initialize() {
		AnchorPane.setLeftAnchor(mainPane, new Double(0));
		AnchorPane.setRightAnchor(mainPane, new Double(0));
		AnchorPane.setTopAnchor(mainPane, new Double(0));
		AnchorPane.setBottomAnchor(mainPane, new Double(0));
		
		issuesButton.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				mainApp.loadIssuesMenu(repository);
			}
		});
	
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		githubModel = this.mainApp.getGitHubModel();

	}

	public void setRepo(Repository repository) {
		this.repository = repository;
		initData();
	}
	
	private void initData(){
		//Open issues
		openIssues = githubModel.getOpenIssues(repository);
		int openSize = 0;
		if(openIssues != null){
			openSize = openIssues.size();
			if(openSize>0)
				openIssuesLabel.setText(new Integer(openSize).toString());
		}
		//Closed issues
		closedIssues = githubModel.getClosedIssues(repository);
		int closedSize = 0;
		if(closedIssues != null){
			closedSize = closedIssues.size();
			if(closedSize>0)
				closedIssuesLabel.setText(new Integer(closedSize).toString());
		}
		//Progress bar
		float progressValue = (float)(closedSize)/((float)closedSize+(float)openSize);
		issuesAdvancement.setProgress(progressValue);

	}
}
