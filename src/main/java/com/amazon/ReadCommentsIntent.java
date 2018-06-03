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
import com.restfb.types.Comment;
import com.restfb.types.Post;

import java.util.List;

public class ReadCommentsIntent implements IntentHandler {

    @Override
    public SpeechletResponse handle(IntentRequest request, Session session) {
        Connection<Comment> commentDetails;
        List<Comment> commentList;
        Comment comment;
        Slot slot = request.getIntent().getSlot("whichComment");
        if(slot == null || slot.getValue() == null) {
            return null;
        }

        switch (slot.getValue()) {
            case "recent":
                commentDetails = getCommentsObject(session);
                 commentList = commentDetails.getData();
                if(commentList == null || commentList.isEmpty()) return null;
                comment = commentList.get(commentList.size() - 1);
               return SetSpeechText("Comment is " + comment.getMessage() + "And Written by" + comment.getFrom().getName());
            case "first":
                commentDetails = getCommentsObject(session);
                commentList = commentDetails.getData();
                if(commentList == null || commentList.isEmpty()) return null;
                 comment = commentList.get(0);
                return SetSpeechText("Comment is " + comment.getMessage() + "And Written by" + comment.getFrom().getName());
        }
        return null;
    }

    public String getLastId(Session session)
    {
        Post aPost = null;
        String accessToken = (String)session.getAttribute("fbToken");
        FacebookClient fbclient = new DefaultFacebookClient(accessToken);
        Connection<Post> result = fbclient.fetchConnection("me/feed", Post.class);
        for(List<Post> page : result)
        {
             aPost = page.get(0);
        }
        return aPost.getId();
    }

    public Connection<Comment> getCommentsObject(Session session)
    {
        String accessToken = (String)session.getAttribute("fbToken");
        FacebookClient fbclient = new DefaultFacebookClient(accessToken);
        Connection<Comment> commentDetails = fbclient.fetchConnection("520343811486616_838021093052218"+ "/comments", Comment.class,
                Parameter.with("fields", "message,from{id,name}"));
        return commentDetails;
    }
    private SpeechletResponse SetSpeechText(String speechText) {
        SimpleCard card = new IntentHelper().getSimpleCard("HelloWorld", speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new IntentHelper().getPlainTextOutputSpeech(speechText);
        return SpeechletResponse.newTellResponse(speech, card);
    }

}
