package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.sap.requestTypes.HttpRequest;
import com.sap.requestTypes.HttpPostRequest;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class StartThread
 */
@WebServlet("/")
public class StartThread extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StartThread() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 try
	       {
	      
	      
		// TODO Auto-generated method stub
		String[] typesArray = new String[]{"PerPerson","PayComp","EmpJob","EmpEmployment","EmpCompensation","PerPersonal","PerEmail"};
	//	String[] typesArray = new String[]{"PerPerson"};
		String id = request.getParameter("userID");
		Thread myThreads[] = new Thread[typesArray.length];
		System.out.println("Thread started....");
		String output = "";
		//response.getWriter().append("Thread started....");
		if(id != null){
	  
			for(int i=0; i<typesArray.length; i++) {
				myThreads[i] = new HttpRequest(id,typesArray[i]);
				myThreads[i].start();
				}
				for (int j = 0; j < typesArray.length; j++) {
				    
				    try{
				    	myThreads[j].join();
					    }catch(InterruptedException ie){}
					 
					    String result = ((HttpRequest) myThreads[j]).getResult();
					  
					    if(result != null){
					    
					    if(j == 0){
					    	
					    	output = output + "{" + '"' + "root"  + '"' + ":";
					    	output = output + result.substring(0, result.length()-1).replace("feed", typesArray[j]);
					    	
					    } else {
					    	
					    	output = output + result.substring(1, result.length()-1).replace("feed", typesArray[j]);
					    }
					    		    	    
					    if(j < typesArray.length - 1 ){
					    	 
					    	output = output + ",";
					    	
					    } else if (j == typesArray.length - 1) {
					    	
					    	output = output + "}";
					    	output = output + "}";
					    }
					    }
				}
				
				output = output.replaceFirst(",", "{");
				response.getWriter().append(output);
	    }
		
		System.out.println("Done");
		//response.getWriter().append("Done");
	       }
	       catch(Exception e)
	       {
	    	   response.getWriter().append(e.getMessage());
	       }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			  throws ServletException, IOException {
			
			  String body = null;
			 
			  try {
				 body = getBody(request);
			  } catch (Exception e) {}

			  try {
			  //  JSONObject jsonObject = JSONObject.fromObject(jb.toString());
			
				  HttpPostRequest httpPostRequest = new HttpPostRequest(body);
				  httpPostRequest.start();
				  System.out.println("Thread started....");
				  			  
				  try{
					  	httpPostRequest.join();
					    }catch(InterruptedException ie){}
				  
				  		String result = httpPostRequest.getResult();
					 
				  		response.getWriter().append(result);
				  
			  } catch (ParseException e) {
			    // crash and burn
			    throw new IOException("Error parsing JSON request string");
			  }
			  System.out.println("Done");
			  // Work with the data using methods like...
			  // int someInt = jsonObject.getInt("intParamName");
			  // String someString = jsonObject.getString("stringParamName");
			  // JSONObject nestedObj = jsonObject.getJSONObject("nestedObjName");
			  // JSONArray arr = jsonObject.getJSONArray("arrayParamName");
			  // etc...
			}
	
	public static String getBody(HttpServletRequest request) throws IOException {

	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}

}
