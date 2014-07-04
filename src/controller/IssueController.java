package controller;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.GitHubModel;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.util.MilestoneComparator;

import customContainers.EditableMardownViewer;
import application.Main;

public class IssueController {

	private Main mainApp;
	private GitHubModel GHmodel;
	private Issue issue;
	private List<Comment> comments;
	private Repository repository;
	List<org.eclipse.egit.github.core.Label> selectedLabels;
	
	
	@FXML
	private Label titleLabel;
	@FXML
	private Label informationLabel;
	@FXML
	private AnchorPane contentPane;
	@FXML
	private VBox commentBox;
	@FXML
	private HBox labelBox;
	@FXML
	private HBox milestoneBox;
	@FXML 
	private HBox assigneeBox;
	
	private Button commentButton;
	private Button closeButton;
	@FXML
	private HBox titleBox;
	private Button editTitleButton;
	private TextField editTitleField;
	private HBox hbox;


	
	private EditableMardownViewer contentMarkdown;
	
	@FXML
	public void initialize(){
		labelBox.setSpacing(10);
		milestoneBox.setSpacing(10);
		
	}
	
	public void setMainApp(Main main) {
		this.mainApp = main;
		this.GHmodel = main.getGitHubModel();
		
		editTitleButton = new Button("Edit");
		editTitleButton.setStyle("-fx-border-radius:5;-fx-background-radius:5;");
		editTitleField = new TextField();
		editTitleField.setMinHeight(30);
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		HBox.setHgrow(editTitleField, Priority.ALWAYS);
		titleBox.getChildren().add(spacer);
		titleBox.getChildren().add(editTitleButton);
		editTitleButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if(editTitleButton.getText().compareTo("Save") != 0){
					titleBox.getChildren().remove(titleLabel);
					titleBox.getChildren().add(0,editTitleField);
					editTitleButton.setText("Save");
				}else{
					titleBox.getChildren().remove(editTitleField);
					titleBox.getChildren().add(0,titleLabel);
					editTitleButton.setText("Edit");
					titleLabel.setText(editTitleField.getText());
					issue.setTitle(editTitleField.getText());
					GHmodel.saveIssue(repository, issue);
				}
			}
		});
		
		contentMarkdown = new EditableMardownViewer(GHmodel);
		contentPane.getChildren().add(contentMarkdown);
		
		commentButton = new Button("Comment issue");
		closeButton = new Button("Close issue");
		
		commentButton.setStyle("-fx-border-color:white;-fx-border-radius:5;-fx-background-radius:5;");
		closeButton.setStyle("-fx-border-color:white;-fx-border-radius:5;-fx-background-radius:5;");

		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if(issue.getState().compareTo("open")==0){
					issue.setState("close");
					closeButton.setText("Reopen issue");
					titleLabel.setStyle("-fx-text-fill: orangered ;");

				}else{
					issue.setState("open");
					closeButton.setText("Close issue");
					titleLabel.setStyle("-fx-text-fill: limegreen ;");

				}
				GHmodel.saveIssue(repository, issue);
			}
		});
		
		commentButton.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				int idx = commentBox.getChildren().indexOf(hbox);
				final EditableMardownViewer newComment = new EditableMardownViewer(GHmodel);
				final Comment com = GHmodel.createNewComment(repository, issue.getNumber());
				newComment.setRepository(repository);
				newComment.setComment(com);
				newComment.setMinHeight(200);

				newComment.setOnRemoveAction(new EventHandler<ActionEvent>() {

					public void handle(ActionEvent event) {
						GHmodel.removeComment(repository, com.getId());
						commentBox.getChildren().remove(newComment);
					}
				});
				commentBox.getChildren().add(idx,newComment);
			}
		});
		
		hbox = new HBox();
		hbox.setSpacing(20);
		hbox.getChildren().addAll(commentButton,closeButton);

		
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
		
		if(issue.getState().compareTo("open")==0){
			closeButton.setText("Close issue");

		}else{
			closeButton.setText("Reopen issue");

		}
		editTitleField.setText(issue.getTitle());
		titleLabel.setText(issue.getTitle());
		if(issue.getState().compareTo("open") == 0){
			this.titleLabel.setStyle("-fx-text-fill: limegreen ;");
		}else{
			this.titleLabel.setStyle("-fx-text-fill: orangered ;");
		}
		
		selectedLabels = issue.getLabels();
		
			List<org.eclipse.egit.github.core.Label> repositoryLabels = GHmodel.getRepositoryLabels(repository);
			if(repositoryLabels != null){
			for (final org.eclipse.egit.github.core.Label label :  repositoryLabels) {
				Label label2 = new Label(label.getName());

				label2.setMinWidth(30);
				label2.setStyle("-fx-background-color:#"+label.getColor()+";"+
								"-fx-background-radius:5;-fx-padding: 5 15 5 15;-fx-border-radius:5;"
						
						);
				labelBox.getChildren().add(label2);
				
				final Label labelForListener = label2;
				label2.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						if(labelForListener.getGraphic() == null){
							ImageView iv = new ImageView();
							iv.setImage(new Image("/Images/validate.png",20,20,false,false));
							labelForListener.setGraphic(iv);
							selectedLabels.add(label);
						}else{
							labelForListener.setGraphic(null);
							selectedLabels.remove(label);
						}
						IssueController.this.issue.setLabels(selectedLabels);
						GHmodel.saveIssue(IssueController.this.repository, IssueController.this.issue);
					}
				});
				
				
				label2.setOnMouseEntered(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						labelForListener.setStyle(labelForListener.getStyle()+";-fx-border-color:white;");
					}
				});
				
				label2.setOnMouseExited(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						labelForListener.setStyle(labelForListener.getStyle()+";-fx-border-color:#1d1d1d;");
					}
				});
				if(selectedLabels.contains(label)){
					ImageView iv = new ImageView();
					iv.setImage(new Image("/Images/validate.png",20,20,false,false));
					label2.setGraphic(iv);
				}
			}
		}
		
		contentMarkdown.setRepository(repository);
		contentMarkdown.setIssue(issue);

		comments = GHmodel.getComments(repository, issue.getNumber());
		for (final Comment com : comments) {
			final EditableMardownViewer commentViewer = new EditableMardownViewer(GHmodel);
			commentBox.getChildren().add(commentViewer);
			commentViewer.setRepository(repository);
			commentViewer.setComment(com);
			if(com.getUser().getLogin().compareTo(GHmodel.connectedUser())!=0){
				commentViewer.hideRemoveButton();
			}
			commentViewer.setMinHeight(200);
			commentViewer.setOnRemoveAction(new EventHandler<ActionEvent>() {

				public void handle(ActionEvent event) {
					GHmodel.removeComment(IssueController.this.repository, com.getId());
					commentBox.getChildren().remove(commentViewer);
				}
			});

		}
		contentMarkdown.hideRemoveButton();
		
		for (final Milestone milestone : GHmodel.getOpenMilestones(repository)) {
			
			final Label label = new Label(milestone.getTitle());

			if(issue.getMilestone() != null){
				if(new MilestoneComparator().compare(issue.getMilestone(),milestone) == 0){
					ImageView iv = new ImageView();
					iv.setImage(new Image("/Images/validate.png",20,20,false,false));
					label.setGraphic(iv);
				}
			}
			label.getStyleClass().add("item-title");
			label.setStyle("-fx-border-color:white;-fx-padding: 5 15 5 15;");
			label.setMinWidth(30);
			milestoneBox.getChildren().add(label);
			
			label.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					Milestone newMilestone = new Milestone();
					Milestone milestone2 = IssueController.this.issue.getMilestone();
					if(milestone2 != null && milestone2.getTitle() != null && milestone!=null && new MilestoneComparator().compare(milestone2,milestone) == 0){
						label.setGraphic(null);
					}else{
						for (Object iterable_element : milestoneBox.getChildren().toArray()) {
							Label lab = (Label)iterable_element;
							lab.setGraphic(null);
						}
						newMilestone = milestone;

						ImageView iv = new ImageView();
						iv.setImage(new Image("/Images/validate.png",20,20,false,false));
						label.setGraphic(iv);
					}
					
					IssueController.this.issue.setMilestone(newMilestone);
					
					GHmodel.saveIssue(IssueController.this.repository, IssueController.this.issue);
				}
			});
		}
		
		for (final User user : GHmodel.getCollaborators(repository)) {
			final Label label = new Label(user.getLogin());

			if(issue.getAssignee() != null){
				if(issue.getAssignee().getLogin().compareTo(user.getLogin()) == 0){
					ImageView iv = new ImageView();
					iv.setImage(new Image("/Images/validate.png",20,20,false,false));
					label.setGraphic(iv);
				}
			}
			label.getStyleClass().add("item-title");
			label.setMinWidth(30);
			HBox hb = new HBox();
			hb.setStyle("-fx-padding: 5 15 5 15;");

			hb.getChildren().add(label);
			ImageView iv = new ImageView();
			iv.setImage(new Image(user.getAvatarUrl(),30,30,false,false));
			hb.getChildren().add(iv);

			assigneeBox.getChildren().add(hb);
			
			label.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					User userToAdd = new User();
					User assignee = IssueController.this.issue.getAssignee();
					if(assignee != null && assignee.getLogin() != null && user.getLogin() != null && assignee.getLogin().compareTo(user.getLogin()) == 0){
						label.setGraphic(null);
					}else{
						if(assigneeBox.getChildren()!= null){
							for (Object iterable_element : assigneeBox.getChildren().toArray()) {
								HBox box = (HBox)iterable_element;
								Label lab = (Label)box.getChildren().get(0);
								lab.setGraphic(null);
							}
						}
						userToAdd = user;

						ImageView iv = new ImageView();
						iv.setImage(new Image("/Images/validate.png",20,20,false,false));
						label.setGraphic(iv);
					}
					
					IssueController.this.issue.setAssignee(userToAdd);
					
					
					GHmodel.saveIssue(IssueController.this.repository, IssueController.this.issue);
				}
			});
		}

		commentBox.getChildren().add(hbox);
		
		
	}
	
	

}
