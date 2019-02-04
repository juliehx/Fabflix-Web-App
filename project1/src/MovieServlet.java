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
import java.util.ArrayList;
import java.sql.ResultSet;

@WebServlet(name = "MovieServlet", urlPatterns = "/api/movies")
public class MovieServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	//Initialize a dataSource which is in the web.xml file
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource; //variable to determine where information is being pulled from
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		
		
//		
//		System.out.println(request.getHeader("referer"));
		
		//gets number of results to display per page
		String itemLimit = request.getParameter("limit");
//		int itemLimit = 20;
		
		PrintWriter out = response.getWriter();
		
		try {
			//create a connection type to the database
			Connection dbcon = dataSource.getConnection();
			
			String order = request.getParameter("order");
			
			//gets us the string mode 
			String mode = request.getParameter("mode");
			int page = (Integer.parseInt(request.getParameter("page")) - 1) * Integer.parseInt(itemLimit);
			
			String base_query = "select movies.id, movies.title, movies.year, movies.director, r.rating,\n" + 
					"					group_concat(distinct g.name) as genre_name, \n" + 
					"        			group_concat(distinct g.id) as genre_id, \n" + 
					"        			group_concat(distinct s.id, ',', s.name separator ';') as star\n" + 
					"			 from movies\n" + 
					"			 left join genres_in_movies gim on gim.movieId = movies.id\n" + 
					"			 left join genres g on g.id = gim.genreId\n" + 
					"            left join stars_in_movies sim on sim.movieId = movies.id\n" + 
					"            left join stars s on s.id = sim.starId\n" + 
					"            left join ratings r on r.movieID = movies.id\n";

			
//			String outer_query = "select d.id,d.title,d.genres,d.stars,d.year,d.director,d.rating\r\n" + 
//					"from (";
//			
//			//base string query for all searches/browsing
//			String query = "select movies.id,title,group_concat(distinct genres.id, ',', genres.name separator ';') as genres, group_concat(distinct stars.id, ',' , stars.name separator ';') as stars,year,director,rating \r\n" + 
//					"								from movies,ratings, genres_in_movies, genres, stars, stars_in_movies\r\n";
			
			//gets information on what the user wants to sort and order by
//			String orderBy = request.getParameter("orderBy");
//			String sortingAttribute = request.getParameter("sortAttribute");

			
			if(mode.equals("browse")) {
				String genre_id = request.getParameter("id");
				//System.out.println("Genre Id: " + genre_id);
				if(genre_id != null) {//means that there is an id
					base_query += "group by movies.id, movies.title, movies.year, movies.director, r.rating\n" + 
								  "having find_in_set(" + genre_id + ", genre_id)\n";
//					query +="								where ratings.movieID = movies.id and genres_in_movies.movieId = movies.id\r\n" + 
//							"								and genres_in_movies.genreId = genres.id\r\n" + 
//							"								and stars_in_movies.movieId = movies.id and stars.id = stars_in_movies.starId \r\n" + 
//							"								group by movies.id, title, year, director, rating \r\n" + 
//							"								order by rating desc) as d \r\n" + 
//							"where genres LIKE concat('%', (select g.name from genres g where g.id = " + genre_id + "), '%') \r\n" +
//							" 								limit " + itemLimit + " offset " + page;
//					outer_query += query;
//					query = outer_query;
					
				}
				else {//means that they have selected browsing by letter
					String first_letter = request.getParameter("search");
					//System.out.println(first_letter);
					base_query += "where movies.title like '" + first_letter + "%'\n" + 
								  "group by movies.id, movies.title, movies.year, movies.director, r.rating\n";
//					query +="								where title LIKE '"+  first_letter + "%'" + "and ratings.movieID = movies.id and genres_in_movies.movieId = movies.id\r\n" + 
//							"								and genres_in_movies.genreId = genres.id\r\n" + 
//							"								and stars_in_movies.movieId = movies.id and stars.id = stars_in_movies.starId \r\n" + 
//							"								group by movies.id, title, year, director, rating \r\n" + 
//							"								order by rating desc"+
//							" 								limit " + itemLimit + " offset " + page;
				}
			}
			else if(mode.equals("search")) {
//				//can be empty or null
//				
				String search_query = "";
//				
//				String inner_query_where = "where ratings.movieID = movies.id \r\n" + 
//						"                                and genres_in_movies.movieId = movies.id\r\n" + 
//						"								and genres_in_movies.genreId = genres.id\r\n" + 
//						"								and stars_in_movies.movieId = movies.id \r\n" + 
//						"                                and stars.id = stars_in_movies.starId \r\n";
//				
				String search_title = request.getParameter("title");
				String search_director = request.getParameter("director");
				String search_year = request.getParameter("year");
				String search_star = request.getParameter("star");
				
//				System.out.println(search_star);
				
				boolean searchTitleExist = search_title != null && !search_title.isEmpty();
				boolean searchDirectorExist = search_director != null && !search_director.isEmpty();
				boolean searchYearExist = search_year != null && !search_year.isEmpty();
				boolean searchStarExist = search_star != null && !search_star.isEmpty();
				
				ArrayList<String> queryList = new ArrayList<String>();
				
//				System.out.println(searchTitleExist);
//				System.out.println(searchDirectorExist);
//				System.out.println(searchYearExist);
//				System.out.println(searchStarExist);
				
				if(searchTitleExist || searchDirectorExist || searchYearExist || searchStarExist) {
					search_query += "where ";
					
					if(searchTitleExist)
						queryList.add("movies.title like '%" + search_title + "%'\n");
					if(searchDirectorExist)
						queryList.add("movies.director like '%" + search_director + "%'\n");
					if(searchYearExist)
						queryList.add("movies.year like '%" + search_year + "%'\n");
					if(searchStarExist)
						queryList.add("s.name like '%" + search_star + "%'\n");
				}
				
				if(queryList.size() >= 1) {
					search_query += queryList.get(0);
					for(int i = 1; i < queryList.size(); i++) {
						search_query += "and " + queryList.get(i);
					}
				}
				
				base_query += search_query + "group by movies.id, movies.title, movies.year, movies.director, r.rating\n";
//				
//				
//				if(search_title != null && !search_title.isEmpty()) {
//					inner_query_where += " and movies.title LIKE '%"+ search_title +"%' \r\n";
//				}
//				if( search_director != null && !search_director.isEmpty()) {
//					inner_query_where += " and movies.director LIKE '%" + search_director + "%' \r\n";
//				}
//				if(search_year != null && !search_year.isEmpty()  ) {
//					inner_query_where += " and movies.year = " + search_year + " \r\n" ;
//				}
//				//doesn't matter as we just add the last parts to the query
//				
//				inner_query_where += "group by movies.id, movies.title, movies.year, movies.director, ratings.rating \r\n" + 
//						"                                order by rating asc ) as d \r\n";
//				
//				//special case to handle the list of stars we have
//				if(search_star != null && !search_star.isEmpty() ) {
//					inner_query_where += "where d.stars LIKE '%" + search_star 	+ "%'";
//				}
//				//add to the base query
//				query += inner_query_where;
//				outer_query += query;
//				query = outer_query;
//				query += "limit " + itemLimit + " offset " + page;
//				
////				System.out.println(query);
			}
			String[] determine_sort = order.split(" ");
			base_query += "order by " + determine_sort[0];
			if(determine_sort.length > 1) {
				if(determine_sort[1].equals("Highest") || determine_sort[1].equals("Z-0")) {
					base_query += " desc\n";
				}
				else if(determine_sort[1].equals("Lowest") || determine_sort[1].equals("0-Z")) {
					base_query += " asc \n";
				}
			}
			else {
				base_query += " desc \n";
			}
//			if(order.equals("title"))
//				base_query += " asc\n";
//			else if(order.equals("rating"))
//				base_query += " desc\n";
				
			base_query += " limit " + itemLimit + " offset " + page;
			
//			System.out.println(base_query);
					
			Statement statement = dbcon.createStatement();
						
			ResultSet rs = statement.executeQuery(base_query);
			
			JsonArray jsonArray = new JsonArray();
			
			while(rs.next()){
				String id = rs.getString("id");
				String title = rs.getString("title");
				String rating = rs.getString("rating");
				String year = rs.getString("year");
				String director = rs.getString("director");
				String[] genres_id = rs.getString("genre_id").split(",");
				String[] genres_name = rs.getString("genre_name").split(",");
				String[] stars = rs.getString("star").split(";");
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("id", id);
				jsonObject.addProperty("title", title);
				jsonObject.addProperty("year",year);
				jsonObject.addProperty("director", director);
				jsonObject.addProperty("rating", rating);
				
				JsonArray genreList = new JsonArray();
				JsonArray starList = new JsonArray();
				
				for(int i = 0; i < genres_id.length; i++) {
					JsonObject genreObj = new JsonObject();
					genreObj.addProperty("genre_id", genres_id[i]);
					genreObj.addProperty("genre_name", genres_name[i]);
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
				
//				HttpSession session = request.getSession();
//				session.setAttribute("title",title);
//				session.setAttribute("order",order);
//				session.setAttribute("limit",itemLimit);
//				session.setAttribute("page",page);
//				session.setAttribute("mode", mode);
				
				
//				System.out.println(sTitle);
//				System.out.println(sOrder);
//				System.out.println(sLimit);
//				System.out.println(sPage);
			}
			String url = request.getHeader("referer");
			
			HttpSession session = request.getSession();
			session.setAttribute("url", url);
			
			
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