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
 * Servlet implementation class AddMovieServlet
 */
@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/add-movie")
public class AddMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddMovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		
		String movie_title = request.getParameter("movie_title");
		String movie_year = request.getParameter("movie_year");
		String movie_director = request.getParameter("movie_director");
		
		String star_name = request.getParameter("star_name");
		String star_year = request.getParameter("star_year");
		if(star_year == "") {star_year = null;}

//		System.out.println("birthYear: " + star_year);
		
		String genre = request.getParameter("genre");
		
		try{
			
			Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                response.getWriter().println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb-write");
			
			Connection dbcon = ds.getConnection();
		
			String query = "call moviedb.add_movie(?,?,?,?,?,?);";
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			
			statement.setString(1, movie_title);
			statement.setString(2, movie_year);
			statement.setString(3, movie_director);
			statement.setString(4, star_name);
			statement.setString(5, star_year);
			statement.setString(6, genre);
//			System.out.println("this is wack");
			int rows = statement.executeUpdate();
//			System.out.println("NUM ROWS: " + rows);
			if(rows == 1) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", "success");
				jsonObject.addProperty("message", "Successfully Added Movie: " + movie_title);
				
				response.getWriter().write((jsonObject.toString()));
				response.setStatus(200);
			}
			statement.close();
			dbcon.close();
			
		}catch (Exception e) {
			System.out.println(e);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", "Movie Already Exists");
			response.getWriter().write(jsonObject.toString());
		}
	}


}
