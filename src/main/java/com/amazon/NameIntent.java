package com.amazon;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

public class NameIntent implements IntentHandler {
    @Override
    public SpeechletResponse handle(IntentRequest request, Session session) {
        String accessToken = (String) session.getAttribute("fbToken");
        FacebookClient fbclient = new DefaultFacebookClient(accessToken);
        User me = fbclient.fetchObject("me", User.class);
        String speechText = "your name is "+ me.getName();
        // Create the Simple card content.
        SimpleCard card = new IntentHelper().getSimpleCard("HelloWorld", speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new IntentHelper().getPlainTextOutputSpeech(speechText);
        return SpeechletResponse.newTellResponse(speech, card);
    }
}
