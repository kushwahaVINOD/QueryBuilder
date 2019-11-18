/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBUtil;



public class Metadata 
{
    private static String tableName;
     private static ResultSet rs;
     private static ResultSetMetaData rsmd;
     private static   int n=0;
      static   String sql;
       public static ObservableList<String> dbDetail;
      
    public static void setTableName(String name){
        tableName=name;
         sql="select * from "+tableName;
    }
 
     
public static  void getRowset(){
    
try{
     rs=DBUtil.dbExecute(sql);
     rsmd=rs.getMetaData();
     
}
     catch(ClassNotFoundException | SQLException e){
    }
}

public static int getNumberOfColumn(){
    
    getRowset();
        try {
            n = rsmd.getColumnCount();
        } catch (SQLException ex) {
            Logger.getLogger(Metadata.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    return n;
}



public static int getNumberOfColumn(ResultSet rs){
    
    ResultSetMetaData rws=null;
        try {
            rws=rs.getMetaData();
        } catch (SQLException ex) {
            Logger.getLogger(Metadata.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            n = rws.getColumnCount();
        } catch (SQLException ex) {
            Logger.getLogger(Metadata.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    return n;
}

public static  String[] getColumnNames() throws SQLException{
   getRowset();
    n=getNumberOfColumn();
    String names[]=new String[n];
    for(int i=0;i<n;i++){
        names[i]=rsmd.getColumnName(i+1);
        
        
     }
    return names;
}
 
public static  String[] getColumnNames(ResultSet rs) throws SQLException{
   ResultSetMetaData rws=rs.getMetaData();;

    n=getNumberOfColumn(rs);
    String names[]=new String[n];
    for(int i=0;i<n;i++){
        names[i]=rws.getColumnName(i+1);
     }
    return names;
}
public static  String[] getColumnTypeNames() throws SQLException{
    
    getRowset();
    String typenames[]=new String[n];
    for(int i=0;i<n;i++){
        typenames[i]=rsmd.getColumnTypeName(i+1);
     }
    return typenames;
}

public static  String[] getColumnTypeNames(ResultSet rs) throws SQLException{
    
    ResultSetMetaData rws=rs.getMetaData();;

    n=getNumberOfColumn(rs);
    String typenames[]=new String[n];
    for(int i=0;i<n;i++){
        typenames[i]=rsmd.getColumnTypeName(i+1);
     }
    return typenames;
}

public static String[] getIsAutoIncrement()throws SQLException{
     getRowset();
    String typenames[]=null;
    int k=0;
    for(int i=0;i<n;i++){
       if(rsmd.isAutoIncrement(i))
        typenames[k++]=rsmd.getColumnTypeName(i+1);
     }
    return typenames;
}

public static ObservableList<String> dbMetadata(){
   
   
    dbDetail=FXCollections.observableArrayList();
    try {  
            Connection conn=DBUtil.dbgetConnection();
            DatabaseMetaData dbmt=conn.getMetaData();
            
            dbDetail.add("URL \t\t\t"+dbmt.getURL());
            dbDetail.add("User Name \t\t"+dbmt.getUserName());
            dbDetail.add("DBMS name \t\t"+dbmt.getDatabaseProductVersion());
            dbDetail.add("Driver name \t\t"+dbmt.getDriverName());
            dbDetail.add("Driver version \t\t"+dbmt.getDriverVersion());
            dbDetail.add("Supported SQl Keys \t"+dbmt.getSQLKeywords());
            
           // conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Metadata.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
    return dbDetail;
}
}