package customContainers;

import model.GitHubModel;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import utils.Utils;
import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class KanbanIssueBox extends VBox {
	
	private VBox container;

	public KanbanIssueBox(final Issue issue, final Repository repository, final Main mainApp,final GitHubModel githubModel,VBox theContainer) {
	
		container = theContainer;
		HBox labelBox = new HBox();
		labelBox.setSpacing(5);
		labelBox.setMinHeight(10);
		labelBox.setMaxHeight(10);
		this.setMinHeight(85);
		this.setStyle("-fx-background-radius:5;-fx-background-color:#a4a4a4;-fx-padding:5 5 5 5;");
		Label issueLabel = new Label(issue.getTitle());
		issueLabel.getStyleClass().add("item-title");
		if(issue.getState().compareTo("closed") == 0){
			ImageView iv = new ImageView();
			iv.setImage(new Image("/Images/validate.png",20,20,false,false));
			issueLabel.setGraphic(iv);
			issueLabel.setStyle("-fx-text-fill:#A80000;-fx-opacity:1;");
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
					this.setMinHeight(this.getMinHeight()+15);
				}
			}
		}

		this.getChildren().add(labelBox);
		this.getChildren().add(issueLabel);
		this.getChildren().add(checkboxBox);
		this.getChildren().add(imageBox);
	    Region spacer = new Region();
	    HBox.setHgrow(spacer, Priority.ALWAYS);
		imageBox.getChildren().addAll(spacer,userImage);
		

		
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				mainApp.loadIssue(repository, issue);
			}
		});				
		
		this.setOnDragDetected(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent event) {
		        /* drag was detected, start a drag-and-drop gesture*/
		        /* allow any transfer mode */
		        Dragboard db = KanbanIssueBox.this.startDragAndDrop(TransferMode.MOVE);
		        
		        /* Put a string on a dragboard */
		        ClipboardContent content = new ClipboardContent();
	        	content.put(Utils.issueFormat,issue);
	        	db.setContent(content);
		        
		        event.consume();
		    }
		});
		
		this.setOnDragDone(new EventHandler<DragEvent>() {
		    public void handle(DragEvent event) {
		        /* the drag and drop gesture ended */
		        /* if the data was successfully moved, clear it */
		        if (event.getTransferMode() == TransferMode.MOVE) {
		            container.getChildren().remove(KanbanIssueBox.this);
		        }
		        event.consume();
		    }
		});
	}
}
