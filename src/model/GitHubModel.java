package model;

import java.io.IOException;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MarkdownService;
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
	private MarkdownService markdownService;
	private CollaboratorService colaboratorService;
	private ContentsService contentsService;
	
	
	private List<Repository> repositories;
	private List<Issue> issues;
	
	private HashMap<String, Image> images;

	private User user;
	
	private Main mainApp;
	private String username;
	private long time;
	
	public GitHubModel(Main mainApp) {
		this.mainApp = mainApp;
		images = new HashMap<String, Image>();
	}
	
	public boolean connect(String username, String password){

		this.username=username;
		client = new GitHubClient();
		client.setCredentials(username, password);
		
		repoService = new RepositoryService(client);
		userService = new UserService(client);
		issueService = new IssueService(client);
		milestoneService = new MilestoneService(client);
		labelService = new LabelService(client);
		commitService = new CommitService(client);
		markdownService = new MarkdownService(client);
		colaboratorService = new CollaboratorService(client);
		contentsService = new ContentsService(client);
		try {
			loadInformations();
			return true;
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
			return false;
		}
	}
	
	public Image getUserImage(User user){
		if(images.containsKey(user.getLogin())){
			return images.get(user.getLogin());
		}else{
			
			Image img = new Image(user.getAvatarUrl(),40,40,true,true);
			images.put(user.getLogin(), img);
			return img;
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
		Image image = new Image(user.getAvatarUrl(), 50, 50, false,false);
		images.put(user.getLogin(), image);
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
	
	public String markdownToHtml(String text, Repository repository){
		try {
			return markdownService.getRepositoryHtml(repository,text);
		} catch (IOException e) {
			mainApp.writeNotification(e.getMessage());
		}
		return null;
	}
	
	public List<Comment> getComments(Repository repository, long issueId){
		try {
			return issueService.getComments(repository, new Long(issueId).toString());
		} catch (IOException e) {
			mainApp.writeNotification("Error Get comments: "+e.getMessage());
		}
		return null;
	}

	public String connectedUser() {
		return username;
	}
	
	public void saveIssue(Repository repository, Issue issue){
		try {
			issueService.editIssue(repository, issue);
		} catch (IOException e) {
			mainApp.writeNotification("Failed saving issue.");
		}
	}
	
	public void saveComment(Repository repository, Comment comment){
		try {
			issueService.editComment(repository, comment);
		} catch (IOException e) {
			mainApp.writeNotification("Failed saving comment.");
		}
	}
	
	public Label saveLabel(Repository repository, Label label){
		try {
			System.out.println(label.getName());
			return labelService.editLabel(repository, label);
		} catch (IOException e) {
			mainApp.writeNotification("Failed saving label."+e.getMessage());
			return null;
		}
	}
	
	public Label createLabel(Repository repository, Label label){
		try {
			return labelService.createLabel(repository, label);
		} catch (IOException e) {
			mainApp.writeNotification("Failed creating label."+e.getMessage());
		}
		return null;
	}
	
	public void removeLabel(Repository repository, Label label){
		try {
			//labelService.deleteLabel(repository, label.getName());
			String string = "/repos/"+repository.getOwner().getLogin()+"/"+repository.getName()+"/labels/"+label.getName();
			client.delete(string);
		} catch (IOException e) {
			mainApp.writeNotification("Failed removing label: "+e.getMessage());
		}
	}
	
	public void removeComment(Repository repository, long commentId){
		try {
			issueService.deleteComment(repository, commentId);
		} catch (IOException e) {
			mainApp.writeNotification("Failed deleting comment.");
		}
	}
	
	public List<Label> getRepositoryLabels(Repository repository){
		try {
			return labelService.getLabels(repository);
		} catch (IOException e) {
			mainApp.writeNotification("Failed getting repositories labels.");
		}
		return null;
	}

	public Comment createNewComment(IRepositoryIdProvider repository, int issueNumber){
		try {
			return issueService.createComment(repository, issueNumber, "TO DO : fill comment");
		} catch (IOException e) {
			mainApp.writeNotification("Failed to create a new comment.");
		}
		return null;
	}
	
	public Issue createNewIssue(IRepositoryIdProvider repository,Issue issue){
		try {
			return issueService.createIssue(repository, issue);
		} catch (IOException e) {
			mainApp.writeNotification("Failed to create a new Issue.");
		}
		return null;
	}
	
	public List<Milestone> getOpenMilestones(IRepositoryIdProvider repository){
		try {
			return milestoneService.getMilestones(repository, "open");
		} catch (IOException e) {
			mainApp.writeNotification("Failed getting milestones.");
		}
		return null;
	}
	
	public List<Milestone> getClosedMilestones(IRepositoryIdProvider repository){
		try {
			List<Milestone> milestones = milestoneService.getMilestones(repository, "close");
			return milestones;
		} catch (IOException e) {
			mainApp.writeNotification("Failed getting milestones.");
		}
		return null;
	}
	
	public void cloneRepository(Repository repository){
		//?? JGIT ?
	}
	
	public List<User> getCollaborators(Repository repository){
		try {
			List<User> collaborators = colaboratorService.getCollaborators(repository);
			return collaborators;
		} catch (IOException e) {
			mainApp.writeNotification("Failed getting the collaborators.");
		}
		return null;
	}
	
	public List<Issue> getIssuesByMilestone(Repository repository, Milestone milestone){		
		try {
			Map<String, String> filterData = new HashMap<String, String>();
			filterData.put(IssueService.FILTER_MILESTONE, Integer.toString(milestone.getNumber()));
			List<Issue> issues = issueService.getIssues(repository, filterData);
			filterData.put(IssueService.FILTER_STATE, "close");
			issues.addAll(issueService.getIssues(repository, filterData));
			return issues;
		} catch (IOException e) {
			mainApp.writeNotification("Failed getting the issues.");
		}
		return null;
	}
	
	public List<RepositoryContents> getContents(Repository repository){
		try {
			return contentsService.getContents(repository);
		} catch (IOException e) {
			mainApp.writeNotification("Failed getting contents.");
		}
		return null;
	}

}
