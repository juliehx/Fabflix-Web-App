import com.google.gson.Gson;
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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.ResultSet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ShowMetadataServlet
 */
@WebServlet(name = "ShowMetadataServlet", urlPatterns = "/api/show-metadata")
public class ShowMetadataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name="jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowMetadataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		String dbName = "moviedb";
		
		try {
			
			Connection dbcon = dataSource.getConnection();
			
			DatabaseMetaData meta = dbcon.getMetaData();
			
			String[] types = {"TABLE"};
			ResultSet tables = meta.getTables(dbName,null, "%", types);
			
			HashMap <String,ArrayList<HashMap<String,String>>> metaData = new HashMap<String,ArrayList<HashMap<String,String>>>();
			
			while(tables.next()) {
				
				String table_name = tables.getString("TABLE_NAME");
				ResultSet columns = meta.getColumns(dbName,null, table_name, "%");//get column name and type of column
				ArrayList<HashMap<String,String>> column_info_list = new ArrayList<HashMap<String,String>>();
				
				while(columns.next()) {
					HashMap <String,String> column_info = new HashMap<String,String>();
					String column_name = columns.getString("COLUMN_NAME");
					String data_type = columns.getString("COLUMN_SIZE");
					column_info.put("columnName", column_name);
					column_info.put("dataType",data_type);
					column_info_list.add(column_info);
				}
				
				columns.close();
				metaData.put(table_name, column_info_list);		
				
			}
			
			Gson gson = new Gson();
			String data = gson.toJson(metaData);
			response.getWriter().write(data.toString());
			
			response.setStatus(200);
			
			tables.close();
			dbcon.close();
			
		}catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "Fail");
			jsonObject.addProperty("message", "Failed to retrieve Database Metadata.");
			response.getWriter().write(jsonObject.toString());
		}
	}

}
