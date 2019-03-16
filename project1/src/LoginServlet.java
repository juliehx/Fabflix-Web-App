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
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

import org.jasypt.util.password.StrongPasswordEncryptor;


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
		String loginType = request.getParameter("type");
//		System.out.println(loginType);
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String userAgent = request.getHeader("User-Agent");
//        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

		//get information from mysql database
		//use query to get information 
        
//        if (userAgent != null && !userAgent.contains("Android")) {
//            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//            // verify recaptcha first
//            try {
//                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
//            } catch (Exception e) {
//                System.out.println("recaptcha success");
//                JsonObject responseJsonObject = new JsonObject();
//                responseJsonObject.addProperty("status", "fail");
//                responseJsonObject.addProperty("message", e.getMessage());
//                response.getWriter().write(responseJsonObject.toString());
//                return;
//           }
//        }
    	try {
    		  Context initCtx = new InitialContext();

              Context envCtx = (Context) initCtx.lookup("java:comp/env");
              if (envCtx == null)
                  response.getWriter().println("envCtx is NULL");

              // Look up our data source
              DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
              
			Connection dbcon = ds.getConnection();	
			
//    			Statement statement = dbcon.createStatement();
			
			String query = "select email,password from customers where email = '" + username + "'";//and password = ?";
			
			if(loginType != null && loginType.equals("employee")) {
				query = "select email, password from employees where email = '" + username + "'";
			}
			
			PreparedStatement statement = dbcon.prepareStatement(query);
//    			statement.setString(1, username);
//    			statement.setString(2, password);
			
			ResultSet rs = statement.executeQuery();
			
//			try {
				
				boolean success = false;
				if(rs.next()) {
					String encryptedPassword = rs.getString("password");
					success = new StrongPasswordEncryptor().checkPassword(password,encryptedPassword);
				}
				else {
					JsonObject responseJsonObject = new JsonObject();
		            responseJsonObject.addProperty("status", "fail");
		            responseJsonObject.addProperty("message", "username and password do not match");
		            response.getWriter().write(responseJsonObject.toString());
				}
				
				if(success) {
					String sessionId = ((HttpServletRequest) request).getSession().getId();
					Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
					
					JsonObject responseJsonObject = new JsonObject();
					
					if(loginType == null) {
						request.getSession().setAttribute("user", username);
						responseJsonObject.addProperty("type", "user");
					} else if(loginType.equals("employee")) {
						request.getSession().setAttribute("employee", username);
						responseJsonObject.addProperty("type", "employee");
					}
					
					
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
//    				RecaptchaVerifyUtils.verify(gRecaptchaResponse);
//			}catch(Exception e) {
//				JsonObject responseJsonObject = new JsonObject();
//				responseJsonObject.addProperty("status", "fail");
//				responseJsonObject.addProperty("message","failed recaptcha verification");
//				response.getWriter().write(responseJsonObject.toString());
//			}
			
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
