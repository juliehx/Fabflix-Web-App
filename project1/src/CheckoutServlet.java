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
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.ArrayList;
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
		String first_name = request.getParameter("firstName");
		String last_name = request.getParameter("lastName");
		String address = request.getParameter("address");
		String ccId = request.getParameter("ccId");
		String expDate = request.getParameter("expDate");
		HttpSession session = request.getSession();
		try {
			Connection dbcon = dataSource.getConnection();
			
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
				
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "Successful Purchase! Thank You, " + first_name + " " + last_name);
//				responseJsonObject.addProperty("sessionId", sessionId);
//				responseJsonObject.addProperty("lastAccessTime", lastAccessTime);
				
				ArrayList<JsonObject> cart = (ArrayList<JsonObject>) session.getAttribute("cart");
				
				Date date = new Date(lastAccessTime);
				LocalDate currentDay = date.toLocalDate();
				int year = currentDay.getYear();
				int month =currentDay.getMonthValue();
				int day = currentDay.getDayOfMonth();
				
				String fullDate = String.format("%1$-%02d$-%03d$",year,month,day);
				
				String insert_query = "insert into sales(customerId,movieId,saleDate)\r\n" + 
						"VALUES(?,?,?)";
				
				PreparedStatement insert_statement = dbcon.prepareStatement(insert_query);
				
				JsonArray jsonArray = new JsonArray();
				
				for(int i = 0; i < cart.size(); i++) {
					JsonObject jsonObject = cart.get(i);
					String id = jsonObject.get("id").toString();
					String title = jsonObject.get("title").toString();
					
					insert_statement.setString(1, cust_id);//customer id
					insert_statement.setString(2, id);//movie id 
					insert_statement.setString(3, fullDate);
					
					ResultSet rs2 = insert_statement.executeQuery();//to get sale ID
					String sale_id = rs2.getString("id");
					JsonObject rs2JsonObject = new JsonObject();
					rs2JsonObject.addProperty("sale_id", sale_id);
					rs2JsonObject.addProperty("title", title);
					jsonArray.add(rs2JsonObject);
					
				}
				responseJsonObject.add("sales", jsonArray);
				
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
