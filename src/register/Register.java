package register;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	//CSV file header
	private static final String FILE_HEADER = "system_account_number,vendor,client_id,root_url";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String inputArcGISClientId = request.getParameter("inputArcGISClientId");
		String inputArcGISRootUrl = request.getParameter("inputArcGISRootUrl");

		if(inputArcGISClientId == "" || inputArcGISClientId == null){
			inputArcGISClientId = "Unknown";
		}
		if(inputArcGISRootUrl == "" || inputArcGISRootUrl == null){
			inputArcGISRootUrl = "Unknown";
		}
		
		long timestamp = System.currentTimeMillis();
		JSONArray json = new JSONArray();

		Writer writer = null;
		FileWriter fileWriter = null;
		
		try {
	    	System.out.println("The registered_oauth_applications.csv is located here: " + new java.io.File("").getAbsolutePath());
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("registered_oauth_applications.csv"), "utf-8"));
			fileWriter = new FileWriter("registered_oauth_applications.csv");

			//Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());
			
			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			//Write a new student object list to the CSV file
			fileWriter.append("001"); //Example of some internal system number
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append("ArcGIS"); //Example of a vendor that uses the OAuth2 spec
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(inputArcGISClientId);
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(inputArcGISRootUrl);
			fileWriter.append(NEW_LINE_SEPARATOR);

			System.out.println("CSV file was created successfully !!!");
			JSONObject jsono = new JSONObject();
			jsono.put("inputArcGISClientId", inputArcGISClientId);
			jsono.put("inputArcGISRootUrl", inputArcGISRootUrl);
			jsono.put("timestamp",timestamp);
			json.put(jsono);
			System.out.println(json.toString());
			//response.setContentType("application/json");
			response.setContentType("text/plain");
			response.setDateHeader("Last-Modified",(System.currentTimeMillis()/1000*1000));
			response.setCharacterEncoding("UTF-8"); 
			response.getWriter().write(json.toString()); 
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			
			try {
				writer.close();
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public String getArcGISClientId(){
		String result = null;
		String csvFile = "registered_oauth_applications.csv";
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
				String vendor = lineArray[1].toString();
				if(vendor.equals("ArcGIS")){
					System.out.println("[client_id= " + lineArray[2] + " , root_url=" + lineArray[3] + "]");
					jsono.put("inputArcGISClientId", lineArray[2]);
					jsono.put("inputArcGISRootUrl", lineArray[3]);
				}
			}
			System.out.println(jsono.toString());
			result = jsono.toString();

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
		return result;
	  }

}
