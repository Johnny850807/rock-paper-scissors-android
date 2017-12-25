package com.afinal.youjinchen.rock_paper_scissors;


public interface Callback {

    void onOneRoundFightOver(Fist yourFist, Fist aiFist, int result, int round);

    void onGameOver(int yourScore, int aiScore);

    void onGameStarted();
}
