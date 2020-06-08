package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item;

import java.util.HashSet;
import java.util.Set;

public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ItemHistory() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userId = request.getParameter("user_id");

		MySQLConnection connection = new MySQLConnection();
		Set<Item> items = connection.getFavoriteItems(userId);
		connection.close();

		JSONArray array = new JSONArray();
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			// <3
			obj.put("favorite", true);
			array.put(obj);
		}
		
		RpcHelper.writeJsonArray(response, array);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Init connection -> get the user_id from request -> init the item by RpcHelper
		// -> set fav job -> close the connection
		MySQLConnection connection = new MySQLConnection();

		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = input.getString("user_id");
		Item item = RpcHelper.parseFavoriteItem(input.getJSONObject("favorite"));

		connection.setFavoriteItems(userId, item);
		connection.close();
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Init connection -> get the user_id from request -> init the item by RpcHelper
		// -> unset fav job -> close the connection
		MySQLConnection connection = new MySQLConnection();

		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = input.getString("user_id");
		Item item = RpcHelper.parseFavoriteItem(input.getJSONObject("favorite"));

		connection.unsetFavoriteItems(userId, item.getItemId());
		connection.close();
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	}

}
