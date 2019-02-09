import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//Create username/password objects that can be sent to mysql database
		String username = request.getParameter("username");
		String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

		//get information from mysql database
		//use query to get information 
		
		try {
			Connection dbcon = dataSource.getConnection();
			
//			Statement statement = dbcon.createStatement();
			
			String query = "select email,password from customers where email = ? and password = ?";
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);
			
			ResultSet rs = statement.executeQuery();
			
			try {
				RecaptchaVerifyUtils.verify(gRecaptchaResponse);
				if(rs.next()) {
					String sessionId = ((HttpServletRequest) request).getSession().getId();
					Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
					request.getSession().setAttribute("user", username);
					
					JsonObject responseJsonObject = new JsonObject();
					responseJsonObject.addProperty("status", "success");
					responseJsonObject.addProperty("message", "success");
					responseJsonObject.addProperty("sessionId", sessionId);
					responseJsonObject.addProperty("lastAccessTime", lastAccessTime);
					
					
					response.getWriter().write(responseJsonObject.toString());
				}
				else {
					JsonObject responseJsonObject = new JsonObject();
		            responseJsonObject.addProperty("status", "fail");
		            responseJsonObject.addProperty("message", "username and password do not match");
		            response.getWriter().write(responseJsonObject.toString());
				}
			}catch(Exception e) {
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "fail");
				responseJsonObject.addProperty("message","failed recaptcha verification");
				response.getWriter().write(responseJsonObject.toString());
			}
			
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
			
			
		}catch(Exception e){
			JsonObject responseJsonObject = new JsonObject();
			responseJsonObject.addProperty("errorMessage", e.getMessage());
			response.getWriter().write(responseJsonObject.toString());
			response.setStatus(500);
		}
		
	}

}
