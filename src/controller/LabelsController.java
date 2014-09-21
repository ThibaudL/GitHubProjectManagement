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
import javafx.scene.paint.Color;
import model.GitHubModel;

import org.eclipse.egit.github.core.Repository;





import application.Main;

public class LabelsController {
	
	private Main mainApp;
	private Repository repository;
	private GitHubModel ghModel;
	private List<org.eclipse.egit.github.core.Label> labels;
	
	@FXML
	private ScrollPane labelScroll;
	@FXML
	private VBox labelsBox;
	@FXML
	private TextField labelNameField;
	@FXML
	private ColorPicker labelColorPicker;
	@FXML
	private Button labelSaveButton;
	@FXML
	private Button deleteButton;
	@FXML
	private AnchorPane labelManagementPane;

	@FXML
	public void initialize(){
		
		labelsBox.prefWidthProperty().bind(labelScroll.widthProperty().add(-2));
		labelsBox.prefHeightProperty().bind(labelScroll.heightProperty().add(-2));
		labelsBox.setSpacing(10);
		
		labelManagementPane.setVisible(false);
		
		
	}
	
	public void setMainApp(Main main) {
		this.mainApp = main;
		this.ghModel = main.getGitHubModel();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
		labels = ghModel.getRepositoryLabels(repository);
		if(labels != null)
		for (org.eclipse.egit.github.core.Label label : labels){
			addLabel(label,null);
	
		}
		
		final Label label2 = new Label("Create new label");
		label2.setMinWidth(200);
		label2.setStyle("-fx-border-color:white;-fx-border-radius:5;-fx-background-radius:5;-fx-padding: 5 15 5 15;-fx-border-radius:5;-fx-text-fill:white;"
				
				);
		ImageView iv = new ImageView();
		iv.setImage(new Image("/Images/add.png",30,30,true,true));
		label2.setGraphic(iv);
		labelsBox.getChildren().add(label2);
		
		label2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				org.eclipse.egit.github.core.Label newLabel = new org.eclipse.egit.github.core.Label();
				newLabel.setName("TO DO: change labels Name & Color");
				newLabel.setColor("9a9a9a");
				ghModel.createLabel(LabelsController.this.repository, newLabel);
				addLabel(newLabel,labelsBox.getChildren().size()-1);
			}
		});		
	}
	private void addLabel(final org.eclipse.egit.github.core.Label label,Integer position){
		
		final Label label2 = new Label(label.getName());
		label2.setMinWidth(200);
		label2.setStyle("-fx-background-color:#"+label.getColor()+";"+
						"-fx-background-radius:5;-fx-padding: 5 15 5 15;-fx-border-radius:5;"
				
				);
		if(position != null)
			labelsBox.getChildren().add(position,label2);
		else{
			labelsBox.getChildren().add(label2);
		}
		label2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				labelManagementPane.setVisible(true);
				labelNameField.setText(label.getName());
				labelColorPicker.setValue(Color.web("#"+label.getColor(),1));
				labelColorPicker.fireEvent(new ActionEvent(new Button(), labelColorPicker));
			
				labelSaveButton.setOnAction(new EventHandler<ActionEvent>() {

					public void handle(ActionEvent event) {
						String oldLabelName = label.getName();
						label.setName(labelNameField.getText());
						String color = labelColorPicker.getValue().toString().replace("0x","");
						char[] newColor = new char[6];
						color.getChars(0, 6, newColor, 0);
						String newColorS = new String(newColor);
						label.setColor(newColorS);
						labelManagementPane.setVisible(false);
						ghModel.saveLabel(repository, label,oldLabelName);
						
						labelsBox.getChildren().clear();
						setRepository(repository);						
					}
				});
				
				deleteButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						ghModel.removeLabel(repository, label);
						labelManagementPane.setVisible(false);
						
						labelsBox.getChildren().clear();
						setRepository(repository);
					}
				});
			}
		});	
	}

}
