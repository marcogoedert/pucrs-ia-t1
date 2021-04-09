import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner; 
import javafx.scene.layout.VBox;
import javafx.geometry.Insets; 
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;

public class MainInterface extends Application {
    private static Random rand = new Random();
    public static int[][] alunos; // Configuração inicial
    public static int[][] populacao;
    public static int[][] intermediaria;
    public static int TAM_POPULACAO = 5;
    public static int TAM_ALUNOS;
    private Desktop desktop = Desktop.getDesktop();
    private ObservableList list; 
    final MainController controller = new MainController();

    private boolean isCompleteVisualization = false;

    private int numberPopulation;

    private ScrollPane sp = new ScrollPane();

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        final FileChooser fileChooser = new FileChooser();

 
        final Button openButton = new Button("Abrir arquivo...");
        final Button startButton = new Button("Iniciar experimento");
        TextField numberOfPopulation = new TextField("Numero da população");

        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Visualização rapida");  
        rb1.setToggleGroup(group);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Visualização completa");
        rb2.setToggleGroup(group);
 
        HBox hbox = new HBox(openButton, startButton, rb1,rb2);
        hbox.setSpacing(20);

        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        String result = controller.loadFile(file);
                        showExperiment(result);
                    }
                }
            }
        );
            
        startButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                //aqui vai iniciar a simulacao de fato
                    RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                    String selected = selectedRadioButton.getText();
                    if(selected == "Visualização rapida") {
                        isCompleteVisualization = false;
                    } else {
                        isCompleteVisualization =true;
                    }
                    controller.startExperiment();
                    showExperiment(controller.showFinalResult());
                    numberPopulation = Integer.parseInt(numberOfPopulation.getText()); 
                    System.out.println("Valor digitado" + numberPopulation);

                    System.out.println("Tipo de visualizacao" + isCompleteVisualization);
                }
            });
                    

        //Setting the space between the nodes of a VBox pane 
        root.setSpacing(5);   
      
        //Setting the margin to the nodes 
        root.setMargin(hbox, new Insets(20, 20, 20, 20));  

        list = root.getChildren(); 
        list.addAll(hbox, numberOfPopulation);
        list.add(sp);
        Scene scene = new Scene(root, 740, 580);
        stage.setScene(scene);
        stage.show();
    }

    private void showExperiment(String result) { 
        Label l = new Label(result);
        //list.add(l);
        sp.setContent(l);
    }

    public static void main(String[] args) {
        launch();
    }

}
