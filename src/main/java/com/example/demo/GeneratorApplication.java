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
import java.util.stream.Collectors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeneratorApplication
{
	private static String ln = System.getProperty( "line.separator" );
	private static List<String> files = new ArrayList<>();
	private static String dataBaseName = "marketplace";
	private static List<String> listTablesName = getListTable();
	private static List<EntityName> listEntityName = entityFiles();
	private static List<Relations> listRelation = new ArrayList<>();
	public static void main(String[] args) 
	{
		SpringApplication.run(GeneratorApplication.class, args);
		fillFiles();
		setRelation();
		createEntity();
		
	}
	private static void setRelation()
	{
		try
		{
			for(String tableName: listTablesName)
			{
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dataBaseName,"root","root");  
				Statement stmt = con.createStatement(); 
				String sql = " SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = \"" +  dataBaseName + "\" AND TABLE_NAME = \"" + tableName + "\" AND REFERENCED_COLUMN_NAME IS NOT NULL;";
				
				ResultSet rs = stmt.executeQuery(sql );			
				while (rs.next())
				{
					Relations myRelations = new Relations();
					myRelations.setCOLUMN_NAME(rs.getString("COLUMN_NAME"));
					myRelations.setCONSTRAINT_NAME(rs.getString("CONSTRAINT_NAME"));
					myRelations.setREFERENCED_TABLE_NAME(rs.getString("REFERENCED_TABLE_NAME"));
					myRelations.setREFERENCED_COLUMN_NAME(rs.getString("REFERENCED_COLUMN_NAME"));
					myRelations.setTABLE_NAME(rs.getString("TABLE_NAME"));
					listRelation.add(myRelations);
				}
				con.close();			
			}
		}
		catch(Exception e)
		{
			 System.out.println(e);
		}
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
	private static void createFilesController(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(0) + tableName + ".java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package big.open.controller;" + ln);
			myWriter.write("import javax.validation.Valid;" + ln);
			myWriter.write("import org.springframework.beans.factory.annotation.Autowired;" + ln);
			myWriter.write("import org.springframework.http.ResponseEntity;" + ln);
			myWriter.write("import org.springframework.web.bind.annotation.CrossOrigin;" + ln);
			myWriter.write("import org.springframework.web.bind.annotation.DeleteMapping;" + ln);
			myWriter.write("import org.springframework.web.bind.annotation.GetMapping;" + ln);
			myWriter.write("import org.springframework.web.bind.annotation.PathVariable;" + ln);
			myWriter.write("import org.springframework.web.bind.annotation.PostMapping;" + ln);
			myWriter.write("import org.springframework.web.bind.annotation.RequestBody;" + ln);
			myWriter.write("import org.springframework.web.bind.annotation.RequestMapping;" + ln);
			myWriter.write("import org.springframework.web.bind.annotation.RestController;" + ln);
			myWriter.write("import big.open.payload.request."+ getNameProperty(tableName, true) +"Request;" + ln);
			myWriter.write("import big.open.payload.response."+ getNameProperty(tableName, true) +"Response;" + ln);
			myWriter.write("import big.open.service."+ getNameProperty(tableName, true) +"Service;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@CrossOrigin(origins = \"*\", maxAge = 3600)" + ln);
			myWriter.write("@RestController" + ln);
			myWriter.write("@RequestMapping(\"/api/" + tableName + "\")" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"Controller " + ln);
			myWriter.write("{" + ln);
			myWriter.write("	@Autowired" + ln);
			myWriter.write("	" + getNameProperty(tableName, true) + "Service " + getNameProperty(tableName, false)+  "Service" + ln);
			myWriter.write("	@GetMapping(\"/findById/{id}\")" + ln); 
			myWriter.write("	public ResponseEntity<"+ getNameProperty(tableName, true) + "ResponseFindById> findById(@PathVariable(\"id\") " + getTypePrimeryKey(entitiName) + " id)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return ResponseEntity.ok(" + getNameProperty(tableName, false)+ "Service.findById(id));" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	@PostMapping(\"/signin\")" + ln);
			myWriter.write("	public ResponseEntity<"+ getNameProperty(tableName, true) + "ResponseSignin> signin(@Valid @RequestBody "+ getNameProperty(tableName, true) + "Request "+ getNameProperty(tableName, false) + "Request) " + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return ResponseEntity.ok(" + getNameProperty(tableName, false)+  "Service.signin(" + getNameProperty(tableName, false)+  "Request));" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	@PostMapping(\"/save\")" + ln);
			myWriter.write("	public ResponseEntity<"+ getNameProperty(tableName, true) + "ResponseSave> save(@Valid @RequestBody "+ getNameProperty(tableName, true) + "Request "+ getNameProperty(tableName, false) + "Request) " + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return ResponseEntity.ok(" + getNameProperty(tableName, false)+  "Service.save(" + getNameProperty(tableName, false)+  "Request));" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	@DeleteMapping(\"/delete/{id}\")" + ln);
			myWriter.write("	public ResponseEntity<String> delete(@PathVariable(\"id\") " + getTypePrimeryKey(entitiName) + " id)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return ResponseEntity.ok(" + getNameProperty(tableName, false)+  "Service.delete(id));" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesEntity(EntityName entitiName )
	{
		String tableName = entitiName.getName();
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
			for(EntityProperty property : entitiName.getListEntityProperty())
			{				
				if(property.getKey().equals("PRI"))
				{
					myWriter.write("	@NotNull()" + ln);
					myWriter.write("	@Id@GeneratedValue(strategy = GenerationType.IDENTITY)" + ln);
					myWriter.write("	@Column(name=\"" + property.getField() + "\")" + ln);
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}				
				else if(!property.getKey().equals("MUL"))
				{
					myWriter.write("	@Column(name=\"" + property.getField() + "\")" + ln);
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}
			}
			for(String relation : getSingleRelation( tableName))
			{
				myWriter.write("	private " + getNameProperty(relation, true) + " " + getNameProperty(relation, false) + ";" +ln);
			}
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			for(Relations relation : findListRelation)
			{
				myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);
			}
			myWriter.write("}" + ln);
			myWriter.close();
		}
		catch(Exception e)
		{ System.out.println(e);}
	}
	private static void createFilesResponse(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(2) + tableName + ".java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package big.open.payload.response;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"Response" + ln);	
			myWriter.write("{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				if(property.getKey().equals("PRI") || !property.getKey().equals("MUL"))
				{
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}				
			}
			for(String relation : getSingleRelation( tableName))
			{
				myWriter.write("	private " + getNameProperty(relation, true) + "Response " + getNameProperty(relation, false) + "Response;" +ln);
			}
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			for(Relations relation : findListRelation)
			{
				myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "Response> list" + getNameProperty(relation.getTABLE_NAME(), true) + "Response;" +ln);
			}
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesRepository(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(5) + tableName + ".java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package big.open.repository;" + ln);
			myWriter.write("import org.springframework.data.jpa.repository.JpaRepository;" + ln);
			myWriter.write("import org.springframework.stereotype.Repository;" + ln);
			myWriter.write("import big.open.entity."+ getNameProperty(tableName, true) +";" + ln);
			
			myWriter.write("@Repository" + ln);
			myWriter.write("public interface "+ getNameProperty(tableName, true) +"Repository extends JpaRepository<"+ getNameProperty(tableName, true) +", " + getTypePrimeryKey(entitiName) + ">" + ln);
			myWriter.write("{" + ln);
			myWriter.write("	" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesProgect(EntityName entitiName)
	{
		createFilesEntity(entitiName);
		createFilesController(entitiName);
		createFilesResponse(entitiName);
		createFilesRepository(entitiName);
		
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
	
	private static void createEntity() 
	{
		try
		{  
			Class.forName("com.mysql.cj.jdbc.Driver");  
			// static List<String> tablesName1 = tablesName;
			

			for(EntityName entitiName: listEntityName)
			{				
				createFolderProgect(entitiName.getName());
				createFilesProgect(entitiName);				
			}
			
		}
		catch(Exception e)
		{ System.out.println(e);}
	}
	private static List<String> getListTable()
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
	private static List<EntityName> entityFiles()
	{
		List<EntityName> listEntityName = new ArrayList<>();
		try
		{
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dataBaseName,"root","root");  
			for(String tableName: listTablesName)
			{
				EntityName entityName = new EntityName();
				entityName.setName(tableName);
				Statement stmt = con.createStatement(); 
				ResultSet rs=stmt.executeQuery("SHOW COLUMNS FROM "+ dataBaseName + "." + tableName );	
				List<EntityProperty> listEntityProperty = new ArrayList<EntityProperty>() ;

				while (rs.next())
				{
					EntityProperty f = new EntityProperty();
					f.setField(rs.getString("Field"));
					f.setType(rs.getString("Type"));
					f.setKey(rs.getString("Key"));
					listEntityProperty.add(f);
				}
				entityName.setListEntityProperty(listEntityProperty);
				listEntityName.add(entityName);
			}			
			con.close();
			
		}
		catch(Exception e)
		{
		}
		return listEntityName;
	}
	private static List<String> getSingleRelation(String tableName)
	{
		List<String> tablesname = new ArrayList<>();
		try
		{
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dataBaseName,"root","root");  
			Statement stmt = con.createStatement(); 
			String sql = " SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = \"" +  dataBaseName + "\" AND TABLE_NAME = \"" + tableName + "\" AND REFERENCED_COLUMN_NAME IS NOT NULL;";
			
			ResultSet rs = stmt.executeQuery(sql );			
			while (rs.next())
			{
				tablesname.add(rs.getString("REFERENCED_TABLE_NAME"));
			}
			con.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
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
	private static String getTypePrimeryKey(EntityName entitiName)
	{
		try
		{
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				if(property.getKey().equals("PRI"))
				{
					return getTypeProperty(property.getType());
				}
			}
		}
		catch(Exception e){}
		return "";
	}
}