package com.example.gta5vrcloud;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;

public class MainActivity extends Activity {
    private TextView status;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        enterImmersiveMode();
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48,36,48,36);
        root.setBackgroundColor(Color.rgb(15,8,30));
        TextView title = new TextView(this);
        title.setText("GTA5 VR"); title.setTextColor(Color.WHITE);
        title.setTextSize(38); title.setTypeface(Typeface.DEFAULT_BOLD); title.setGravity(Gravity.CENTER);
        root.addView(title);
        TextView sub = new TextView(this);
        sub.setText("CLOUD EDITION  •  QUEST PROTOTYPE");
        sub.setTextColor(Color.rgb(190,150,255)); sub.setTextSize(14); sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1,-2); sp.bottomMargin=36; root.addView(sub,sp);
        status = new TextView(this);
        status.setText("Launcher loaded. OpenXR runtime and cloud streaming are not connected yet.");
        status.setTextColor(Color.rgb(235,225,255)); status.setTextSize(17); status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams st = new LinearLayout.LayoutParams(-1,-2); st.bottomMargin=28; root.addView(status,st);
        Button connect = new Button(this); connect.setText("TEST CLOUD CONNECTION");
        connect.setOnClickListener(v -> status.setText("No cloud endpoint configured. Streaming is not available in this prototype."));
        root.addView(connect,new LinearLayout.LayoutParams(-1,-2));
        TextView note = new TextView(this);
        note.setText("Launcher prototype only. Does not contain or run GTA V.");
        note.setTextColor(Color.LTGRAY); note.setTextSize(12); note.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1,-2); np.topMargin=28; root.addView(note,np);
        setContentView(root);
    }
    private void enterImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus); if (hasFocus) enterImmersiveMode();
    }
}
