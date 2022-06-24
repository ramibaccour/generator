package com.example.demo;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
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
	private static List<String> files = new ArrayList<>();
	public static void main(String[] args) 
	{
		SpringApplication.run(GeneratorApplication.class, args);
		fillFiles();
		createEntity("marketplace");
		
	}
	private static void fillFiles()
	{
		String path = "C:\\geneation\\";
		String pathController = path + "controller\\";
		String pathEntity =     path + "entity\\";
		String pathResponse =   path + "payload\\response\\";
		String pathErreor =     path + "payload\\response\\erreor\\";
		String pathRequest =    path + "payload\\request\\";
		String pathRepository = path + "repository\\";
		String pathService =    path + "service\\";
		
		files.add(pathController);
		files.add(pathEntity);
		files.add(pathResponse);
		files.add(pathErreor);
		files.add(pathRequest);
		files.add(pathRepository);
		files.add(pathService);
	}
	private static void createFilesEntity(ResultSet rs, String tableName)
	{
		try
		{ 
			String strpath = files.get(1) + tableName + ".java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package com.delivery.entity;" + ln);
			myWriter.write("import java.util.ArrayList;" + ln);
			myWriter.write("import java.util.List;" + ln);
			myWriter.write("import org.springframework.data.mongodb.core.mapping.DBRef;" + ln);
			myWriter.write("import org.springframework.data.mongodb.core.mapping.Document;" + ln);
			myWriter.write("import org.springframework.data.mongodb.core.mapping.FieldType;" + ln);
			myWriter.write("import org.springframework.data.mongodb.core.mapping.MongoId;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("@Document(collection = \"" +tableName +"\")" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class " + getNameProperty(tableName, true) + " " + ln);
			myWriter.write("{" + ln);
			while (rs.next())
			{
				if(rs.getString("Key").equals("PRI"))
				{
					myWriter.write("	@NotNull()" + ln);
					myWriter.write("	@Id@GeneratedValue(strategy = GenerationType.IDENTITY)" + ln);
					myWriter.write("	@Column(name=\"" + rs.getString("Field") + "\")" + ln);
					myWriter.write("	private " + getTypeProperty(rs.getString("Type"))+ " " + getNameProperty(rs.getString("Field"), false)+ ";" + ln) ;
				}
				else if(rs.getString("Key").equals("MUL"))
				{
					
				}
				else
				{
					myWriter.write("	@Column(name=\"" + rs.getString("Field") + "\")" + ln);
					myWriter.write("	private " + getTypeProperty(rs.getString("Type"))+ " " + getNameProperty(rs.getString("Field"), false)+ ";" + ln) ;
				}
					
				
			}
			
			
			myWriter.write("}" + ln);
			myWriter.close();
		}
		catch(Exception e)
		{ System.out.println(e);}
	}
	private static void createFilesProgect(ResultSet rs, String tableName)
	{
		createFilesEntity(rs, tableName);
		
	}
	private static void createFolderProgect(String tableName)
	{
		try
		{ 
			
			for(String fileName : files)
			{
				String strpath = fileName + tableName + ".java";
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
			}
			
			
		}
		catch(Exception e)
		{ System.out.println(e);}
	}
	
	private static void createEntity(String dataBaseName) 
	{
		try
		{  
			Class.forName("com.mysql.cj.jdbc.Driver");  
			List<String> tablesName = getListTable(dataBaseName);
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dataBaseName,"root","root");  

			for(String tableName: tablesName)
			{
				Statement stmt = con.createStatement(); 
				ResultSet rs=stmt.executeQuery("SHOW COLUMNS FROM "+ dataBaseName + "." + tableName );	

				createFolderProgect(tableName);
				createFilesProgect(rs, tableName);				
			}
			con.close();
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
	private  static String getNameProperty(String propetyName, Boolean isClass)
	{
		String name = "";
		var tab = propetyName.split("_");
		var compte = 0;
		for(String str : tab)
		{
			if(isClass)
				name += str.substring(0,1).toUpperCase() + str.substring(1,str.length());
			else
			{
				if(compte>0)
					name += str.substring(0,1).toUpperCase() + str.substring(1,str.length());
				else
					name += str;
			}
			compte++;
		}
		return name;
	}
	private  static String getTypeProperty(String type)
	{
		var myType = "";
		if(type.equals("int"))
			myType = "int";
		if(type.indexOf("varchar")>=0)
			myType = "String";
		if(type.equals("double"))
			myType = "double";
		if(type.equals("date"))
			myType = "LocalDateTime";
		return myType;
	}
}
