/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author welcome
 */
public class QueryBuilder extends Application {
    
    public static Stage window;
    private static Scene displayScene;
    
    @Override
    public void start(Stage stage) throws Exception {
        window=stage;
        Parent root = FXMLLoader.load(getClass().getResource("LoginWimdow.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        
        
       // window.setOnCloseRequest(e->{  e.consume();  closeProgram();});
    }
    
    public static void changeScene(Scene sn)
    {
        displayScene=sn;
        window.setScene(displayScene);
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
   
    
    
    
}
