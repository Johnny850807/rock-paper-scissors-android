package com.afinal.youjinchen.rock_paper_scissors;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static com.afinal.youjinchen.rock_paper_scissors.Fist.PAPER;
import static com.afinal.youjinchen.rock_paper_scissors.Fist.ROCK;
import static com.afinal.youjinchen.rock_paper_scissors.Fist.SCISSORS;

public class Game {
    public static final int DUEL = 0;
    public static final int YOU = 1;
    public static final int AI = 2;
    public static final int MAXROUND = 6;
    private int round = 1;
    private Player p1;
    private AI p2;
    private Callback callback;
    private Map<Integer, Integer> scores = new HashMap<>();

    public Game(Player p1) {
        this.p1 = p1;
        this.p2 = new AI();
        scores.put(YOU, 0);
        scores.put(AI, 0);
    }

    public void start(Callback callback){
        this.callback = callback;
        Log.d("myLog", "Game started.");
        callback.onGameStarted();
    }

    public void decide(final Fist yourFist){
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    detectResultAndBroadcast(yourFist);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void detectResultAndBroadcast(Fist yourFist) {
        Log.d("myLog", "Fist fighting.");
        Fist aiFist = p2.decide();
        int result = getResult(yourFist, aiFist);

        String winner = (result == YOU ? "player" : result == AI ? "AI" : "Duel!");
        Log.d("myLog", "Winner: " + winner);
        if(result != DUEL)
        {
            scores.put(result, scores.get(result) + 1);
            Log.d("myLog", "Winner score counted: " + winner + "(" + scores.get(result) + ")");
        }

        Log.d("myLog", "Current round: " + round);
        if (round++ == MAXROUND)
        {
            Log.d("myLog", "Game over, player score: " + scores.get(YOU) + ", ai score: " + scores.get(AI));
            callback.onGameOver(scores.get(YOU), scores.get(AI));
        }
        else
            callback.onOneRoundFightOver(yourFist, aiFist, result, round);
    }

    private int getResult(Fist yourFist, Fist aiFist){
        if (yourFist == aiFist)
            return DUEL;
        else
        {
            switch (yourFist)
            {
                case SCISSORS:
                    return aiFist == ROCK ? AI : YOU;
                case PAPER:
                    return aiFist == SCISSORS ? AI : YOU;
                case ROCK:
                    return aiFist == PAPER ? AI : YOU;
            }
        }

        throw new IllegalStateException("Something goes wrong!!");
    }
}
