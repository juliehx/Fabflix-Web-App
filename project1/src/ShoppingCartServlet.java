import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.ResultSet;
/**
 * Servlet implementation class ShoppingCartServlet
 */
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/cart")
public class ShoppingCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource; //variable to determine where information is being pulled from
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShoppingCartServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		String action = request.getParameter("action");
		String movie_id = request.getParameter("id");
		System.out.println(movie_id);
		System.out.println(action);
		HttpSession session = request.getSession();

		try {
			Connection dbcon = dataSource.getConnection();
			String query = "select id, title from movies where id = '" + movie_id + "'";
			
			Statement statement = dbcon.createStatement();
			
			ResultSet rs = statement.executeQuery(query);
			
			rs.next();
			String id = rs.getString("id");
			String title = rs.getString("title");
			
			JsonArray jsonArray = new JsonArray();
			ArrayList<JsonObject> cart = (ArrayList<JsonObject>) session.getAttribute("cart"); 
//			if(action.equals("add")) {
				if (cart == null) {
					cart = new ArrayList<>();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("id", id);
					jsonObject.addProperty("title", title);
//					jsonArray.add(jsonObject);
					
					cart.add(jsonObject);
				
					session.setAttribute("cart", cart);
//					System.out.println(cart);
				} else {
					synchronized(cart) {
						JsonObject jsonObject = new JsonObject();
						jsonObject.addProperty("id", id);
						jsonObject.addProperty("title", title);
//						jsonArray.add(jsonObject);
						cart.add(jsonObject);
					}
				}
//			}else if(action.equals("delete")) {
//				synchronized(cart) {
//					cart.remove(title);
//				}
//			}	
				
			for(int i=0; i < cart.size();i++) {
				jsonArray.add(cart.get(i));
			}
			out.write(jsonArray.toString());
			
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
			
		}catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			
			response.setStatus(500);
		}
		out.close();		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
