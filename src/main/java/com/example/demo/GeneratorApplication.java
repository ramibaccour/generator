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
	private static String packageName = "marketplace";
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
		String path = "C:\\geneation\\";
		String pathController = path + "controller\\";
		String pathEntity =     path + "entity\\";
		String pathResponse =   path + "payload\\response\\";
		String pathError =     path + "payload\\response\\error\\";
		String pathRequest =    path + "payload\\request\\";
		String pathRepository = path + "repository\\";
		String pathService =    path + "service\\";
		
		files.add(pathController);
		files.add(pathEntity);
		files.add(pathResponse);
		files.add(pathError);
		files.add(pathRequest);
		files.add(pathRepository);
		files.add(pathService);
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
			myWriter.write("import javax.persistence.JoinColumn;" + ln);
			myWriter.write("import javax.persistence.JoinTable;" + ln);
			myWriter.write("import javax.persistence.Table;" + ln);
			myWriter.write("import javax.persistence.FetchType;" + ln);	
			myWriter.write("import java.util.Set;" + ln);
			myWriter.write("import java.util.HashSet;" + ln);
			myWriter.write("import javax.persistence.ManyToOne;" + ln);
			myWriter.write("import javax.persistence.OneToMany;" + ln);
			myWriter.write("import javax.persistence.ManyToMany;" + ln);	
			myWriter.write("import java.util.List;" + ln);
			myWriter.write("import org.springframework.data.geo.Point;" + ln);
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
				myWriter.write("	private List<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + "> list" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ";" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
			}
			// for(Relations relation : findListRelation)
			// {
			// 	if(checkManyToMany(relation.getTABLE_NAME(), tableName))
			// 	{
					
			// 		//name = "users_roles",  joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id")
			// 		var listForeignKey = getListForeignKey(relation.getTABLE_NAME());
			// 		if(listForeignKey.size() == 2)
			// 		{
			// 			// myWriter.write("	@ManyToMany(fetch = FetchType.EAGER)" + ln);
			// 			// myWriter.write("	@JoinTable(name = \""+ relation.getTABLE_NAME() +"\", joinColumns = @JoinColumn(name = \"" + listForeignKey.get(0) + "\"), inverseJoinColumns = @JoinColumn(name = \""+  listForeignKey.get(1) +"\"))" + ln);
			// 			myWriter.write("	@Transient" + ln);
			// 			myWriter.write("	private List<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + "> list" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ";" +ln);// " = new HashSet<" + getNameProperty(getNameEntity(tableName, relation.getTABLE_NAME()), true) + ">();" +ln);
			// 		}
						
			// 	}
			// 	// else
			// 	// {
			// 	// 	myWriter.write("	@OneToMany(fetch = FetchType.EAGER)" + ln);
			// 	// 	myWriter.write("	@JoinColumn(name = \""+ relation.getCOLUMN_NAME() +"\")" + ln);
			// 	// 	myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);
			// 	// }				
				
			// }
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
			String strpath = files.get(2) + getNameProperty(tableName, true) + "Response.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".payload.response;" + ln);
			myWriter.write("import lombok.AllArgsConstructor;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import java.util.List;" + ln);
			myWriter.write("import java.time.LocalDateTime;" + ln);
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("import org.springframework.data.geo.Point;" + ln);
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
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			for(Relations relation : findListRelation)
			{
				myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "Response> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);
			}
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
			myWriter.write("import lombok.NoArgsConstructor;" + ln);
			myWriter.write("@AllArgsConstructor" + ln);
			myWriter.write("@NoArgsConstructor" + ln);
			myWriter.write("@Data" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"ResponseList" + ln);	
			myWriter.write("{" + ln);
			myWriter.write("	private List<"+ getNameProperty(tableName, true) +"Response> list"+ getNameProperty(tableName, true) +"Response;" + ln);
			myWriter.write("	private Long count;" + ln);
			myWriter.write("	private String message;" + ln);
			myWriter.write("	public "+ getNameProperty(tableName, true) +"ResponseList(List<"+ getNameProperty(tableName, true) +"Response> list"+ getNameProperty(tableName, true) +"Response, Long count)" + ln);
			myWriter.write("	{" + ln);
			myWriter.write("		super();" + ln);
			myWriter.write("		this.list"+ getNameProperty(tableName, true) +"Response = list"+ getNameProperty(tableName, true) +"Response;" + ln);
			myWriter.write("		this.count = count;" + ln);
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
			myWriter.write("import org.springframework.data.geo.Point;" + ln);			
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
			
			for(Relations relation : findListRelation)
			{
				myWriter.write("	private List<" + getNameProperty(relation.getTABLE_NAME(), true) + "Request> list" + getNameProperty(relation.getTABLE_NAME(), true) + ";" +ln);
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
			myWriter.write("import "+packageName+".entity."+ getNameProperty(tableName, true) +";" + ln);
			
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
	private static void createFilesService(EntityName entitiName )
	{
		String tableName = entitiName.getName();
		try
		{
			String strpath = files.get(6) + getNameProperty(tableName, true) + "Service.java";
			FileWriter myWriter = new FileWriter(strpath);
			myWriter.write("package "+packageName+".service;" + ln);
			myWriter.write("import java.util.Optional;" + ln);
			myWriter.write("import org.springframework.beans.factory.annotation.Autowired;" + ln);
			myWriter.write("import org.springframework.stereotype.Service;" + ln);
			
			myWriter.write("import "+packageName+".entity."+ getNameProperty(tableName, true) +";" + ln);
			myWriter.write("import "+packageName+".payload.request."+ getNameProperty(tableName, true) +"Request;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"Response;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"ResponseFindById;" + ln);
			myWriter.write("import "+packageName+".payload.response."+ getNameProperty(tableName, true) +"ResponseSave;" + ln);
			myWriter.write("import "+packageName+".payload.response.error."+ getNameProperty(tableName, true) +"ResponseError;" + ln);
			myWriter.write("import "+packageName+".repository."+ getNameProperty(tableName, true) +"Repository;" + ln);
			myWriter.write("import "+packageName+".security.jwt.JwtUtils;" + ln);
			myWriter.write("import "+packageName+".utility.ObjectMapperUtility;" + ln);
			myWriter.write("import "+packageName+".utility.Utility;" + ln);
			myWriter.write("import org.springframework.security.core.context.SecurityContextHolder;" + ln);
			myWriter.write("import org.springframework.security.authentication.AuthenticationManager;" + ln);
			myWriter.write("import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;" + ln);
			myWriter.write("import org.springframework.security.core.Authentication;" + ln);
			myWriter.write("import org.springframework.security.crypto.password.PasswordEncoder;" + ln);
			myWriter.write("@Service" + ln);
			myWriter.write("public class "+ getNameProperty(tableName, true) +"Service" + ln);
			myWriter.write("{" + ln);
			myWriter.write("	@Autowired" + ln);
			myWriter.write("	JwtUtils jwtUtils;" + ln);			
			myWriter.write("	@Autowired" + ln);
			myWriter.write("	AuthenticationManager authenticationManager;" + ln);
			myWriter.write("	@Autowired" + ln);
			myWriter.write("	PasswordEncoder encoder;" + ln);
			myWriter.write("	@Autowired" + ln);
			myWriter.write("	"+ getNameProperty(tableName, true) +"Repository "+ getNameProperty(tableName, false) +"Repository;" + ln);
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
			myWriter.write("				return  new "+ getNameProperty(tableName, true) +"ResponseSave(\"Erreur d'enregistrement\");" + ln);
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
	private static void createFilesProgect(EntityName entitiName)
	{
		createFilesEntity(entitiName);
		createFilesController(entitiName);
		createFilesResponse(entitiName);
		createFilesResponseFindById(entitiName);
		createFilesResponseList(entitiName);
		createFilesResponseSave(entitiName);
		createFilesError(entitiName);
		createFilesRequest(entitiName);
		createFilesRepository(entitiName);
		createFilesService(entitiName);		
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
	private static List<Relations> getSingleRelation(String tableName)
	{
		List<Relations> tablesname = listRelation.stream().filter(r -> r.getTABLE_NAME().equals(tableName)).collect(Collectors.toList());
//		try
//		{
//			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dataBaseName,"root","root");  
//			Statement stmt = con.createStatement(); 
//			String sql = " SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = \"" +  dataBaseName + "\" AND TABLE_NAME = \"" + tableName + "\" AND REFERENCED_COLUMN_NAME IS NOT NULL;";
//			
//			ResultSet rs = stmt.executeQuery(sql );			
//			
//			while (rs.next())
//			{
//				Relations myRelations = new Relations();
//				myRelations.setCOLUMN_NAME(rs.getString("COLUMN_NAME"));
//				myRelations.setCONSTRAINT_NAME(rs.getString("CONSTRAINT_NAME"));
//				myRelations.setREFERENCED_TABLE_NAME(rs.getString("REFERENCED_TABLE_NAME"));
//				myRelations.setREFERENCED_COLUMN_NAME(rs.getString("REFERENCED_COLUMN_NAME"));
//				myRelations.setTABLE_NAME(rs.getString("TABLE_NAME"));
//				tablesname.add(myRelations);
//			}
//			con.close();
//		}
//		catch(Exception e)
//		{
//			System.out.println(e);
//		}
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
			myType = "Integer";
		else if(type.indexOf("varchar")>=0)
			myType = "String";
		else if(type.equals("double"))
			myType = "double";
		else if(type.equals("date") || type.equals("datetime"))
			myType = "LocalDateTime";
		else if(type.equals("tinyint") || type.equals("tinyint(1)"))
			myType = "boolean";
		else if(type.equals("point"))
			myType = "Point";
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
	private static boolean checkManyToMany(String entitiName1, String entitiName2)
	{
		var isManyToMany = true;
		var tabEntity = entitiName1.split("_");
		if(tabEntity.length == 2 && entitiName2.equals(tabEntity[0]))
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
		else
			return tabEntity[1];
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