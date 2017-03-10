package home;

import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class IndexAction extends ActionSupport{
	
	private String username;
	
	public String index() {
		Map session	= ActionContext.getContext().getSession();
		if(session.get("username") != null){
			username = session.get("username").toString();
		}
		System.out.print(username);
		return SUCCESS;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
