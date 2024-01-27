package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;
// this class reads and writes to the SQL database
// take the values that were collected from the user that are contained in a project table using JDBC method calls
public class ProjectDao extends DaoBase{
	//add constants for table names(its a good idea to add constants for values used over and over again in classes) constants are specified by static final keywords
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	public Project insertProject(Project project) {
		// first step in saving project details is to create an SQL statement
		// @formatter:off
		String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		// now obtain a connection and assign it a variable type connection conn
		try(Connection conn = DbConnection.getConnection()) {
			// start a transaction
			startTransaction(conn);
			//obtain a PreparedStatement object from the connection object
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				//set the project details as parameters in the preparedStatement object
				//** more explanation here**
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				// now save the project details
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
			} // end of inner try
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			} // end of inner catch
		} // end of outer try
		catch(SQLException e) {
			throw new DbException(e);
		} // end of outer catch
		
	} // end of insertProject method

}
