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
			myWriter.write("import javax.persistence.Table;" + ln);
			myWriter.write("import javax.persistence.Transient;" + ln);
			myWriter.write("import java.util.List;" + ln);
			myWriter.write("import org.springframework.data.geo.Point;" + ln);
			myWriter.write("import java.time.LocalDateTime;" + ln);
			myWriter.write("import lombok.Data;" + ln);
			myWriter.write("import javax.validation.constraints.NotNull;" + ln);
			myWriter.write("@Data" + ln);
			

			myWriter.write("@Entity" + ln);
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
				else if(!property.getKey().equals("MUL"))
				{
					myWriter.write("	@Column(name=\"" + property.getField() + "\")" + ln);
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}
			}
			for(String relation : getSingleRelation( tableName))
			{
				myWriter.write("	@Transient" + ln);
				myWriter.write("	private " + getNameProperty(relation, true) + " " + getNameProperty(relation, false) + ";" +ln);
			}
			var copyListRelation = listRelation;
			var findListRelation = copyListRelation.stream().filter(relation -> relation.getREFERENCED_TABLE_NAME().equals(tableName)).collect(Collectors.toList());
			for(Relations relation : findListRelation)
			{
				myWriter.write("	@Transient" + ln);
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
				if(property.getKey().equals("PRI") || !property.getKey().equals("MUL"))
				{
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}				
			}
			for(String relation : getSingleRelation( tableName))
			{
				myWriter.write("	private " + getNameProperty(relation, true) + "Response " + getNameProperty(relation, false) + ";" +ln);
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
			myWriter.write("	private boolean have_error;" + ln);
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
			for(EntityProperty property : entitiName.getListEntityProperty())
			{
				if(property.getKey().equals("PRI") || !property.getKey().equals("MUL"))
				{
					myWriter.write("	private " + getTypeProperty(property.getType())+ " " + getNameProperty(property.getField(), false)+ ";" + ln) ;
				}				
			}
			for(String relation : getSingleRelation( tableName))
			{
				myWriter.write("	private " + getNameProperty(relation, true) + "Request " + getNameProperty(relation, false) + ";" +ln);
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
			myWriter.write("		if("+ getNameProperty(tableName, false) +"ResponseError.isHave_error())" + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			return new "+ getNameProperty(tableName, true) +"ResponseSave("+ getNameProperty(tableName, false) +"ResponseError);" + ln);
			myWriter.write("		}" + ln);
			myWriter.write("		else" + ln);
			myWriter.write("		{" + ln);
			myWriter.write("			try" + ln);
			myWriter.write("			{" + ln);
			myWriter.write("				"+ getNameProperty(tableName, true) +" "+ getNameProperty(tableName, false) +" = "+ getNameProperty(tableName, false) +"Repository.save(ObjectMapperUtility.map("+ getNameProperty(tableName, false) +"Request, "+ getNameProperty(tableName, true) +".class));" + ln);
			myWriter.write("				return  new "+ getNameProperty(tableName, true) +"ResponseSave(ObjectMapperUtility.map("+ getNameProperty(tableName, false) +", "+ getNameProperty(tableName, true) +"Response.class));" + ln);
			myWriter.write("			}" + ln);
			myWriter.write("			catch(Exception e)" + ln);
			myWriter.write("			{" + ln);
			myWriter.write("				"+ getNameProperty(tableName, false) +"ResponseError.setHave_error(true);" + ln);
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
			myWriter.write("		"+ getNameProperty(tableName, false) +"ResponseError.setHave_error(false);" + ln);
			myWriter.write("		if(Utility.isEmpty("+ getNameProperty(tableName, false) +"Request.get"+ getNameProperty(getFieldPrimeryKey(entitiName), true) +"()) )"+ ln);
			myWriter.write("		{"+ ln);
			myWriter.write("			"+ getNameProperty(tableName, false) +"Request.set"+ getNameProperty(getFieldPrimeryKey(entitiName), true) +"(-1);"+ ln);
			myWriter.write("		}"+ ln);
			myWriter.write("		//if(Utility.isEmpty("+ getNameProperty(tableName, false) +"Request.get()) )" + ln);
			myWriter.write("		//{" + ln);
			myWriter.write("				//"+ getNameProperty(tableName, false) +"ResponseError.setHave_error(true);" + ln);
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
}