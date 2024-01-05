package myschoolapp.com.gsnedutech.Util.zoomConst;

public interface Constants {

	// TODO Change it to your web domain
    String WEB_DOMAIN = "zoom.us";

	// TODO change it to your user ID
    String USER_ID = "Your user ID from REST API";
	
	// TODO change it to your token
    String ZOOM_ACCESS_TOKEN = "Your zak from REST API";
	
	// TODO Change it to your exist meeting ID to start meeting
    String MEETING_ID = "86756718641";

    /**
     * We recommend that, you can generate jwttoken on your own server instead of hardcore in the code.
     * We hardcore it here, just to run the demo.
     *
     * You can generate a jwttoken on the https://jwt.io/
     * with this payload:
     * {
     *     "appKey": "string", // app key
     *     "iat": long, // access token issue timestamp
     *     "exp": long, // access token expire time
     *     "tokenExp": long // token expire time
     * }
     */
    public final static String SDK_JWTTOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJWTk1qbHBNbFJnMjJRUWdUMGpZWjF3IiwiZXhwIjoxNjExMTQ4NTI0ODE0LCJpYXQiOjE2MTExNDc5MjR9.SlG7y7LIkHNhZZG-vs_wqmpwF1HJcthHfClnqjIF_2Q";

}
