package connect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import register.Register;
import signin.Signin;

/**
 * Servlet implementation class Connect
 */
@WebServlet("/Connect")
public class Connect extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public String portal = "https://www.arcgis.com";
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	public HttpSession session = null;
  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Connect() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		session = request.getSession(true);
		String code = request.getParameter("code");
		if(code == "" || code == null){
			code = "Unknown";
		}
		System.out.println(code);
		Map<String,Object> params = new LinkedHashMap<>();
		params.put("client_id", "UziRXpkEKRl7ATU8");
		params.put("code", code);
		params.put("grant_type", "authorization_code");
		params.put("redirect_uri", "http://localhost:8080/Handlers/Connect");
		String oauth2TokenUrl = portal + "/sharing/rest/oauth2/token/";
		String codeResponse = this.runPost(params, oauth2TokenUrl);
		System.out.println(codeResponse);
		JSONObject codeResponseObj = new JSONObject(codeResponse);
		this.persistTokens(codeResponseObj);
		response.sendRedirect("http://localhost:8080/Handlers/index.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public String runPost(Map<String,Object> params, String request) throws IOException{
		StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
		int    postDataLength = postDataBytes.length;
		URL    url            = new URL( request );
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
           
		return response;
	}
	
	@SuppressWarnings("resource")
	public void persistTokens(JSONObject codeResponseObj) throws IOException{
		Signin signIn = new Signin();
		String userprofile = String(session.getAttribute("userprofile"));
		JSONObject userprofileaObj = new JSONObject(userprofile);
		String uid = userprofileaObj.getString("uid");
		Boolean hasArcGISConnection = signIn.isUserConnectedToArcGIS(uid);
		if(hasArcGISConnection){
			return;
			//String content = this.getSecureArcGISContent();
		}

		String access_token = codeResponseObj.getString("access_token");
		String username = codeResponseObj.getString("username");
		String refresh_token = codeResponseObj.getString("refresh_token");
		String grant_type = "refresh_token";
		Register register = new Register();
		String registered_accounts = register.getArcGISClientId();
		JSONObject registered_accounts_obj = new JSONObject(registered_accounts);
		String client_id = registered_accounts_obj.getString("inputArcGISClientId");
		
		
		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter("connected_oauth_accounts.csv", true));
		bw.write("\n");
		bw.write(uid + "," + "ArcGIS," + username + "," + grant_type + "," + refresh_token + "," + client_id);
		bw.newLine();
	    bw.flush();

		session.setAttribute("arcgis_access_token", access_token);
		System.out.println("CSV file was created successfully !!!");
		String t = (String)session.getAttribute("arcgis_access_token");
		System.out.println(t);
	}

	private String String(Object attribute) {
		// TODO Auto-generated method stub
		return null;
	}

}
