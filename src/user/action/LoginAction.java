/*
 *Author TT.Manh
 *10/03/2017 
 */
package user.action;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.net.httpserver.Authenticator.Success;

/*
 * Class Login
 * Process login, logout action from user
 */
public class LoginAction extends ActionSupport{

	private String email;
	private String password;
	private String username;

	/*
	 * Process login action 
	 * @parameter	none
	 * @return 		string		 
	 */
	public String login() {
		//Get session
		Map session		= ActionContext.getContext().getSession();
		//Check if user is already logged in, if yes return success
		if(session.get("username") != null)
			return SUCCESS;
		//Check if user is not logged in and try to log in
		if(session.get("username") == null && email == null)
			return "not_logged_in";
		
		//Process login info then return result
		String ret 		= ERROR;
		Connection conn = null;
		try {			
			//Get user info from db with login info
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn 					= DriverManager.getConnection("jdbc:mysql://localhost/imusic", "root", "");
			String sql 				= "SELECT username FROM user WHERE email = ? AND password = ? AND is_active = 1";
			PreparedStatement ps	= conn.prepareStatement(sql);
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs_select 	= ps.executeQuery();

			while (rs_select.next()) {
				//If able to find user, log info to session
				username		= rs_select.getString("username");
				session.put("username", username);
				session.put("email", email);

				//Update db
				sql 			= "UPDATE `user` SET is_login = 1, last_login_dt = NOW() WHERE email = ?";
				ps				= conn.prepareStatement(sql);
				ps.setString(1, email);
				ps.executeUpdate();
				
				//Set return success
				ret 			= SUCCESS;
			}
		} catch (Exception e) {
			//If have error, log error and set return error
			System.out.print(e);
			ret = ERROR;
		} finally {
			//Close connection
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					System.out.print(e);
				}
			}
		}
		return ret;
	}
	
	/*
	 * Process logout action 
	 * @parameter	none
	 * @return 		string		 
	 */
	public String logout(){
		//Get session
		Map session		= ActionContext.getContext().getSession();
		//If username is exist, remove username
		if(session.get("email") != null){
			email 		= session.get("email").toString();
			session.remove("username");
			session.remove("email");
			
			//Update db
			Connection conn = null;
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				conn					= DriverManager.getConnection("jdbc:mysql://localhost/imusic", "root", "");
				String sql 				= "UPDATE `user` SET is_login = 0 WHERE email = ?";
				PreparedStatement ps	= conn.prepareStatement(sql);
				ps.setString(1, email);
				ps.executeUpdate();
			}catch (Exception e) {
				//If have error, log error
				System.out.print(e);
			} finally {
				//Close connection
				if (conn != null) {
					try {
						conn.close();
					} catch (Exception e) {
						System.out.print(e);
					}
				}
			}
		}
		return SUCCESS;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}