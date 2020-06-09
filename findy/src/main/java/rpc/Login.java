package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;

public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public Login() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// For validation not creation
		HttpSession session = request.getSession(false);
		
		JSONObject obj = new JSONObject();
		
		if (session != null) {
			MySQLConnection connection = new MySQLConnection();
			String userId = session.getAttribute("user_id").toString();
			
			obj.put("status", "OK");
			obj.put("user_id", userId);
			obj.put("name", connection.getFullname(userId));
			
			connection.close();
		} else {
			obj.put("status", "Invalid Session");
			response.setStatus(403);
		}
		
		RpcHelper.writeJsonObject(response, obj);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = input.getString("user_id");
		String password = input.getString("password");

		MySQLConnection connection = new MySQLConnection();
		JSONObject obj = new JSONObject();

		if (connection.verifyLogin(userId, password)) {
			
			// Create a new session
			HttpSession session = request.getSession();
			session.setAttribute("user_id", userId);
			session.setMaxInactiveInterval(600);
			
			obj.put("status", "OK");
			obj.put("user_id", userId);
			obj.put("name", connection.getFullname(userId));
			
		} else {
			obj.put("status", "Please check your user id and password again");
			response.setStatus(401);
		}
		
		connection.close();
		RpcHelper.writeJsonObject(response, obj);
	}

}
