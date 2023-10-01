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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeneratorApplication
{
	private static String ln = System.getProperty( "line.separator" );
	private static List<String> files = new ArrayList<>();
	private static String dataBaseName = "big_open";
	private static String packageName = "bigopen";
	private static String propertyIsDeletedName = "is_deleted";
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
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dataBaseName,"root","root"); 
			
				Statement stmt = con.createStatement(); 
				String sql = " SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = \"" +  dataBaseName  + "\" AND REFERENCED_COLUMN_NAME IS NOT NULL;";
				
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
		catch(Exception e)
		{
			 System.out.println(e);
		}
	}
	private static void fillFiles()
	{
		String pathJava = "C:\\geneation\\java\\";		
		String pathAngular = "C:\\geneation\\angular\\";

		String pathController = pathJava + "controller\\";
		String pathEntity =     pathJava + "entity\\";
		String pathResponse =   pathJava + "payload\\response\\";
		String pathError =     pathJava + "payload\\response\\error\\";
		String pathRequest =    pathJava + "payload\\request\\";
		String pathRepository = pathJava + "repository\\";
		String pathService =    pathJava + "service\\";
		String pathFilter =   pathJava + "payload\\filter\\";
		String pathServiceImp =    pathJava + "service_imp\\";
		// pour angular
		String pathResponseAngular =   pathAngular + "payload\\response\\"; 
		String pathResponseListeAngular =    pathAngular + "payload\\response\\list\\"; 
		String pathResponseSaveAngular =    pathAngular + "payload\\response\\save\\";
		String pathResponseErrorAngular =     pathAngular + "payload\\response\\error\\";
		String pathRequestAngular =    pathAngular + "payload\\request\\";
		String pathFilterAngular =    pathAngular + "payload\\filter\\";
		String pathServiceAngular =    pathAngular + "services\\";
		
		files.add(pathController);
		files.add(pathEntity);
		files.add(pathResponse);
		files.add(pathError);
		files.add(pathRequest);
		files.add(pathRepository);
		files.add(pathService);
		files.add(pathFilter);
		files.add(pathServiceImp);
		// pour angular
		files.add(pathResponseAngular);//////// 9
		files.add(pathResponseListeAngular); // 10
		files.add(pathResponseErrorAngular);// 11
		files.add(pathResponseSaveAngular);// 12
		files.add(pathRequestAngular);// 13
		files.add(pathServiceAngular);// 14
		files.add(pathFilterAngular);// 15
	}
	private static void createFilesController(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(0) +  getNameProperty(tableName, true) + "Controller.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".controller;" + ln);
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
			myWriter.write("import "+packageName+".payload.request."+ getNameProperty(tableName, true) +"Request;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"ResponseFindById;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"ResponseSave;" + ln);
			myWriter.write("import "+packageName+".service."+ getNameProperty(tableName, true) +"Service;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@CrossOrigin(origins = \"*\", maxAge = 3600)" + ln);
			myWriter.write("@RestController" + ln);
			myWriter.write("@RequestMapping(\"/api/" + getNameProperty(tableName, false) + "\")" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"Controller " + ln);
			myWriter.write("{" + ln);
			myWriter.write("	@Autowired" + ln);
			myWriter.write("	" + getNameProperty(tableName, true) + "Service " + getNameProperty(tableName, false)+  "Service;" + ln);
			myWriter.write("	@GetMapping(\"/findById/{id}\")" + ln); 
			myWriter.write("	public ResponseEntity<"+ getNameProperty(tableName, true) + "ResponseFindById> findById(@PathVariable(\"id\") " + getTypePrimeryKey(entitiName) + " id)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return ResponseEntity.ok(" + getNameProperty(tableName, false)+ "Service.findById(id));" + ln);
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
			String strpath = files.get(1) + getNameProperty(tableName, true) + ".java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".entity;" + ln);
			myWriter.write("import javax.persistence.Column;" + ln);
			myWriter.write("import javax.persistence.Entity;" + ln);
			myWriter.write("import javax.persistence.GeneratedValue;" + ln);
			myWriter.write("import javax.persistence.GenerationType;" + ln);
			myWriter.write("import javax.persistence.Id;" + ln);
			// myWriter.write("import javax.persistence.JoinColumn;" + ln);
			// myWriter.write("import javax.persistence.JoinTable;" + ln);
			myWriter.write("import javax.persistence.Table;" + ln);
			myWriter.write("import javax.persistence.Transient;" + ln);
			// myWriter.write("import javax.persistence.FetchType;" + ln);	
			// myWriter.write("import java.util.Set;" + ln);
			// myWriter.write("import java.util.HashSet;" + ln);
			// myWriter.write("import javax.persistence.ManyToOne;" + ln);
			// myWriter.write("import javax.persistence.OneToMany;" + ln);
			// myWriter.write("import javax.persistence.ManyToMany;" + ln);	
			myWriter.write("import java.util.List;" + ln);
			
			myWriter.write("import java.time.LocalDateTime;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("import javax.validation.constraints.NotNull;" + ln);
			myWriter.write("@Data" + ln);
			

			myWriter.write("@Entity" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Table(name = \"" + tableName +"\")" + ln);
			myWriter.write("public class " + getNameProperty(tableName, true) + " " + ln);
			myWriter.write("{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{				
				if(property.getKey().equals("PRI"))
				{
					myWriter.write("	@NotNull()" + ln);
					myWriter.write("	@Id" + ln);
					myWriter.write("	@GeneratedValue(strategy = GenerationType.IDENTITY)" + ln);
					myWriter.write("	@Column(name=\"" + property.getField() + "\")" + ln);
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}				
				else //if(!property.getKey().equals("MUL"))
				{
					myWriter.write("	@Column(name=\"" + property.getField() + "\")" + ln);
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}
			}
			for(Relations relation : getSingleRelation( tableName))
			{
				// myWriter.write("	@ManyToOne(fetch = FetchType.EAGER)" + ln);
				// myWriter.write("	@JoinColumn(name = \""+ relation.getCOLUMN_NAME() +"\")" + ln);
				myWriter.write("	@Transient" + ln);
				myWriter.write("	private " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + " " +  getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + ";" +ln);
			}
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			
			for(Relations relation : findListRelation)
			{
				myWriter.write("	@Transient" + ln);
				myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
				if(checkManyToMany(relation.getTABLE_NAME(), tableName,0))
				{
					
					//name = "users_roles",  joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id")
					var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
					if(listForeignKey.size() == 2)
					{
						// myWriter.write("	@ManyToMany(fetch = FetchType.EAGER)" + ln);
						// myWriter.write("	@JoinTable(name = \""+ relation.getTABLE_NAME() +"\", joinColumns = @JoinColumn(name = \"" + listForeignKey.get(0) + "\"), inverseJoinColumns = @JoinColumn(name = \""+  listForeignKey.get(1) +"\"))" + ln);
						var entity = getNameEntity(tableName, relation.getTABLE_NAME());
						if(entity != "")
						{
							myWriter.write("	@Transient" + ln);
							myWriter.write("	private List<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + "> list" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ";" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
						}
					}
						
				}
				// else
				// {
				// 	myWriter.write("	@OneToMany(fetch = FetchType.EAGER)" + ln);
				// 	myWriter.write("	@JoinColumn(name = \""+ relation.getCOLUMN_NAME() +"\")" + ln);
				// 	myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);
				// }				
				
			}
			myWriter.write("}" + ln);
			myWriter.close();
		}
		catch(Exception e)
		{ System.out.println(e);}
	}
	private static void createFilesFilter(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(7) + getNameProperty(tableName, true) + "Filter.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".payload.filter;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.StringFilter;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.IntegerFilter;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.DoubleFilter;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.BooleanFilter;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.LocalDateTimeFilter;"+ ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"Filter" + ln);	
			myWriter.write("{" + ln);
			myWriter.write("	private Pager pager;" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				if(property.getKey().equals("PRI") || !property.getKey().equals("MUL"))
				{
					myWriter.write("	private " + getTypeProperty(property.getType())+ "Filter " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}				
			}
			
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesResponse(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			String strpath = files.get(2) + getNameProperty(tableName, true) + "Response.java";
			FileWriter myWriter = new FileWriter(strpath);
			//import bigopen.payload.response.error.UniteResponseError;
			myWriter.write("package "+packageName+".payload.response;" + ln);
			myWriter.write("import "+packageName+".payload.response.error."+ getNameProperty(tableName, true) +"ResponseError;" + ln);

			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import java.util.List;" + ln);
			myWriter.write("import java.time.LocalDateTime;" + ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"Response" + ln);	
			myWriter.write("{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				// if(property.getKey().equals("PRI") || !property.getKey().equals("MUL"))
				// {
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				// }				
			}
			for(Relations relation : getSingleRelation( tableName))
			{
				myWriter.write("	private " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "Response " +  getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + ";" +ln);
			}
			for(Relations relation : findListRelation)
			{
				myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "Response> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
				if(checkManyToMany(relation.getTABLE_NAME(), tableName,0))
				{
					var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
					if(listForeignKey.size() == 2)
					{
						var entity = getNameEntity(tableName, relation.getTABLE_NAME());
						if(entity != "")
						{
							myWriter.write("	private List<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + "Response> list" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ";" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
						}
					}
				}
			}
			myWriter.write("	private "+ getNameProperty(tableName, true) +"ResponseError responseError;" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"Response(" +getNameProperty(tableName, true)+ "ResponseError responseError)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this.responseError = responseError;" + ln);
			myWriter.write("	}" + ln);


			myWriter.write("	public "+ getNameProperty(tableName, true) +"Response(" + getTypePrimeryKey(entitiName) + " id)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this." + getNameProperty(getFieldPrimeryKey(entitiName),false) + " = id;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesResponseFindById(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(2) + getNameProperty(tableName, true) + "ResponseFindById.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".payload.response;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"ResponseFindById" + ln);	
			myWriter.write("{" + ln);
			myWriter.write("	private "+ getNameProperty(tableName, true) +"Response "+ getNameProperty(tableName, false) +"Response;" + ln);
			myWriter.write("	private String message;" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseFindById("+ getNameProperty(tableName, true) +"Response "+ getNameProperty(tableName, false) +"Response)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this."+ getNameProperty(tableName, false) +"Response = "+ getNameProperty(tableName, false) +"Response;" + ln);
			myWriter.write("	}" + ln);
			
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseFindById(String message) " + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this.message = message;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	
	private static void createFilesResponseList(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(2) + getNameProperty(tableName, true) + "ResponseList.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".payload.response;" + ln);
			myWriter.write("import java.util.List;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import bigopen.payload.filter.Pager;" + ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"ResponseList" + ln);	
			myWriter.write("{" + ln);
			myWriter.write("	private List<"+ getNameProperty(tableName, true) +"Response> list"+ getNameProperty(tableName, true) +"Response;" + ln);
			myWriter.write("	private Pager pager;" + ln);
			myWriter.write("	private String message;" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseList(List<"+ getNameProperty(tableName, true) +"Response> list"+ getNameProperty(tableName, true) +"Response, Pager pager)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this.list"+ getNameProperty(tableName, true) +"Response = list"+ getNameProperty(tableName, true) +"Response;" + ln);
			myWriter.write("		this.pager = pager;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesResponseSave(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(2) + getNameProperty(tableName, true) + "ResponseSave.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".payload.response;" + ln);
			myWriter.write("import "+packageName+".payload.response.error."+ getNameProperty(tableName, true) +"ResponseError;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"ResponseSave" + ln);	
			myWriter.write("{" + ln);
			myWriter.write("	private "+ getNameProperty(tableName, true) +"Response "+ getNameProperty(tableName, false) +"Response;" + ln);
			myWriter.write("	private "+ getNameProperty(tableName, true) +"ResponseError "+ getNameProperty(tableName, false) +"ResponseError;" + ln);
			myWriter.write("	private String message;" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseSave("+ getNameProperty(tableName, true) +"Response "+ getNameProperty(tableName, false) +"Response)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this."+ getNameProperty(tableName, false) +"Response = "+ getNameProperty(tableName, false) +"Response;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseSave("+ getNameProperty(tableName, true) +"ResponseError "+ getNameProperty(tableName, false) +"ResponseError) " + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this."+ getNameProperty(tableName, false) +"ResponseError = "+ getNameProperty(tableName, false) +"ResponseError;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseSave(String message) " + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this.message = message;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesError(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(3) + getNameProperty(tableName, true) + "ResponseError.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".payload.response.error;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"ResponseError" + ln);	
			myWriter.write("{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				if(property.getKey().equals("PRI") || !property.getKey().equals("MUL"))
				{
					myWriter.write("	private String " +  getNameProperty(property.getField(), false)+ ";" + ln) ;
				}				
			}
			myWriter.write("	private boolean haveError;" + ln);
			myWriter.write("	private String message ;" + ln);
			
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseError(String message)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this.message = message;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesRequest(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(4) + getNameProperty(tableName, true) + "Request.java";
			FileWriter myWriter = new FileWriter(strpath);
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			myWriter.write("package "+packageName+".payload.request;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import java.time.LocalDateTime;" + ln);
			myWriter.write("import java.util.List;" + ln);
						
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);			
			myWriter.write("public class "+ getNameProperty(tableName, true) +"Request" + ln);	
			myWriter.write("{" + ln);
			myWriter.write("	private Integer page;" + ln);
			myWriter.write("	private Integer size;" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				// if(property.getKey().equals("PRI") || !property.getKey().equals("MUL"))
				// {
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				// }				
			}
			for(Relations relation : getSingleRelation( tableName))
			{
				myWriter.write("	private " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "Request " +  getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + ";" +ln);
			}
			
			// for(Relations relation : findListRelation)
			// {
			// 	myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "Request> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);
			// }
			for(Relations relation : findListRelation)
			{
				myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "Request> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
				if(checkManyToMany(relation.getTABLE_NAME(), tableName,0))
				{
					var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
					if(listForeignKey.size() == 2)
					{
						var entity = getNameEntity(tableName, relation.getTABLE_NAME());
						if(entity != "")
						{
							myWriter.write("	private List<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + "Request> list" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ";" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
						}
						// else
						// {
						// 	myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "Request> list" + getNameProperty(relation.getTABLE_NAME(), true) + "Request;" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
						// }
					}
				}
			}
			myWriter.write("	public "+ getNameProperty(tableName, true) +"Request(" + getTypePrimeryKey(entitiName) + " id)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this." + getNameProperty(getFieldPrimeryKey(entitiName),false) + " = id;" + ln);
			myWriter.write("	}" + ln);
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
			String strpath = files.get(5) + getNameProperty(tableName, true) + "Repository.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".repository;" + ln);
			myWriter.write("import org.springframework.data.jpa.repository.JpaRepository;" + ln);
			myWriter.write("import org.springframework.stereotype.Repository;" + ln);
			myWriter.write("import org.springframework.data.jpa.repository.Query;" + ln);
			myWriter.write("import java.util.Optional;" + ln);
			myWriter.write("import java.util.List;" + ln);
			myWriter.write("import "+packageName+".entity."+ getNameProperty(tableName, true) +";" + ln);
			
			myWriter.write("@Repository" + ln);
			myWriter.write("public interface "+ getNameProperty(tableName, true) +"Repository extends JpaRepository<"+ getNameProperty(tableName, true) +", " + getTypePrimeryKey(entitiName) + ">" + ln);
			myWriter.write("{" + ln);
			myWriter.write("	Optional<List<" + getNameProperty(tableName, true) + ">> findBy"  + getNameProperty(getFieldPrimeryKey(entitiName), true) + "In(List<"+ getTypePrimeryKey(entitiName) +"> list"+ getNameProperty(getFieldPrimeryKey(entitiName), true) + ");" + ln);
			for(Relations relation : getSingleRelation( tableName))
			{
				myWriter.write("	Optional<List<" + getNameProperty(relation.getTABLE_NAME(), true) + ">> findBy"+ getNameProperty(relation.getCOLUMN_NAME(), true) + "(Integer "+ getNameProperty(relation.getCOLUMN_NAME(), false) + ");" + ln);
				myWriter.write("	Optional<List<" + getNameProperty(relation.getTABLE_NAME(), true) + ">> findBy"+ getNameProperty(relation.getCOLUMN_NAME(), true) + "In(List<Integer> list"+ getNameProperty(relation.getCOLUMN_NAME(), true) + ");" + ln);
				
			}



			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			
			for(Relations relation : findListRelation)
			{
				if(checkManyToMany(relation.getTABLE_NAME(), tableName,1))
				{
					var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
					if(listForeignKey.size() == 2)
					{
						var entity = getNameEntity(tableName, relation.getTABLE_NAME());
						if(entity != "")
						{
							myWriter.write("	@Query(value = \"select * from "+ tableName + " inner join " + relation.getTABLE_NAME() + " on " + tableName  + "." + getFieldPrimeryKey(entitiName) + " = " + relation.getTABLE_NAME() + "." + relation.getCOLUMN_NAME() + " where " + relation.getTABLE_NAME() + "."+ listForeignKey.get(0)+ " =?1\", nativeQuery = true)" + ln);
							myWriter.write("	Optional<List<"+ getNameProperty(tableName, true) + ">> findBy" + getNameProperty(listForeignKey.get(0), true) + "(Integer " + getNameProperty(listForeignKey.get(0), false) + ");" + ln);
						}
					}
				}
				// if(!tableName.equals(relation.getTABLE_NAME()) && checkManyToMany(relation.getTABLE_NAME(), tableName,0))
				// {
				// 	var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
				// 	if(listForeignKey.size() == 2)
				// 	{
				// 		var entity = getNameEntity(tableName, relation.getTABLE_NAME());
				// 		if(!entity.isEmpty())
				// 		{
				// 			var myE = listEntityName.stream().filter(e -> e.getName().equals(relation.getREFERENCED_TABLE_NAME())).findFirst();
				// 			if(myE.isPresent())
				// 			{
				// 				String primeryKey = getFieldPrimeryKey(myE.get());
				// 				myWriter.write("	Optional<List<"+ getNameProperty(tableName, true) + ">> " +  "findBy" + getNameProperty(primeryKey, true) + getNameProperty(relation.getREFERENCED_TABLE_NAME(),true) + ln) ;
				// 			}
							
				// 		}
				// 	}
				// }
			}
			myWriter.write("	" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesServiceImp(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			String strpath = files.get(8) + getNameProperty(tableName, true) + "ServiceImp.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".service_imp;" + ln);
			myWriter.write("import java.util.Optional;" + ln);
			myWriter.write("import org.springframework.beans.factory.annotation.Autowired;" + ln);
			myWriter.write("import org.springframework.stereotype.Service;" + ln);			//
			myWriter.write("import "+packageName+".entity."+ getNameProperty(tableName, true) +";" + ln);
			myWriter.write("import "+packageName+".payload.filter."+ getNameProperty(tableName, true) +"Filter;" + ln);
			myWriter.write("import "+packageName+".payload.request."+ getNameProperty(tableName, true) +"Request;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"Response;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"ResponseFindById;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"ResponseSave;" + ln);
			myWriter.write("import "+packageName+".payload.response.error."+ getNameProperty(tableName, true) +"ResponseError;" + ln);
			myWriter.write("import "+packageName+".repository."+ getNameProperty(tableName, true) +"Repository;" + ln);
			List<String> listImport = new ArrayList<>();
			for(Relations relation : getSingleRelation( tableName))
			{
				if(listImport.stream().filter(str -> str.equals(relation.getREFERENCED_TABLE_NAME())).count() == 0)
				{
					listImport.add(relation.getREFERENCED_TABLE_NAME());
					myWriter.write("import "+packageName+".repository."+ getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) +"Repository;" + ln);
					myWriter.write("import "+packageName+".entity."+ getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) +";" + ln);
				}
			}
			for(Relations relation : findListRelation)
			{
				if(listImport.stream().filter(str -> str.equals(relation.getTABLE_NAME())).count() == 0)
				{
					listImport.add(relation.getTABLE_NAME());
					myWriter.write("import "+packageName+".repository."+ getNameProperty(relation.getTABLE_NAME(), true) +"Repository;" + ln);
					myWriter.write("import "+packageName+".entity."+ getNameProperty(relation.getTABLE_NAME(), true) +";" + ln);
				}
				if(!tableName.equals(relation.getTABLE_NAME()) && checkManyToMany(relation.getTABLE_NAME(), tableName,0))
				{
					var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
					if(listForeignKey.size() == 2)
					{
						var entity = getNameEntity(tableName, relation.getTABLE_NAME());
						if(!entity.isEmpty())
						{	
							listImport.add(entity);
							myWriter.write("import "+packageName+".entity."+ getNameProperty(entity, true) +";" + ln);
							myWriter.write("import "+packageName+".repository."+ getNameProperty(entity, true) +"Repository;" + ln);
						}
					}
				}
			}
			myWriter.write("import "+packageName+".security.jwt.JwtUtils;" + ln);
			myWriter.write("import "+packageName+".utility.ObjectMapperUtility;" + ln);
			myWriter.write("import "+packageName+".utility.Utility;" + ln);
			myWriter.write("import "+packageName+".service."+ getNameProperty(tableName, true) + "Service;" + ln);
			// myWriter.write("import org.springframework.security.core.context.SecurityContextHolder;" + ln);
			myWriter.write("import java.util.List;" + ln);
			myWriter.write("import javax.persistence.Query;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.StringFilter;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.IntegerFilter;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.DoubleFilter;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.BooleanFilter;"+ ln);
			myWriter.write("import "+packageName+".payload.filter.operateur.LocalDateTimeFilter;"+ ln);
			myWriter.write("import javax.persistence.EntityManager;" + ln);
			myWriter.write("import java.util.stream.Collectors;" + ln);
			// myWriter.write("import org.springframework.security.authentication.AuthenticationManager;" + ln);
			// myWriter.write("import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;" + ln);
			// myWriter.write("import org.springframework.security.core.Authentication;" + ln);
			// myWriter.write("import org.springframework.security.crypto.password.PasswordEncoder;" + ln);
			myWriter.write("@Service" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"ServiceImp implements "+ getNameProperty(tableName, true) + "Service" + ln);
			myWriter.write("{" + ln);
			myWriter.write("	@Autowired" + ln);
			myWriter.write("	JwtUtils jwtUtils;" + ln);			
			// myWriter.write("	@Autowired" + ln);
			// myWriter.write("	AuthenticationManager authenticationManager;" + ln);
			// myWriter.write("	@Autowired" + ln);
			// myWriter.write("	PasswordEncoder encoder;" + ln);

			myWriter.write("	@Autowired" + ln);
			myWriter.write("	private EntityManager entityManager;" + ln);



			myWriter.write("	@Autowired" + ln);
			myWriter.write("	"+ getNameProperty(tableName, true) +"Repository "+ getNameProperty(tableName, false) +"Repository;" + ln);
			List<String> listAutoWired = new ArrayList<>();
			listAutoWired.add(tableName);
			for(Relations relation : getSingleRelation( tableName))
			{
				if(listAutoWired.stream().filter(str -> str.equals(relation.getREFERENCED_TABLE_NAME())).count() == 0)
				{
					listAutoWired.add(relation.getREFERENCED_TABLE_NAME());
					myWriter.write("	@Autowired" + ln);
					myWriter.write("	" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "Repository " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + "Repository;" + ln);	
				}
			}
			for(Relations relation : findListRelation)
			{
				if(listAutoWired.stream().filter(str -> str.equals(relation.getTABLE_NAME())).count() == 0)
				{
					listAutoWired.add(relation.getTABLE_NAME());
					myWriter.write("	@Autowired" + ln);
					myWriter.write("	" + getNameProperty(relation.getTABLE_NAME(), true) + "Repository " + getNameProperty(relation.getTABLE_NAME(), false) + "Repository;" + ln);	
			
				}
				if(!tableName.equals(relation.getTABLE_NAME()) && checkManyToMany(relation.getTABLE_NAME(), tableName,0))
				{
					var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
					if(listForeignKey.size() == 2)
					{
						var entity = getNameEntity(tableName, relation.getTABLE_NAME());
						if(!entity.isEmpty())
						{	
							if(listAutoWired.stream().filter(str -> str.equals(entity)).count() == 0)
							{
								listAutoWired.add(entity);
								myWriter.write("	@Autowired" + ln);
								myWriter.write("	" + getNameProperty(entity, true) + "Repository " + getNameProperty(entity, false) + "Repository;" + ln);	
							}
						}
					}
				}
			}
			myWriter.write("	public List<"+ getNameProperty(tableName, true) +"> findByFilter(" + getNameProperty(tableName, true) +"Filter " + getNameProperty(tableName, false) + "Filter)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		StringBuilder sb = new StringBuilder();" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				if(property.getKey().equals("PRI") || !property.getKey().equals("MUL"))
					myWriter.write("	 	Utility.getPredicate(sb ,"+ getNameProperty(tableName, false) + "Filter.get" + getNameProperty(property.getField(), true) + "(), \"" + getNameProperty(tableName, true) + "." + getNameProperty(property.getField(), false) + "\", " + getTypeProperty(property.getType()) + "Filter.class.getName());"+ ln);
			}
			myWriter.write("		 String sql = \"SELECT " + getNameProperty(tableName, true) + " FROM " + getNameProperty(tableName, true) + " " + getNameProperty(tableName, true) + "\";" + ln);
			myWriter.write("		 if(sb.length()>0)" + ln);
			myWriter.write("		 {" + ln);
			myWriter.write("		 	sql = sql.concat(\" WHERE \");" + ln);
			myWriter.write("		 	sql = sql.concat(sb.toString());" + ln);
			myWriter.write("		 }" + ln);
			myWriter.write("		 Query query = entityManager.createQuery(sql, " + getNameProperty(tableName, true) + ".class);" + ln);
			myWriter.write("		 return Utility.castList(" + getNameProperty(tableName, true) + ".class, query.getResultList());" + ln);
			myWriter.write("	}" + ln);
			
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseFindById findById(" + getTypePrimeryKey(entitiName) + " id)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		Optional<"+ getNameProperty(tableName, true) +"> "+ getNameProperty(tableName, false) +" = "+ getNameProperty(tableName, false) +"Repository.findById(id);" + ln);
			myWriter.write("		if("+ getNameProperty(tableName, false) +".isPresent())" + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			"+ getNameProperty(tableName, true) +"Response "+ getNameProperty(tableName, false) +"Response = ObjectMapperUtility.map("+ getNameProperty(tableName, false) +".get(),"+ getNameProperty(tableName, true) +"Response.class);" + ln);
			myWriter.write("			return new "+ getNameProperty(tableName, true) +"ResponseFindById("+ getNameProperty(tableName, false) +"Response);" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("		return new "+ getNameProperty(tableName, true) +"ResponseFindById(\"\");" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseSave save("+ getNameProperty(tableName, true) +"Request "+ getNameProperty(tableName, false) +"Request)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		"+ getNameProperty(tableName, true) +"ResponseError "+ getNameProperty(tableName, false) +"ResponseError = check"+ getNameProperty(tableName, true) +"ResponseError("+ getNameProperty(tableName, false) +"Request);" + ln);
			myWriter.write("		if("+ getNameProperty(tableName, false) +"ResponseError.isHaveError())" + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			return new "+ getNameProperty(tableName, true) +"ResponseSave("+ getNameProperty(tableName, false) +"ResponseError);" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("		else" + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			try" + ln);
			myWriter.write("			{" + ln);
			if(checkProperty(entitiName,propertyIsDeletedName))
			{		
				myWriter.write("				if("+ getNameProperty(tableName, false) +"Request.get" + getNameProperty(getFieldPrimeryKey(entitiName),true) + "() == -1)" + ln);
				myWriter.write("					"+ getNameProperty(tableName, false) +"Request.setIsDeleted(0);" + ln);
			}
			myWriter.write("				"+ getNameProperty(tableName, true) +" "+ getNameProperty(tableName, false) +" = "+ getNameProperty(tableName, false) +"Repository.save(ObjectMapperUtility.map("+ getNameProperty(tableName, false) +"Request, "+ getNameProperty(tableName, true) +".class));" + ln);
			myWriter.write("				return  new "+ getNameProperty(tableName, true) +"ResponseSave(ObjectMapperUtility.map("+ getNameProperty(tableName, false) +", "+ getNameProperty(tableName, true) +"Response.class));" + ln);
			myWriter.write("			}" + ln);
			myWriter.write("			catch(Exception e)" + ln);
			myWriter.write("			{" + ln);
			myWriter.write("				"+ getNameProperty(tableName, false) +"ResponseError.setHaveError(true);" + ln);
			myWriter.write("				return  new "+ getNameProperty(tableName, true) +"ResponseSave(\"Erreur d'enregistrsqlent\");" + ln);
			myWriter.write("			}" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	public String delete(" + getTypePrimeryKey(entitiName) + " id)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		try" + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			var "+ getNameProperty(tableName, false) +" = "+ getNameProperty(tableName, false) +"Repository.findById(id);" + ln);
			if(checkProperty(entitiName,propertyIsDeletedName))
			{
			myWriter.write("			if("+ getNameProperty(tableName, false) +".get().getIsDeleted() == 0)" + ln);
			myWriter.write("				"+ getNameProperty(tableName, false) +".get().setIsDeleted(1);" + ln);
			myWriter.write("			else" + ln);
			myWriter.write("				"+ getNameProperty(tableName, false) +".get().setIsDeleted(0);" + ln);
			}
			myWriter.write("			"+ getNameProperty(tableName, false) +"Repository.save("+ getNameProperty(tableName, false) +".get());" + ln);
			myWriter.write("			return \"\";" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("		catch(Exception e)" + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			return \"Erreur de suppression\";" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("	}" + ln);
			for(Relations relation : getSingleRelation( tableName))
			{
				if(!tableName.equals(relation.getREFERENCED_TABLE_NAME()))
				{
					myWriter.write("	public void set" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "Relation(" + getNameProperty(tableName, true) +" " + getNameProperty(tableName, false) + ")" + ln);
					myWriter.write("	{" + ln);
					myWriter.write("		if(" + getNameProperty(tableName, false) + ".get" + getNameProperty(relation.getCOLUMN_NAME(), true) + "() != null)"+ ln);
					myWriter.write("		{" + ln);
					myWriter.write("			var " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + " = " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + "Repository.findById(" + getNameProperty(tableName, false) + ".get" + getNameProperty(relation.getCOLUMN_NAME(), true) + "());" + ln);
					myWriter.write("			if(" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + ".isPresent())" + ln);
					myWriter.write("				" + getNameProperty(tableName, false) + ".set" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "(" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + ".get());" + ln);
					myWriter.write("		}" + ln);
					myWriter.write("	}" + ln);			
					
					myWriter.write("	public void set" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "ListRelation(List<" + getNameProperty(tableName, true) +"> list" + getNameProperty(tableName, true) + ")" + ln);
					myWriter.write("	{" + ln);
					myWriter.write("		List<Integer> list" + getNameProperty(relation.getCOLUMN_NAME(), true) + " = list" + getNameProperty(tableName, true) +".stream().map(obj -> obj.get" + getNameProperty(relation.getCOLUMN_NAME(), true) + "()).collect(Collectors.toList());" + ln);
					myWriter.write("		Optional<List<" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) +">> list"+ getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + " = " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + "Repository.findBy" + getNameProperty(relation.getREFERENCED_COLUMN_NAME(), true) + "In(list" + getNameProperty(relation.getCOLUMN_NAME(), true) + ");"  + ln);
					myWriter.write("		if(list" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + ".isPresent() && list" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + ".get().size()>0)" + ln);
					myWriter.write("		{" + ln);
					myWriter.write("			for(" + getNameProperty(tableName, true) + " " + getNameProperty(tableName, false) + " : list" + getNameProperty(tableName, true) + ")" + ln);
					myWriter.write("			{" + ln);
					myWriter.write("				List<" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "> list" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "2 = list" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + ".get().stream().filter(obj -> obj.get" + getNameProperty(relation.getREFERENCED_COLUMN_NAME(), true) + "().equals(" + getNameProperty(tableName, false) + ".get" + getNameProperty(relation.getCOLUMN_NAME(), true) + "())).collect(Collectors.toList());" + ln);
					myWriter.write("				if(list" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "2 != null && list" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "2.size()>0)" + ln);
					myWriter.write("					" + getNameProperty(tableName, false) + ".set" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "(list" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "2.get(0));" + ln);
					myWriter.write("			}" + ln);
					myWriter.write("		}" + ln);
					myWriter.write("	}" + ln);

				}
			}

			
			for(Relations relation : findListRelation)
			{
				if(!tableName.equals(relation.getTABLE_NAME()))
				{
					myWriter.write("	public void setList" + getNameProperty(relation.getTABLE_NAME(), true) + "Relation ("  + getNameProperty(tableName, true) + " " + getNameProperty(tableName, false) + ")" + ln);
					myWriter.write("	{" + ln);
					myWriter.write("		var list" + getNameProperty(relation.getTABLE_NAME(), true) + " = " + getNameProperty(relation.getTABLE_NAME(), false) + "Repository.findBy" + getNameProperty(relation.getCOLUMN_NAME(), true) + "(" + getNameProperty(tableName, false) + ".get" + getNameProperty(getFieldPrimeryKey(entitiName), true) + "());" + ln);
					myWriter.write("		if(list" + getNameProperty(relation.getTABLE_NAME(), true) + ".isPresent())" + ln);
					myWriter.write("			" + getNameProperty(tableName, false) + ".setList"+ getNameProperty(relation.getTABLE_NAME(), true) + "(list"+ getNameProperty(relation.getTABLE_NAME(), true) + ".get());" + ln);
					myWriter.write("	}" + ln);

					myWriter.write("	public void setList" + getNameProperty(relation.getTABLE_NAME(), true) + "List"  + getNameProperty(tableName, true) + "Relation (List<"  + getNameProperty(tableName, true) + "> list"  + getNameProperty(tableName, true) + ")" + ln);
					myWriter.write("	{" + ln);
					myWriter.write("		List<" + getTypePrimeryKey(entitiName) + "> list" + getNameProperty(relation.getREFERENCED_COLUMN_NAME(), true) + " = list"  + getNameProperty(tableName, true) + ".stream().map(car -> car.get" + getNameProperty(getFieldPrimeryKey(entitiName), true) + "()).collect(Collectors.toList());" + ln);
					myWriter.write("		var list" + getNameProperty(relation.getTABLE_NAME(), true) + " = " + getNameProperty(relation.getTABLE_NAME(), false) + "Repository.findBy" + getNameProperty(relation.getCOLUMN_NAME(), true) + "In(list" + getNameProperty(relation.getREFERENCED_COLUMN_NAME(), true) + ");" + ln);
					myWriter.write("		if(list" + getNameProperty(relation.getTABLE_NAME(), true) + ".isPresent())" + ln);
					myWriter.write("		{" + ln);
					myWriter.write("			for("  + getNameProperty(tableName, true) + " "  + getNameProperty(tableName, false) + " : list"  + getNameProperty(tableName, true) + ")" + ln);
					myWriter.write("			{" + ln);
					myWriter.write("				List<" + getNameProperty(relation.getTABLE_NAME(), true) + "> list"  + getNameProperty(relation.getTABLE_NAME(), true) + "Filtred = list" + getNameProperty(relation.getTABLE_NAME(), true) + ".get().stream().filter(obj -> obj.get" + getNameProperty(relation.getCOLUMN_NAME(), true) + "().equals(" + getNameProperty(tableName, false) + ".get" + getNameProperty(getFieldPrimeryKey(entitiName), true) + "())).collect(Collectors.toList());" + ln);
					myWriter.write("				if(list"  + getNameProperty(relation.getTABLE_NAME(), true) + "Filtred != null && list"  + getNameProperty(relation.getTABLE_NAME(), true) + "Filtred.size()>0)" + ln);
					myWriter.write("					"  + getNameProperty(tableName, false) + ".setList" + getNameProperty(relation.getTABLE_NAME(), true) + "(list"  + getNameProperty(relation.getTABLE_NAME(), true) + "Filtred);" + ln);
					myWriter.write("			}" + ln);
					myWriter.write("		}" + ln);
					myWriter.write("	}" + ln);
					if(checkManyToMany(relation.getTABLE_NAME(), tableName,0))
					{
						var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
						if(listForeignKey.size() == 2)
						{
							var entity = getNameEntity(tableName, relation.getTABLE_NAME());
							if(!entity.isEmpty())
							{	
								String namRela =  getNameProperty(relation.getREFERENCED_TABLE_NAME(),true) + getNameProperty(entity, true);						
								myWriter.write("	public void setList" + getNameProperty(entity, true) + "Relation("  + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + " " +  getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + ")" + ln);
								myWriter.write("	{" + ln);
								myWriter.write("		var list" + namRela +" = " + getNameProperty(relation.getREFERENCED_TABLE_NAME(),false) + getNameProperty(entity, true) + "Repository.findById" + getNameProperty(entity, true) + "(" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + ".getId());" + ln);
								myWriter.write("		if(list" + namRela + ".isPresent())" + ln);
								myWriter.write("		{" + ln);
								myWriter.write("			var listId = list" + namRela + ".get().stream().map(ac -> ac.getId()).collect(Collectors.toList());" + ln);
								myWriter.write("			Optional<List<" + getNameProperty(entity, true) + ">> list"  + getNameProperty(entity, true) +  " = " + getNameProperty(entity, false) +  "Repository.findByIdIn(listId);" + ln);
								myWriter.write("			if(list" + getNameProperty(entity, true) + ".isPresent())" + ln);
								myWriter.write("				" + getNameProperty(relation.getREFERENCED_TABLE_NAME(),false) + ".setList" + getNameProperty(entity, true) + "(list" + getNameProperty(entity, true) + ".get());" + ln);
								myWriter.write("		}" + ln);
								
								myWriter.write("	}" + ln);
								myWriter.write("	" + ln);

							}
						}
					}
			
				}
			}
			myWriter.write("	" + ln);
			myWriter.write("	private "+ getNameProperty(tableName, true) +"ResponseError check"+ getNameProperty(tableName, true) +"ResponseError ("+ getNameProperty(tableName, true) +"Request "+ getNameProperty(tableName, false) +"Request)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		"+ getNameProperty(tableName, true) +"ResponseError "+ getNameProperty(tableName, false) +"ResponseError = new "+ getNameProperty(tableName, true) +"ResponseError();" + ln);
			myWriter.write("		"+ getNameProperty(tableName, false) +"ResponseError.setHaveError(false);" + ln);
			myWriter.write("		if(Utility.isEmpty("+ getNameProperty(tableName, false) +"Request.get"+ getNameProperty(getFieldPrimeryKey(entitiName), true) +"()) )"+ ln);
			myWriter.write("		{"+ ln);
			myWriter.write("			"+ getNameProperty(tableName, false) +"Request.set"+ getNameProperty(getFieldPrimeryKey(entitiName), true) +"(-1);"+ ln);
			myWriter.write("		}"+ ln);
			myWriter.write("		//if(Utility.isEmpty("+ getNameProperty(tableName, false) +"Request.get()) )" + ln);
			myWriter.write("		//{" + ln);
			myWriter.write("				//"+ getNameProperty(tableName, false) +"ResponseError.setHaveError(true);" + ln);
			myWriter.write("				//"+ getNameProperty(tableName, false) +"ResponseError.set(\"Le nom d'utilisateur est obligatoire\");" + ln);
			myWriter.write("		//}" + ln);
			myWriter.write("		return "+ getNameProperty(tableName, false) +"ResponseError;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesService(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			String strpath = files.get(6) + getNameProperty(tableName, true) + "Service.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".service;" + ln);
			// myWriter.write("import java.util.Optional;" + ln);
			// myWriter.write("import org.springframework.beans.factory.annotation.Autowired;" + ln);
			myWriter.write("import org.springframework.stereotype.Service;" + ln);			//
			myWriter.write("import "+packageName+".entity."+ getNameProperty(tableName, true) +";" + ln);
			myWriter.write("import "+packageName+".payload.request."+ getNameProperty(tableName, true) +"Request;" + ln);
			myWriter.write("import "+packageName+".payload.filter."+ getNameProperty(tableName, true) +"Filter;" + ln);
			// myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"Response;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"ResponseFindById;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"ResponseSave;" + ln);
			// myWriter.write("import "+packageName+".payload.response.error."+ getNameProperty(tableName, true) +"ResponseError;" + ln);
			// myWriter.write("import "+packageName+".repository."+ getNameProperty(tableName, true) +"Repository;" + ln);
			// List<String> listImport = new ArrayList<>();
			// for(Relations relation : getSingleRelation( tableName))
			// {
			// 	if(listImport.stream().filter(str -> str.equals(relation.getREFERENCED_TABLE_NAME())).count() == 0)
			// 	{
			// 		listImport.add(relation.getREFERENCED_TABLE_NAME());
			// 		myWriter.write("import "+packageName+".repository."+ getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) +"Repository;" + ln);
			// 		myWriter.write("import "+packageName+".entity."+ getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) +";" + ln);
			// 	}
			// }
			// for(Relations relation : findListRelation)
			// {
			// 	if(listImport.stream().filter(str -> str.equals(relation.getTABLE_NAME())).count() == 0)
			// 	{
			// 		listImport.add(relation.getTABLE_NAME());
			// 		myWriter.write("import "+packageName+".repository."+ getNameProperty(relation.getTABLE_NAME(), true) +"Repository;" + ln);
			// 		myWriter.write("import "+packageName+".entity."+ getNameProperty(relation.getTABLE_NAME(), true) +";" + ln);
			// 	}
			// }
			// myWriter.write("import "+packageName+".security.jwt.JwtUtils;" + ln);
			// myWriter.write("import "+packageName+".utility.ObjectMapperUtility;" + ln);
			// myWriter.write("import "+packageName+".utility.Utility;" + ln);
			// myWriter.write("import org.springframework.security.core.context.SecurityContextHolder;" + ln);
			myWriter.write("import java.util.List;" + ln);
			// myWriter.write("import java.util.stream.Collectors;" + ln);
			// myWriter.write("import org.springframework.security.authentication.AuthenticationManager;" + ln);
			// myWriter.write("import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;" + ln);
			// myWriter.write("import org.springframework.security.core.Authentication;" + ln);
			// myWriter.write("import org.springframework.security.crypto.password.PasswordEncoder;" + ln);
			myWriter.write("@Service" + ln);
			myWriter.write("public interface "+ getNameProperty(tableName, true) +"Service" + ln);
			myWriter.write("{" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseFindById findById(" + getTypePrimeryKey(entitiName) + " id);" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseSave save("+ getNameProperty(tableName, true) +"Request "+ getNameProperty(tableName, false) +"Request);" + ln);
			myWriter.write("	public String delete(" + getTypePrimeryKey(entitiName) + " id);" + ln);
			myWriter.write("	public List<"+ getNameProperty(tableName, true) +"> findByFilter("+ getNameProperty(tableName, true) +"Filter "+ getNameProperty(tableName, false) +"Filter);" + ln);
			for(Relations relation : getSingleRelation( tableName))
			{
				if(!tableName.equals(relation.getREFERENCED_TABLE_NAME()))
				{
					myWriter.write("	public void set" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "Relation(" + getNameProperty(tableName, true) +" " + getNameProperty(tableName, false) + ");" + ln);
					myWriter.write("	public void set" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + "ListRelation(List<" + getNameProperty(tableName, true) +"> list" + getNameProperty(tableName, true) + ");" + ln);
				}
			}
			for(Relations relation : findListRelation)
			{
				if(!tableName.equals(relation.getTABLE_NAME()))
				{
					myWriter.write("	public void setList" + getNameProperty(relation.getTABLE_NAME(), true) + "Relation ("  + getNameProperty(tableName, true) + " " + getNameProperty(tableName, false) + ");" + ln);
					myWriter.write("	public void setList" + getNameProperty(relation.getTABLE_NAME(), true) + "List"  + getNameProperty(tableName, true) + "Relation (List<"  + getNameProperty(tableName, true) + "> list"  + getNameProperty(tableName, true) + ");" + ln);
					if(checkManyToMany(relation.getTABLE_NAME(), tableName,0))
					{
						var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
						if(listForeignKey.size() == 2)
						{
							var entity = getNameEntity(tableName, relation.getTABLE_NAME());
							if(entity != "")
							{							
								myWriter.write("	public void setList" + getNameProperty(entity, true) + "Relation("  + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + " " +  getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + ");" + ln);
								myWriter.write("	" + ln);
							}
						}
					}
				}
			}			
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesResponseListAngular(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(10) + getNameProperty(tableName, true) + "ResponseList.ts";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("import { Pager } from \"../../filter/Pager\";" + ln);
			myWriter.write("import { " + getNameProperty(tableName, true) + "Response } from \"../" + getNameProperty(tableName, true) + "Response\";" + ln);
			myWriter.write("export class " + getNameProperty(tableName, true) + "ResponseList" + ln);
			myWriter.write("{" + ln);
			myWriter.write("	list" + getNameProperty(tableName, true) + "Response : " + getNameProperty(tableName, true) + "Response[];" + ln);
			myWriter.write("	pager : Pager;" + ln);
			myWriter.write("	message  : String;" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{

		}
	}
	private static void createFilesResponseSaveAngular(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{			
			String strpath = files.get(12) + getNameProperty(tableName, true) + "ResponseSave.ts";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("import { " + getNameProperty(tableName, true) + "Response } from \"../" + getNameProperty(tableName, true) + "Response\";" + ln);
			myWriter.write("import { " + getNameProperty(tableName, true) + "ResponseError } from \"../error/" + getNameProperty(tableName, true) + "ResponseError\";" + ln);
			myWriter.write("export class " + getNameProperty(tableName, true) + "ResponseSave" + ln);
			myWriter.write("{" + ln);
			myWriter.write("	" + getNameProperty(tableName, false) + "Response : " + getNameProperty(tableName, true) + "Response;" + ln);
			myWriter.write("	" + getNameProperty(tableName, false) + "ResponseError : " + getNameProperty(tableName, true) + "ResponseError;" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{
		}
	}
	private static void createFilesResponseAngular(EntityName entitiName,String prefix,String prefix2, Integer index )
	{
		String tableName = entitiName.getName();
		try
		{
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			String strpath = files.get(index) + getNameProperty(tableName, true) + prefix + ".ts";
			FileWriter myWriter = new FileWriter(strpath);
			
			myWriter.write("import { " + getNameProperty(tableName, true) + getNameProperty(prefix2, true) + " } from \"../" + prefix2 + "/"+ getNameProperty(tableName, true)+ getNameProperty(prefix2, true) +"\";" + ln);
			List<String> listImport = new ArrayList<>();
			listImport.add(tableName);
			for(Relations relation : getSingleRelation( tableName))
			{
				if(listImport.stream().filter(str -> str.equals(relation.getREFERENCED_TABLE_NAME())).count() == 0)
				{
					listImport.add(relation.getREFERENCED_TABLE_NAME());
					myWriter.write("import { " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + prefix + " } from \"./" + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true) + prefix + "\";" + ln);
				}
			}
			
			for(Relations relation : findListRelation)
			{
				if(listImport.stream().filter(str -> str.equals(relation.getTABLE_NAME())).count() == 0)
				{
					listImport.add(relation.getTABLE_NAME());
					myWriter.write("import { " + getNameProperty(relation.getTABLE_NAME(), true) + prefix + " } from \"./" + getNameProperty(relation.getTABLE_NAME(), true) + prefix + "\";" + ln);
				}
			}
			myWriter.write("export class " + getNameProperty(tableName, true) + prefix + " " + ln);
			myWriter.write("{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				myWriter.write("	" +  getNameProperty(property.getField(), false) + " : "  + getTypePropertyAngular(property.getType())+  ";" + ln);
			}
			listImport = new ArrayList<>();
			for(Relations relation : getSingleRelation( tableName))
			{
				if(listImport.stream().filter(str -> str.equals(relation.getREFERENCED_TABLE_NAME())).count() == 0)
				{
					listImport.add(relation.getREFERENCED_TABLE_NAME());
					myWriter.write("	" +  getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + " : "  + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true)+ prefix +  " = new " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), true)+ "();" + ln);
				}
			}
			var listImport1 = new ArrayList<>();
			for(Relations relation : findListRelation)
			{
				if(listImport1.stream().filter(str -> str.equals(relation.getTABLE_NAME())).count() == 0)
				{
					listImport1.add(relation.getTABLE_NAME());
					myWriter.write("	list" +  getNameProperty(relation.getTABLE_NAME(), true) + " : "  + getNameProperty(relation.getTABLE_NAME(), true)+ prefix +  "[] = [];" + ln);
					if(checkManyToMany(relation.getTABLE_NAME(), tableName,0))
					{
						var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
						if(listForeignKey.size() == 2)
						{
							var entity = getNameEntity(tableName, relation.getTABLE_NAME());
							if(entity != "" && listImport1.stream().filter(str -> str.equals(relation.getTABLE_NAME())).count() == 0)
							{
								myWriter.write("	list" +  getNameProperty(relation.getTABLE_NAME(), true) + " : "  + getNameProperty(relation.getTABLE_NAME(), true)+ prefix +  "[] = [];" + ln);
							}
						}
					}
				}
				else
				{
					var s = listImport1;
				}
			}
			myWriter.write("	constructor() " + ln);
			myWriter.write("	{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				myWriter.write("		this." +  getNameProperty(property.getField(), false) + "  = null;"   + ln);
			}
			for(Relations relation : getSingleRelation( tableName))
			{
				myWriter.write("		this." +  getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) + " = new " + getNameProperty(relation.getREFERENCED_TABLE_NAME(), false) +  "();" + ln);
			}
			for(Relations relation : findListRelation)
			{
				myWriter.write("		this.list" +  getNameProperty(relation.getTABLE_NAME(), true) + " = [];" + ln);
				if(checkManyToMany(relation.getTABLE_NAME(), tableName,0))
				{
					var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
					if(listForeignKey.size() == 2)
					{
						var entity = getNameEntity(tableName, relation.getTABLE_NAME());
						if(entity != "")
						{
							myWriter.write("		this.list" +  getNameProperty(relation.getTABLE_NAME(), true) + " = [];"  + ln);
						}
					}
				}
			}
			myWriter.write("	}" + ln);
			myWriter.write("	static getKeys(): string[] " + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		const keys: string[] = [];" + ln);
			myWriter.write("		let " + getNameProperty(tableName, false) + "Res = new "  + getNameProperty(tableName, true) +  prefix + "();" + ln);
			myWriter.write("		for (const key in " + getNameProperty(tableName, false) + "Res) " + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			keys.push(key);" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("		return keys;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	static to"  + getNameProperty(prefix2, true) +  "(" + getNameProperty(tableName, false) + "): " + getNameProperty(tableName, true) + getNameProperty(prefix2, true) +  ln);
			myWriter.write("	{" + ln);
			myWriter.write("		let " + getNameProperty(tableName, false)  + getNameProperty(prefix2, true) +  " = new " + getNameProperty(tableName, true)  + getNameProperty(prefix2, true) + "();" + ln);
			myWriter.write("		for (const attr of " + getNameProperty(tableName, true)  + getNameProperty(prefix2, true) +  ".getKeys()) " + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			" + getNameProperty(tableName, false)  + getNameProperty(prefix2, true) +  "[attr] = " + getNameProperty(tableName, false) + "[attr];" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("		return " + getNameProperty(tableName, false)  + getNameProperty(prefix2, true) +  ";" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	to" + getNameProperty(prefix2, true) + "(): " + getNameProperty(tableName, true)  + getNameProperty(prefix2, true) +  "" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		let " + getNameProperty(tableName, false)  + getNameProperty(prefix2, true) +  " = new "+ getNameProperty(tableName, true) + getNameProperty(prefix2, true) + "();" + ln);
			myWriter.write("		for (const attr of " + getNameProperty(tableName, true) + getNameProperty(prefix2, true) + ".getKeys()) " + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			" + getNameProperty(tableName, false) + getNameProperty(prefix2, true) + "[attr] = this[attr];" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("		return " + getNameProperty(tableName, false) + getNameProperty(prefix2, true) + ";" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("	to" + getNameProperty(prefix, true) + "(" + getNameProperty(tableName, false) + getNameProperty(prefix, true) + " : " + getNameProperty(tableName, true) + prefix + "): " + getNameProperty(tableName, true) + prefix + " " + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		let res = new " + getNameProperty(tableName, true) + getNameProperty(prefix, true) + "()" + ln);
			myWriter.write("		for (const attr of " + getNameProperty(tableName, true) + getNameProperty(prefix, true) + ".getKeys()) " + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			res[attr] = " + getNameProperty(tableName, false) + getNameProperty(prefix, true) + "[attr];" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("		return res;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.write("	" + ln);
			myWriter.close();
		}
		catch(Exception e){}
	}
	private static void createFilesResponseErrorAngular(EntityName entitiName)
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(11) + getNameProperty(tableName, true) + "ResponseError.ts";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("export class " + getNameProperty(tableName, true) + "ResponseError" + ln);
			myWriter.write("{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				myWriter.write("	" +  getNameProperty(property.getField(), false) + " : string;" + ln);
			}
			myWriter.write("	public message: string;" + ln);
			myWriter.write("	public haveError: boolean;" + ln);
			myWriter.write("	constructor()" + ln);
			myWriter.write("	{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				myWriter.write("		this." +  getNameProperty(property.getField(), false) + " = \"\";" + ln);
			}
			myWriter.write("		this.message = \"\";" + ln);
			myWriter.write("		this.haveError = false;" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{
		}
	}
	private static void createFilesFilterAngular(EntityName entitiName)
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(15) + getNameProperty(tableName, true) + "Filter.ts";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("import { BaseFilter } from \"./BaseFilter\";" + ln);
			myWriter.write("import { Pager } from \"./Pager\";" + ln);
			myWriter.write("export class " + getNameProperty(tableName, true) + "Filter" + ln);
			myWriter.write("{" + ln);
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				myWriter.write("	" +  getNameProperty(property.getField(), false) + " : BaseFilter;" + ln);
			}
			myWriter.write("	public pager: Pager;" + ln);
			myWriter.write("	constructor()" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		this.pager = new Pager()" + ln);
			myWriter.write("	}" + ln);
			myWriter.write("}" + ln);
			myWriter.close();
		}		
		catch(Exception e)
		{
		}
	}
	private static void createFilesServiceAngular(EntityName entitiName)
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(14) + getNameProperty(tableName, false) + ".service.ts";
			FileWriter myWriter = new FileWriter(strpath);
			
			myWriter.write("import { HttpClient } from '@angular/common/http';" + ln);
			myWriter.write("import { Injectable } from '@angular/core';" + ln);
			myWriter.write("import { GeneralService } from './general.service';" + ln);
			myWriter.write("import { catchError } from 'rxjs/operators';" + ln);
			myWriter.write("import { Observable } from 'rxjs/internal/Observable';" + ln);
			myWriter.write("import { of } from 'rxjs';" + ln);
			
			myWriter.write("import { " + getNameProperty(tableName, true) + "Request } from '../payload/request/" + getNameProperty(tableName, true) + "Request';" + ln);
			myWriter.write("import { " + getNameProperty(tableName, true) + "Filter } from '../payload/filter/" + getNameProperty(tableName, true) + "Filter';" + ln);
			myWriter.write("import {  "+ getNameProperty(tableName, true) + "Response } from '../payload/response/" + getNameProperty(tableName, true) + "Response';" + ln);
			myWriter.write("import { " + getNameProperty(tableName, true) + "ResponseList } from '../payload/response/list/" + getNameProperty(tableName, true) + "ResponseList';" + ln);
			myWriter.write("import { MatDialogRef } from '@angular/material/dialog';" + ln);
			myWriter.write("import { DialogComponent } from '../shared/utility/dialog/dialog.component';" + ln);
			myWriter.write("import { " + getNameProperty(tableName, true) + "ResponseSave } from '../payload/response/save/" + getNameProperty(tableName, true) + "ResponseSave';" + ln);
			myWriter.write("@Injectable({ providedIn: 'root'})" + ln);
			myWriter.write("export class " + getNameProperty(tableName, true) + "Service " + ln);
			myWriter.write("{" + ln);
			myWriter.write("	constructor(private http: HttpClient,private generalService : GeneralService) { }" + ln);			
			myWriter.write("	modeModal = false;" + ln);
			myWriter.write("	public url = this.generalService.url + \"/" + getNameProperty(tableName, false) + "/\";" + ln);
			myWriter.write("	id" + getNameProperty(tableName, true) + " : number;" + ln);
			myWriter.write("	dialogRef" + getNameProperty(tableName, true) + " : MatDialogRef<any, any>;" + ln);
			myWriter.write("	erreur = ()=>{this.generalService.showSpinner = false;return of([]);}" + ln);
			
			myWriter.write("	findById(id : number, error?) : Observable<" + getNameProperty(tableName, true) + "Response>" + ln);
			myWriter.write("	{" + ln);			
			myWriter.write("		return this.http.get<any>(this.url + \"findById/\" + id, {headers : this.generalService.headers} ).pipe(catchError(error || this.generalService.error));" + ln);
			myWriter.write("	}" + ln);
		
			myWriter.write("	liste" + getNameProperty(tableName, true) + "(" + getNameProperty(tableName, false) + "Filter :" + getNameProperty(tableName, true) + "Filter, error?): Observable<" + getNameProperty(tableName, true) + "ResponseList>" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return this.http.post<any>(this.url + \"findByFilter\" , " + getNameProperty(tableName, false) + "Filter, {headers : this.generalService.headers}).pipe(catchError(error || this.generalService.error));" + ln);
			myWriter.write("	}" + ln);	

			myWriter.write("	signin(" + getNameProperty(tableName, false) + " : " + getNameProperty(tableName, true) + "Request,error?): Observable<" + getNameProperty(tableName, true) + "Response>" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return this.http.post<any>(this.url + \"signin\"," + getNameProperty(tableName, false) + " ).pipe(catchError(error || this.generalService.erreur));" + ln);
			myWriter.write("	}" + ln);

			myWriter.write("	save(" + getNameProperty(tableName, false) + " : " + getNameProperty(tableName, true) + "Request, error?) : Observable<" + getNameProperty(tableName, true) + "ResponseSave>" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return this.http.put<any>(this.url + \"save\" , " + getNameProperty(tableName, false) + ", {headers : this.generalService.headers}).pipe(catchError(error || this.generalService.error));" + ln);
			myWriter.write("	}" + ln);	

			myWriter.write("	savePassword(" + getNameProperty(tableName, false) + " : " + getNameProperty(tableName, true) + "Request, error?) : Observable<" + getNameProperty(tableName, true) + "ResponseSave>" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		return this.http.put<any>(this.url + \"savePassword\" , " + getNameProperty(tableName, false) + ", {headers : this.generalService.headers}).pipe(catchError(error || this.generalService.error));" + ln);
			myWriter.write("	}" + ln);
    
			myWriter.write("	delete(event, id : number,fn, error?)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		let btnDel = event.component.icon == \"delete\";" + ln);
			myWriter.write("		const dialogRef = this.generalService.dialog.open(DialogComponent,{data : this.generalService.getDataDelete(btnDel)})" + ln);
			myWriter.write("		dialogRef.afterClosed().subscribe(result => " + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			if(result && result == \"ok\")" + ln);
			myWriter.write("			{" + ln);
			myWriter.write("				this.http.delete<any>(this.url + \"delete/\" + id, {headers : this.generalService.headers}).pipe(catchError(error? error: ()=>{ return of([]); })).subscribe(fn);" + ln);
			myWriter.write("			}" + ln);
			myWriter.write("		 });" + ln);
			myWriter.write("	}" + ln);

			
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
		createFilesFilter(entitiName);
		createFilesResponse(entitiName);
		createFilesResponseFindById(entitiName);
		createFilesResponseList(entitiName);
		createFilesResponseSave(entitiName);
		createFilesError(entitiName);
		createFilesRequest(entitiName);
		createFilesRepository(entitiName);
		createFilesServiceImp(entitiName);		
		createFilesService(entitiName);		
		// pour angualr
		createFilesResponseAngular(entitiName, "Response", "request", 9);		
		createFilesResponseAngular(entitiName, "Request", "response", 13);
		createFilesResponseListAngular(entitiName);
		createFilesResponseErrorAngular(entitiName);
		createFilesResponseSaveAngular(entitiName);
		createFilesFilterAngular(entitiName);
		createFilesServiceAngular(entitiName);
		
	}
	private static void createFolderProgect(String tableName)
	{
		try
		{ 
			for(String fileName : files)
			{
				String strpath = fileName + getNameProperty(tableName, true) + ".java";
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
	private static List<Relations> getSingleRelation(String tableName)
	{
		return listRelation.stream().filter(r -> r.getTABLE_NAME().equals(tableName)).collect(Collectors.toList());
	}
	private  static String getNameProperty(String propetyName, Boolean isClass)
	{
		String name = "";
		var tab = propetyName.split("_");
		var compte = 0;
		for(String str : tab)
		{
			if(Boolean.TRUE.equals(isClass))
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
			myType = "Integer";
		else if(type.indexOf("varchar")>=0 || type.indexOf("text")>=0)
			myType = "String";
		else if(type.equals("double"))
			myType = "Double";
		else if(type.equals("date") || type.equals("datetime"))
			myType = "LocalDateTime";
		else if(type.equals("tinyint") || type.equals("tinyint(1)"))
			myType = "Boolean";
		else if(type.equals("point"))
			myType = "Point";
		else
			myType = type;
		return myType;
	}
	private  static String getTypePropertyAngular(String type)
	{
		var myType = "";
		if(type.equals("int"))
			myType = "number | null";
		else if(type.indexOf("varchar")>=0 || type.indexOf("text")>=0)
			myType = "string | null";
		else if(type.equals("double"))
			myType = "number | null";
		else if(type.equals("date") || type.equals("datetime"))
			myType = "Date | null";
		else if(type.equals("tinyint") || type.equals("tinyint(1)"))
			myType = "boolean";
		else
			myType = type;
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
	private static String getFieldPrimeryKey(EntityName entitiName)
	{
		try
		{
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				if(property.getKey().equals("PRI"))
				{
					return (property.getField());
				}
			}
		}
		catch(Exception e){}
		return "";
	}
	private static boolean checkProperty(EntityName entitiName, String propertyName)
	{
		try
		{
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				if(property.getField().equals(propertyName))
				{
					return true;
				}
			}
		}
		catch(Exception e){}
		return false;
	}
	private static boolean checkManyToMany(String entitiName1, String entitiName2, int index)
	{
		var isManyToMany = true;
		var tabEntity = entitiName1.split("_");
		if(tabEntity.length == 2 && entitiName2.equals(tabEntity[index]))
		{
			for(String entity : tabEntity)
			{
				if(listTablesName.stream().filter(e -> e.equals(entity)).count()==0)
					isManyToMany = false;				
			}
		}
		else
			isManyToMany = false;
		return isManyToMany;
	}
	private static String getNameEntity(String nameEntity1, String nameEntity2)
	{
		var tabEntity = nameEntity2.split("_");
		if(!tabEntity[0].equals(nameEntity1))
		{
			return tabEntity[0];
		}
		else if(tabEntity.length>1)
			return tabEntity[1];
		else
			return "";
		
	}
	private static List<String> getListForeignKey(String entitiName)
	{
		List<String> listForeignKey = new ArrayList<String>();
		var tabEntity = entitiName.split("_");
		if(tabEntity.length == 2)
		{
			var listFindRelation = listRelation.stream().filter(relation -> relation.getTABLE_NAME().equals(entitiName)).collect(Collectors.toList());
			for(Relations relation : listFindRelation)
			{
				if(relation.getREFERENCED_TABLE_NAME().equals(tabEntity[0]) || relation.getREFERENCED_TABLE_NAME().equals(tabEntity[1]))
				{
					listForeignKey.add(relation.getCOLUMN_NAME());
				}
			}
		}
		return listForeignKey;
	}
}