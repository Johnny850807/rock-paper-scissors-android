package com.afinal.youjinchen.rock_paper_scissors;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Callback{
    private static final Map<Integer, Fist> fistMap = new HashMap<>();
    private static final Map<Integer, String> msgMap = new HashMap<>();
    private static final String SHPNAME = "右津是老闆";
    private static final String LASTED = "飲料真棒";
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Player you;
    private Game game;
    private TextView infoTxt;
    private ImageView aiFistImg;
    private EditText nameTxt;
    private boolean waitingForResponse = false;
    private boolean gameStarted = false;
    private View animatedView;

    static {
        fistMap.put(R.id.s, Fist.SCISSORS);
        fistMap.put(R.id.r, Fist.ROCK);
        fistMap.put(R.id.p, Fist.PAPER);

        msgMap.put(Game.YOU, "恭喜你這輪猜贏了！");
        msgMap.put(Game.AI, "這輪你猜輸囉！");
        msgMap.put(Game.DUEL, "這場平手！");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (detectLastedPlay())
        {
            this.mediaPlayer = MediaPlayer.create(this, R.raw.song);
            findViews();
            showDialogAskingName();
            startGame();
            animationInfo();
        }
    }

    private boolean detectLastedPlay() {
        SharedPreferences sp = getSharedPreferences(SHPNAME, MODE_PRIVATE);
        long lasted = sp.getLong(LASTED, -1);
        if (lasted != -1)
        {
            Log.d("myLog", "Lasted play: " + new Date(lasted));
            long intervalDays = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toDays(lasted);
            if (intervalDays < 1)
            {
                evitPlayerToSleep();
                return false;
            }
        }
        sp.edit().putLong(LASTED, System.currentTimeMillis()).apply();
        return true;
    }

    private void evitPlayerToSleep(){
        new AlertDialog.Builder(this)
                .setTitle("你今天已經玩過了吧")
                .setMessage("請隔天再來試試手氣喔！每天一猜，猜出你今天運勢呵呵。")
                .setCancelable(false)
                .setPositiveButton("去睡覺", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }


    private void findViews() {
        nameTxt = new EditText(this);
        aiFistImg = (ImageView) findViewById(R.id.aiFist);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(35, 22, 35, 22);
        nameTxt.setLayoutParams(lp);
        nameTxt.setHint("輸入你的大名");
        infoTxt = (TextView) findViewById(R.id.infoTxt);
    }

    private void showDialogAskingName() {
        new AlertDialog.Builder(this)
                .setTitle("設置你的姓名！")
                .setView(nameTxt)
                .setCancelable(false)
                .setPositiveButton("開始遊戲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startGame();
                    }
                })
                .show();
    }

    private void startGame() {
        game = new Game(you = new HumanPlayer() {
            @Override
            public String getName() {
                return nameTxt.getText().toString();
            }
        });
        game.start(this);
    }

    public void onFistClick(View view) {
        if (!waitingForResponse)
        {
            waitingForResponse = true;
            aiFistImg.setImageResource(R.drawable.baby2);
            Fist yourFist = fistMap.get(view.getId());
            game.decide(yourFist);
            animatedView = view;
            animatedView.animate().setDuration(800).translationY(-100).start();
        }
    }

    @Override
    public void onOneRoundFightOver(Fist yourFist, final Fist aiFist, final int result, final int round) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                animatedView.animate().setDuration(800).translationY(50).start();
                aiFistImg.setImageResource(R.drawable.bossbaby);
                waitingForResponse = false;
                aiFistImg.setImageResource(aiFist == Fist.PAPER ? R.drawable.p : aiFist == Fist.SCISSORS ? R.drawable.s : R.drawable.r);
                createAndShowDialogAfterMoment(round, result);
            }
        });
    }

    private void createAndShowDialogAfterMoment(final int round, final int result) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("第" + round + "回")
                        .setMessage(msgMap.get(result))
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                aiFistImg.setImageResource(R.drawable.bossbaby);
                            }
                        })
                        .show();
            }
        }, 1300);
    }

    @Override
    public void onGameOver(final int yourScore, final int aiScore) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String resultMsg = yourScore > aiScore ? "你贏了" : yourScore == aiScore ?
                        "平手" : "你輸了";
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(resultMsg)
                        .setMessage(String.format(Locale.TAIWAN, "%s 今天一共拿了 %d 分，好好地為今天努力吧！", you.getName(), yourScore))
                        .setCancelable(false)
                        .setPositiveButton("睡覺去", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onGameStarted() {
        gameStarted = true;
        Toast.makeText(getApplicationContext(), "遊戲開始！", Toast.LENGTH_LONG).show();
    }

    private void animationInfo() {
        new Thread() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        infoTxt.animate().setDuration(2000).alpha(0).start();
                    }
                }, 5000);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(gameStarted)
            mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(gameStarted)
            mediaPlayer.pause();
    }
}
