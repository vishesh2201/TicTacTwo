package com.example.tictactwo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    ImageButton modeButton;
    ImageButton infoButton;
    boolean isDarkMode = false;
    int activePlayer = 0;
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int[][] winPositions = { {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6} };
    boolean gameActive = true;

    Queue<Integer> xMoves = new LinkedList<>();
    Queue<Integer> oMoves = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modeButton = findViewById(R.id.mode_button);
        infoButton = findViewById(R.id.info_button);
        findViewById(R.id.restartButton).setVisibility(View.GONE);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView img = findViewById(R.id.infotext);
                if (img.getVisibility() == View.VISIBLE) {
                    HideInfo(v);
                } else {
                    ShowInfo(v);
                }
            }
        });

        // Load saved theme preference
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("isDarkMode", false);
        updateTheme();
    }

    public void ChangeMode(View view) {
        isDarkMode = !isDarkMode;
        updateTheme();

        // Save theme preference
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putBoolean("isDarkMode", isDarkMode);
        editor.apply();
    }

    private void updateTheme() {
        int backgroundColor;
        int textColor;
        if (isDarkMode) {
            backgroundColor = getResources().getColor(R.color.colorPrimaryDark);
            textColor = getResources().getColor(R.color.colorPrimary);
        } else {
            backgroundColor = getResources().getColor(R.color.colorPrimary);
            textColor = getResources().getColor(R.color.colorPrimaryDark);
        }

        findViewById(R.id.main).setBackgroundColor(backgroundColor);
        ((TextView)findViewById(R.id.heading)).setTextColor(textColor);
        ((TextView)findViewById(R.id.credit)).setTextColor(textColor);
        ((TextView)findViewById(R.id.status)).setTextColor(textColor);
        modeButton.setBackgroundColor(backgroundColor);
        infoButton.setBackgroundColor(backgroundColor);
    }

    public void playerTap(View view) {
        if (!gameActive) {
            return;
        }
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());

        if (gameState[tappedImage] == 2) {
            gameState[tappedImage] = activePlayer;
            img.setTranslationY(-1000f);
            TextView status = findViewById(R.id.status);
            if (activePlayer == 0) {
                img.setImageResource(R.drawable.x_light_mode); // Single drawable for both modes
                xMoves.add(tappedImage);
                if (xMoves.size() > 3) {
                    int oldestMove = xMoves.poll();
                    gameState[oldestMove] = 2;
                    ((ImageView) findViewById(getResources().getIdentifier("imageView" + oldestMove, "id", getPackageName()))).setImageResource(0);
                }
                activePlayer = 1;
                status.setText("O's Turn - Tap To Play!");
            } else {
                img.setImageResource(R.drawable.o_light_mode); // Single drawable for both modes
                oMoves.add(tappedImage);
                if (oMoves.size() > 3) {
                    int oldestMove = oMoves.poll();
                    gameState[oldestMove] = 2;
                    ((ImageView) findViewById(getResources().getIdentifier("imageView" + oldestMove, "id", getPackageName()))).setImageResource(0);
                }
                activePlayer = 0;
                status.setText("X's Turn - Tap To Play!");
            }
            img.animate().translationYBy(1000f).setDuration(300);
        } else {
            TextView status = findViewById(R.id.status);
            status.setText("Invalid move! Try again.");
        }

        checkGameStatus();
    }

    private void checkGameStatus() {
        // Check for a win
        for (int[] winPosition : winPositions) {
            if (gameState[winPosition[0]] == gameState[winPosition[1]] &&
                    gameState[winPosition[1]] == gameState[winPosition[2]] &&
                    gameState[winPosition[0]] != 2) {
                gameActive = false;
                String winnerStr = gameState[winPosition[0]] == 0 ? "X has won!" : "O has won!";
                TextView status = findViewById(R.id.status);
                status.setText(winnerStr);
                findViewById(R.id.restartButton).setVisibility(View.VISIBLE);
                return;
            }
        }
    }

    public void restartGame(View view) {
        gameActive = true;
        activePlayer = 0;
        Arrays.fill(gameState, 2);

        xMoves.clear();
        oMoves.clear();

        int[] imageViews = {R.id.imageView0, R.id.imageView1, R.id.imageView2, R.id.imageView3,
                R.id.imageView4, R.id.imageView5, R.id.imageView6,
                R.id.imageView7, R.id.imageView8,};

        for (int id : imageViews) {
            ((ImageView)findViewById(id)).setImageResource(0);
        }

        TextView status = findViewById(R.id.status);
        status.setText("Welcome Back! Tap to play.");
        findViewById(R.id.restartButton).setVisibility(View.GONE);
    }

    public void ShowInfo(View view) {
        ImageView img = findViewById(R.id.infotext);
        if (img.getVisibility() == View.INVISIBLE) {
            img.setVisibility(View.VISIBLE);
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slidedown);
            img.startAnimation(slideDown);
        }
    }

    public void HideInfo(View view) {
        ImageView img = findViewById(R.id.infotext);
        if (img.getVisibility() == View.VISIBLE) {
            img.setVisibility(View.INVISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slideup);
            img.startAnimation(slideUp);
        }
    }
}
