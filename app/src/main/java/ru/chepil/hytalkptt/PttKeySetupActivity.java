package ru.chepil.hytalkptt;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity for detecting and displaying the PTT hardware key code.
 * Stores keyCode on ACTION_DOWN; "Save settings" saves it to app sandbox.
 */
public class PttKeySetupActivity extends AppCompatActivity {

    private static final String TAG = "PttKeySetupActivity";

    private TextView tvKeyCode;
    /** Last keyCode from ACTION_DOWN (repeatCount == 0). -1 if none yet. */
    private int lastKeyCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptt_key_setup);

        tvKeyCode = (TextView) findViewById(R.id.tv_key_code);
        if (tvKeyCode != null) {
            tvKeyCode.setFocusable(true);
            tvKeyCode.setFocusableInTouchMode(true);
            tvKeyCode.requestFocus();
        }

        Button btnSave = (Button) findViewById(R.id.btn_save_settings);
        if (btnSave != null) {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastKeyCode >= 0) {
                        PttPreferences.setPttKeyCode(PttKeySetupActivity.this, lastKeyCode);
                        Toast.makeText(PttKeySetupActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(PttKeySetupActivity.this, "Press PTT button first", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvKeyCode != null) {
            tvKeyCode.requestFocus();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(TAG, "BACK button pressed (KEYCODE_BACK, code 4) – exiting");
            Toast.makeText(this, "BACK pressed – exiting", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        String info = formatEvent(event);
        Log.d(TAG, info);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            lastKeyCode = event.getKeyCode();
        }

        if (tvKeyCode != null) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                tvKeyCode.setText(info);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                String downAndUpText = tvKeyCode.getText() + "\n\n" + info;
                tvKeyCode.setText(downAndUpText);
            }
        }

        return false;
    }

    private String formatEvent(KeyEvent e) {
        String action;
        switch (e.getAction()) {
            case KeyEvent.ACTION_DOWN:
                action = "DOWN";
                break;
            case KeyEvent.ACTION_UP:
                action = "UP";
                break;
            default:
                action = String.valueOf(e.getAction());
        }

        String keyName = KeyEvent.keyCodeToString(e.getKeyCode());

        return action +
                "  keyCode=" + e.getKeyCode() + " (" + keyName + ")" +
                "  scanCode=" + e.getScanCode();
    }
}
