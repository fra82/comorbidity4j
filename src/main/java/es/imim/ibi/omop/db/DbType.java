package es.imim.ibi.omop.db;

public class DbType {
	public static DbType	MYSQL		= new DbType("mysql");
	public static DbType	MSSQL		= new DbType("mssql");
	public static DbType	PDW			= new DbType("pdw");
	public static DbType	ORACLE		= new DbType("oracle");
	public static DbType	POSTGRESQL	= new DbType("postgresql");
	public static DbType	MSACCESS	= new DbType("msaccess");
	public static DbType	REDSHIFT	= new DbType("redshift");
	public static DbType	TERADATA	= new DbType("teradata");

	private enum Type {
		MYSQL, MSSQL, PDW, ORACLE, POSTGRESQL, MSACCESS, REDSHIFT, TERADATA
	};

	private Type type;

	public DbType(String type) {
		this.type = Type.valueOf(type.toUpperCase());
	}

	public boolean equals(Object other) {
		if (other instanceof DbType && ((DbType) other).type == type)
			return true;
		else
			return false;
	}
}
