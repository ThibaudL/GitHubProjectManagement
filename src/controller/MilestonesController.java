package controller;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.GitHubModel;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;







import application.Main;

public class MilestonesController {
	
	private Main mainApp;
	private Repository repository;
	private GitHubModel ghModel;
	private List<Milestone> milestones;
	
	@FXML
	private ScrollPane milestoneScroll;
	@FXML
	private VBox milestonesBox;
	@FXML
	private TextField milestoneNameField;
	@FXML
	private Button milestoneSaveButton;
	@FXML
	private Button deleteButton;
	@FXML
	private AnchorPane milestoneManagementPane;

	@FXML
	public void initialize(){
		
		milestonesBox.prefWidthProperty().bind(milestoneScroll.widthProperty());
		milestonesBox.prefHeightProperty().bind(milestoneScroll.heightProperty());
		milestonesBox.setSpacing(10);
		
		milestoneManagementPane.setVisible(false);
		
		
	}
	
	public void setMainApp(Main main) {
		this.mainApp = main;
		this.ghModel = main.getGitHubModel();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
		milestones = ghModel.getMilestones(repository);
		if(milestones != null)
		for (Milestone milestone : milestones){
			addmilestone(milestone,null);
	
		}
		
		final Label milestone2 = new Label("Create new milestone");
		milestone2.setMinWidth(200);
		milestone2.getStyleClass().add("item-title");
		milestone2.setStyle("-fx-border-color:white;-fx-padding: 5 15 5 15;");
		ImageView iv = new ImageView();
		iv.setImage(new Image("/Images/add.png",30,30,true,true));
		milestone2.setGraphic(iv);
		milestonesBox.getChildren().add(milestone2);
		
		milestone2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				Milestone newmilestone = new Milestone();
				newmilestone.setTitle("TO DO: change milestones Name");
				ghModel.createMilestone(MilestonesController.this.repository, newmilestone);
				addmilestone(newmilestone,milestonesBox.getChildren().size()-1);
			}
		});		
	}
	private void addmilestone(final Milestone milestone,Integer position){
		
		final Label milestone2 = new Label(milestone.getTitle());
		milestone2.setMinWidth(200);
		milestone2.getStyleClass().add("item-title");
		milestone2.setStyle("-fx-border-color:white;-fx-padding: 5 15 5 15;");

		if(position != null)
			milestonesBox.getChildren().add(position,milestone2);
		else{
			milestonesBox.getChildren().add(milestone2);
		}
		milestone2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				milestoneManagementPane.setVisible(true);
				milestoneNameField.setText(milestone.getTitle());
			
				milestoneSaveButton.setOnAction(new EventHandler<ActionEvent>() {

					public void handle(ActionEvent event) {
						String oldmilestoneName = milestone.getTitle();
						milestone.setTitle(milestoneNameField.getText());
						milestoneManagementPane.setVisible(false);
						ghModel.saveMilestone(repository, milestone,oldmilestoneName);
						
						milestonesBox.getChildren().clear();
						setRepository(repository);						
					}
				});
				
				deleteButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						ghModel.removeMilestone(repository, milestone);
						milestoneManagementPane.setVisible(false);
						
						milestonesBox.getChildren().clear();
						setRepository(repository);
					}
				});
			}
		});	
	}

}
