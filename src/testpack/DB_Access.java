package testpack;

import java.sql.*;
import java.util.ArrayList;

public class DB_Access {
	private String url = "jdbc:mysql://localhost:3306/test";
	private String driver = "com.mysql.jdbc.Driver";
	private String uname = "root";
	private String upass = "password";
	
	private Connection c;
	private Statement st;
	private PreparedStatement pst;
	
	
	public DB_Access() {
		try {
			Class.forName(driver);
			c = DriverManager.getConnection(url, uname, upass);
			st = c.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public int validateLogin(String un, String up) {
		int uid = -1; // -1 is for invalid user login
		
		String sql = "select uid from t_users where loginname = ? and loginpass = ?";
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, un);
			pst.setString(2, up);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				uid = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return uid;
	}
	
	public String getUserName(int uid) {
		String sql = "select name from t_users where uid = " + uid;
		String uname = "";
		try {
			ResultSet rs = st.executeQuery(sql);
			if(rs.next()) uname = rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uname;
	}
	
	public ArrayList<Item> getAllUserItems(int uid) {
		ArrayList<Item> all = new ArrayList<Item>();
		
		String sql = "select iid, itemname, qty from t_items where uid = " + uid;
		
		try {
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				Item i = new Item(rs.getInt(1), rs.getString(2), rs.getInt(3));
				all.add(i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return all;
	}
	
	public int createUserAccount(User u) {
		// 0 means everything is OK, user is created
		// 1 means values are too long
		// 2 means unique constraint on the login name has been violated
		// 3 means that an empty form field was submitted
		// 4 means that the passwords are not the same
		int status = 0;
		
		if(u.getLoginName().trim().equals("") || 
				u.getName().trim().equals("") || 
				u.getLoginPass1().trim().equals("") ||
				u.getLoginPass2().trim().equals("")) return 3;
		if(u.getLoginName().trim().length() > 20 || 
				u.getName().trim().length() > 20 || 
				u.getLoginPass1().trim().length()> 20 ||
				u.getLoginPass2().trim().length()>20) return 1;
		
		if(!u.getLoginPass1().trim().equals(u.getLoginPass2().trim())) return 4;
		
		String sql = "insert into t_users (LoginName, Name, LoginPass) values (?, ?, ?)";
		
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, u.getLoginName());
			pst.setString(2, u.getName());
			pst.setString(3, u.getLoginPass1());
			pst.executeUpdate();
		} catch (SQLException e) {
			status = 2;
			e.printStackTrace();
		}

		return status;
	}
	
	
	public int addItem(String iname, String iqty, Integer uid) {
		int res = 0;
		// 0 - OK - item was inserted
		// 1 - item name was not given
		// 2 - item qty was either not given or not a valid int
		int qty = 0;
		if(iname == null || iname.trim().equals("")) return 1;
		try {
			qty = Integer.parseInt(iqty);
		}catch(Exception e) {return 2;}
		
		String sql = "insert into t_items (ItemName, Qty, uid) values (?, ?, ?)";
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, iname);
			pst.setInt(2, qty);
			pst.setInt(3, uid);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
}












