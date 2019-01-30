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
		
		int itemLimit = 20;
		
		PrintWriter out = response.getWriter();
		
		try {
			//create a connection type to the database
			Connection dbcon = dataSource.getConnection();
			
			//gets us the string mode 
			String mode = request.getParameter("mode");
			int page = (Integer.parseInt(request.getParameter("page")) - 1) * itemLimit;
			
			String outer_query = "select d.id,d.title,d.genres,d.stars,d.year,d.director,d.rating\r\n" + 
					"from (";
			
			//base string query for all searches/browsing
			String query = "select movies.id,title,group_concat(distinct genres.id, ',', genres.name separator ';') as genres, group_concat(distinct stars.id, ',' , stars.name separator ';') as stars,year,director,rating \r\n" + 
					"								from movies,ratings, genres_in_movies, genres, stars, stars_in_movies\r\n";
			
			//gets information on what the user wants to sort and order by
//			String orderBy = request.getParameter("orderBy");
//			String sortingAttribute = request.getParameter("sortAttribute");

			
			if(mode.equals("browse")) {
				String genre_id = request.getParameter("id");
				System.out.println("Genre Id: " + genre_id);
				if(genre_id != null) {//means that there is an id
					query +="								where ratings.movieID = movies.id and genres_in_movies.movieId = movies.id\r\n" + 
							"								and genres_in_movies.genreId = genres.id\r\n" + 
							"								and stars_in_movies.movieId = movies.id and stars.id = stars_in_movies.starId \r\n" + 
							"								group by movies.id, title, year, director, rating \r\n" + 
							"								order by rating desc) as d \r\n" + 
							"where genres LIKE concat('%', (select g.name from genres g where g.id = " + genre_id + "), '%') \r\n" +
							" 								limit " + itemLimit + " offset " + page;
					outer_query += query;
					query = outer_query;
					
				}
				else {//means that they have selected browsing by letter
					String first_letter = request.getParameter("search");
					System.out.println(first_letter);
					query +="								where title LIKE '"+  first_letter + "%'" + "and ratings.movieID = movies.id and genres_in_movies.movieId = movies.id\r\n" + 
							"								and genres_in_movies.genreId = genres.id\r\n" + 
							"								and stars_in_movies.movieId = movies.id and stars.id = stars_in_movies.starId \r\n" + 
							"								group by movies.id, title, year, director, rating \r\n" + 
							"								order by rating desc"+
							" 								limit " + itemLimit + " offset " + page;
				}
			}
			else if(mode.equals("search")) {
				//can be empty or null
				
				
				
				String inner_query_where = "where ratings.movieID = movies.id \r\n" + 
						"                                and genres_in_movies.movieId = movies.id\r\n" + 
						"								and genres_in_movies.genreId = genres.id\r\n" + 
						"								and stars_in_movies.movieId = movies.id \r\n" + 
						"                                and stars.id = stars_in_movies.starId \r\n";
				
				String search_title = request.getParameter("title");
				String search_director = request.getParameter("director");
				String search_year = request.getParameter("year");
				String search_star = request.getParameter("star");
				
				
				if(search_title != null && !search_title.isEmpty()) {
					inner_query_where += " and movies.title LIKE '%"+ search_title +"%' \r\n";
				}
				if( search_director != null && !search_director.isEmpty()) {
					inner_query_where += " and movies.director LIKE '%" + search_director + "%' \r\n";
				}
				if(search_year != null && !search_year.isEmpty()  ) {
					inner_query_where += " and movies.year = " + search_year + " \r\n" ;
				}
				//doesn't matter as we just add the last parts to the query
				
				inner_query_where += "group by movies.id, movies.title, movies.year, movies.director, ratings.rating \r\n" + 
						"                                order by rating asc ) as d \r\n";
				
				//special case to handle the list of stars we have
				if(search_star != null && !search_star.isEmpty() ) {
					inner_query_where += "where d.stars LIKE '%" + search_star 	+ "%'";
				}
				//add to the base query
				query += inner_query_where;
				outer_query += query;
				query = outer_query;
				query += "limit " + itemLimit + " offset " + page;
				
//				System.out.println(query);
			}
					
			Statement statement = dbcon.createStatement();
						
			ResultSet rs = statement.executeQuery(query);
			
			JsonArray jsonArray = new JsonArray();
			
			while(rs.next()){
				String id = rs.getString("id");
				String title = rs.getString("title");
				String rating = rs.getString("rating");
				String year = rs.getString("year");
				String director = rs.getString("director");
				String[] genres = rs.getString("genres").split(";");
				String[] stars = rs.getString("stars").split(";");
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("id", id);
				jsonObject.addProperty("title", title);
				jsonObject.addProperty("year",year);
				jsonObject.addProperty("director", director);
				jsonObject.addProperty("rating", rating);
				
				JsonArray genreList = new JsonArray();
				JsonArray starList = new JsonArray();
				
				for(int i = 0; i < genres.length; i++) {
					JsonObject genreObj = new JsonObject();
					String[] genre_info = genres[i].split(",");
					genreObj.addProperty("genre_id", genre_info[0]);
					genreObj.addProperty("genre_name", genre_info[1]);
					genreList.add(genreObj);
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