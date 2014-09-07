package com.shouwangchong;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import jxl.CellType;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelManager
{
	private String filePath;
	private String sheet1Name="sheet1";
	private int rows;
	private int cols;
	private jxl.Workbook rw;
	private jxl.write.WritableWorkbook  wwb;
	private jxl.write.WritableSheet ws;
	private jxl.write.WritableCell wc;
	private ArrayList<String> valuesInCol[];
	private ArrayList<String> arrUniqColValue[];
	private int ivtStatuesCol;//��ʾ�̵����������
	private boolean bFileOpened = false;
	private String intentory_result_title = "";
	private int searchColIndex =0;


	public ExcelManager()
	{
	}

	public boolean openFile(String strFilePath)
	{
		if(bFileOpened)
		{
			if(strFilePath.equals(this.filePath))
			{
				bFileOpened = true;
				return true;
			}
			else
			{
				closeFile();
				bFileOpened = false;
			}
		}
		this.filePath = strFilePath;
		try 
		{
			InputStream is = new FileInputStream(filePath);
			// ����ֻ���� Excel �������Ķ���.
			rw = jxl.Workbook.getWorkbook(is);
			sheet1Name = rw.getSheet(0).getName();
			// ������д��� Excel ����������
			wwb = Workbook.createWorkbook(new File(filePath), rw);

			//wwb = Workbook.createWorkbook(new File(outputFilePath), rw);
			// ��ȡ��һ�Ź�����
			ws = wwb.getSheet(0);
			rows = ws.getRows();
			cols = ws.getColumns();
			valuesInCol = new ArrayList[cols+1];
			arrUniqColValue = new ArrayList[cols+1];
//			log("cols="+cols+"rows="+rows);
	       	ivtStatuesCol = cols-1;
			for(int col = 0;col <=cols;col ++)
			{
				valuesInCol[col] = new ArrayList<String>();
				for (int row=0;row<rows;row++)
				{
					String strColContent = getCellValue(row,col);
					valuesInCol[col].add(strColContent);
					if(row==0)
					{
						arrUniqColValue[col] = new ArrayList();
						if(col==cols-1)//���һ��
	   					   {
	   						   //δ���ô���ʱ
	   						   String inventoryResultTitle = intentory_result_title;
	   						   if(!strColContent.equals(inventoryResultTitle))
	   						   {
	   							   ivtStatuesCol = cols;
	   							   updateCell(0,cols,inventoryResultTitle);
	   						   }
	   					   }
					}
					else
					{
						//��ȡ���ݣ�����ÿһ����������ȥ���ظ�������
						setUniqColValue(col,strColContent);
					}
				}
			}
//			for (int i =0;i<rows;i++)
//			{
//				log(arrUniqColValue[5].get(i));
//			}
			System.out.println("opened sucess:"+strFilePath);
			bFileOpened = true;
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();  
		}
		System.out.println("open failed:"+strFilePath);
		bFileOpened = false;
		return false;
	}
	
    /*
     * 
     * �����У����̵������û���ظ�������
     */
    private void setUniqColValue(int col,String value)
    {
    	if(!arrUniqColValue[col].contains(value))
	   {
		   arrUniqColValue[col].add(value);
//		   if(col==6)log("uniq:"+value);
	   }
    }
    
    public ArrayList getUniqCol(int col)
    {
    	return arrUniqColValue[col];
    }
    
    public ArrayList getRow(int row)
    {
//    	log("start getRow:"+row);
    	ArrayList rs = new ArrayList();
//    	log("cols:"+cols);
    	for (int col=0;col<=cols;col++)
    	{
    		try
    		{
//	    		log("i="+col+"-->"+valuesInCol[col].get(row));
	    		rs.add(valuesInCol[col].get(row));
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return rs;
    }
    
    /*
     * to be tested
     */
    private void refreshUniqColValue(int col)
    {
    	arrUniqColValue[col] = new ArrayList();
    	for (int i = 1;i<valuesInCol[col].size();i++)
 	   {
    		setUniqColValue(col,valuesInCol[col].get(i).toString());
 	   }
    }
    
	public boolean updateCell(int row,int col,String updateStr)
	{
		try
		{	
//			System.out.println("updateing cell...col:"+col+" row:"+row + " string:"+updateStr);
			
			wc = ws.getWritableCell(col, row);
			Label label = new Label(col, row, updateStr);  
			// ������õĵ�Ԫ����ӵ���������  
			ws.addCell(label);  
			try
			{
				valuesInCol[col].set(row, updateStr);
			}
			catch(Exception e)
			{
				//rows out of bound
//				for(int j=row;j<rows;j++)
//				{
					for(int i = 0;i<col+1;i++)
					{
						if(i==col)
						{
							valuesInCol[i].add(updateStr);
						}
						else
						{
//							log("add empty @"+i);
							valuesInCol[i].add("");
						}
					}
//				}
				rows = valuesInCol[col].size();
				e.printStackTrace();
			}
			refreshUniqColValue(col);
			System.out.println("updateing cell...success..col length:"+getUniqCol(col).size());
			return true;
		}
		catch(Exception e)
		{
			System.out.println("updateing cell...fail");
			e.printStackTrace();
			return false;
		}
	}
	
	public String getCell(int row,int col)
	{
		String ret = "";
		try
		{
			ret = valuesInCol[col].get(row);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	private String getCellValue(int row,int col)
	{
		try
		{
			return ws.getCell(col,row).getContents();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	private void writeFile()
	{
		
		
		try {  
            // �������Excel�ļ�  
            WritableWorkbook book = Workbook.createWorkbook(new File(  
            		filePath));  
  
            // ������Ϊ����һҳ���Ĺ�����,����0��ʾ���ǵ�һҳ  
            WritableSheet sheet1 = book.createSheet(sheet1Name, 0);  
//            WritableSheet sheet2 = book.createSheet("����ҳ", 2);  
  
            // ��Label����Ĺ��캯����,Ԫ��λ���ǵ�һ�е�һ��(0,0)�Լ���Ԫ������Ϊtest  
            int r = getRows();
    		int c = getCols();
    		for(int i = 0 ;i<r;i++)
    		{
    			for(int j = 0;j<c+1;j++)
    			{
    	            Label label = new Label(j, i, getCell(i,j));  
    				System.out.print(getCell(i,j)+",");
		            // ������õĵ�Ԫ����ӵ���������  
		            sheet1.addCell(label);  
    			}
    			System.out.println();
    		}
  
            /* 
             * ����һ���������ֵĵ�Ԫ��.����ʹ��Number��������·��,�������﷨���� 
             */  
//            jxl.write.Number number = new jxl.write.Number(1, 0, 555.12541);  
//            sheet2.addCell(number);  
  
            // д�����ݲ��ر��ļ�  
            book.write();  
            book.close();  
        } catch (Exception e) {  
            System.out.println(e);  
        }  
		// д�� Excel ����
//		try {
//			wwb.write();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public boolean closeFile()
	{
		try
		{
			writeFile();
			// �رտ�д��� Excel ����
			wwb.close();
			// �ر�ֻ���� Excel ����
			rw.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public ArrayList getCatory(int col,String keyWord)
	{
		ArrayList ret = new ArrayList();
		for(int row = 0;row < rows;row++)
		{
			try
			{
//				System.out.println("rows"+rows + " row"+row+" col"+col+" //"+valuesInCol[col].get(row));
				if(valuesInCol[col].get(row).equals(keyWord))
				{
					ret.add(getRow(row));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}

	public boolean isFileOpened() {
		return bFileOpened;
	}
	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

//	private void log(String str)
//	{
//		System.out.println(str);
//	}
//	private void log(int str)
//	{
//		System.out.println(str);
//	}
//	private void log(Object str)
//	{
//		System.out.println(str);
//	}


	public String getFilePath() {
		return filePath;
	}
	
	public void setInventoryResultTitle(String title)
	{
		this.intentory_result_title = title;
	}

	public int getStatuesColId() {
		return ivtStatuesCol;
	}

	public int getSearchColIndex() {
		return searchColIndex;
	}

	public void setSearchColIndex(int searchColIndex) {
		this.searchColIndex = searchColIndex;
	}

}
