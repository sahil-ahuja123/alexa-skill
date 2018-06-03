package com.amazon;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Comment;
import com.restfb.types.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.json.SpeechletRequestEnvelope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class HelloWorldSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(HelloWorldSpeechlet.class);

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        String accessToken = requestEnvelope.getSession().getUser().getAccessToken();
        if(!"prod".equals(System.getenv("env"))) {
            accessToken = System.getenv("facebookToken");
        }
        setAttribute(requestEnvelope.getSession(), "facebookToken", accessToken);
        log.info("onSessionStarted requestId={}, sessionId={}, fbToken={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId(),
                accessToken);
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId{}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());

        return new IntentHelper().onLaunchIntentResponse(getFacebookToken(requestEnvelope.getSession()) != null);
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), requestEnvelope.getSession().getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        try {
            if ("AMAZON.HelpIntent".equals(intentName)) {
                return new IntentHelper().getHelpResponse(getFacebookToken(requestEnvelope.getSession()) != null);
            } else if ("AMAZON.CancelIntent".equals(intentName)) {
                return new IntentHelper().getCancelResponse();
            } else if ("AMAZON.StopIntent".equals(intentName)) {
                return new IntentHelper().getStopResponse();
            } else {
                FacebookClient fbclient = new DefaultFacebookClient(getFacebookToken(requestEnvelope.getSession()));
                Connection<Post> result = fbclient.fetchConnection("me/feed", Post.class);
                List<Post> posts =  result.getData();
                if ("LastPostCounterIntent".equals(intentName)) {
                    if(posts.isEmpty()) {
                        return new IntentHelper().lastPostNotFoundResponse();
                    }

                    final Post lastPost = posts.get(0);

                    Slot slot = intent.getSlot("what");

                    long comment = lastPost.getCommentsCount(),
                           share = lastPost.getSharesCount(),
                           likes = lastPost.getLikesCount();
                    if ("comment".equalsIgnoreCase(slot.getValue())) {
                        return new IntentHelper().getLastPostCommentCountResponse(comment);
                    } else if ("likes".equalsIgnoreCase(slot.getValue())) {
                        return new IntentHelper().getLastPostLikesCountResponse(likes);
                    } else if ("share".equalsIgnoreCase(slot.getValue())) {
                        return new IntentHelper().getLastPostShareCountResponse(share);
                    } else {
                        return new IntentHelper().getLastPostCountResponse(comment, share, likes);
                    }
                } else if ("LastPostCommentReaderIntent".equals(intentName)) {
                    if(posts.isEmpty()) {
                        return new IntentHelper().lastPostNotFoundResponse();
                    }
                    Slot slot = intent.getSlot("count");
                    long count = 1;
                    if(slot != null && slot.getValue() != null) {
                        try {
                            count = Integer.parseInt(slot.getValue());
                            if(count > 10) {
                                count = 10;
                            }
                        } catch (Exception e) {
                        }
                    }
                    final Post lastPost = posts.get(0);
                    if(lastPost.getCommentsCount() < 1) {
                        return new IntentHelper().noCommentFoundResponse();
                    }
                    count = (count > lastPost.getCommentsCount()) ? lastPost.getCommentsCount() : count;

                    List<String> comments = new ArrayList<>((int) count);
                    Iterator<Comment> iterator =  lastPost.getComments().getData().iterator();
                    for(int i = 0; i < count; i ++) {
                        if(iterator.hasNext()) {
                            comments.add(iterator.next().getMessage());
                        }
                    }
                    return new IntentHelper().readCommentResponse(comments);
                } else {
                    return new IntentHelper().invalidIntentResponse();
                }
            }
        } catch (Exception e) {
            log.error("exception while processing intent.", e);
            return new IntentHelper().facebookTokenExpired();
        }
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    private void setAttribute(Session session, String key, String value) {
        session.setAttribute(key, value);
    }

    private Object getAttribute(Session session, String key) {
        return session.getAttribute(key);
    }

    private String getFacebookToken(Session session) {
        return (String) getAttribute(session, "facebookToken");
    }
}
