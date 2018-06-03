package com.amazon;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;

import java.util.List;

public class TotalNumberOfPostIntent implements IntentHandler {


    /**
     * Creates a {@code SpeechletResponse} for the PostCount intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    @Override
    public SpeechletResponse handle(IntentRequest request, Session session) {
        try {
            String accessToken = (String) session.getAttribute("fbToken");
            FacebookClient fbclient = new DefaultFacebookClient(accessToken);
            // User me = fbclient.fetchObject("me", User.class);
            Connection<Post> result = fbclient.fetchConnection("me/feed", Post.class);
            int counter = 0;
            for(List<Post> page : result)
            {
                for(Post apost : page)
                    counter++;

            }
            String speechText = "Your total number of post is "+ counter;
            // Create the Simple card content.
            SimpleCard card = new IntentHelper().getSimpleCard("HelloWorld", speechText);


            // Create the plain text output.
            PlainTextOutputSpeech speech = new IntentHelper().getPlainTextOutputSpeech(speechText);
            return SpeechletResponse.newTellResponse(speech, card);
        } catch(Exception e) {
           System.out.println("exception e"+ e);
        }

        return null;
    }
}
