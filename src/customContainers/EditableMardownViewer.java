package customContainers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;

import model.GitHubModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class EditableMardownViewer extends AnchorPane{

	private ScrollPane scroll;
	private WebView webView;
	private Label stateLabel;
	private Label authorLabel;
	private Label dateLabel;
	private Button editButton;
	private Button removeButton;
	private Button cancelButton;
	private ImageView userImage;
	private HBox hbox;
	private TextArea editArea;
	
	private GitHubModel GHModel;
	private final WebEngine webEngine;
	
	private Repository repository;
	private Issue issue = null;
	private Comment comment = null;

	
	public EditableMardownViewer(GitHubModel GHModel) {
		this.GHModel = GHModel;
		webView = new WebView();
		stateLabel = new Label();
		authorLabel = new Label();
		dateLabel = new Label();
		editButton = new Button("Edit");
		removeButton = new Button("Remove");
		cancelButton = new Button("Cancel");
		hbox = new HBox();
		scroll = new ScrollPane();
		userImage = new ImageView();
		editArea = new TextArea();
		
		webEngine =  webView.getEngine();
		
		scroll.setContent(webView);
		this.getChildren().add(scroll);
		this.getChildren().add(hbox);
		
		hbox.getChildren().add(userImage);
		hbox.getChildren().add(stateLabel);
		hbox.getChildren().add(authorLabel);
		hbox.getChildren().add(dateLabel);
		
	    Region spacer = new Region();
	    HBox.setHgrow(spacer, Priority.ALWAYS);

		hbox.getChildren().add(spacer);
		hbox.getChildren().add(editButton);
		hbox.getChildren().add(removeButton);

		AnchorPane.setBottomAnchor(scroll, (double) 0);
		AnchorPane.setTopAnchor(scroll, (double) 40);
		AnchorPane.setLeftAnchor(scroll, (double) 0);
		AnchorPane.setRightAnchor(scroll, (double) 0);
		
		AnchorPane.setTopAnchor(hbox, (double) 0);
		AnchorPane.setLeftAnchor(hbox, (double) 0);
		AnchorPane.setRightAnchor(hbox, (double) 0);
		
		this.setStyle("-fx-border-color:#c3c1c1;-fx-border-radius:5;-fx-padding: 5 5 5 5;");
		editButton.setStyle("-fx-border-color:white;-fx-border-radius:5;-fx-background-radius:5;");
		removeButton.setStyle("-fx-border-color:white;-fx-border-radius:5;-fx-background-radius:5;");
		cancelButton.setStyle("-fx-border-color:white;-fx-border-radius:5;-fx-background-radius:5;");
	
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if(editButton.getText().compareTo("Edit") == 0){
					scroll.setContent(editArea);
					editButton.setText("Save");
					hbox.getChildren().add(cancelButton );
				}else{
					
					hbox.getChildren().remove(cancelButton );

					scroll.setContent(webView);
					editButton.setText("Edit");
				
					setContentHTML(EditableMardownViewer.this.GHModel.markdownToHtml(editArea.getText(),repository));
					
					if(issue != null && repository != null){
						issue.setBody(editArea.getText());
						EditableMardownViewer.this.GHModel.saveIssue(repository, issue);
					}else if(comment != null && repository != null){
						comment.setBody(editArea.getText());
						EditableMardownViewer.this.GHModel.saveComment(repository, comment);

					}
				}
			}
		});
		
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				hbox.getChildren().remove(cancelButton );
				scroll.setContent(webView);
				editButton.setText("Edit");
				editArea.setText(issue.getBody());
			}
		});
		
		
		scroll.heightProperty().addListener(new ChangeListener<Number>() {
		    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		    	webView.setPrefHeight(newSceneHeight.doubleValue()-2);
		    	editArea.setPrefHeight(newSceneHeight.doubleValue()-2);
		    }
		});
		scroll.widthProperty().addListener(new ChangeListener<Number>() {
		    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
		    	webView.setPrefWidth(newSceneWidth.doubleValue()-2);
		    	editArea.setPrefWidth(newSceneWidth.doubleValue()-2);
		    }
		});
	}
	
	private void setContentHTML(String htmlContent){
		String html = 
				"<link href=\"https://assets-cdn.github.com/assets/github-c13b2c9e805745ba25729ccbf701703a88a37633.css\" media=\"all\" rel=\"stylesheet\" type=\"text/css\" />"+
						"<div class=\"comment-body markdown-body markdown-format js-comment-body\">";
			
		html+=htmlContent;
        webEngine.loadContent(html);
	}
	
	public void setContentMarkdown(String content){
		editArea.setText(content);
		setContentHTML(GHModel.markdownToHtml(content,repository));
	}
	
	
	
	public void setDate(String date){
		this.dateLabel.setText(date);
		this.dateLabel.getStyleClass().add("item-h4");
	}
	
	public void setAuthor(String author){
		this.authorLabel.setText(author);
		this.authorLabel.getStyleClass().add("item-h2");

	}
	
	public void setAuhtorImage(String url){
		Image img = new Image(url,30,30,false,false);
		userImage.setImage(img);
		userImage.setBlendMode(BlendMode.LIGHTEN);
	}
	
	
	public void hideRemoveButton(){
		hbox.getChildren().remove(removeButton);
	}
	
	public void setIssue(Issue issue){
		this.issue = issue;
		setContentMarkdown(issue.getBody());

		setAuthor(issue.getUser().getLogin());
		setAuhtorImage(issue.getUser().getAvatarUrl());
		SimpleDateFormat format = new SimpleDateFormat("MMMMM dd");
		Date closedAt = issue.getClosedAt();
		if(closedAt != null)
			setDate("Opened on "+format.format(issue.getCreatedAt())+ ", Updated on "+format.format(issue.getUpdatedAt()) +" and Closed on "+ format.format(closedAt) );
		else
			setDate("Opened on "+format.format(issue.getCreatedAt())+ "and Updated on "+format.format(issue.getUpdatedAt()));

	}

	public void setComment(Comment com) {
		this.comment = com;
		String body = com.getBody();
		if(body != null)
			setContentMarkdown(body);

		setAuthor(com.getUser().getLogin());
		setAuhtorImage(com.getUser().getAvatarUrl());
		SimpleDateFormat format = new SimpleDateFormat("MMMMM dd");
		setDate("Created on "+format.format(com.getCreatedAt()) + " and Updated on "+format.format(com.getUpdatedAt()) );
	}
	
	public void setRepository(Repository repository){
		this.repository = repository;
		
	}
	
	public void setOnRemoveAction(EventHandler<ActionEvent> eventH){
		removeButton.setOnAction(eventH);
		
	}
	
	
}
