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
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * Servlet implementation class SingleStarServlet
 */
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SingleStarServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
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
		
		try {
			Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                response.getWriter().println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
            
			Connection dbcon = ds.getConnection();
			String query = "select s.id,s.name,s.birthYear,group_concat(distinct m.id, ',' , m.title separator ';') as moviegroup " +
					"from stars_in_movies sim, movies m, stars s " +
					"where sim.movieId = m.id and sim.starId = ? and s.id = ? ";
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);
			statement.setString(2, id);
			
			ResultSet rs = statement.executeQuery();
			
			JsonArray jsonArray = new JsonArray();
			
			rs.next();
			
			String star_id = rs.getString("id");
			String name = rs.getString("name");
			String birthYear = rs.getString("birthYear");
			String[] movieStringList = rs.getString("moviegroup").split(";");
			
			JsonArray movieList = new JsonArray();
			
			for(int i = 0; i < movieStringList.length; i++) {
				JsonObject movieObj = new JsonObject();
				String[] movie_info = movieStringList[i].split(",");
				movieObj.addProperty("movie_id", movie_info[0]);
				movieObj.addProperty( "movie_name", movie_info[1]);
				movieList.add(movieObj);
			}
			
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", star_id);
			jsonObject.addProperty("name", name);
			jsonObject.addProperty("birthYear", birthYear);
			jsonObject.add("movies",movieList);
			
			HttpSession session = request.getSession();
			String url = (String) session.getAttribute("url");
			jsonObject.addProperty("url", url);
						
			jsonArray.add(jsonObject);
			
			out.write(jsonArray.toString());
			
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
			
		}catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			
			response.setStatus(500);
		}
		out.close();
	}
}
