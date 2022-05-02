package come.example.smartbike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Hall extends AppCompatActivity {
    private TextView textView;
    private Button button;
    private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        button=findViewById(R.id.button);
        button.setOnClickListener(sign_out);
    }
    private View.OnClickListener sign_out=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signOut();
        }
    };
    private void signOut() {
        LoginManager.getInstance().logOut();
//        googleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Intent intent=new Intent();
//                        intent.setClass(Hall.this,MainActivity.class);
//                        startActivity(intent);
//                    }
//                });
        Intent intent=new Intent();
        intent.setClass(Hall.this,MainActivity.class);
        startActivity(intent);

    }
}