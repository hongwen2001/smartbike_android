package come.example.smartbike;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private HttpURLConnection httpURLConnection;
    private URL url;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private WebView webView;
    private ImageButton imageButton;
    AlertDialog alertDialog;
    private GoogleSignInClient googleSignInClient;
    private SignInButton googlesign;
    private LoginButton Fb_sign_in_button;
    int RC_SIGN_IN = 10;
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FB登入設定
        callbackManager=CallbackManager.Factory.create();
        Fb_sign_in_button=(LoginButton)findViewById(R.id.Fb_login_button);

        Fb_sign_in_button.setOnClickListener(Fb_sign_event);
        //建立google登入
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        googlesign = findViewById(R.id.google_sign);
        googlesign.setSize(SignInButton.SIZE_STANDARD);
        googlesign.setOnClickListener(googlesign_event);


        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignInResult(task);
            }
        });
        //smartbike登入設定
        imageButton = findViewById(R.id.login);
        imageButton.setOnClickListener(smartbike_event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
        AccessToken accessToken= AccessToken.getCurrentAccessToken();
        if (accessToken!=null){
            Log.d("已經登入了", "onStart: ");
        }
        if (accessToken!=null && !accessToken.isExpired()){
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,Hall.class);
            startActivity(intent);

        }
        if (account!=null){
            Log.d("有帳號", "onStart: ");
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,Hall.class);
            startActivity(intent);

        }
    }

    public View.OnClickListener smartbike_event = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View wview = LayoutInflater.from(getApplicationContext()).inflate(R.layout.loginandregister, null, false);
            webView = wview.findViewById(R.id.webview);
            //        webView = findViewById(R.id.webview);
            String urlstring = "http://172.18.8.158:8082/login";
            try {
                url = new URL(urlstring);
                Log.d(String.valueOf(url), "--------------------------------");
                webView.loadUrl(String.valueOf(url));
                WebSettings webSettings = webView.getSettings();
                webView.requestFocusFromTouch();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("?code=")) {
                            Log.d("成功", "onPageFinished: ");
                            Uri uri = Uri.parse(url);
                            Log.d("成功", "onPageFinished:" + uri.getQueryParameter("code"));

                            get_access_token(uri.getQueryParameter("code"));

                        } else {
                            Log.d("失敗??", "onPageFinished: " + url);
                        }
                    }
                });
            } catch (MalformedURLException e) {
                Log.d("報錯", "onClick: ");
                e.printStackTrace();

            }
            Log.d("這裡是1", "onClick: ");
            alertDialog = new AlertDialog.Builder(MainActivity.this).setView(wview).setCancelable(false).show();
            alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
    };

    public void get_access_token(String token) {
        alertDialog.dismiss();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://172.18.8.158:8082/oauth/token");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("grant_type", "authorization_code");
                    jsonObject.put("client_id", "8");
                    jsonObject.put("client_secret", "aDRvzpid0Vu7tUi39yss5IDqtcyCUVRDf2KmnFst");
                    jsonObject.put("redirect_uri", "http://172.18.8.158:8082/authorize2/callback");
                    jsonObject.put("code", token);
                    printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                    printWriter.write(jsonObject.toString());
                    printWriter.flush();
                    Log.d("statu", "" + httpURLConnection.getResponseCode());
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    jsonObject = new JSONObject(bufferedReader.readLine());
                    Log.d("", "run:" + jsonObject.toString());
                    String accesstoken = jsonObject.getString("access_token");
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    url = new URL("http://172.18.8.158:8082/api/v1/testin");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Authorization", "Bearer " + accesstoken);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.connect();
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    Intent intent = new Intent(MainActivity.this, Hall.class);
                    intent.putExtra("data", bufferedReader.readLine());
                    intent.putExtra("accesstoken", accesstoken);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.startActivity(intent);
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private View.OnClickListener googlesign_event = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sign_google();
        }
    };
    private View.OnClickListener Fb_sign_event=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList());
        }
    };

    void sign_google() {
        Intent intent = googleSignInClient.getSignInIntent();
        intentActivityResultLauncher.launch(intent);
//        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RC_SIGN_IN) {
            Log.d("來過", "onActivityResult: ");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(""+account.getEmail(), "handleSignInResult: ");
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,Hall.class);
            startActivity(intent);
        } catch (ApiException e) {
            Log.d("錯誤", "handleSignInResult: ");
        }
    }

//    private void signOut() {
//        googleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Intent intent=new Intent();
//                        intent.setClass(MainActivity.this,Hall.class);
//                        startActivity(intent);
//                    }
//                });
//    }
}
