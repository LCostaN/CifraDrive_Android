package cifradrive.fatec.br.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WebRequest extends StringRequest {
    private String cookie;
    private String setCookie, cookieKey, sessionCookie, sessionId;

    public WebRequest(int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Map<String, String> headers = response.headers;
        Log.v("IN_HEADERS", headers.toString() );
        if ( headers.containsKey(setCookie) && headers.get(setCookie).startsWith(sessionCookie) ) {
            String cookie = headers.get(setCookie);
            Log.d("COOKIE_HEADER_IN", cookie);
            assert cookie != null;
            if (cookie.length() > 0) {
                String[] splitCookie = cookie.split(";");
                String[] splitSessionId = splitCookie[0].split("=");
                cookie = splitSessionId[1];
                this.cookie = cookie;
            }
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if ( headers == null || headers.equals( Collections.emptyMap() ) ) {
            headers = new HashMap<>();
        }

        if( ( sessionCookie != null && !sessionCookie.isEmpty() ) && ( cookieKey != null &&
                !cookieKey.isEmpty() ) && ( sessionId != null && !sessionId.isEmpty() ) ){
            headers = AddCookie(headers);
        }

        Log.d("HEADERS_OUT", headers.toString() );
        return headers;
    }

    private Map<String, String> AddCookie(Map<String, String> headers){
        StringBuilder builder = new StringBuilder();
        builder.append(sessionCookie);
        builder.append("=");
        builder.append(sessionId);
        if (headers.containsKey(cookieKey)) {
            builder.append("; ");
            builder.append(headers.get(cookieKey));
        }
        headers.put(cookieKey, builder.toString());

        return headers;
    }

    public void keysForCheckingCookie(String setCookie, String sessionCookie){
        this.setCookie = setCookie;
        this.sessionCookie = sessionCookie;
    }

    public void addCookieToRequest(String cookieKey, String sessionCookie, String sessionId){
        this.cookieKey      = cookieKey;
        this.sessionCookie  = sessionCookie;
        this.sessionId      = sessionId;
    }

    public String getCookieValue(){
        return cookie;
    }
}
