package controller;





import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;

import utils.NamedNode;
import application.Main;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class IndexController {

	protected static final double MIN_WIDTH = 1200;
	protected static final double MIN_HEIGHT = 800;
	
	@FXML
	private Pane topBar;
	@FXML
	private Rectangle quit;
	@FXML
	private Rectangle minimize;
	@FXML
	private Rectangle maximize;
	@FXML
	private Rectangle innerMaximize;
	@FXML
	private Label quitLabel;
	@FXML
	private Label quitNotification;
	@FXML 
	private AnchorPane contentPane;
	@FXML
	private AnchorPane notificationPane;
	@FXML 
	private TextArea notificationLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private ImageView userImage;
	@FXML
	private Polygon backButton;
	@FXML
	private Polygon nextButton;
	@FXML
	private ImageView resizeImage;
	@FXML
	private ScrollPane scroll;
	
	//LEFT BAR
	@FXML
	private TextField otherRepositoryField;
	@FXML
	private Button otherOkButton;
	@FXML
	private VBox leftBox;
	@FXML
	private HBox otherChoiceBox;
	
	
	@FXML
	private Line leftBar;
	@FXML
	private Line topBarLine;
	

	@FXML
	private Label contentTitle;

	


	
	private List<NamedNode> memory;
	private int currentIdx=-1;
	
	private Main mainApp;
	private double dragDeltaX = 0;
	private double dragDeltaY = 0;
	private double pressedX = 0;
	private double pressedY = 0;
	private double draggedX = 0;
	private double draggedY = 0;
	BoundingBox savedBounds, savedFullScreenBounds;
    boolean maximized = false;
	
	public IndexController() {

	}
	
	@FXML
    private void initialize() {
		quitLabel.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
             
            public void handle(MouseEvent t) {
                Platform.exit();
            }
        });
		
		userImage.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
             
            public void handle(MouseEvent t) {
                IndexController.this.mainApp.loadHomeView();
            }
        });
		
		quitNotification.setOnMouseClicked(new EventHandler<MouseEvent>()
	    {
	         
	        public void handle(MouseEvent t) {
	            notificationPane.setVisible(false);
	        }
	    });
		
		topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent mouseEvent) {
		    Stage stage = mainApp.getPrimaryStage();
		    dragDeltaX = stage.getX() - mouseEvent.getScreenX();
		    dragDeltaY = stage.getY() - mouseEvent.getScreenY();
		  }
		});
		topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent mouseEvent) {
		    Stage stage = mainApp.getPrimaryStage();
		    stage.setX(mouseEvent.getScreenX() + dragDeltaX);
		    stage.setY(mouseEvent.getScreenY() + dragDeltaY);
		  }
		});
		
		
		maximize.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
             
            public void handle(MouseEvent t) {
                maximizeAction();
            }
        });
		
		innerMaximize.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
             
            public void handle(MouseEvent t) {
                maximizeAction();
            }
        });
		
		minimize.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
             
            public void handle(MouseEvent t) {
            	minimizeAction();
            }
        });
		
		backButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
             
            public void handle(MouseEvent t) {
            	previousPane();
            }
        });
		
		nextButton.setOnMouseClicked(new EventHandler<MouseEvent>()
	    {
	         
	        public void handle(MouseEvent t) {
	        	nextPane();
	        }
	    });
		
		resizeImage.setOnMousePressed(new EventHandler<MouseEvent>(){
             
            public void handle(MouseEvent e) {
                pressedX = e.getX();
                pressedY = e.getY();
            }
        });
		
		resizeImage.setOnMouseDragged(new EventHandler<MouseEvent>(){
             
            public void handle(MouseEvent e) {
                draggedX = e.getX();
                draggedY = e.getY();

                double differenceX = draggedX - pressedX;
                double differenceY = draggedY - pressedY;

                Stage primaryStage = mainApp.getPrimaryStage();
                if(primaryStage.getWidth() + differenceX>MIN_WIDTH)
                	primaryStage.setWidth(primaryStage.getWidth() + differenceX);
                if(primaryStage.getHeight() + differenceY>MIN_HEIGHT)
                	primaryStage.setHeight(primaryStage.getHeight() + differenceY);
            }
        });
		
		otherOkButton.setOnAction(new EventHandler<ActionEvent>() {
			 
			public void handle(ActionEvent event) {
				final Repository repo = mainApp.getGitHubModel().loadRepository(otherRepositoryField.getText());
				if(repo != null){
					final String repoName = repo.getName();
					EventHandler<ActionEvent> clicEvent = new EventHandler<ActionEvent>() {
						 
						public void handle(ActionEvent event) {
							IndexController.this.mainApp.loadRepoWiew(repo);
						}
					};
					addBoxButtonWithImage(repo.getName(),repo.getId(),new Image(repo.getOwner().getAvatarUrl(),50,50,true,true),leftBox,clicEvent);
				}
			}
		});
		
		otherRepositoryField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER)
				{
					final Repository repo = mainApp.getGitHubModel().loadRepository(otherRepositoryField.getText());
					if(repo != null){
						final String repoName = repo.getName();
						EventHandler<ActionEvent> clicEvent = new EventHandler<ActionEvent>() {
							public void handle(ActionEvent event) {
								IndexController.this.mainApp.loadRepoWiew(repo);
							}
						};
						
						addBoxButtonWithImage(repo.getName(),repo.getId(),new Image(repo.getOwner().getAvatarUrl(),50,50,true,true),leftBox,clicEvent);
					}
				}
			}
		});
		
		notificationPane.setVisible(false);
		leftBox.setVisible(false);
		
		nameLabel.setText("Welcome");
		memory = new ArrayList<NamedNode>();
		
		contentPane.prefWidthProperty().bind(scroll.widthProperty().add(-2));
		contentPane.prefHeightProperty().bind(scroll.heightProperty().add(-2));

		scroll.setStyle("-fx-background-color:transparent;");
		
		resizeImage.setImage(new Image("/Images/resize.png"));

		
	

	}


    protected void previousPane() {
    	if(currentIdx >= 1){
        	contentPane.getChildren().clear();
        	currentIdx --;
    		contentPane.getChildren().add(memory.get(currentIdx).getNode());
    		setContentTitle(memory.get(currentIdx).getName());
    	}
    	if(currentIdx == 0){
    		leftBox.setVisible(false);
    	}
	}
    
    protected void nextPane() {
    	if(memory.size()>currentIdx+1){
        	contentPane.getChildren().clear();
        	currentIdx++;
    		contentPane.getChildren().add(memory.get(currentIdx).getNode());
    		setContentTitle(memory.get(currentIdx).getName());
    	}
    	if(currentIdx == 1){
    		leftBox.setVisible(true);
    	}
	}

	public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        
        Stage stage = mainApp.getPrimaryStage();
		Scene scene = stage.getScene();
		scene.widthProperty().addListener(new ChangeListener<Number>() {
		    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {		    	
		    	topBarLine.setEndX(newSceneWidth.intValue()-520);
		    }
		});
		scene.heightProperty().addListener(new ChangeListener<Number>() {
		    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		    	leftBar.setEndY(newSceneHeight.doubleValue()-120);
		    }
		});
    }
    
    
    private void maximizeAction(){
	    Stage stage = mainApp.getPrimaryStage();
	    if (maximized) {
	    	stage.setX(savedBounds.getMinX());
            stage.setY(savedBounds.getMinY());
            stage.setWidth(savedBounds.getWidth());
            stage.setHeight(savedBounds.getHeight());
            savedBounds = null;
            maximized = false;
        } else {
            ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            Screen screen = screensForRectangle.get(0);
            Rectangle2D visualBounds = screen.getVisualBounds();

            savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());
            maximized = true;
        }
    }
    
    public void minimizeAction() {

        if (!Platform.isFxApplicationThread()) // Ensure on correct thread else hangs X under Unbuntu
        {
            Platform.runLater(new Runnable() {
                 
                public void run() {
                    _minimize();
                }
            });
        } else {
            _minimize();
        }
    }

    private void _minimize() {
        Stage stage = mainApp.getPrimaryStage();
        stage.setIconified(true);
    }

	public void setContent(Node content,String nodeTitle) {
		
		ObservableList<Node> list = contentPane.getChildren();
		currentIdx++;
		memory.add(currentIdx,new NamedNode(content,nodeTitle));
		for(int i = memory.size()-1 ; i > currentIdx ; i--){
			memory.remove(i);
		}
		
		list.clear();
		list.add(content);
		content.autosize();
		
		content.setStyle(
				"-fx-min-width:"+contentPane.getWidth()+";" +
				"-fx-min-height:"+contentPane.getHeight()+";" 
						);
		
		if(content instanceof AnchorPane){
			((AnchorPane) content).prefWidthProperty().bind(contentPane.widthProperty());
			((AnchorPane) content).prefHeightProperty().bind(contentPane.heightProperty());

		}
		
		

	}
	
	public AnchorPane getContent(){
		return contentPane;
	}

	public void writeNotification(String message) {
		notificationLabel.setText(message);
		notificationPane.setOpacity(0);
		notificationPane.setVisible(true);
		FadeTransition ft = new FadeTransition(Duration.millis(4000), notificationPane);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.setCycleCount(Timeline.INDEFINITE);
		ft.setCycleCount(2);
		ft.setAutoReverse(true);
		ft.play();
	}

	public void setName(String name) {
		nameLabel.setText(name);
	}

	public void setUserImage(Image image) {
		userImage.setImage(image);
	}
	
	
	
	private Button createLeftButton(String name,long id, EventHandler<ActionEvent> event){
		Button butt = new Button();
		butt.setId(new Long(id).toString());
		butt.setPrefWidth(270);
		butt.setPrefHeight(50);
		butt.setText(name);
		butt.setStyle("    -fx-font-size: 12pt;"+
					"-fx-text-alignment: left;"+
					"-fx-padding: 10 10 10 10;");
		
		butt.setOnAction(event);

		return butt;
	}
	
	private void addBoxButtonWithImage(String name,long id, Image img,VBox dest,EventHandler<ActionEvent> event){
		HBox hb = new HBox();
		dest.getChildren().add(hb);
		ImageView imageView = new ImageView(img);
		imageView.setFitHeight(50);
		imageView.setFitWidth(50);
		hb.getChildren().add(imageView);
		hb.getChildren().add(createLeftButton(name,id,event));
	}

	public void setLeftBar(){
		leftBox.setVisible(true);
		leftBox.getChildren().clear();
		leftBox.getChildren().add(otherChoiceBox);
		for (final Repository repo : mainApp.getGitHubModel().getRepositories()) {
			final String repoName = repo.getName();
			EventHandler<ActionEvent> clicEvent = new EventHandler<ActionEvent>() {
				 
				public void handle(ActionEvent event) {
					IndexController.this.mainApp.loadRepoWiew(repo);
					/*List<RepositoryContents> contents = IndexController.this.mainApp.getGitHubModel().getContents(repo);
					if(contents != null){
						for (RepositoryContents repositoryContents : contents) {
							System.out.println(repositoryContents.getName() + " --- "+repositoryContents.getType());
						}
					}*/
				}
			};
			addBoxButtonWithImage(repo.getName(),repo.getId(),mainApp.getGitHubModel().getUserImage(repo.getOwner()),leftBox,clicEvent);
		}
	}


	public void setContentTitle(String contentTitle) {
		this.contentTitle.setText(contentTitle);
	}
}



