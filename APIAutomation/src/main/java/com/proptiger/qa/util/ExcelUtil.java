package com.proptiger.qa.util;

/**
 * User: Himanshu.Verma
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ExcelUtil {

	/**
	 * This function is used to read the excel file
	 * @param fileName : Filename of the excel file
	 * @param sheetName : SheetName of the excel file
	 * @return Object[][]tabArray: array of sheet. 
	 * @throws IOException
	 */
	
	public static Object[][] readExcelDatatoMap(String fileName, String sheetName) throws IOException{

		int headPos = 1;
		
		Object[][] tabArray = null;
		
		InputStream is = ExcelUtil.class.getClassLoader().getResourceAsStream(fileName);

		HSSFWorkbook myWorkBook = new HSSFWorkbook (is);
		HSSFSheet sheet = myWorkBook.getSheet(sheetName);
		

		//System.out.println("sheet.getPhysicalNumberOfRows() - headPos=" + (sheet.getPhysicalNumberOfRows() - headPos));
		tabArray = new Object[sheet.getPhysicalNumberOfRows() - headPos][1];

		Iterator<Row> rowIterator = sheet.rowIterator();
		Row row = rowIterator.next(); // First Iteration of row is to get the column name only
		
		String [] columnName = new String [sheet.getRow(0).getPhysicalNumberOfCells()];
		int counter =0;
		Iterator<Cell> cellIterator	=	row.cellIterator();
		while(cellIterator.hasNext())
		{
			HSSFCell cell	=	(HSSFCell) cellIterator.next();
			if(cell.getCellType()!=3)
			{
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(cell.toString().trim().equals("null"))
				{
//					System.out.println("inside null block");
					columnName[counter]	=	"unNamedColumn"+counter;
//					System.out.println("modifying null value :: "+tabArray[i][j]);
				}
				else{
					columnName[counter]	=	cell.getStringCellValue();
//					System.out.println("tabArray[i][j]="+tabArray[i][j]);
				}
				counter++;
			}
		}
		
		//Now add the remaining rows
		//HashMap<String, String> valueMap = new HashMap<String, String>();
		int i=0;
		while (rowIterator.hasNext()){
			HashMap<String, String> valueMap = new HashMap<String, String>();

			row	=	rowIterator.next();
			int j=0;
			int totalCellNum = row.getLastCellNum();
			while(j<totalCellNum)
			{
				//System.out.println("Index -"+j);
				HSSFCell cell	=	(HSSFCell) row.getCell(j);
				if(cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK){
					// valueMap.put(columnName[j], null);
					//					System.out.println("CELL IS BLANK");

				}else if(cell.getCellType()!=3){
					cell.setCellType(Cell.CELL_TYPE_STRING);
						//			System.out.println("cell value  : "+cell);
					valueMap.put(columnName[j], cell.getStringCellValue());
					//System.out.println("valueMap "+valueMap); 
						//			System.out.println("tabArray[i][j]="+tabArray[i][j]);

				}
				j++;
			}
			tabArray[i][0] = valueMap;
			//System.out.println(((HashMap<String, String>)tabArray[i][0]).toString());
			i++;
		
		} 

		is.close();
		return tabArray; 
	}
	
}
