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
import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
/**
 * Servlet implementation class AdjustCartServlet
 */
@WebServlet(name = "AdjustCartServlet", urlPatterns="/api/adjust-cart")
public class AdjustCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdjustCartServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		
		String id = request.getParameter("id");
		String action = request.getParameter("action");
		
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		
		try {
			HashMap<String, HashMap<String, Object>> cart = (HashMap) session.getAttribute("cart");
			synchronized(cart) {
				
				HashMap<String,Object> updated_product = new HashMap<String,Object>();
				int value = (Integer) cart.get(id).get("quantity");
				updated_product.put("title",cart.get(id).get("title"));
				
				if(action.equals("add")) {
					updated_product.put("quantity",value + 1);
					cart.put(id, updated_product);
				}else if(action.equals("subtract")) {
					if(value - 1 == 0) {
						cart.remove(id);
					}
					else {
						updated_product.put("quantity",value - 1);
						cart.put(id, updated_product);
					}							
				}
			}

			Gson gson = new Gson();
			String cartItems = gson.toJson(cart);
			out.write(cartItems.toString());
			
			response.setStatus(200);
			
		}catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			
			response.setStatus(500);
		}
		out.close();
	}

	

}
