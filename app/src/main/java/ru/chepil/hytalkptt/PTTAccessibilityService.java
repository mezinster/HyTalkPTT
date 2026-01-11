package ru.chepil.hytalkptt;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class PTTAccessibilityService extends AccessibilityService {

    private static final String TAG = "PTTAccessibilityService";
    private static final int PTT_KEYCODE = 228; // Physical PTT button keycode (Motorola LEX F10)

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not needed - we only use KeyEvent interception
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "PTT Accessibility Service connected");
        
        // Configure service to request key event filtering
        AccessibilityServiceInfo info = getServiceInfo();
        if (info != null) {
            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
            setServiceInfo(info);
        } else {
            // Fallback: create new service info
            AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
            serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
            serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            serviceInfo.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
            serviceInfo.notificationTimeout = 100;
            setServiceInfo(serviceInfo);
        }
    }
    
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        
        // Only handle PTT button events (code 228)
        if (keyCode == PTT_KEYCODE) {
            if (action == KeyEvent.ACTION_DOWN) {
                Log.d(TAG, "PTT button pressed (onKeyEvent - ACTION_DOWN)");
                MainActivity.isPTTButtonPressed = true;
                
                // Launch MainActivity if app was killed (it will handle HyTalk launch)
                launchMainActivityIfNeeded();
                
                // Send Broadcast Intent for HyTalk (based on pttremap app logic)
                // HyTalk listens to "android.intent.action.PTT_DOWN" broadcast
                if (event.getRepeatCount() == 0) {
                    sendPTTBroadcast(true);
                }
                
                return true; // Consume the event - don't pass it to other apps
            } else if (action == KeyEvent.ACTION_UP) {
                Log.d(TAG, "PTT button released (onKeyEvent - ACTION_UP)");
                MainActivity.isPTTButtonPressed = false;
                
                // Send Broadcast Intent for HyTalk (based on pttremap app logic)
                sendPTTBroadcast(false);
                
                return true; // Consume the event - don't pass it to other apps
            }
        }
        
        // Don't intercept other keys
        return false;
    }
    
    /**
     * Launches MainActivity if the app process was killed.
     * This ensures MainActivity is running to handle HyTalk launch logic.
     */
    private void launchMainActivityIfNeeded() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching MainActivity", e);
        }
    }
    
    /**
     * Sends Broadcast Intent for HyTalk PTT button.
     * Based on pttremap app logic: sends "android.intent.action.PTT_DOWN" or "PTT_UP"
     */
    private void sendPTTBroadcast(boolean isDown) {
        try {
            String action = isDown ? "android.intent.action.PTT_DOWN" : "android.intent.action.PTT_UP";
            Intent intent = new Intent(action);
            sendBroadcast(intent);
            Log.d(TAG, "Sent PTT Broadcast Intent: " + action);
        } catch (Exception e) {
            Log.e(TAG, "Error sending PTT Broadcast Intent", e);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "PTT Accessibility Service destroyed");
    }
}
