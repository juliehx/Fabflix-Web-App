

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ViewCartServlet
 */
@WebServlet(name = "ViewCartServlet", urlPatterns = "/api/view-cart")
public class ViewCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name="jdbc/moviedb")
	private DataSource dataSource;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewCartServlet() {
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
		
		HttpSession session = request.getSession();
	
		
		try {
			HashMap <String, HashMap<String, Object>> cart = (HashMap) session.getAttribute("cart");
			if(cart != null && !cart.isEmpty()) {
				Gson gson = new Gson();
				String cartItems = gson.toJson(cart);
				System.out.println(cartItems);
				
				out.write(cartItems.toString());
				
			} else {
				JsonObject emptyMsg = new JsonObject();
				emptyMsg.addProperty("message", "Your cart is empty.");
				out.write(emptyMsg.toString());
			}
			
			response.setStatus(200);
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			
			response.setStatus(500);
		}
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
