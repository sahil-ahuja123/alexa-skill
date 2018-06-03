package com.amazon;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface IntentHandler {
    SpeechletResponse handle(IntentRequest request, Session session);
}
