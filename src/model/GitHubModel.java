package model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import application.Main;

public class GitHubModel {

	private GitHubClient client;
	private RepositoryService repoService;
	private UserService userService;
	private IssueService issueService;
	private MilestoneService milestoneService;
	private LabelService labelService;
	private CommitService commitService;
	
	private List<Repository> repositories;
	private List<Issue> issues;

	private User user;
	
	private Main mainApp;
	
	public GitHubModel(Main mainApp) {
		this.mainApp = mainApp;
	}
	
	public void connect(String username, String password){
		client = new GitHubClient();
		client.setCredentials(username, password);
		
		repoService = new RepositoryService(client);
		userService = new UserService(client);
		issueService = new IssueService(client);
		milestoneService = new MilestoneService(client);
		labelService = new LabelService(client);
		commitService = new CommitService(client);
		
		try {
			loadInformations();
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
		}
	}

	private void loadInformations() throws IOException {
		repositories = repoService.getRepositories();
		user = userService.getUser();
	}
	
	public String getConnectedUserName(){
		return user.getName();
	}
	
	public Image getConnectedUserImage(){
		Image image = new Image(user.getAvatarUrl(), 40, 40, false,false);
		return image;
	}
	
	public List<Repository> getRepositories(){
		return repositories;
	}
	
	public Repository loadRepository(String name){
		String[] str = name.split("/");
		if(str.length <= 1){
			mainApp.writeNotification("Repository name must be \"owner/name\".");
		}else{
			try {
				Repository repository = repoService.getRepository(str[0], str[1]);
				repositories.add(repository);
				return repository;
			} catch (IOException e) {
				mainApp.writeNotification("Repository not found.");
			}
		}
		return null;
	}
	
	public List<User> getFollowers(){
		try {
			return userService.getFollowers();
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
		}
		return null;
	}
	
	public List<User> getFollowing(){
		try {
			return userService.getFollowing();
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
		}
		return null;
	}
	
	public List<RepositoryCommit> getCommit(Repository repository){
		try{
			return commitService.getCommits(repository);
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
		}
		return null;
	}
	
	public List<Issue> getOpenIssues(Repository repository){
		try {
			Map<String,String> options = new HashMap<String, String>();
			options.put(IssueService.FILTER_STATE,IssueService.STATE_OPEN);
			return issueService.getIssues(repository,options);
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
		}
		return null;
	}
	
	public List<Issue> getClosedIssues(Repository repository){
		try {
			Map<String,String> options = new HashMap<String, String>();
			options.put(IssueService.FILTER_STATE,IssueService.STATE_CLOSED);
			return issueService.getIssues(repository,options);
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
		}
		return null;
	}
	
	public List<Milestone> getMilestones(Repository repository){
		try {
			return milestoneService.getMilestones(repository, "open");
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
		}
		return null;
	}
}
