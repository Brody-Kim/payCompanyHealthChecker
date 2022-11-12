package com.finance.healthchecker.comm.util;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.springframework.beans.factory.annotation.Value;


public class SlackUtil {

    @Value("${slack.comparebot.token}")
    private static String slack_token;

    @Value("${slack.channel.id}")
    private static String slack_channel_id;

    public static void postSlackMessage(String message){
        try {

            Slack slack = Slack.getInstance();
            MethodsClient methods = slack.methods(slack_token);

            // Build a request object
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(slack_channel_id)
                    .text(message)
                    .build();

            // Get a response as a Java object
            ChatPostMessageResponse response = methods.chatPostMessage(request);
            //System.out.println(response.toString());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
