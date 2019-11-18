/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import util.DBUtil;

/**
 * FXML Controller class
 *
 * @author welcome
 */
public class LoginWimdowController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private  TextField username;
    @FXML
    private TextField password;
   
    String uname,pass;
    int flag=0;
    @FXML
    public void loginAction() throws SQLException,ClassNotFoundException{
        uname=username.getText();
        pass=password.getText();
        
        try {
            flag=DBUtil.getUser(uname,pass);
        } catch (Exception ex) {
            
        }
       
        if(flag==1)
        {
            try {
                Scene s=new Scene(FXMLLoader.load(getClass().getResource("FXMLDocument.fxml")));
                QueryBuilder.changeScene(s);
            } catch (IOException ex) {
                Logger.getLogger(LoginWimdowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
       
    }
    
    
    public  void onClear(ActionEvent event){
        username.setText(null);
        password.setText(null);
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
