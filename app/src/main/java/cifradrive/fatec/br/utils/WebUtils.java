package cifradrive.fatec.br.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import cifradrive.fatec.br.R;
import cifradrive.fatec.br.models.WebRequest;

public class WebUtils {
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "PHPSESSID";

    private static WebUtils instance;
    private SharedPreferences preferences;
    private RequestQueue queue;
    private Context ctx;

    private WebUtils(Context context) {
        ctx = context;
        queue = getRequestQueue();
        preferences = context.getApplicationContext().getSharedPreferences( ctx.getString( R.string.preferences_file ),Context.MODE_PRIVATE);
    }

    public synchronized static WebUtils getInstance(Context context) {
        if (instance == null) {
            instance = new WebUtils(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue( ctx.getApplicationContext() );
            RequestQueue.RequestFinishedListener<String> getSessionCookieListener = new RequestQueue.RequestFinishedListener<String>() {
                @Override
                public void onRequestFinished(Request<String> request) {
                    checkRequestSessionCookie( (WebRequest) request);
                }
            };
            queue.addRequestFinishedListener(getSessionCookieListener);
        }
        return queue;
    }

    /**
     * Checks the request for session cookie and saves it
     * if it finds it.
     */
    private void checkRequestSessionCookie(WebRequest request) {
        String cookie = request.getCookieValue();
        if( cookie != null && !cookie.isEmpty() ){
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putString(SESSION_COOKIE, cookie);
            prefEditor.apply();
        }
    }

    /**
     * Adds session cookie to headers if exists.
     */
    private void addSessionCookie(WebRequest request) {
            String sessionId = preferences.getString(SESSION_COOKIE, "");
            assert sessionId != null;
            if (sessionId.length() > 0) {
                request.addCookieToRequest(COOKIE_KEY, SESSION_COOKIE, sessionId);
            }
    }

    public void addToRequestQueue(WebRequest req) {
        req.keysForCheckingCookie(SET_COOKIE_KEY, SESSION_COOKIE);
        addSessionCookie(req);
        getRequestQueue().add(req);
    }
}
