package signin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import connect.Connect;

/**
 * Servlet implementation class Signin
 */
@WebServlet("/Signin")
public class Signin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpSession session = null;
	public Connect connect = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Create a session object if it is already not  created.
	    session = request.getSession(true);

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		if(username == "" || username == null){
			username = "Unknown";
		}
		if(password == "" || password == null){
			password = "Unknown";
		}
		
		String result = null;
		String csvFile = "user-store.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		JSONObject jsono = new JSONObject();

		try {

			br = new BufferedReader(new FileReader(csvFile));
			int iteration = 0;
			while ((line = br.readLine()) != null) {
			    if(iteration == 0) {
			        iteration++;  
			        continue;
			    }
				String[] lineArray = line.split(cvsSplitBy);
				System.out.println(lineArray[1]);
				if(lineArray[1].toString().equals(username) && lineArray[2].toString().equals(password)){
					
					jsono.put("uid", lineArray[0]);
					jsono.put("username", lineArray[1]);
					
					Boolean hasArcGISConnection = this.isUserConnectedToArcGIS(lineArray[0]);
					if(hasArcGISConnection){
						String access_token = (String)session.getAttribute("arcgis_access_token");
						jsono.put("access_token", access_token);
						//String content = this.getSecureArcGISContent();
					}
					session.setAttribute("userprofile", jsono.toString());
				}
			}
			System.out.println(jsono.toString());
			
			
			//String user = (String)session.getAttribute("user");
			
			
			
			result = jsono.toString();
			response.setContentType("application/json");
			//response.setContentType("text/plain");
			response.setDateHeader("Last-Modified",(System.currentTimeMillis()/1000*1000));
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write(result);
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private String getSecureArcGISContent() throws IOException {
		String access_token = (String)session.getAttribute("arcgis_access_token");
		Map<String,Object> params = new LinkedHashMap<>();
		params.put("f", "json");
		params.put("token", access_token);
		params.put("studyAreas", "[{\"geometry\":{\"x\":-117.1956,\"y\":34.0572}}]");
		String secureArcGISContent = connect.runPost(params, "http://geoenrich.arcgis.com/arcgis/rest/services/World/GeoenrichmentServer/Geoenrichment/enrich");
		//JSONObject refreshResponseObj = new JSONObject(refreshResponse);
		//String accessToken = refreshResponseObj.getString("access_token");
		return secureArcGISContent;
	}

	public Boolean isUserConnectedToArcGIS(String uid) throws IOException {
		String csvFile = "connected_oauth_accounts.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		JSONObject jsono = new JSONObject();
		try {
			br = new BufferedReader(new FileReader(csvFile));
			int iteration = 0;
			while ((line = br.readLine()) != null) {
			    if(iteration == 0) {
			        iteration++;  
			        continue;
			    }
				String[] lineArray = line.split(cvsSplitBy);
				if(lineArray[0].toString().equals(uid)){
					this.refreshToken(lineArray[5].toString(),lineArray[4].toString(),lineArray[3].toString());
					return true;
				}else{
					return false;
				}
			}
			System.out.println(jsono.toString());


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void refreshToken(String client_id, String refresh_token , String grant_type) throws IOException {
		connect = new Connect();
		Map<String,Object> params = new LinkedHashMap<>();
		params.put("client_id", client_id);
		params.put("grant_type", grant_type);
		params.put("refresh_token", refresh_token);
		String oauth2TokenUrl = connect.portal + "/sharing/rest/oauth2/token/";
		String refreshResponse = connect.runPost(params, oauth2TokenUrl);
		JSONObject refreshResponseObj = new JSONObject(refreshResponse);
		String accessToken = refreshResponseObj.getString("access_token");
		session.setAttribute("arcgis_access_token", accessToken);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
