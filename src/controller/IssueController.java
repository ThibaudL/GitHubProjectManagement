package controller;

import org.eclipse.egit.github.core.Issue;

import application.Main;

public class IssueController {

	private Main mainApp;
	private Issue issue;
	
	public void setMainApp(Main main) {
		this.mainApp = main;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

}
