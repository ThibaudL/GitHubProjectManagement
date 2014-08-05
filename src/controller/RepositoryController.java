package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.GitHubModel;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.util.MilestoneComparator;

import com.sun.javafx.scene.input.DragboardHelper;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
	@FXML 
	private HBox milestoneBox;
	@FXML 
	private ScrollPane milestoneScroll;
	
	@FXML
	private Button labelsButton;
	@FXML
	private Button milestonesButton;
	@FXML
	private Button filesButton;

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
		
		labelsButton.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				mainApp.loadLabelsView(repository);

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
		//Milestone box
		milestoneBox.prefWidthProperty().bind(milestoneScroll.widthProperty().add(-2));
		milestoneBox.prefHeightProperty().bind(milestoneScroll.heightProperty().add(-2));
		List<Milestone> milestones = githubModel.getOpenMilestones(repository);
		if(milestones != null)
		for (final Milestone milestone : milestones) {
			VBox  vbox = new VBox();
			vbox.setSpacing(10);
			milestoneBox.getChildren().add(vbox);
			

			vbox.setStyle("-fx-background-radius:5;-fx-background-color:#4a4a4a;-fx-padding:5 5 5 5;");
			VBox.setVgrow(vbox, Priority.ALWAYS);
			
			Label milestoneLabel = new Label(milestone.getTitle());
			milestoneLabel.setMinWidth(200);
			milestoneLabel.getStyleClass().add("item-title");
			
			SimpleDateFormat format1 = new SimpleDateFormat("MMMMM dd");
			SimpleDateFormat format2 = new SimpleDateFormat("hh:mm");
			
			VBox title = new VBox();
			title.setMinWidth(200);
			title.getChildren().add(milestoneLabel);


			Date dueOn = milestone.getDueOn();
			if(dueOn != null){
				Label dateLabel = new Label("Due on "+format1.format(dueOn)+" at "+format2.format(dueOn));
				dateLabel.getStyleClass().add("item-h4");
				if(dueOn.compareTo(new Date(System.currentTimeMillis()))>0)
				{
					dateLabel.setStyle("-fx-padding: 2 5 5 0;-fx-text-fill:green;");
				}else{
					dateLabel.setStyle("-fx-padding: 2 5 5 0;-fx-text-fill:red;");
				}
				title.getChildren().add(dateLabel);
			}
			vbox.getChildren().add(title);

			
			List<Issue> issues = githubModel.getIssuesByMilestone(repository, milestone);
			for (final Issue issue : issues) {
				final VBox issueBox = new VBox();
				HBox labelBox = new HBox();
				labelBox.setSpacing(5);
				labelBox.setMinHeight(10);
				labelBox.setMaxHeight(10);
				issueBox.setMinHeight(75);
				issueBox.setStyle("-fx-background-radius:5;-fx-background-color:#a4a4a4;-fx-padding:5 5 5 5;");
				Label issueLabel = new Label(issue.getTitle());
				issueLabel.getStyleClass().add("item-title");
				if(issue.getState().compareTo("closed") == 0){
					ImageView iv = new ImageView();
					iv.setImage(new Image("/Images/validate.png",20,20,false,false));
					issueLabel.setGraphic(iv);
					issueLabel.setStyle("-fx-text-fill:red;-fx-opacity:1;");
				}else{
					issueLabel.setStyle("-fx-text-fill:green;-fx-opacity:1;");

				}
				
				for (org.eclipse.egit.github.core.Label label : issue.getLabels()) {
					Label labelLabel = new Label(" ");
					labelLabel.setMinWidth(20);
					labelLabel.setMaxWidth(20);
					labelLabel.setMinHeight(10);
					labelLabel.setMaxHeight(10);

					labelLabel.setStyle("-fx-background-color:#"+label.getColor()+";"+
									"-fx-background-radius:5;-fx-padding: 0 15 5 15;"
							
							);
					labelBox.getChildren().add(labelLabel);
				}
				HBox imageBox = new HBox();
				ImageView userImage = new ImageView();
				User assignee = issue.getAssignee();
				if(assignee != null){
					Image img = githubModel.getUserImage(assignee);
					userImage.setFitHeight(30);
					userImage.setFitWidth(30);
					userImage.setImage(img);
				}
				
				VBox checkboxBox = new VBox();
				checkboxBox.setSpacing(5);
				String body = issue.getBody();
				if(body!=null && (body.contains("- [ ]") || body.contains("- [x]"))){
					String[] split = body.split("\n");
					for (final String string : split) {
						if(string.contains("- [ ]") || string.contains("- [x]")){
							final CheckBox ch = new CheckBox(string.replace("- [ ]","").replace("- [x]",""));
							if(string.contains("- [x]")){
								ch.setSelected(true);
								
							}
							ch.setOnAction(new EventHandler<ActionEvent>() {
								public void handle(ActionEvent event) {
									String unselected = "[ ]";
									String selected = "[x]";
									String before = selected;
									String after = unselected;
									if(ch.isSelected()){
										before = unselected;
										after = selected;
									}
									String body = issue.getBody();
									body = body.replace(string.replaceAll("\\[(.+)\\]",before), string.replaceAll("\\[(.+)\\]",after));
									issue.setBody(body);

									githubModel.saveIssue(repository, issue);
									
								}
							});
							ch.setStyle("-fx-text-fill:#e4e4e4");

							checkboxBox.getChildren().add(ch);
						}
					}
				}

				issueBox.getChildren().add(labelBox);
				issueBox.getChildren().add(issueLabel);
				issueBox.getChildren().add(checkboxBox);
				issueBox.getChildren().add(imageBox);
			    Region spacer = new Region();
			    HBox.setHgrow(spacer, Priority.ALWAYS);
				imageBox.getChildren().addAll(spacer,userImage);
				

				vbox.getChildren().add(issueBox);
				
				issueBox.setOnMouseClicked(new EventHandler<MouseEvent>() {

					public void handle(MouseEvent event) {
						mainApp.loadIssue(repository, issue);
					}
				});				

			}
			
			
			final VBox issueBox = new VBox();
			HBox labelBox = new HBox();
			labelBox.setSpacing(5);
			labelBox.setMinHeight(10);
			labelBox.setMaxHeight(10);
			issueBox.setMinHeight(20);
			issueBox.setStyle("-fx-background-radius:5;-fx-background-color:#a4a4a4;-fx-padding:5 5 5 5;");
			Label issueLabel = new Label("Add new card");
			issueLabel.getStyleClass().add("item-title");
			issueLabel.setStyle("-fx-text-fill:#747474;-fx-opacity:1;");	

			issueBox.getChildren().add(labelBox);
			issueBox.getChildren().add(issueLabel);
			

			vbox.getChildren().add(issueBox);
			
			issueBox.setOnMouseClicked(new EventHandler<MouseEvent>() {

				public void handle(MouseEvent event) {
					Issue newIssue = new Issue();
					newIssue.setTitle("TO DO: fill title");
					newIssue.setMilestone(milestone);
					newIssue.setBody("TO DO: fill issues body.");
					newIssue = githubModel.createNewIssue(repository,newIssue) ;
					mainApp.loadIssue(repository, newIssue);
				}
			});		

		}

	}
	
}
