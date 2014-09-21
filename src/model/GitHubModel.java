package model;

import java.io.IOException;
import java.net.Socket;
import java.sql.Blob;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_USERS;

import com.google.gson.reflect.TypeToken;
















import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import application.Main;

public class GitHubModel {

	private GitHubClient client;
	private MyRepositoriesService repoService;
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
		
		repoService = new MyRepositoriesService(client);
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
		
		repositories.addAll(getStarredRepositories());
		
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
			mainApp.writeNotification("Error Get comments.\n"+e.getMessage());
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
	
	public Label saveLabel(Repository repository, Label label,String oldLabelName){
		try {
			//System.out.println(label.getName());
			//label.setName(label.getName().replace(" ", "%x62"));
			//System.out.println(label.getName());
			if(oldLabelName.compareTo(label.getName()) == 0){
				return labelService.editLabel(repository, label);
			}else{
				labelService.deleteLabel(repository, oldLabelName);
				return labelService.createLabel(repository, label);
			}
		} catch (IOException e) {
			mainApp.writeNotification("Failed saving label.\n"+e.getMessage());
			return null;
		}
	}
	
	public Label createLabel(Repository repository, Label label){
		try {
			return labelService.createLabel(repository, label);
		} catch (IOException e) {
			mainApp.writeNotification("Failed creating label.\n"+e.getMessage());
		}
		return null;
	}
	
	public void removeLabel(Repository repository, Label label){
		try {
			//labelService.deleteLabel(repository, label.getName());
			String string = "/repos/"+repository.getOwner().getLogin()+"/"+repository.getName()+"/labels/"+label.getName();
			client.delete(string);
		} catch (IOException e) {
			mainApp.writeNotification("Failed removing label.\n"+e.getMessage());
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
	
	public ArrayList<Issue> getIssuesByMilestone(Repository repository, Milestone milestone){		
		try {
			Map<String, String> filterData = new HashMap<String, String>();
			filterData.put(IssueService.FILTER_MILESTONE, Integer.toString(milestone.getNumber()));
			filterData.put(IssueService.FILTER_STATE, IssueService.STATE_CLOSED);
			

			ArrayList<Issue> issues = new ArrayList<Issue>(issueService.getIssues(repository, filterData));
			filterData.put(IssueService.FILTER_STATE, IssueService.STATE_OPEN);
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
	
	public List<Repository> getStarredRepositories(){
		try {
			List<Repository> repositories = repoService.getStarredRepositories(user);
			return repositories;
			
		} catch (IOException e) {
			mainApp.writeNotification("Failed getting starred repositories.");
		}
		return null;
	}
	
	public void createP2PadresBook(Repository repository){
		List<RepositoryContents> contents = getContents(repository);
		
		if(contents != null && !contents.contains("connectedUsers.txt")){
		/*	try {
				Gist gist = new Gist();
				HashMap<String, GistFile> map = new HashMap<String, GistFile>();
				GistFile file = new GistFile();
				file.setContent("MY IP ADRESS xxx.xxx.xxx.xxx");
				file.setFilename("connectedUsers.txt");
				map.put("connectedUsers.txt", file);
				gist.setFiles(map);
				gistService.createGist(gist);
				List<Gist> gists = gistService.getGists(user.getLogin());
				for (Gist gist2 : gists) {
					System.out.println(gist2.getHtmlUrl());
				}
			} catch (IOException e) {
				mainApp.writeNotification("Failed creating the gist for the p2p board.");
			}*/
		}
	}

	public void createMilestone(Repository repository, Milestone newmilestone) {
		try {
			milestoneService.createMilestone(repository, newmilestone);
		} catch (IOException e) {
			mainApp.writeNotification("Failed creating the milestone.");
		}
	}

	public Milestone saveMilestone(Repository repository, Milestone milestone,
			String oldmilestoneName) {
		try {
			System.out.println(oldmilestoneName);
			System.out.println(milestone.getTitle());

			if(oldmilestoneName.compareTo(milestone.getTitle()) == 0){
				return milestoneService.editMilestone(repository, milestone);
			}else{
				labelService.deleteLabel(repository, oldmilestoneName);
				return milestoneService.createMilestone(repository, milestone);
			}
		} catch (IOException e) {
			mainApp.writeNotification("Failed saving the milestone.\n"+e.getMessage());
			return null;
		}
	}

	public void removeMilestone(Repository repository, Milestone milestone) {
		try {
			milestoneService.deleteMilestone(repository, milestone.getTitle());
		} catch (IOException e) {
			mainApp.writeNotification("Failed deleting the milestone.");
		}
	}
	
	public Hashtable<Milestone,ArrayList<Issue>> getIssuesSortedByMilestone(final Repository repository, List<Milestone> milestones){
		
		final class TheThread extends Thread {
			Hashtable<Milestone,ArrayList<Issue>> retour ;
			Milestone milestone;
			public TheThread(Hashtable<Milestone,ArrayList<Issue>> toBeReturned,Milestone milestoneToUse) {
				this.retour = toBeReturned;
				milestone = milestoneToUse;
			}
			public void run() {
				retour.put(milestone,getIssuesByMilestone(repository, milestone));
			}
		}
		
		Hashtable<Milestone,ArrayList<Issue>> retour = new Hashtable<Milestone,ArrayList<Issue>>();
		ArrayList<TheThread> threads = new ArrayList<TheThread>();
		
		if(milestones != null){
			for (final Milestone milestone : milestones) {
				TheThread th = new TheThread(retour,milestone);
				threads.add(th);
				th.start();
			}
		}
		
		for (TheThread theThread : threads) {
			try {
				theThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return retour;
	}

}
