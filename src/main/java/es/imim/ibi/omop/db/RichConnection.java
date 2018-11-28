package es.imim.ibi.omop.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.imim.ibi.comorbidity4j.analysis.ComorbidityMiner;

public class RichConnection {
	
	private static final Logger logger = LoggerFactory.getLogger(ComorbidityMiner.class);
	
	private Connection				connection;
	private boolean					verbose				= false;
	private DbType					dbType;

	public RichConnection(String server, String domain, String user, String password, DbType dbType) {
		this.connection = DBConnector.connect(server, domain, user, password, dbType);
		this.dbType = dbType;
	}

	/**
	 * Execute the given SQL statement.
	 * 
	 * @param sql
	 */
	public ResultSet executeReadQuery(String sql)  throws SQLException {
		Statement stmt = null;
	    
		try {
	        stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(sql);
	        return rs;
	    } catch (SQLException e ) {
	    	logger.error("Exception while executing query " + ((sql != null) ? sql : "NULL") + " - " + e.getMessage());
	    	if(this.verbose) {
	    		e.printStackTrace();
	    	}
	    } finally {
	        if (stmt != null) { stmt.close(); }
	    }
	    
	    return null;
	}

	/**
	 * Close the connection to the database.
	 */
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check verbose output
	 * 
	 * @return
	 */
	public boolean isVerbose() {
		return verbose;
	}
	
	/**
	 * Set verbose output
	 * 
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	
}
