<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" session="true"%>
<%@ page import="java.util.*, register.OAuthClients" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Home | Signin</title>
    <link crossorigin="anonymous" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" rel=
    "stylesheet">
    <style>
    #top-menu {
    	float:right;
    }
    #top-menu a {
    	margin:10px;
    }
    .center {
    	display: table;
    	margin: 0 auto;
    }
    
    .login {
    	width:500px;
    }
    
    #connect{
    	display:none;
    }
    </style>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    

    
    <%		
	OAuthClients client = new OAuthClients();
	String arcgis_client_id = client.getArcGISClientId();
	System.out.println(arcgis_client_id);		
	%>
	
	<script>
		var appConfig = <% out.print(arcgis_client_id);%>;
	</script>
	
	<%
	HttpSession sess = request.getSession(true);
	String user = "{}";
	try{
		user = (String) sess.getAttribute("userprofile");
	}catch(Exception e){
		System.out.println(e);
	}
	
	%>
	
	<script>
		var userConfig = <% out.print(user);%>;
	</script>
	
    <script>
    $(document).ready(function() {
	    $("#signin").submit(function(event) {
	        console.log(event);
	        //event.preventDefault();
	        var username = event.target[0].value;
	        var password = event.target[1].value;
	        var jqxhr = $.get( "Signin?username="+username+"&password="+password, function(msg) {
	            $("#message").html("Login success!");
	            $("#message-json").html(msg);
	            }).done(function(msg) {
	            }).fail(function(error) {
	            }).always(function(msg) {
	            });
	    });
	    
	    //Display the connect button if the user is signed in and if AcGIS has been registered.
	    
		if(userConfig.uid != null && appConfig.inputArcGISClientId != null){
			 $("#connect").show();
		}
	    
    });
    </script>
    
</head>
<body>
    <div class="row">
        <div class="col-xs-12">
        <div id="top-menu">
        
			
      		<a class="btn btn-default" href="#" role="button">Existing Sign-in</a>
        	<a class="btn btn-default" href="/Handlers/register.html" role="button">Register Portal for ArcGIS</a>
        	<a id="connect" class="btn btn-default" href="https://www.arcgis.com/sharing/oauth2/authorize?client_id=UziRXpkEKRl7ATU8&response_type=code&expiration=-1&redirect_uri=http://localhost:8080/Handlers/Connect" role="button">Connect ArcGIS Account</a>

        
        </div>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-12">
        <div class="center"><h3>ArcGIS Server Side Authentication Example </h3></div>
        <div class="center"><p>Enable non-Esri accounts to authenticate into ArcGIS on-premises using OAuth2 specification concepts.</p>
        </div>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-12">
            <form class="center login" id=signin>
                <div class="form-group">
                    <label for="exampleInputEmail1">Username</label> <input class="form-control" id="username" placeholder="Username" type="input">
                </div>
                <div class="form-group">
                    <label for="password">Password</label> <input class="form-control" id="password" placeholder="Password" type="password">
                </div><button class="btn btn-default" type="submit">Submit</button>
            </form>
        </div>
        <div class="row"></div>
        <div class="col-xs-12">
        <div class="center">
            <p id="message"></p>
            <p id="message-json"></p>
        </div>
        </div>
    </div>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js">
    </script>
</body>
</html>