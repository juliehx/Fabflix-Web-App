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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
		//START TS Timer
		long startTsTime = System.nanoTime();
		long elapsedTsTime = 0;
		long elapsedTjTime = 0;
		response.setContentType("application/json");
		
		//gets number of results to display per page
		String itemLimit = request.getParameter("limit");
		
		PrintWriter out = response.getWriter();
		
		try {
			
			String order = request.getParameter("order");
			
			//gets us the string mode 
			String mode = request.getParameter("mode");
			int page = (Integer.parseInt(request.getParameter("page")) - 1) * Integer.parseInt(itemLimit);
			
			ArrayList<String> query_items = new ArrayList<String>();
			
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
			
			if(mode.equals("browse")) {
				String genre_id = request.getParameter("id");
				if(genre_id != null) {//means that there is an id
					base_query += "group by movies.id, movies.title, movies.year, movies.director, r.rating\n" + 
								  "having find_in_set(?, genre_id)\n";
					query_items.add(genre_id);
					}
				else {//means that they have selected browsing by letter
					String first_letter = request.getParameter("search");
					//first_letter
					first_letter = first_letter + '%';
					base_query += "where movies.title like ?\n" + 
								  "group by movies.id, movies.title, movies.year, movies.director, r.rating\n";
					query_items.add(first_letter);
				}
			}
			else if(mode.equals("search")) {
				String search_query = "";
				
				String search_title = request.getParameter("title");
				String search_director = request.getParameter("director");
				String search_year = request.getParameter("year");
				String search_star = request.getParameter("star");
				
				
				boolean searchTitleExist = search_title != null && !search_title.isEmpty();
				boolean searchDirectorExist = search_director != null && !search_director.isEmpty();
				boolean searchYearExist = search_year != null && !search_year.isEmpty();
				boolean searchStarExist = search_star != null && !search_star.isEmpty();
				
				ArrayList<String> queryList = new ArrayList<String>();
							
				if(searchTitleExist || searchDirectorExist || searchYearExist || searchStarExist) {
					search_query += "where ";
					
					if(searchTitleExist) {
						String[] words = search_title.split(" ");
						String base_string = "";
						for(int i = 0; i < words.length; i++) {
							base_string += "+" + words[i] + "*";
						}
						
						String title_search_query = "match(movies.title) against(? in boolean mode)\n";
						queryList.add(title_search_query);
						query_items.add(base_string);
					}
					if(searchDirectorExist) {
						//search_director
						search_director = '%' + search_director + '%';
						queryList.add("movies.director like ?\n");
						query_items.add(search_director);
					}
					if(searchYearExist) {
						//search_year
						search_year = '%' + search_year + '%';
						queryList.add("movies.year like ?\n");
						query_items.add(search_year);
					}
					if(searchStarExist) {
						//search_star
						search_star = '%' + search_star + '%';
						queryList.add("s.name like ?\n");
						query_items.add(search_star);
					}
				}
				
				if(queryList.size() >= 1) {
					search_query += queryList.get(0);
					for(int i = 1; i < queryList.size(); i++) {
						search_query += "and " + queryList.get(i);
					}
				}
				
				base_query += search_query + "group by movies.id, movies.title, movies.year, movies.director, r.rating\n";

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
			//limit and page
			base_query += " limit " + itemLimit + " offset " + page;
		
			Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                response.getWriter().println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
			//create a connection type to the database
			
			//start TJ Timer
            long startTjTime = System.nanoTime();
			Connection dbcon = ds.getConnection();

			
			PreparedStatement statement = dbcon.prepareStatement(base_query);
			
			for(int i = 0; i <query_items.size();i++) {
				statement.setString(i+1, query_items.get(i));
			}
			
//			System.out.println(statement.toString());
			
			ResultSet rs = statement.executeQuery();
			long endTjTime = System.nanoTime();
			elapsedTjTime = endTjTime - startTjTime;
//			System.out.println("ContextPath: " + getServletContext().getContextPath());
//			System.out.println("RealPath: " + getServletContext().getRealPath("/"));
//			System.out.println("elapsedTjTime: "+ elapsedTjTime);
			
			//endTJ Timer here
			JsonArray jsonArray = new JsonArray();
			
			while(rs.next()){
				String id = rs.getString("id");
				String title = rs.getString("title");
				String rating = rs.getString("rating");
				String year = rs.getString("year");
				String director = rs.getString("director");
				String genres_id = rs.getString("genre_id"); //.split(",");
				String genres_name = rs.getString("genre_name"); //.split(",");
				String stars = rs.getString("star"); //.split(";");
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("id", id);
				jsonObject.addProperty("title", title);
				jsonObject.addProperty("year",year);
				jsonObject.addProperty("director", director);
//				System.out.println(rating);
				jsonObject.addProperty("rating", rating);
				
				JsonArray genreList = new JsonArray();
				JsonArray starList = new JsonArray();
				
				if(genres_id != null) {
					String[] genreIdList = genres_id.split(",");
					String[] genreNameList = genres_name.split(",");
					for(int i = 0; i < genreIdList.length; i++) {
						JsonObject genreObj = new JsonObject();
						genreObj.addProperty("genre_id", genreIdList[i]);
						genreObj.addProperty("genre_name", genreNameList[i]);
						genreList.add(genreObj);
					}
				}
				
				
				
				
				if(stars != null) {
					String[] sList = stars.split(";");
					for(int i = 0; i < sList.length; i++) {
						JsonObject starObj = new JsonObject();
						String[] star_info = sList[i].split(",");
						starObj.addProperty("star_id", star_info[0]);
						starObj.addProperty("star_name", star_info[1]);
						starList.add(starObj);
					}
				}
				
				
				
				jsonObject.add("genres", genreList);
				jsonObject.add("stars", starList);
				
				jsonArray.add(jsonObject);
				
//				System.out.println(jsonArray.toString());
				
			}
//			System.out.println("working");
			String url = request.getHeader("referer");
			
			HttpSession session = request.getSession();
			session.setAttribute("url", url);
			
//			System.out.println("working");
			
			
			out.write(jsonArray.toString());
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			
			response.setStatus(500);
		}
		out.close();
		long endTsTime = System.nanoTime();
		elapsedTsTime = endTsTime - startTsTime;//elapsed time in nanoseconds.
//		System.out.println("elapsedTsTime: "+ elapsedTsTime);
		String path = getServletContext().getRealPath("/");
		String logFilePath = path + "test";
//		System.out.println(logFilePath);
		File myfile = new File(logFilePath);
		if(!myfile.exists()) {
			myfile.createNewFile();
		}
		
		FileWriter logWriter = new FileWriter(myfile,true);
		BufferedWriter bufferedWriter = new BufferedWriter(logWriter);
		bufferedWriter.write("elapsedTsTime:" + elapsedTsTime + " elapsedTjTime:" + elapsedTjTime);
		bufferedWriter.newLine();
		bufferedWriter.close();
//		logWriter.close();
		
		//END TS Timer
	}
}