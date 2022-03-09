package com.example.realuno;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.realuno.databinding.ActivityPlayBinding;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Play extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private ActivityPlayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }




        });
        backButtonClick();
        imageButtonClick();



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton.setOnTouchListener(mDelayHideTouchListener);
        MainCard[] deck = new MainCard[108];
        ArrayList<ArrayList<MainCard>> game = new ArrayList<ArrayList<MainCard>>();
        buildDeck(deck);
        Stack<MainCard> drawPile = new  Stack<>();
        Stack<MainCard> discard = new Stack<>();
        shuffleDeck(deck);
        setUpGame(deck, drawPile, game);
        discard.push(drawPile.pop());
        MainCard topOfDiscard = discard.peek();
        ArrayList<MainCard> currentHand = game.get(0);
        int next = 1;
        boolean isInProgress = gameOver(game);
        while (isInProgress) {
            isInProgress = gameOver(game);
            if (canPlayCard(currentHand, topOfDiscard)) {
                // player action
            } else {
                drawCards(1, currentHand, drawPile);
            }
            if (next + 1 == game.size()) {
                next = 0;
            } else {
                next++;
            }
            currentHand = game.get(next);
        }

    }



    private void backButtonClick(){
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        finish();

                    }
                }
        );
    }

    private void imageButtonClick(){
        ImageButton button = findViewById(R.id.imageButton);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Intent i = new Intent(Play.this, TestActivity.class);
                        startActivity(i);
                    }
                }
        );
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public static void buildDeck(MainCard[] arr) {
        // every color has 1 0 and 2 of each number 1-9
        // every color has two of each action card (6)
        // they're are 8 wild cards 4 color pickers, and 4 +4's
        // [0] - [18] red
        // [19] - [37] blue
        // [38] - [56] green
        // [57] - [75] yellow
        // [76] - [81] red action
        // [82] - [87] blue action
        // [88] - [93] green action
        // [94] - [99] yellow action
        // [100] - [107] wild cards

        Color card;
        Numbers temp;
        Action ab;
        Special special;
        int startSequence;
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                card = Color.RED;
                startSequence = 0;
            } else if (i == 1) {
                card = Color.BLUE;
                startSequence = 19;
            } else if (i == 2) {
                card = Color.GREEN;
                startSequence = 38;
            } else {
                card = Color.YELLOw;
                startSequence = 57;
            }
            arr[startSequence] = new MainCard(card, Numbers.ZERO);
            for (int k = 1; k <= 9; k++) {
                for (int l = 0; l < 2; l++) {
                    if (k == 1) {
                        temp = Numbers.ONE;
                    } else if (k == 2) {
                        temp = Numbers.TWO;
                    } else if (k == 3) {
                        temp = Numbers.THREE;
                    } else if (k == 4) {
                        temp = Numbers.FOUR;
                    } else if (k == 5) {
                        temp = Numbers.FIVE;
                    } else if (k == 6) {
                        temp = Numbers.SIX;
                    } else if (k == 7) {
                        temp = Numbers.SEVEN;
                    } else if (k == 8) {
                        temp = Numbers.EIGHT;
                    } else {
                        temp = Numbers.NINE;
                    }
                    arr[(k + l) + (k - 1) + (i * 19)] = new MainCard(card, temp);
                }
            }
            for (int j = 76; j <= 81; j++) {
                if (j <= 77) {
                    ab = Action.SKIP;
                } else if (j <= 79) {
                    ab = Action.REVERSE;
                } else {
                    ab = Action.DRAW2;
                }
                arr[j + (i * 6)] = new ActionCardColored(ab, card);
            }
        }
        for (int g = 100; g < 108; g++) {
            if (g <= 103) {
                special = Special.DRAW4;
            } else {
                special = Special.PICKCOLOR;
            }
            arr[g] = new ActionCards(special);
        }

    }

    public static void shuffleDeck(MainCard[] arr) {

        List<MainCard> temp = Arrays.asList(arr);
        Collections.shuffle(temp);
        temp.toArray(arr);

    }

    public static void setUpGame(MainCard[] arr, Stack<MainCard> draw, ArrayList<ArrayList<MainCard>> hands) {
        for (int i = 0; i < 108; i ++) {
            draw.push(arr[i]);
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                hands.get(i).add(draw.pop());
            }
        }
    }

    public static void drawCards(int amount, ArrayList<MainCard> recipient, Stack<MainCard> drawPile) {
        for (int i = 0; i < amount; i++) {
            recipient.add(drawPile.pop());
        }
    }

    public static boolean gameOver(ArrayList<ArrayList<MainCard>> arr) {
        for (ArrayList<MainCard> a : arr) {
            if (a.size() == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean canPlayCard(ArrayList<MainCard> hand, MainCard topOfDiscard) {
        for (MainCard c : hand) {
            if (c.matches(topOfDiscard)) {
                return true;
            }
        }
        return false;
    }

    public static void playCard(ArrayList<MainCard> hand, MainCard card, Stack<MainCard> discard) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) == card) {
                hand.remove(i);
            }
        }
        discard.push(card);
    }


}

}