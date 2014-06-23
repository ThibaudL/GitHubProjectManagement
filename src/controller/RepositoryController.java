package controller;

import java.util.List;

import model.GitHubModel;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;

import javafx.fxml.FXML;
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
	
	@FXML
	private Label openIssuesLabel;
	@FXML
	private Label closedIssuesLabel;
	@FXML 
	private ProgressBar issuesAdvancement;
	@FXML
	private AnchorPane mainPane;

	public RepositoryController() {
	}
	
	@FXML
	private void initialize() {
		AnchorPane.setLeftAnchor(mainPane, new Double(0));
		AnchorPane.setRightAnchor(mainPane, new Double(0));
		AnchorPane.setTopAnchor(mainPane, new Double(0));
		AnchorPane.setBottomAnchor(mainPane, new Double(0));
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		githubModel = this.mainApp.getGitHubModel();

	}

	public void setRepoId(Repository repository) {
		this.repository = repository;
		initData();
	}
	
	private void initData(){
		openIssues = githubModel.getOpenIssues(repository);
		int openSize = 0;
		if(openIssues != null){
			openSize = openIssues.size();
			if(openSize>0)
				openIssuesLabel.setText(new Integer(openSize).toString());
		}
		closedIssues = githubModel.getClosedIssues(repository);
		int closedSize = 0;
		if(closedIssues != null){
			closedSize = closedIssues.size();
			if(closedSize>0)
				closedIssuesLabel.setText(new Integer(closedSize).toString());
		}
		
		float progressValue = (float)(closedSize)/((float)closedSize+(float)openSize);
		
		issuesAdvancement.setProgress(progressValue);

	}
}
