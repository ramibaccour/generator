package com.example.demo;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeneratorApplication
{
	private static String ln = System.getProperty( "line.separator" );
	public static void main(String[] args) 
	{
		SpringApplication.run(GeneratorApplication.class, args);
		createEntity("big-open");
		
	}
	private static void createEntity(String dataBaseName) 
	{
		try
		{  
			Class.forName("com.mysql.cj.jdbc.Driver");  
			List<String> tablesName = getListTable(dataBaseName);
			for(String tableName: tablesName)
			{
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dataBaseName,"root","root");  
				Statement stmt = con.createStatement(); 
				ResultSet rs=stmt.executeQuery("SHOW COLUMNS FROM "+ dataBaseName + "." + tableName );
				String basePath = "C:\\geneation\\";
				String strpath = basePath + tableName + ".java";
				File file = new File(strpath);	
				if (!file.exists())
					file.mkdirs();
				
				if (!file.createNewFile())
				{
					Files.deleteIfExists(file.toPath());
				} 
				else
				{
					file = new File(strpath);	
					file.createNewFile();
				}
				FileWriter myWriter = new FileWriter(strpath);
				myWriter.write("package com.delivery.entity;" + ln);
				myWriter.write("import java.util.ArrayList;" + ln);
			   
				while (rs.next())
				{
					myWriter.write(rs.getString("Field") + ln);
					myWriter.write(rs.getString("Type") + ln);
					myWriter.write(rs.getString("Key") + ln);
					
				}
				myWriter.close();
			}
			  
		}
		catch(Exception e)
		{ System.out.println(e);}
	}
	private static List<String> getListTable(String dataBaseName)
	{
		List<String> tablesname = new ArrayList<>();
		try
		{
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dataBaseName,"root","root");  
			Statement stmt = con.createStatement(); 
			String sql = "SHOW TABLES FROM `" + dataBaseName+"`";
			ResultSet rs = stmt.executeQuery(sql );			
			while (rs.next())
			{
				tablesname.add(rs.getString("Tables_in_" + dataBaseName));
			}
			con.close();
		}
		catch(Exception e)
		{
		}
		return tablesname;
	}
	
	
}
