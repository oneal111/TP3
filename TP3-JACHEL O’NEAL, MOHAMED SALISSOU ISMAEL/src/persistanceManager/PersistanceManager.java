package persistanceManager;


import persistanceManager.Annotation.*;
import persistanceManager.Table.Etudiant;

import java.beans.Beans;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class PersistanceManager {

	private final String name;
	private final String host;
	private final int port;
	private final String user;
	private final String passwd;

	private Connection conn = null;

	public PersistanceManager(String name, String user, String passwd) {
		this.name = name;
		host = "localhost";
		port = 5432;
		this.user = user;
		this.passwd = passwd;
	}

	public PersistanceManager(String name, String host, int port, String user, String passwd) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.user = user;
		this.passwd = passwd;
	}

	public void connect() {
		try {
			conn = DriverManager.getConnection(getURL(), user, passwd);
			System.out.println("Connexion a  la base de donnée " + host + ":" + port + "/" + name + " accomplie");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
	}

	public void close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	public Connection getConnection() {
		return conn;
	}

	public int getNextIndex(String seqNum) throws SQLException, PersistentException {
		Statement stat = conn.createStatement();
		stat.execute("SELECT nextval('" + seqNum + "'::regclass)");
		ResultSet rs = stat.getResultSet();
		if (rs.next()) {
			return rs.getInt(1) + 1;
		}
		throw new PersistentException("Impossible de récupérer l'index courant : " + seqNum);
	}

	public <T> ArrayList<T> retrieve(Class<T> beanClass, String sql) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		ArrayList<T> beans = new ArrayList<>();	
		Statement stat = conn.createStatement();
		System.out.println("SQL : " + sql);
		ResultSet result = stat.executeQuery(sql);	

		T bean;	
		T tmpBean;	
		Annotation anno;	
		String className;	
		String table;	
		String primaryKey;	
		int column;	
		int id;	
		String sqlRequest;	

		while (result.next()) {
			bean = beanClass.newInstance();	
			Field[] fields = beanClass.getDeclaredFields();
			for (Field f : fields) {
				f.setAccessible(true); 
				if (f.getAnnotations().length != 0) {	
					anno = f.getAnnotations()[0];
					if (anno instanceof Ignore) {	
						continue;
					}
					if (anno instanceof BeansList) {	
						className = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0].getTypeName();	
						table = Class.forName(className).getAnnotation(Bean.class).table();
						primaryKey = beanClass.getAnnotation(Bean.class).pK();
						id = (int) result.getObject(result.findColumn(primaryKey));
						sqlRequest = "SELECT * FROM " + table + " WHERE " + primaryKey + " = " + id;
						f.set(bean, retrieve(Class.forName(className), sqlRequest));
					} else if (anno instanceof BeanInterne) {	
						className = f.getType().getName();
						table = Class.forName(className).getAnnotation(Bean.class).table();
						primaryKey = Class.forName(className).getAnnotation(Bean.class).pK();
						id = (int) result.getObject(result.findColumn(primaryKey));
						if ((tmpBean = checkExistence((Class<T>) Class.forName(className), id)) != null) {	
							f.set(bean, tmpBean);
						} else {
							sqlRequest = "SELECT * FROM " + table + " WHERE " + primaryKey + " = " + id;
							f.set(bean, retrieve(Class.forName(className), sqlRequest).get(0));
						}
					}
				} else if (f.getType().isPrimitive() || f.getType().isInstance(new String())) {	
					column = result.findColumn(f.getName());	
					f.set(bean, result.getObject(column));	
				}
			}
			String fieldId = beanClass.getAnnotation(Bean.class).pK();
			Field idField = beanClass.getDeclaredField(fieldId);
			idField.setAccessible(true);
			if ((tmpBean = checkExistence(beanClass, idField.getInt(bean))) != null) {	
				beans.add(tmpBean);
			} else {
				beans.add(bean);
			}
		}
		result.close();
		stat.close();
		return beans;
	}

	private <T> T checkExistence(Class<T> beanClass, int id) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InstantiationException {
		String fieldId = beanClass.getAnnotation(Bean.class).pK();
		String fieldList = beanClass.getAnnotation(Bean.class).instancesList();
		Field idField = beanClass.getDeclaredField(fieldId);
		idField.setAccessible(true);
		Field listField = beanClass.getDeclaredField(fieldList);
		ArrayList<T> list = (ArrayList<T>) listField.get(null);
		System.out.print("TEST : " + beanClass.getSimpleName() + " " + id);
		for (T bean : list) {	
			if (idField.getInt(bean) == id) {	
				System.out.println(" existe deja  !");
				return bean;
			}
		}
		System.out.println(" n'existe pas !");
		return null;
	}

	private String getURL() {
		return "jdbc:postgresql://" + host + ":" + port + "/" + name;
	}

	public <T> int bulkInsert(List<T> beans) throws SQLException, IllegalArgumentException, IllegalAccessException, PersistentException {
		int num = 0;
		for (T bean : beans) {
			if (!checkExistence(bean)) {
			}
		}
		return num;
	}

	public <T> void insert(T bean) throws SQLException, IllegalArgumentException, IllegalAccessException, PersistentException {
		String sqlUpdate = "INSERT INTO etudiant (etudiantid, lname, age, fname) VALUES ( ";	
		sqlUpdate += Etudiant.getEtudiantid() + " , "+ Etudiant.getFname() +" , "+ Etudiant.getLname() +" , " +Etudiant.getAge() + " )";
		System.out.println("SQL : " + sqlUpdate);
		Statement stat = conn.createStatement();
		conn.setAutoCommit(false);
		stat.execute(sqlUpdate);
	}




	private <T> void setPrimaryKeyValue(T bean, String primaryKey, int primaryKeyValue) throws IllegalArgumentException, IllegalAccessException {
		for (Field f : bean.getClass().getDeclaredFields()) {
			if (f.getName().equals(primaryKey)) {
				f.setAccessible(true);
				f.setInt(bean, primaryKeyValue);
			}
		}
	}

	private <T> boolean checkExistence(T bean) throws SQLException, IllegalArgumentException, IllegalAccessException, PersistentException {
		String primaryKey = bean.getClass().getAnnotation(Bean.class).pK();
		for (Field f : bean.getClass().getDeclaredFields()) {
			if (f.getName().equals(primaryKey)) {
				f.setAccessible(true);
				return f.getInt(bean) != 0;
			}
		}
		throw new PersistentException("Le champ de la clé primaire est introuvable !");
	}
}
