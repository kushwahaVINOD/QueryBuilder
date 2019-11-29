
package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.DBUtil;

public class Operations extends Application {
    Stage opWindow;
    Scene opScene;
    VBox vbox;
    String promptxt[],type[],values[],autofield[],sql,optr,tableName;
    int n;
    Button btn,close,clear;
    public boolean flag;
    String dataType[]=new String[]{"char","varchar","int","long","double","decimal","date","timestamp"};
    String midOperators[]=new String[]{"=","!=","<",">","<=",">=","LIKE","!<","!>"};
    String conjOperators[]=new String[]{"AND","OR"};
    ArrayList<ComboBox<String>> midOperator,conjOperator;
    ArrayList<ComboBox<String>> midOperator1;
    ArrayList<javafx.scene.control.TextField> atxt=new ArrayList<>();
    ArrayList<String> selectedfield=new ArrayList<>();
    ArrayList<String> selectedvalues=new ArrayList<>();
      ArrayList<String> selectedType=new ArrayList<>();
       ArrayList<String> selectedOperator=new ArrayList<>();
       ArrayList<String> selectedConjOperator=new ArrayList<>();
       ArrayList<String> autoincmts=new ArrayList<>();
       ArrayList<CheckBox> field=new ArrayList<>();
    ObservableList<ObservableList> data;
    TableView tableview;
     ResultSet rs,rs2,rs3;
     String[] table1colmns;
    
    public Operations(){
         opWindow=new Stage();
        opWindow.initModality(Modality.APPLICATION_MODAL);
        opWindow.setMinWidth(800);
        btn=new Button();
        close=new Button();
        clear=new Button();
           vbox=new VBox(30);
            n=Metadata.getNumberOfColumn();
            midOperator=new ArrayList<ComboBox<String>>();
             midOperator1=new ArrayList<ComboBox<String>>();
            conjOperator=new ArrayList<ComboBox<String>>();
          
        try {
            promptxt=Metadata.getColumnNames();
             type=Metadata.getColumnTypeNames();
        } catch (SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getTableName(){
        tableName=FXMLDocumentController.selectedTable();
        System.out.println("setop-"+tableName);
    }
    
    public  void insertScene()
    {
        getTableName();
       opWindow.setTitle("Insert Records");
            System.out.println("no.="+n);
            for(int i=0;i<n;i++){
                TextField tf=new TextField();
                tf.setId("textfield"+(i+1));
   
               if(type[i].equalsIgnoreCase("date"))
                    tf.setPromptText("  "+promptxt[i]+"("+type[i]+" dd/mm/yyyy)");
               else if(type[i].equalsIgnoreCase("timestamp"))
                    tf.setPromptText("  "+promptxt[i]+"("+type[i]+" hh:mi:ss)");
               else
                    tf.setPromptText("  "+promptxt[i]+"("+type[i]+" )");
                atxt.add(tf);
               vbox.getChildren().add(tf);
            }
            vbox.setAlignment(Pos.CENTER);
            
            btn.setText("ADD");
            
            close.setText("close"); 
            
            clear.setText("Reset Values");
            
            int size=atxt.size();
            values=new String[size];
           
             
             btn.setOnAction((ActionEvent event) ->
             {
                 int i=0;
                 for(TextField tf:atxt){
                     System.out.println("sahi d");
                     if(tf.getText().contains("Auto increment"))
                           values[i++]="seq_"+tableName+".nextval";
                     else
                            values[i++]=tf.getText();
                 }
                 
                 sql="insert into "+tableName+" values(";
                 for(i=0;i<values.length;i++){
            
                  System.out.println(values[i]);
            
                  if(type[i].contains("TIMESTAMP"))
                       sql=sql+"to_timestamp('"+values[i]+"','HH24:MI:SS.FF')";
                   else if(type[i].contains("DATE"))
                           sql=sql+"to_date('"+values[i]+"','DD/MM/YYYY')";
                  else
                       sql=sql+"'"+values[i]+"'";
                  
            if(!(i==values.length-1))
                sql=sql+",";
            else
                sql=sql+")";
        }
                 System.out.println(sql);
                 
                 try {
                     DBUtil.dbExecuteQuery(sql);
                 } catch (SQLException | ClassNotFoundException ex) {
                     Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                 }
           });
             
             close.setOnAction((ActionEvent e) -> {
                 opWindow.close();
           });
             clear.setOnAction((ActionEvent e)->{
             
             for(TextField tf:atxt){
                    
                 tf.setText(null);
             }
             
             });
             HBox hb=new HBox(20);
             hb.getChildren().addAll(btn,clear,close);
              vbox.getChildren().add(hb);
              ScrollPane sp=new ScrollPane(vbox);
             opScene=new Scene(sp,400,400);
             opWindow.setScene(opScene);
             opWindow.showAndWait();
    }
    
    
    public TableView createTableView(ResultSet rs)throws SQLException,ClassNotFoundException{
        
        tableview=new TableView();
        data=FXCollections.observableArrayList();
        data.clear();
       //Table creation columns
       int count=Metadata.getNumberOfColumn(rs);
       String colNames[]=Metadata.getColumnNames(rs);
       for(int i=0;i<count;i++)
       {
           final int j=i;
           TableColumn col=new TableColumn(colNames[i]);
           col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>()
           {
               @Override
               public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                     SimpleStringProperty ssp=new SimpleStringProperty(param.getValue().get(j).toString());
                        return ssp ;
               }
           });
           tableview.getColumns().addAll(col);
           System.out.println("Column["+i+"]");
           
       }
       while(rs.next())
       {
           ObservableList<String> row=FXCollections.observableArrayList();
           for(int i=1;i<=count;i++){
               //iterate column
               if(!(rs.getString(i) == null))
               row.add(rs.getString(i));
               else
               row.add("-");    
           }
            System.out.println("Row add"+row);
            data.add(row);
       }
       tableview.setItems(data);
       return tableview;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ResultSet getSingleSearch(){
        
        getTableName();
        opScene=null;
        vbox=new VBox();
      
        Label lb=new Label("Select  a field");
        vbox.getChildren().add(lb);
        ToggleGroup radioGroup=new ToggleGroup();
        ArrayList<RadioButton> field=new ArrayList<>();
        opWindow.setTitle("Single field Search");
        for(int i=0;i<n;i++)
        {
            RadioButton rb=new RadioButton(promptxt[i]);
            rb.setToggleGroup(radioGroup);
            rb.setId("radio"+(i+1));
            field.add(rb);
            ComboBox<String> cb=new ComboBox<>();
            cb.getItems().addAll(midOperators);
            midOperator.add(cb);
                TextField tf=new TextField();
                tf.setId("textfield"+(i+1));
                tf.setPromptText("value type-"+type[i]);
                atxt.add(tf);
               HBox hb=new HBox(20);
               hb.setPadding(new Insets(5));
               hb.getChildren().addAll(rb,cb,tf);
                 vbox.getChildren().add(hb);
        }
            vbox.setAlignment(Pos.CENTER);
            btn.setText("ok");
            close.setText("close");       
            btn.setOnAction(e->
             {
                 int i=0;
                 for(RadioButton r:field){
                     System.out.println("radio check"); 
                     if(r.isSelected())
                     {
                         selectedfield.add(r.getText());
                         selectedvalues.add(atxt.get(i).getText());
                         selectedType.add(type[i]);
                         optr=midOperator.get(i).getValue();
                         i++;
                     }
                     else
                         i++;
                 }
                 i=0;
                 System.out.print("op -"+tableName);
                 System.out.print("op type -"+selectedType.get(0));
                 sql="select * from "+tableName+" where ";
                 for(String str:selectedfield)
                 {
                     if((selectedType.get(i)).contains("VARCHAR") && !optr.equalsIgnoreCase("like"))
                             sql=sql+str+" "+optr+" '"+selectedvalues.get(i)+"'";
                     else if(optr.equalsIgnoreCase("like")){
                          sql=sql+"  "+str+" "+optr+" '"+selectedvalues.get(i)+"'";
                     }
                     else
                         sql=sql+str+optr+" "+selectedvalues.get(i);
                     i++;
                 } 
  
            try {
                System.out.print(sql);
                rs=DBUtil.dbExecute(sql);
                opWindow.close();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
           });
             
             close.setOnAction((ActionEvent e) -> {
                 opWindow.close();
           });
             HBox hb=new HBox(10);
             hb.getChildren().addAll(btn,close);
              vbox.getChildren().addAll(hb);
              vbox.setPadding(new Insets(5));
              ScrollPane sp=new ScrollPane(vbox);
             opScene=new Scene(sp,400,400);
             opWindow.setScene(opScene);
             opWindow.showAndWait();
             return rs;
        
    }
    
    public ResultSet getMultipleSearch(){
        getTableName();
        opWindow.setTitle("Multiple Field Search ");
      Label lb=new Label("Select fields");
        vbox.getChildren().add(lb);
        for(int i=0;i<n;i++)
        {
            CheckBox rb=new CheckBox(promptxt[i]);
            field.add(rb);
            
             ComboBox<String> cb=new ComboBox<>();
            cb.getItems().addAll(midOperators);
            midOperator.add(cb);
            
             TextField tf=new TextField();
                tf.setId("textfield"+(i+1));
                tf.setPromptText("value type-"+type[i]);
                atxt.add(tf);
               HBox hb=new HBox(10);
               hb.setPadding(new Insets(5));
               hb.getChildren().addAll(rb,cb,tf);
       
            vbox.getChildren().add(hb);
            if(0<=i && i<promptxt.length-1 && promptxt.length>1){
                ComboBox<String> conj=new ComboBox<>();
                conj.getItems().addAll(conjOperators);
                conjOperator.add(conj);
                vbox.getChildren().add(conj);
            }
            }
        
            vbox.setAlignment(Pos.CENTER_LEFT);
            vbox.setPadding(new Insets(10));
            btn.setText("ok");
            close.setText("close"); 
             btn.setOnAction(e->
             {
                 int i=0;
                 for(CheckBox r:field){
                     System.out.println(" check");
                     if(r.isSelected())
                     {
                         selectedfield.add(r.getText());
                         selectedvalues.add(atxt.get(i).getText());
                         selectedType.add(type[i]);
                          optr=midOperator.get(i).getValue();
                          selectedOperator.add(optr);
                     }
                     if(0<=i && i<field.size()-1 && conjOperator.get(i).getValue()!=null){
        
                         selectedConjOperator.add(conjOperator.get(i).getValue());
                         System.out.println("conj="+selectedConjOperator.get(i));
                           }
       
                     i++;
                 }
                 i=0;
                 int k=0;
                 System.out.print("op -"+tableName);
                 System.out.print("op type -"+selectedType.get(0));
                 sql="select * from "+tableName+" where ";
                 for(String str:selectedfield)
                 {
                     if((selectedType.get(i)).contains("VARCHAR") && !selectedOperator.get(i).equalsIgnoreCase("like"))
                     { sql=sql+"  "+str+" "+selectedOperator.get(i)+" '"+selectedvalues.get(i)+"'";
                     }
                     else if(selectedOperator.get(i).equalsIgnoreCase("like")){
                          sql=sql+"  "+str+" "+selectedOperator.get(i)+" '"+selectedvalues.get(i)+"'";
                     }
                     else
                     { sql=sql+str+" "+selectedOperator.get(i)+" "+selectedvalues.get(i);
                     
                     }
                    if(k<selectedfield.size()-1)
                    {sql=sql+" "+selectedConjOperator.get(k)+" ";
                     System.out.println("sqls -"+sql);
                    
                    k++;
                    }
                     i++;
                 }   
                 k=0;i=0;
            try {
                System.out.print(sql);
               rs=DBUtil.dbExecute(sql);
               
                opWindow.close();
           } catch (SQLException | ClassNotFoundException ex) {
               Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
           }
           });
             
             close.setOnAction((ActionEvent e) -> {
                 opWindow.close();
           });
             
             HBox hb=new HBox(20);
             hb.getChildren().addAll(btn,close);
              vbox.getChildren().add(hb);
              ScrollPane sp=new ScrollPane(vbox);
             opScene=new Scene(sp,400,400);
             opWindow.setScene(opScene);
             opWindow.showAndWait();
             return rs;
        
    }

    public String createNewTable()
    {
      opWindow.setTitle("Create New Table");
       Label tname=new Label("Enter Table Name");
       TextField newtablename=new TextField();
       HBox hb=new HBox(10);
       hb.getChildren().addAll(tname,newtablename);
       
       ArrayList<String> colnNme=new ArrayList<>();
       ArrayList<String> typeCont=new ArrayList<>();
        Label colnamelabel=new Label("Enter Column Name");
       TextField colnamefield=new TextField();
       TextField typelength=new TextField();
       typelength.setPromptText(" length");
       ComboBox<String> type=new ComboBox<>();
       type.getItems().addAll(dataType);
       RadioButton primaryKey=new RadioButton("Primary Key");
       RadioButton nullable=new RadioButton("Nullable");
       RadioButton notNullable=new RadioButton("Not Nullable");
       ToggleGroup tg=new ToggleGroup();
       primaryKey.setToggleGroup(tg);
       nullable.setToggleGroup(tg);
       notNullable.setToggleGroup(tg);
       Button add=new Button("ADD Column");
       Button newCol=new Button("Add another Column");
       Button create=new Button("Create");
        close=new Button("Close");
       HBox hb2=new HBox(10);
       hb2.getChildren().addAll(colnamelabel,colnamefield,type,typelength);
       HBox hb3=new HBox(10);
       hb3.getChildren().addAll(primaryKey,nullable,notNullable);
       add.setOnAction(e->{
       
       if(!colnamefield.getText().equals("")){
           colnNme.add(colnamefield.getText());
       }
       if(!type.getValue().equals("")){
           optr=type.getValue();
           if(!(typelength.getText().equals("")|| optr.equalsIgnoreCase("int")||optr.equalsIgnoreCase("long")))
                optr=optr+"("+typelength.getText()+")";
           if(primaryKey.isSelected())
               optr=optr+" Primary key";
           if(notNullable.isSelected())
               optr=optr+" NOT NULL";
           optr=optr+",";
        
           typeCont.add(optr);
       }
       });
       newCol.setOnAction(e->{
       colnamefield.setText(null);
       type.setValue(null);
       primaryKey.setSelected(false);
       nullable.setSelected(false);
       notNullable.setSelected(false);
       });
       
      create.setOnAction(e->{
       
       optr=newtablename.getText();
       if(!(optr.equals("")|| optr.equals(" ")))
       {
            sql="CREATE TABLE "+optr+"(";
            n=colnNme.size();
            for(int i=0;i<n;i++)
            {
                 optr=colnNme.get(i)+" "+typeCont.get(i);
                
               if(i==n-1){
                   int k=optr.lastIndexOf(",");
                   optr=optr.substring(0,k);
                   System.out.println("create last optr== "+optr);
                   
               }
               sql=sql+optr;
               
           }
           sql=sql+")";
           System.out.println("create == "+sql);
        }
         try {
              DBUtil.dbExecuteQuery(sql);
               flag=true;
               
               } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
     });
     close.setOnAction((ActionEvent e) -> {
          opWindow.close();
       });
        HBox hb4=new HBox(10);
        hb4.getChildren().addAll(add,newCol);
        HBox hb5=new HBox(10);
        hb5.getChildren().addAll(create,close);
   
        vbox.getChildren().addAll(hb,hb2,hb3,hb4,hb5);
        opScene=new Scene(vbox,400,400);
        opWindow.setScene(opScene);
        opWindow.showAndWait();
        
        return newtablename.getText();       
    }
    
    public void onDelete()throws SQLException,ClassNotFoundException{
        getTableName();
        opWindow.setTitle("Delete Records ");
         Label lb=new Label("Select field conditions to delete records!");
          vbox.getChildren().add(lb);
         field=new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            CheckBox rb=new CheckBox(promptxt[i]);
            field.add(rb);
             ComboBox<String> cb=new ComboBox<>();
            cb.getItems().addAll(midOperators);
            midOperator.add(cb);
             TextField tf=new TextField();
                tf.setId("textfield"+(i+1));
                tf.setPromptText("value type-"+type[i]);
                atxt.add(tf);
               HBox hb=new HBox(10);
               hb.setPadding(new Insets(5));
               hb.getChildren().addAll(rb,cb,tf);
               vbox.getChildren().add(hb);
              if(0<=i && i<promptxt.length-1 && promptxt.length>1){
                ComboBox<String> conj=new ComboBox<>();
                conj.getItems().addAll(conjOperators);
                conjOperator.add(conj);
                vbox.getChildren().add(conj);
            }
            }
            vbox.setAlignment(Pos.CENTER_LEFT);
             vbox.setPadding(new Insets(10));
            btn.setText("ok");
            
            close.setText("close"); 
            btn.setOnAction(e->
             {
                 int i=0;
                 for(CheckBox r:field){
                     System.out.println(" check");
                     if(r.isSelected())
                     {
                         selectedfield.add(r.getText());
                         selectedvalues.add(atxt.get(i).getText());
                         selectedType.add(type[i]);
                          optr=midOperator.get(i).getValue();
                     }
                     if(0<=i && i<field.size()-1 && conjOperator.get(i).getValue()!=null){
                         selectedConjOperator.add(conjOperator.get(i).getValue());
                         System.out.println("conj="+selectedConjOperator.get(i));
                           }
                     i++;
                 }
                 i=0;
                 int k=0;
                 System.out.print("op -"+tableName);
                 System.out.print("op type -"+selectedType.get(0));
                 sql="delete from "+tableName+" where";
                 for(String str:selectedfield)
                 {
                     if((selectedType.get(i)).contains("VARCHAR") && !optr.equalsIgnoreCase("like"))
                     { sql=sql+" "+str+" "+optr+" '"+selectedvalues.get(i)+"'";
                     }
                     else if(optr.equalsIgnoreCase("like")){
                          sql=sql+"  "+str+" "+optr+" '"+selectedvalues.get(i)+"'";
                     }
                     else
                     { sql=sql+str+" "+optr+" "+selectedvalues.get(i);
                     
                     }
                    if(k<selectedfield.size()-1)
                    {sql=sql+" "+selectedConjOperator.get(k);
                     System.out.println("sqls -"+sql);
                    k++;
                    }
                     i++;
                 }   
                 k=0;i=0;
            try {
                System.out.print(sql);
               DBUtil.dbExecuteQuery(sql);
               
                opWindow.close();
           } catch (SQLException | ClassNotFoundException ex) {
               Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
           }
           });
             close.setOnAction((ActionEvent e) -> {
                 opWindow.close();
           });
             
             HBox hb=new HBox(20);
             hb.getChildren().addAll(btn,close);
              vbox.getChildren().add(hb);
              ScrollPane sp=new ScrollPane(vbox);
             opScene=new Scene(sp,400,400);
             opWindow.setScene(opScene);
             opWindow.showAndWait();
    }

    public void onUpdate()throws SQLException,ClassNotFoundException{
       
         getTableName();
        opScene=null;
        vbox=new VBox();
        opWindow.setTitle("Update Records");
        Label lb=new Label("Select column and Enter column vlaues to update");
        vbox.getChildren().add(lb);
        ArrayList<RadioButton> rfield=new ArrayList<>();
         ArrayList<javafx.scene.control.TextField> atxt2=new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            RadioButton rb=new RadioButton(promptxt[i]);
            rb.setId("radio"+(i+1));
            rfield.add(rb);
            ComboBox<String> cb=new ComboBox<>();
            cb.getItems().addAll("=");
            midOperator.add(cb);
                TextField tf=new TextField();
                tf.setId("textfield"+(i+1));
                tf.setPromptText("value type-"+type[i]);
                atxt.add(tf);
               HBox hb=new HBox(20);
               hb.setPadding(new Insets(5));
               hb.getChildren().addAll(rb,cb,tf);
                 vbox.getChildren().add(hb);
        }
            vbox.setAlignment(Pos.TOP_LEFT);
            Label wherelb=new Label("Select condition on fields to be updated");
            RadioButton allfields=new RadioButton("update All");
            allfields.setSelected(false);
             vbox.getChildren().addAll(wherelb,allfields);
            for(int i=0;i<n;i++)
            {
            CheckBox rb=new CheckBox(promptxt[i]);
            field.add(rb);
            ComboBox<String> cb=new ComboBox<>();
            cb.getItems().addAll(midOperators);
            midOperator1.add(cb);
             TextField tf=new TextField();
                tf.setId("textfield"+(i+1));
                 if(type[i].equalsIgnoreCase("date"))
                    tf.setPromptText("  "+promptxt[i]+"("+type[i]+" dd/mm/yyyy)");
                else if(type[i].equalsIgnoreCase("timestamp"))
                    tf.setPromptText("  "+promptxt[i]+"("+type[i]+" hh:mi:ss)");
                 else
                         tf.setPromptText("value type-"+type[i]);
                atxt2.add(tf);
               HBox hb=new HBox(10);
               hb.setPadding(new Insets(5));
               hb.getChildren().addAll(rb,cb,tf);
           vbox.getChildren().add(hb);
            if(0<=i && i<promptxt.length-1 && promptxt.length>1){
                ComboBox<String> conj=new ComboBox<>();
                conj.getItems().addAll(conjOperators);
                conjOperator.add(conj);
                vbox.getChildren().add(conj);
             }
            }
         allfields.setOnAction(e->{ 
          if(allfields.isSelected()){
            for(CheckBox cb:field)
             cb.setDisable(true);     
            }
         else{
               for(CheckBox cb:field)
             cb.setDisable(false);     
         }
        });
        vbox.setAlignment(Pos.CENTER_LEFT);
             vbox.setPadding(new Insets(10));
            btn.setText("ok");
            
            close.setText("close"); 
             btn.setOnAction(e->
             {
                   int i=0;
                 for(RadioButton r:rfield){
                     System.out.println("radio check"); 
                     if(r.isSelected())
                     {
                         selectedfield.add(r.getText());
                         selectedvalues.add(atxt.get(i).getText());
                         selectedType.add(type[i]);
                        optr=midOperator.get(i).getValue();
                        
                         i++;
                     }
                     else
                         i++;
                 }
                 i=0;
                 System.out.print("op -"+tableName);
                 System.out.print("op type -"+selectedType.get(0));
                 sql="update "+tableName+" set ";
                 for(String str:selectedfield)
                 {
                     if((selectedType.get(i)).contains("VARCHAR") || selectedType.get(i).contains("CHAR"))
                             sql=sql+str+"='"+selectedvalues.get(i)+"',";
                      else if(selectedType.get(i).contains("TIMESTAMP"))
                            sql=sql+str+"="+"to_timestamp('"+selectedvalues.get(i)+"','HH24:MI:SS.FF'),";
                       else if(selectedType.get(i).contains("DATE"))
                           sql=sql+"to_date('"+selectedvalues.get(i)+"','DD/MM/YYYY')";
                        else
                         sql=sql+str+"="+selectedvalues.get(i)+",";
                     i++;
                 } 
                 
                 int t=sql.lastIndexOf(",");
                 //  if(t==rfield.size()-1)
                       sql=sql.substring(0,t);
                   System.out.println("update last rsql== "+sql);
                  int k=0;
                 if(!allfields.isSelected())
                 {
                 i=0;
                 selectedfield.clear();
                 selectedType.clear();
                 selectedvalues.clear();
                 atxt.clear();
                 selectedOperator.clear();
                 for(CheckBox r:field){
                     System.out.println(" check");
                  
                     if(r.isSelected())
                     {
                         selectedfield.add(r.getText());
                         selectedvalues.add(atxt2.get(i).getText());
                         selectedType.add(type[i]);
                         optr=midOperator1.get(i).getValue();
                         selectedOperator.add(optr);
            
                     }
                     if(0<=i && i<field.size()-1 && conjOperator.get(i).getValue()!=null){
        
                         selectedConjOperator.add(conjOperator.get(i).getValue());
                         System.out.println("conj="+selectedConjOperator.get(i));
                           }
       
                     i++;
                 }
                 i=0;
                
                 System.out.print("op -"+tableName);
                 System.out.print("op type -"+selectedType.get(0));
                 sql=sql+" where ";
                 for(String str:selectedfield)
                 {
                     if((selectedType.get(i)).contains("VARCHAR") && !selectedOperator.get(i).equalsIgnoreCase("like"))
                     { sql=sql+" "+str+" "+selectedOperator.get(i)+" '"+selectedvalues.get(i)+"'";
                     }
                     else if(selectedOperator.get(i).equalsIgnoreCase("like")){
                          sql=sql+"  "+str+" "+selectedOperator.get(i)+" '"+selectedvalues.get(i)+"'";
                     }
                      else if(selectedType.get(i).contains("TIMESTAMP"))
                            sql=sql+str+"= to_timestamp('"+selectedvalues.get(i)+"','HH24:MI:SS.FF')";
                       else if(selectedType.get(i).contains("DATE"))
                           sql=sql+"to_date('"+selectedvalues.get(i)+"','DD/MM/YYYY')";
                     else
                     { sql=sql+str+" "+selectedOperator.get(i)+" "+selectedvalues.get(i);
                     
                     }
                    if(k<selectedConjOperator.size())
                    {sql=sql+" "+selectedConjOperator.get(k)+" ";
                     System.out.println("sqls -"+sql);
                    k++;
                    }
                     i++;
                 }
                 k=0;i=0;
                 }
           
                 try {
                System.out.print(sql);
              DBUtil.dbExecuteQuery(sql);
               
                opWindow.close();
           } catch (SQLException | ClassNotFoundException ex) {
               Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
           }
           });
             
             close.setOnAction((ActionEvent e) -> {
                 opWindow.close();
           });
             
             HBox hb=new HBox(20);
             hb.getChildren().addAll(btn,close);
              vbox.getChildren().add(hb);
              ScrollPane sp=new ScrollPane(vbox);
             opScene=new Scene(sp,400,400);
             opWindow.setScene(opScene);
             opWindow.showAndWait();
      
    }

    public void onDetail(ObservableList<String> list){
         opWindow.setTitle("Details");
         ListView<String> detailList=new ListView<>();
         detailList.getItems().addAll(list);
         close.setText("close");
             close.setOnAction((ActionEvent e) -> {
                 opWindow.close();
           });
             vbox.getChildren().addAll(detailList,close);
             opScene=new Scene(vbox,400,600);
             opWindow.setScene(opScene);
             opWindow.showAndWait();
    }

    public void onAlter(){
        
        getTableName();
        VBox left=new VBox(10);
        VBox right=new VBox(10);
        VBox lower=new VBox(10);
       // VBox lowerRight=new VBox();
        HBox h1=new HBox();
        HBox h2=new HBox();
        Label addnew=new Label("ADD NEW COLUMN");
        Label modifyCol=new Label("MODIFY COLUMN");
        //upper left addnew vbox
        GridPane leftgrid=new GridPane();
        leftgrid.setPadding(new Insets(3,3,3,3));
        leftgrid.setVgap(5);
        leftgrid.setHgap(8);
        Label namel=new Label("Column name");
        GridPane.setConstraints(namel, 0, 0);
        TextField colName=new TextField();
        GridPane.setConstraints(colName,1, 0);
        
        Label dtype=new Label("Column Data Type");
        GridPane.setConstraints(dtype, 0, 1);
        ComboBox<String> colType=new ComboBox<>();
        colType.getItems().addAll(dataType);
        colType.setPromptText("select column type");
        GridPane.setConstraints(colType,1, 1);
        TextField limit0=new TextField();
        limit0.setPromptText("limits");
        GridPane.setConstraints(limit0,1,2);
        Button addbtn=new Button("ADD");
        GridPane.setConstraints(addbtn,1, 3);
        
        addbtn.setOnAction(e->{ 
        
        sql="ALTER TABLE "+tableName+" ADD ";
        if(!colName.getText().equalsIgnoreCase(""))
        {
              optr=colType.getValue();
           if(!(limit0.getText().equals("")|| optr.equalsIgnoreCase("int")||optr.equalsIgnoreCase("long")))
                optr=optr+"("+limit0.getText()+")";
            sql=sql+colName.getText()+" "+optr;
        }
            try {
                System.out.println(sql);
                DBUtil.dbExecuteQuery(sql);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        leftgrid.getChildren().addAll(namel,colName,dtype,colType,limit0,addbtn);
        left.getChildren().addAll(addnew,leftgrid);
      
        //upper right drop column vbox
        GridPane rightgrid=new GridPane();
        rightgrid.setPadding(new Insets(3,3,3,3));
        rightgrid.setVgap(5);
        rightgrid.setHgap(8);
        Label namer=new Label("Delete Column");
        Label colNames=new Label("Select Column");
        GridPane.setConstraints(colNames, 0, 0);
        ComboBox<String> colToDrop=new ComboBox<>();
        colToDrop.getItems().addAll(promptxt);
        colToDrop.setPromptText("select column name");
        GridPane.setConstraints(colToDrop,1, 0);
        Button dropbtn=new Button("Delete");
        GridPane.setConstraints(dropbtn,1, 1);
        
        dropbtn.setOnAction(e->{ 
        
        sql="ALTER TABLE "+tableName+" DROP COLUMN "+colToDrop.getValue();
        try {
                System.out.println(sql);
                DBUtil.dbExecuteQuery(sql);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        rightgrid.getChildren().addAll(colNames,colToDrop,dropbtn);
        right.getChildren().addAll(namer,rightgrid);
        right.setPadding(new Insets(0,0,0,20));
        h1.getChildren().addAll(left,right);
       
        //modify vbox
         Label modifylabel=new Label("MODIFY COLUMNS");
        GridPane modifygrid=new GridPane();
       modifygrid.setPadding(new Insets(3,3,3,3));
       modifygrid.setVgap(5);
        modifygrid.setHgap(8);
        Label rename=new Label("Change DataType");
        GridPane.setConstraints(rename, 0, 0);
        
        ComboBox<String> modifycolNames=new ComboBox<>();
        modifycolNames.getItems().addAll(promptxt);
        modifycolNames.setPromptText("select column name");
        GridPane.setConstraints(modifycolNames,0, 1);

        ComboBox<String> colType2=new ComboBox<>();
        colType2.getItems().addAll(dataType);
        colType2.setPromptText("select column type");
        GridPane.setConstraints(colType2,1, 1);
        TextField limit=new TextField();
        limit.setPromptText("limits");
        GridPane.setConstraints(limit,2,1);
        Button modifybtn=new Button("MODIFY");
        GridPane.setConstraints(modifybtn,1, 2);
        
        modifybtn.setOnAction(e->{ 
        
        sql="ALTER TABLE "+tableName+" MODIFY ";
        if(!modifycolNames.getValue().equalsIgnoreCase("")){
            optr=colType2.getValue();
           if(!(limit.getText().equals(" ")|| optr.equalsIgnoreCase("int")||optr.equalsIgnoreCase("long")))
                optr=optr+"("+limit.getText()+")";
            sql=sql+modifycolNames.getValue()+" "+optr;
        }
            try {
                System.out.println(sql);
                DBUtil.dbExecuteQuery(sql);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        modifygrid.getChildren().addAll(rename,modifycolNames,colType2,limit,modifybtn);
        lower.getChildren().addAll(modifylabel,modifygrid);
        vbox.getChildren().addAll(h1,lower);
        ScrollPane sp=new ScrollPane(vbox);
        opScene=new Scene(sp,400,400);
        opWindow.setScene(opScene);
        opWindow.showAndWait();
    }
    
    public String fieldsToDisplay(boolean allflag,String fields[]){
        if(allflag){
            optr="";
            for (String promptxt1 : promptxt) {
                if(promptxt1.equalsIgnoreCase("timestamp"))
                     optr = optr+" to_char(" + promptxt1 + ",'HH12:MI:SS'),";
                else
                     optr = optr+" " + promptxt1 + ",";
            }
        }
        else{
            for (String field1 : fields) {
                optr = optr+" " + field1 + ",";
            }
        }
        optr=optr.substring(0, optr.lastIndexOf(","));
        return optr;
    }

    
    public ResultSet joinOperation(String joinType,ObservableList<String> tableList){
        
        opWindow.setTitle(joinType);
        ComboBox<String> table1=new ComboBox<>();
        table1.getItems().addAll(tableList);
        table1.setPromptText("Select table 1");
        ComboBox<String> table2=new ComboBox<>();
        table2.getItems().addAll(tableList);
        table2.setPromptText("Select table 2");
        GridPane grid=new GridPane();
        table1colmns=null;
        sql=null;
        ComboBox<String> table1colms=new ComboBox<>();
        ComboBox<String> table2colms=new ComboBox<>();
        Label l1=new Label("Select column to display from table 1");
        ListView<CheckBox> table1col=new ListView<>();
        table1col.setMaxHeight(150);
     
        table1.setOnAction(e->{
        try {
            table1col.getItems().clear();
            rs2=DBUtil.dbExecute("select * from "+table1.getValue());
           table1colmns=Metadata.getColumnNames(rs2);
           
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(String str:table1colmns){
            CheckBox ch=new CheckBox(str);
            table1col.getItems().add(ch);
            table1colms.getItems().add(str);
        } 
        });
        
        Label l2=new Label("Select column to display from table 2");
        ListView<CheckBox> table1col2=new ListView<>();
        table1col2.setMaxHeight(150);
     
        table2.setOnAction(e->{
        try {
            table1col2.getItems().clear();
            rs2=DBUtil.dbExecute("select * from "+table2.getValue());
           table1colmns=Metadata.getColumnNames(rs2);
           
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(String str:table1colmns){
            CheckBox ch=new CheckBox(str);
            table1col2.getItems().add(ch);
           table2colms.getItems().add(str); 
        } 
        });
        Label l3=new Label("Join condition on columns(a==b)");
        btn.setText("Execute");
        close.setText("Close");
        GridPane.setConstraints(table1, 0, 0);
        GridPane.setConstraints(table2, 1, 0);
        GridPane.setConstraints(l1, 0, 1);
        GridPane.setConstraints(table1col, 0, 2);
        GridPane.setConstraints(l2, 1, 1);
        GridPane.setConstraints(table1col2, 1, 2);
        GridPane.setConstraints(l3, 0, 3);
        GridPane.setConstraints(table1colms, 0, 4);
        GridPane.setConstraints(table2colms, 1, 4);
      
        grid.getChildren().addAll(table1,l1,table1col,table2,l2,table1col2,l3,table1colms,table2colms);
        
        btn.setOnAction(e->{
          int k=0;
          for(int i=0;i<table1col.getItems().size();i++){
             if(table1col.getItems().get(i).isSelected())
                selectedfield.add(table1col.getItems().get(i).getText());
         }       
        sql="select "+displaylist(table1.getValue(),selectedfield);
         selectedfield.clear();
         k=0;
         for(int i=0;i<table1col2.getItems().size();i++){
             if(table1col2.getItems().get(i).isSelected())
                 selectedfield.add(table1col2.getItems().get(i).getText());
         }  
        sql=sql+","+displaylist(table2.getValue(),selectedfield)+" from "+table1.getValue()+" "+joinType+" "+table2.getValue()+" on "+table1.getValue()+"."+table1colms.getValue()+"="+table2.getValue()+"."+table2colms.getValue();
        
         try {
                System.out.println(sql);
                rs=DBUtil.dbExecute(sql);
            } 
         catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
         close.setOnAction((ActionEvent e) -> {
                 opWindow.close();
           });
         
        HBox hb=new HBox();
        hb.getChildren().addAll(btn,close);
        vbox.getChildren().addAll(grid,hb);
        ScrollPane sp=new ScrollPane(vbox);
        opScene=new Scene(sp,400,400);
        opWindow.setScene(opScene);
        opWindow.showAndWait();
        
        return rs;
    }
    
    public String displaylist(String tableName,ArrayList<String> collist){
        
        String list=" ";
        for(String str :collist){
            list=list+" "+tableName+"."+str+" ,";
            }
        int k=list.lastIndexOf(",");
        list=list.substring(0, k);
        System.out.println("list=="+list);
        return list;
    }
    
    
    
}
