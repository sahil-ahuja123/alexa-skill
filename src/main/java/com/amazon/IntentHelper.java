package com.amazon;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

import java.util.List;

public class IntentHelper {
    // done
    public SpeechletResponse getHelpResponse(boolean isLoggedIn) {
        final String speechText = "Facebook reader can read total number of comments, likes and shares of your recent post from facebook timeline. You can also ask to read comments from the post. ";
        final String helpCardContent = "Facebook reader can read total number of comments, likes and shares of your recent post from facebook timeline.";
        final String accountNotLinked = "Your facebook account is not linked with the skill. Link your account to use the skill.";

        SimpleCard card = getSimpleCard("Help", helpCardContent + (isLoggedIn ? "Try asking - (1) What is total number of comments? (2) What is total number likes? (3) What is total number of shares?" : accountNotLinked));
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText + (isLoggedIn ? "Try by saying - what is total number of likes?" : accountNotLinked));

        if(isLoggedIn) {
            PlainTextOutputSpeech repromptSpeech = getPlainTextOutputSpeech("");
            Reprompt reprompt = getReprompt(repromptSpeech);
            return SpeechletResponse.newAskResponse(speech, reprompt, card);
        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }

    // done
    public SpeechletResponse getCancelResponse() {
        return getStopResponse();
    }

    // done
    public SpeechletResponse getStopResponse() {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Good bye!");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse getLastPostCountResponse(long comment, long share, long likes) {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Here is the report -  " + comment + " comments, " + share + ", shares and " + likes + " likes.");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse getLastPostCommentCountResponse(long comment) {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Total " + comment + " comments.");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse getLastPostShareCountResponse(long share) {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Total " + share + " shares.");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse getLastPostLikesCountResponse(long likes) {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Total " + likes + " likes.");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse lastPostNotFoundResponse() {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("You have posted anything on your timeline.");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse noCommentFoundResponse() {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("No comment found on the post.");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse readCommentResponse(List<String> comments) {
        StringBuilder sb = new StringBuilder();
        sb.append("Reading last " + comments.size() + (comments.size() == 1 ? " comment" : " comments."));
        for(int i=0; i<comments.size(); i++) {
            sb.append(" Comment " + i + ",   ").append(comments.get(i));
        }
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("No comment found on the post.");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse invalidIntentResponse() {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("This is not supported. Try something else, or say help to know more.");
        PlainTextOutputSpeech repromptSpeech = getPlainTextOutputSpeech("");
        Reprompt reprompt = getReprompt(repromptSpeech);
        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    // done
    public SpeechletResponse onLaunchIntentResponse(boolean isLoggedIn) {
        if(isLoggedIn) {
            PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Welcome to facebook reader. How can I help you?");
            PlainTextOutputSpeech repromptSpeech = getPlainTextOutputSpeech("");
            Reprompt reprompt = getReprompt(repromptSpeech);
            return SpeechletResponse.newAskResponse(speech, reprompt);
        }

        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Welcome to facebook reader. Your facebook account is not linked with the skill. Link your account to use the skill.");
        return SpeechletResponse.newTellResponse(speech);
    }

    // done
    public SpeechletResponse facebookTokenExpired() {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Your facebook session is expired. Re-link your account with skill from Alexa app.");
        return SpeechletResponse.newTellResponse(speech);
    }

    /**
     * Helper method that creates a card object.
     * @param title title of the card
     * @param content body of the card
     * @return SimpleCard the display card to be sent along with the voice response.
     */
    public SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);

        return card;
    }

    /**
     * Helper method for retrieving an OutputSpeech object when given a string of TTS.
     * @param speechText the text that should be spoken out to the user.
     * @return an instance of SpeechOutput.
     */
    public PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return speech;
    }
    /**
     * Helper method that returns a reprompt object. This is used in Ask responses where you want
     * the user to be able to respond to your speech.
     * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
     * @return Reprompt instance.
     */
    public Reprompt getReprompt(OutputSpeech outputSpeech) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);

        return reprompt;
    }
}
