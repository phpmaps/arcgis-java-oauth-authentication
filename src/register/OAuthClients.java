package register;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

public class OAuthClients {
	
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
