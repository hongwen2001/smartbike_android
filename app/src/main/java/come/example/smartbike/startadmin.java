package come.example.smartbike;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

public class startadmin extends AppCompatActivity {
    private ImageView bike,S,M,A,RR,T,B,I,K,E;
    private ObjectAnimator anim1,SS,MM,AA,RRR,TT,BB,II,KK,EE;
    private AnimatorSet animatorSet;
    private SoundPool.Builder soundpool= null;;
    private SoundPool soundPool;
    private int high,musicId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startadmin);
        //找出物件
        bike = findViewById(R.id.bike);
        S=findViewById(R.id.S);
        M=findViewById(R.id.M);
        A=findViewById(R.id.A);
        RR=findViewById(R.id.RR);
        T=findViewById(R.id.T);
        B=findViewById(R.id.B);
        I=findViewById(R.id.I);
        K=findViewById(R.id.K);
        E=findViewById(R.id.E);
        //定義smartbike透明度
        S.setAlpha(0f);
        M.setAlpha(0f);
        A.setAlpha(0f);
        RR.setAlpha(0f);
        T.setAlpha(0f);
        B.setAlpha(0f);
        I.setAlpha(0f);
        K.setAlpha(0f);
        E.setAlpha(0f);
        //取得頻幕高度
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d(metrics.heightPixels + "", "onAnimationEnd: ");
        high = metrics.heightPixels;
        //bike圖示定位
        bike.setY(-(high*2/3+480));
        //動畫集合
        animatorSet=new AnimatorSet();
        //版本判斷
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //讀取音檔
            soundpool = new SoundPool.Builder();
            soundpool.setMaxStreams(2);
            AudioAttributes audioAttributes=new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool=soundpool.setAudioAttributes(audioAttributes).build();
            musicId=soundPool.load(getApplicationContext(),R.raw.start_4s,1);
            //監聽讀取狀態
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status==0){
                        //讀取完成建構動畫
                        setAnim1();
                    }
                }
            });
        }
    }
    private void setAnim1(){
        //smartbike字母動畫建構
        SS=ObjectAnimator.ofFloat(S,"alpha",0,1);
        SS.setDuration(200);
        MM=ObjectAnimator.ofFloat(M,"alpha",0,1);
        MM.setDuration(200);
        AA=ObjectAnimator.ofFloat(A,"alpha",0,1);
        AA.setDuration(200);
        RRR=ObjectAnimator.ofFloat(RR,"alpha",0,1);
        RRR.setDuration(200);
        TT=ObjectAnimator.ofFloat(T,"alpha",0,1);
        TT.setDuration(200);
        BB=ObjectAnimator.ofFloat(B,"alpha",0,1);
        BB.setDuration(200);
        II=ObjectAnimator.ofFloat(I,"alpha",0,1);
        II.setDuration(200);
        KK=ObjectAnimator.ofFloat(K,"alpha",0,1);
        KK.setDuration(200);
        EE=ObjectAnimator.ofFloat(E,"alpha",0,1);
        EE.setDuration(200);
        anim1 = ObjectAnimator.ofFloat(bike, "translationY", -(high * 2 / 3 + 489), -high/2, 0);
        anim1.setInterpolator(new BounceInterpolator());
        anim1.setDuration(1000);
        //排列動畫順序
        animatorSet.playSequentially(SS,MM,AA,RRR,TT,BB,II,KK,EE,anim1);
        //監聽動畫
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                //捕捉動畫開始播放，音樂開始播放
                soundPool.play(musicId,1,1,10,0,1);
                Log.d("dfsdfsfsf", "onAnimationStart: ");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //捕捉動畫結束播放，跳轉登入activity
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent();
                                intent.setClass(startadmin.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }).start();
            }
        });
        //動畫播放
        animatorSet.start();
    }
}