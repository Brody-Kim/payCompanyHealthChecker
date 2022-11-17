package com.finance.healthchecker.comm.util;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SlackUtil {

    @Value("${slack.comparebot.token}")
    private String slack_token;

    @Value("${slack.channel.id}")
    private String slack_channel_id;

    public void postSlackMessage(String message){
        try {

            Slack slack = Slack.getInstance();
            MethodsClient methods = slack.methods(this.slack_token);

            // Build a request object
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(this.slack_channel_id)
                    .text(message)
                    .build();

            // Get a response as a Java object
            ChatPostMessageResponse response = methods.chatPostMessage(request);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
