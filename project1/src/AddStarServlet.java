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
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.ResultSet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddStarServlet
 */
@WebServlet("/AddStarServlet")
public class AddStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddStarServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		
		try {
			Connection dbcon = dataSource.getConnection();
			//This query gets the new unique ID for the added star
			String id_query = "(select ifnull\r\n" + 
					"		(concat('nm',LPAD(\r\n" + 
					"			(substring_index\r\n" + 
					"				(max(id),'nm',-1) + 1),8,'0')),1) as newId from stars);";
			
			PreparedStatement newIdStatement = dbcon.prepareStatement(id_query);
			ResultSet rs = newIdStatement.executeQuery();
			
			//information to populate the stars table
			rs.next();
			String newId = rs.getString("newId");
			
			String star_name = request.getParameter("star_name");
			String birth_year = request.getParameter("birth_year");
			
			String add_star_query = "insert into stars (id,name,birthyear) VALUES (?,?,?)";
			
			PreparedStatement star_statement = dbcon.prepareStatement(add_star_query);
			
			star_statement.setString(1, newId);
			star_statement.setString(2, star_name);
			star_statement.setString(3, birth_year);
			ResultSet rs2 = star_statement.executeQuery();
			if(rs2.next()) {
				
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "success");
			jsonObject.addProperty("message", "Successfully Added Star: " + star_name);
			
			response.getWriter().write((jsonObject.toString()));
			response.setStatus(200);
			}
			newIdStatement.close();
			rs.close();
			rs2.close();
			star_statement.close();
			dbcon.close();
			
		}catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", "Failed to Add Star");
			response.getWriter().write(jsonObject.toString());
		}
		
	
	}

}
