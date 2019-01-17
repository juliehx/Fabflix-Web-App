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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

@WebServlet(name = "MovieServlet", urlPatterns = "/api/movies")
public class MovieServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	//Initialize a dataSource which is in the web.xml file
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource; //variable to determine where information is being pulled from
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		try {
			//create a connection type to the database
			Connection dbcon = dataSource.getConnection();
			
			Statement statement = dbcon.createStatement();
			
			// Query used to get list of all movies and its attributes
			// such as title,year,director,listOfGenres,listOfStars,rating
			String query = "select title,rating from movies,ratings where movies.id = ratings.movieID order by rating desc";
			
			
			ResultSet rs = statement.executeQuery(query);
			
			JsonArray jsonArray = new JsonArray();
			
			while(rs.next()){
				String title = rs.getString("title");
				String rating = rs.getString("rating");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("title", title);
				jsonObject.addProperty("rating", rating);
				
				jsonArray.add(jsonObject);
			}
			out.write(jsonArray.toString());
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			
			response.setStatus(500);
		}
		out.close();
	}
}