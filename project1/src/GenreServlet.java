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
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Servlet implementation class GenreServlet
 */
@WebServlet(name = "GenreServlet", urlPatterns = "/api/genres")
public class GenreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GenreServlet() {
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
		
		PrintWriter out = response.getWriter();
		
		try {
			Connection dbcon = dataSource.getConnection();
						
			String query = "select group_concat(m.id, ',' ,m.title separator ';') as moviegroup, g.name\r\n" + 
					"from genres g, genres_in_movies gim, movies m\r\n" + 
					"where g.id = ? and g.id = gim.genreId and m.id = gim.movieId ";
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			
			statement.setString(1, id);
			
			ResultSet rs = statement.executeQuery(query);
			
			JsonArray jsonArray = new JsonArray();
			
			rs.next();	
			String genre_name = rs.getString("name");
			String[] movieStringList = rs.getString("moviegroup").split(";");
			
			JsonArray movieList = new JsonArray();
			
			for(int i = 0; i < movieStringList.length; i++) {
				JsonObject movieObj = new JsonObject();
				String[] movie_info = movieStringList[i].split(",");
				movieObj.addProperty("movie_id", movie_info[0]);
				movieObj.addProperty("movie_name", movie_info[1]);
				movieList.add(movieObj);
			}
			
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", genre_name);
			jsonObject.add("movies", movieList);
			
			jsonArray.add(jsonObject);
			
			out.write(jsonArray.toString());
			
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
			
		}catch (Exception e){
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			
			response.setStatus(500);
		}
		out.close();
	}

}
