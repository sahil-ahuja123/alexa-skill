package com.amazon;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;

import java.util.List;

/**
 *
 * returns - likes, shares and comments count
 *
 * */
public class GetMyRecentPostUpdateIntent implements IntentHandler {

    @Override
    public SpeechletResponse handle(IntentRequest request, Session session) {
        String speechText = null;
        Post post = null;
        Slot slot = request.getIntent().getSlot("whichCount");
        if (slot == null || slot.getValue() == null) {
            // return likes, shares and comments count
            post = getObject(session);
            speechText = "Your Like count is" + post.getLikesCount() + "And Your Comment count is" + post.getCommentsCount() + "And Your Share count is" + post.getSharesCount();
        }
        switch (slot.getValue()) {
            case "like":
                post = getObject(session);
                speechText = "Likes count is " + post.getLikesCount();
                return  SetSpeechText(speechText);
            case "share":
                post = getObject(session);
                speechText = "Shares count is " + post.getSharesCount();
                return  SetSpeechText(speechText);
            case "comment":
                post = getObject(session);
                speechText = "Comments count is " + post.getCommentsCount();
                return  SetSpeechText(speechText);
        }
        return null;
    }

    private Post getObject(Session session) {
        Post apost = null;
        String accessToken = (String)session.getAttribute("fbToken");
        FacebookClient fbclient = new DefaultFacebookClient(accessToken);
        Connection<Post> result = fbclient.fetchConnection("me/feed", Post.class);
        for(List<Post> page : result) {
            apost = page.get(0);
            break;
        }
        Post post = fbclient.fetchObject(apost.getId(),
                Post.class,
                Parameter.with("fields", "from,to,likes.limit(0).summary(true),comments.limit(0).summary(true),shares.limit(0).summary(true)"));
        return post;
    }

    private SpeechletResponse SetSpeechText(String speechText) {
        SimpleCard card = new IntentHelper().getSimpleCard("HelloWorld", speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new IntentHelper().getPlainTextOutputSpeech(speechText);
        return SpeechletResponse.newTellResponse(speech, card);
    }

}
