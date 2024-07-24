package com.example.bankingchatbot;

//import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.cloud.dialogflow.v2.QueryResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private DialogflowUtils dialogflowUtils;
    private LinearLayout chatOutput;
    private EditText chatInput;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatOutput = findViewById(R.id.chatOutput);
        chatInput = findViewById(R.id.chatInput);
        Button sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.scrollView);

        dialogflowUtils = new DialogflowUtils(this, "banking-chatbot-e550b");

        sendButton.setOnClickListener(v -> {
            String inputText = chatInput.getText().toString();
            if (!inputText.trim().isEmpty()) {
                addChatMessage("User", inputText);
                new Thread(() -> {
                    QueryResult result = dialogflowUtils.detectIntent(inputText);
                    runOnUiThread(() -> {
                        if (result != null) {
                            addChatMessage("Bot", result.getFulfillmentText());
                        } else {
                            addChatMessage("Error", "Error detecting intent");
                        }
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    });
                }).start();
                chatInput.setText("");
            }
        });

        // Handle back button press with a confirmation dialog
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(ChatActivity.this)
                        .setMessage("Do you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", null)
                        .show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void addChatMessage(String sender, String message) {
        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.VERTICAL);
        messageLayout.setPadding(16, 16, 16, 16);

        TextView messageTextView = new TextView(this);
        messageTextView.setText(sender + ": " + message);
        messageTextView.setPadding(8, 8, 8, 8);

        TextView timestampTextView = new TextView(this);
        timestampTextView.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
        timestampTextView.setPadding(8, 0, 8, 8);
        timestampTextView.setTextSize(10);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);

        if ("User".equals(sender)) {
            messageTextView.setBackgroundResource(R.drawable.user_message_background);
            params.gravity = Gravity.END;
        } else {
            messageTextView.setBackgroundResource(R.drawable.bot_message_background);
            params.gravity = Gravity.START;
        }

        messageLayout.setLayoutParams(params);
        messageLayout.addView(messageTextView);
        messageLayout.addView(timestampTextView);

        chatOutput.addView(messageLayout);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
