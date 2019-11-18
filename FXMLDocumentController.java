
package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;

import javafx.scene.layout.VBox;


import util.DBUtil;

public class FXMLDocumentController implements Initializable {
    
  @FXML private MenuItem searchBySingleField;
  @FXML private MenuItem searchByMultipleField;
   
   
   @FXML private TextArea  resultConsole;
   @FXML private TextArea  queryConsole;
   
   @FXML private ListView tableList;
    ObservableList<String> tableNames;
   @FXML private MenuItem saveAs;
   @FXML private MenuItem exit;
   @FXML private VBox contentBox;
   @FXML private TableView tableView;
    public static String selectedTableName;
    public static Operations op;
    public static ResultSet rs;
    public static String sql;
   
    
    
      @FXML@Override
 public void initialize(URL location, ResourceBundle resources) {
        try {
                
            
              tableNames=getTableNames();
              tableList.getItems().addAll(tableNames);
              tableList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
             
             selectedTableName=tableNames.get(1);
             Metadata.setTableName(selectedTableName);
              
             // EmployeeDAO.setTableName(selectedTableName);
              //op=new Operations();
   
              System.out.println("ad"+selectedTableName);
            
              sql="select * from "+selectedTableName;
              rs=DBUtil.dbExecute(sql);
            displayTable(rs);
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  

 public static ObservableList<String> getTableNames()throws ClassNotFoundException,SQLException{
         sql="select table_name from user_tables";
         ObservableList<String> array=FXCollections.observableArrayList();
          rs=DBUtil.dbExecute(sql);
         while(rs.next()){
             array.add(rs.getString(1));
             
         }
         return array;
     } 

public static String selectedTable(){
  return selectedTableName;
}

 
   @FXML private void insertEmployee(ActionEvent event)throws ClassNotFoundException,SQLException{
       try{
          
            op=new Operations();
             op.insertScene();
             resultConsole.appendText(" Successful! value add to database \n");
             
              sql="select * from "+selectedTableName;
              rs=DBUtil.dbExecute(sql);
              
              displayTable(rs);
    
       }
       catch(ClassNotFoundException | SQLException e){
           resultConsole.appendText("Enter the field values!! \n");
           System.out.println("Exception in insertion  "+e);
           //throw e;
       }
        
    }
   @FXML private void setOnUpdate(ActionEvent event)throws ClassNotFoundException,SQLException{
         try {
          op=new Operations();
          op.onUpdate();
          resultConsole.appendText("Row updated!! \n");
            searchAllEmployee();
  
      } 
         catch (SQLException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
   @FXML private void setOnDelete(ActionEvent event)throws ClassNotFoundException,SQLException{
        
        try {
          op=new Operations();
          op.onDelete();
          resultConsole.appendText("Values deleted!! \n");
            searchAllEmployee();
          }
        catch (SQLException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
    
    }
       
 public void setTableList(String str){
      try {
          tableNames.clear();
          tableList.getItems().clear();
          tableNames=getTableNames();
          tableList.getItems().addAll(tableNames);
          tableList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
          tableList.getSelectionModel().select(str);
          //tableList.refresh();
      } catch (ClassNotFoundException | SQLException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
             
 }
 @FXML public void getSelectedTable(){
      try {
          
          
          selectedTableName=(String) tableList.getSelectionModel().getSelectedItem();
          System.out.println("get--"+selectedTableName);
          Metadata.setTableName(selectedTableName);
         // EmployeeDAO.setTableName(selectedTableName);
       
          sql="select * from "+selectedTableName;
           rs=DBUtil.dbExecute(sql);
          displayTable(rs);
      } catch (ClassNotFoundException | SQLException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
 }

 
 @FXML   private void searchAllEmployee()throws ClassNotFoundException,SQLException{
             sql="select * from "+selectedTableName;
              rs=DBUtil.dbExecute(sql);
             displayTable(rs);
    }

 @FXML private void exportTable()throws ClassNotFoundException,SQLException, Exception{
     
     ResultSetToExcel rexls=new ResultSetToExcel();
     rexls.createExcel(rs);
     resultConsole.appendText("Table exported to Excel!! \n");
     
 }

 
 
 @FXML private void onExit(ActionEvent event){
     QueryBuilder.window.close();
 }
  
 
 public void displayTable(ResultSet rs)throws SQLException{
     
     this.rs=rs;
      try {
         // contentBox=new VBox();
          contentBox.getChildren().clear();
          //employeeTable=null;
          op=new Operations();
          tableView=op.createTableView(rs);
          tableView.autosize();
          tableView.prefWidthProperty().bind(contentBox.widthProperty());
          contentBox.getChildren().add(tableView);
          
      } catch (ClassNotFoundException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
         
 }


@FXML
private void onsingleFieldSearch(){
      try {
          op=new Operations();
          rs=op.getSingleSearch();
          if(rs!=null)
             displayTable(rs);
          resultConsole.appendText("Search successful!! \n");
      } catch (SQLException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
}
@FXML
private void onmultipleFieldSearch(){
      try {
          op=new Operations();
          rs= op.getMultipleSearch();
         if(rs!=null)
          displayTable(rs);
         resultConsole.appendText("Search sucessfull!! \n");
      } catch (SQLException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
}

@FXML
private void setOnNewTable(){
    
     try{
          
           op=new Operations();
             String tname=op.createNewTable();
             resultConsole.appendText(" Successful!! table created  \n");
             
             setTableList(tname);
             sql="select * from "+tname;
              rs=DBUtil.dbExecute(sql);
            displayTable(rs);
            
    
       }
       catch(ClassNotFoundException | SQLException e){
           resultConsole.appendText("Enter the field values!! \n");
           System.out.println("Exception in creation of new table  "+e);
           //throw e;
       }
}

@FXML private void onDropTable(){
     try {
            sql="DROP TABLE "+selectedTableName;
            DBUtil.dbExecuteQuery(sql);
            resultConsole.appendText("Table droped!! \n");
        } catch (SQLException | ClassNotFoundException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
    setTableList(null);
}

@FXML private void setOnExecute() throws SQLException{
    
      try {
          rs= DBUtil.dbExecute(queryConsole.getText());
          if(rs!=null)
              displayTable(rs);
      } catch (ClassNotFoundException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
}
@FXML private void queryClear(){
    queryConsole.clear();
}

@FXML private void dbDetails(){
    ObservableList<String> dbdetail=Metadata.dbMetadata();
    
    op=new Operations();
   op.onDetail(dbdetail);
    
    
}


@FXML private void onAlter(){
      try {
           op=new Operations();
          op.onAlter();
          searchAllEmployee();
          resultConsole.appendText("Table altered!! \n");
      } catch (ClassNotFoundException | SQLException ex) {
          Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
      }
}

@FXML private void innerJoin(){
    op=new Operations();
    rs=op.joinOperation("inner join", tableNames);
     if(rs!=null)
              try {
                  displayTable(rs);
    } catch (SQLException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
    }
}


@FXML private void leftJoin(){
   op=new Operations();
    rs=op.joinOperation("left join", tableNames);
     if(rs!=null)
              try {
                  displayTable(rs);
    } catch (SQLException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
    } 
}

@FXML private void rightJoin(){
    op=new Operations();
    rs=op.joinOperation("right join", tableNames);
     if(rs!=null)
              try {
                  displayTable(rs);
    } catch (SQLException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
    }
}

@FXML private void fullJoin(){
    op=new Operations();
    rs=op.joinOperation("full join", tableNames);
     if(rs!=null)
              try {
                  displayTable(rs);
    } catch (SQLException ex) {
        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
    }
}

}