import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.ResultSet;
/**
 * Servlet implementation class CheckoutServlet
 */
@WebServlet(name = "CheckoutServlet", urlPatterns="/api/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		
		String first_name = request.getParameter("first-name");
		String last_name = request.getParameter("last-name");
		String address = request.getParameter("address");
		String ccId = request.getParameter("cc-id");
		String expDate = request.getParameter("exp");
		HttpSession session = request.getSession();
		
		try {
			Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                response.getWriter().println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb-write");
            
			Connection dbcon = ds.getConnection();
			
			String query = "select c.id,c.firstName,c.lastName\r\n" + 
					"from customers c, creditcards cc\r\n" + 
					"where c.firstName = ? and c.lastName = ? and cc.firstName = ? and cc.lastName = ? "
					+ "and c.ccId = ? "
					+ "and cc.expiration = ? ";
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, first_name);
			statement.setString(2, last_name);
			statement.setString(3, first_name);
			statement.setString(4,last_name);
			statement.setString(5,ccId);
			statement.setString(6, expDate);
			
			ResultSet rs = statement.executeQuery();
			
			
			if(rs.next()) {//means that there is valid information in the database about the customer
				
				String cust_id = rs.getString("id");
				String cust_firstName = rs.getString("firstName");
				String cust_lastName = rs.getString("lastName");
				String sessionId = ((HttpServletRequest) request).getSession().getId();
				Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
				
				HashMap<String, HashMap<String, Object>> cart = (HashMap) session.getAttribute("cart");
				JsonObject responseJsonObject = new JsonObject();
				
				if(cart!= null && !cart.isEmpty()) {
					responseJsonObject.addProperty("status", "success");
					responseJsonObject.addProperty("message", "Successful Purchase! Thank You, " + first_name + " " + last_name);
					responseJsonObject.addProperty("sessionId", sessionId);
					responseJsonObject.addProperty("lastAccessTime", lastAccessTime);

					LocalDate localDate = LocalDate.now();
					String todayDate = DateTimeFormatter.ofPattern("yyy/MM/dd").format(localDate);
			
					//String fullDate = String.format("%1d-%02d-%02d",year,month,day);
			
					String insert_query = "insert into sales(customerId,movieId,saleDate)\r\n" + 
										"VALUES(?,?,?)";
			
					PreparedStatement insert_statement = dbcon.prepareStatement(insert_query, Statement.RETURN_GENERATED_KEYS);
			
					JsonArray jsonArray = new JsonArray();
			
					//loops through the cart to insert movies into the sales table
					//and gets sale id with the title
			
					for(String key: cart.keySet()) {
//						JsonObject jsonObject = cart.get(i);
//						String id = jsonObject.get("id").toString();
//						String title = jsonObject.get("title").toString();
						for(int i = 0; i < Integer.parseInt(cart.get(key).get("quantity").toString()); i++) {
							insert_statement.setString(1, cust_id);//customer id
							insert_statement.setString(2, key);//movie id 
							insert_statement.setString(3, todayDate);
					
							int rows = insert_statement.executeUpdate();//to get sale ID
							
							if(rows == 1) {
								ResultSet rs2 = insert_statement.getGeneratedKeys();
								if(rs2.next()) {
									int sale_id = rs2.getInt(1);
									JsonObject rs2JsonObject = new JsonObject();
									rs2JsonObject.addProperty("sale_id", sale_id);
									rs2JsonObject.addProperty("movie_id", key);
									rs2JsonObject.addProperty("title", cart.get(key).get("title").toString());
									jsonArray.add(rs2JsonObject);
								}
								rs2.close();
							}
							
//							if(rs2.next()) {
//								String sale_id = rs2.getString("id");
//								JsonObject rs2JsonObject = new JsonObject();
//								rs2JsonObject.addProperty("sale_id", sale_id);
//								rs2JsonObject.addProperty("title", cart.get(key).get("title").toString());
//								jsonArray.add(rs2JsonObject);
//							}
//							rs2.close();
						}
					}
					responseJsonObject.add("sales", jsonArray);
					
					cart.clear();
					
				} else{
					responseJsonObject.addProperty("message", "Your cart is empty.");
				}
				
	            response.getWriter().write(responseJsonObject.toString());

			} else {
				JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "Invalid Customer Information");
	            response.getWriter().write(responseJsonObject.toString());
			}
			
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
			
		} catch(Exception e) {
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("errorMessage", e.getMessage());
			response.getWriter().write(responseJsonObject.toString());
			response.setStatus(500);
		}
	}

}
