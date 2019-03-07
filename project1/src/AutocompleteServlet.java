

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class AutocompleteServlet
 */
@WebServlet(name="AutocompleteServlet", urlPatterns="/api/autocomplete-suggestion")
public class AutocompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AutocompleteServlet() {
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
		
		PrintWriter out = response.getWriter();
		
		try {
			JsonArray jsonArray = new JsonArray();
			
			String title = request.getParameter("query");
			
			if (title == null || title.trim().isEmpty()) {
				out.write(jsonArray.toString());
				return;
			}
			
			Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                response.getWriter().println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
			
			Connection dbcon = ds.getConnection();
						
			String[] words = title.split(" ");
			String base_search = "";
			for(int i = 0; i < words.length;i++) {
				base_search += "+" + words[i];
			}
			base_search += "*";
			
			String query = String.format("select movies.id, movies.title from movies where match(movies.title) against('%s' in boolean mode) limit 10\n", base_search);
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				String movieTitle = rs.getString("title");
				String movieId = rs.getString("id");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("value", movieTitle);
				
				JsonObject jsonData = new JsonObject();
				jsonData.addProperty("id", movieId);
				
				jsonObject.add("data", jsonData);
				
				jsonArray.add(jsonObject);
			}
			
			out.write(jsonArray.toString());
			
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			
			response.setStatus(500);
		}
		
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		doGet(request, response);
//	}

}
