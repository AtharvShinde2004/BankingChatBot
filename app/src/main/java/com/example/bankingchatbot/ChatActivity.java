package com.example.bankingchatbot;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.cloud.dialogflow.v2.QueryResult;

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
    }

    private void addChatMessage(String sender, String message) {
        TextView textView = new TextView(this);
        textView.setText(sender + ": " + message);
        textView.setPadding(16, 16, 16, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);

        if ("User".equals(sender)) {
            textView.setBackgroundResource(R.drawable.user_message_background);
            params.gravity = Gravity.END;
        } else {
            textView.setBackgroundResource(R.drawable.bot_message_background);
            params.gravity = Gravity.START;
        }

        textView.setLayoutParams(params);
        chatOutput.addView(textView);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

}
