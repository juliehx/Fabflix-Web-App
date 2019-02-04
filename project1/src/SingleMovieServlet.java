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
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servlet implementation class SingleMovieServlet
 */
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		
		String id = request.getParameter("id");
		
		PrintWriter out = response.getWriter();
		
		try{
			Connection dbcon = dataSource.getConnection();
			String query = "select movies.id, movies.title, movies.year, movies.director, r.rating, \r\n" + 
					"		group_concat(distinct g.name) as genres, group_concat(distinct s.id, ',', s.name separator ';') as stars\r\n" + 
					"from movies\r\n" + 
					"left join genres_in_movies gim on gim.movieId = movies.id\r\n" + 
					"left join genres g on g.id = gim.genreId\r\n" + 
					"left join stars_in_movies sim on sim.movieId = movies.id\r\n" + 
					"left join stars s on s.id = sim.starId\r\n" + 
					"left join ratings r on r.movieID = movies.id\r\n" + 
					"where movies.id = ?\r\n" + 
					"group by movies.id, movies.title, movies.year, movies.director, r.rating";
					
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);
//			statement.setString(2, id);
//			statement.setString(3, id);
//			statement.setString(4, id);
			ResultSet rs = statement.executeQuery();
			
			JsonArray jsonArray = new JsonArray();
			
			rs.next();
			
			String movie_id = rs.getString("id");
			String title = rs.getString("title");
			String year = rs.getString("year");
			String director = rs.getString("director");
			String[] genreStringList = rs.getString("genres").split(",");
			String[] starStringList = rs.getString("stars").split(";");
			String rating = rs.getString("rating");
			
			JsonArray genreList = new JsonArray();
			JsonArray starList = new JsonArray();
			
			for(int i = 0; i < genreStringList.length; i++) {
				genreList.add(genreStringList[i]);
			}
			
			for(int i = 0; i < starStringList.length; i++) {
				JsonObject starObj = new JsonObject();
				String[] star_info = starStringList[i].split(",");
				starObj.addProperty("star_id", star_info[0]);
				starObj.addProperty("star_name", star_info[1]);
				starList.add(starObj);
			}
			
			JsonObject jsonObject = new JsonObject(); 
			jsonObject.addProperty("id", movie_id);
			jsonObject.addProperty("title", title);
			jsonObject.addProperty("year", year);
			jsonObject.addProperty("director", director);
			jsonObject.add("genres", genreList);
			jsonObject.add("stars", starList);
			jsonObject.addProperty("rating", rating);
			
			HttpSession session = request.getSession();
			String url = (String) session.getAttribute("url");
			jsonObject.addProperty("url", url);
						
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
