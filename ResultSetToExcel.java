/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ResultSetToExcel {
     public static ResultSet resultSet;
    public ResultSetToExcel(){
       
       
    }
    
    
    
    public  void createExcel(ResultSet rs) throws Exception {
     
     resultSet=rs;
      XSSFWorkbook workbook = new XSSFWorkbook(); 
      XSSFSheet spreadsheet = workbook.createSheet("Db sheet");
      
      XSSFRow row = spreadsheet.createRow(1);
      XSSFCell cell;
         String[] colname=Metadata.getColumnNames(resultSet);
         String[] coltype=Metadata.getColumnTypeNames(resultSet);
         for(int i=1;i<=colname.length;i++){
             cell = row.createCell(i);
             cell.setCellValue(colname[i-1].toUpperCase());
         }
      int i = 2;
     resultSet.first();
         
      do {
         row = spreadsheet.createRow(i);
         
          for(int k=1;k<=colname.length;k++){
             cell = row.createCell(k);
             switch(coltype[k-1].toLowerCase()){
                 case "number":  cell.setCellValue(Integer.parseInt(resultSet.getString(k)));  break;
                 case "int": cell.setCellValue(resultSet.getInt(k)); break;
                 case "char":  cell.setCellValue(resultSet.getString(k));  break;
                 case "varchar2":  cell.setCellValue(resultSet.getString(k));  break;
                 case "float":  cell.setCellValue(resultSet.getFloat(k));  break;
                 case "decimal":  cell.setCellValue(resultSet.getDouble(k));  break;
             }
            
         }
         
         i++;
      }
      while(resultSet.next());
     
      
      try{
      FileOutputStream out = new FileOutputStream(new File("exceldatabase.xlsx"));
      workbook.write(out);
      out.close();
      System.out.println("exceldatabase.xlsx written successfully");
      }
      catch(IOException e){
          System.out.println("exception in xls  "+e);
      }
      }
 
}
