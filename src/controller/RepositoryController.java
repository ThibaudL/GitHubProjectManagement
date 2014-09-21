package controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import model.GitHubModel;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;

import utils.Utils;
import customContainers.KanbanIssueBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
		
		milestonesButton.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				mainApp.loadMilestonesView(repository);

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
		githubModel.createP2PadresBook(repository);
		
		//Open issues
		int openSize = 0;
		/*openIssues = githubModel.getOpenIssues(repository);
		if(openIssues != null){
			openSize = openIssues.size();
			if(openSize>0)
				openIssuesLabel.setText(new Integer(openSize).toString());
		}*/
		
		
		//Closed issues
		int closedSize = 0;
		/*closedIssues = githubModel.getClosedIssues(repository);
		if(closedIssues != null){
			closedSize = closedIssues.size();
			if(closedSize>0)
				closedIssuesLabel.setText(new Integer(closedSize).toString());
		}*/
		//Progress bar
		float progressValue = (float)(closedSize)/((float)closedSize+(float)openSize);
		issuesAdvancement.setProgress(progressValue);
		//Milestone box
		milestoneBox.prefWidthProperty().bind(milestoneScroll.widthProperty().add(-2));
		milestoneBox.prefHeightProperty().bind(milestoneScroll.heightProperty().add(-2));
		List<Milestone> milestones = githubModel.getOpenMilestones(repository);
		//if(milestones != null){
			
		Hashtable<Milestone,ArrayList<Issue>> issuesByMilestone = githubModel.getIssuesSortedByMilestone(repository, milestones);
		for (final Milestone milestone : issuesByMilestone.keySet()) {
			final VBox  vbox = new VBox();
			
			vbox.setOnDragOver(new EventHandler<DragEvent>() {
			    public void handle(DragEvent event) {
			        /* data is dragged over the target */
			        /* accept it only if it is not dragged from the same node 
			         * and if it has a string data */
			        if (event.getGestureSource() != vbox &&
			                event.getDragboard().hasContent(Utils.issueFormat)) {
			            /* allow for both copying and moving, whatever user chooses */
			            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			        }
			        
			        event.consume();
			    }
			});
			
			vbox.setOnDragEntered(new EventHandler<DragEvent>() {
			    public void handle(DragEvent event) {
			    /* the drag-and-drop gesture entered the target */
			    /* show to the user that it is an actual gesture target */
			         if (event.getGestureSource() != vbox &&
			                 event.getDragboard().hasContent(Utils.issueFormat)) {
					    	vbox.setStyle("-fx-background-radius:5;-fx-background-color:green;-fx-padding:5 5 5 5;");
			         }
			                
			         event.consume();
			    }
			});
			
			vbox.setOnDragExited(new EventHandler<DragEvent>() {
			    public void handle(DragEvent event) {
			        /* mouse moved away, remove the graphical cues */
			    	vbox.setStyle("-fx-background-radius:5;-fx-background-color:#4a4a4a;-fx-padding:5 5 5 5;");

			        event.consume();
			    }
			});
			
			vbox.setOnDragDropped(new EventHandler<DragEvent>() {
			    public void handle(DragEvent event) {
			        /* data dropped */
			        /* if there is a string data on dragboard, read it and use it */
			        Dragboard db = event.getDragboard();
			        boolean success = false;
			        if (db.hasContent(Utils.issueFormat)) {
			        	Issue issue = (Issue)db.getContent(Utils.issueFormat);
						vbox.getChildren().add(2,new KanbanIssueBox(issue, repository, mainApp, githubModel,vbox));
			        	issue.setMilestone(milestone);
						githubModel.saveIssue(repository, issue);
			        	success = true;
			        }
			        /* let the source know whether the string was successfully 
			         * transferred and used */
			        event.setDropCompleted(success);
			        
			        event.consume();
			     }
			});
			
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
				//dateLabel.getStyleClass().add("item-h4");
				if(dueOn.compareTo(new Date(System.currentTimeMillis()))>0)
				{
					dateLabel.setStyle("-fx-padding: 2 5 5 0;-fx-text-fill:green;");
				}else{
					dateLabel.setStyle("-fx-padding: 2 5 5 0;-fx-text-fill:#A80000 ;");
				}
				title.getChildren().add(dateLabel);
			}
			vbox.getChildren().add(title);

			final VBox issueBox = new VBox();
			HBox labelBox = new HBox();
			labelBox.setSpacing(5);
			labelBox.setMinHeight(10);
			labelBox.setMaxHeight(10);
			issueBox.setMinHeight(60);
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
			
			//List<Issue> issues = githubModel.getIssuesByMilestone(repository, milestone);
			for (final Issue issue : issuesByMilestone.get(milestone)) {
				vbox.getChildren().add(new KanbanIssueBox(issue, repository, mainApp, githubModel,vbox));
			}
			
			
				

		}

	}
	
}
