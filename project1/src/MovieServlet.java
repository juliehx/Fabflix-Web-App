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
			//String query = "select id,title,rating from movies,ratings where movies.id = ratings.movieID order by rating desc";
			
			String query = "select movies.id,title,group_concat(distinct genres.name) as genres, group_concat(distinct stars.id, ',' , stars.name separator ';') as stars,year,director,rating\n" + 
					"from movies,ratings, genres_in_movies, genres, stars, stars_in_movies\n" + 
					"where ratings.movieID = movies.id and genres_in_movies.movieId = movies.id\n" +
					"and genres_in_movies.genreId = genres.id\n" +
					"and stars_in_movies.movieId = movies.id and stars.id = stars_in_movies.starId\n" + 
					"group by movies.id, title, year, director, rating\n" +
					"order by rating desc";
			
			
			ResultSet rs = statement.executeQuery(query);
			
			JsonArray jsonArray = new JsonArray();
			
			while(rs.next()){
				String id = rs.getString("id");
				String title = rs.getString("title");
				String rating = rs.getString("rating");
				String year = rs.getString("year");
				String director = rs.getString("director");
				String[] genres = rs.getString("genres").split(",");
				String[] stars = rs.getString("stars").split(";");
				System.out.println(stars.length);
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("id", id);
				jsonObject.addProperty("title", title);
				jsonObject.addProperty("year",year);
				jsonObject.addProperty("director", director);
				jsonObject.addProperty("rating", rating);
				
				JsonArray genreList = new JsonArray();
				JsonArray starList = new JsonArray();
				
				for(int i = 0; i < genres.length; i++) {
					genreList.add(genres[i]);
				}
				
				
				
				for(int i = 0; i < stars.length; i++) {
					JsonObject starObj = new JsonObject();
					String[] star_info = stars[i].split(",");
					starObj.addProperty("star_id", star_info[0]);
					starObj.addProperty("star_name", star_info[1]);
					starList.add(starObj);
				}
				
				
				jsonObject.add("genres", genreList);
				jsonObject.add("stars", starList);
				
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