package com.jcloisterzone.game.save;

import java.io.Writer;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.jcloisterzone.wsio.MessageParser;
import com.jcloisterzone.wsio.message.AbstractWsMessage;
import com.jcloisterzone.wsio.message.WsReplayableMessage;

public class SavedGameParser {

    private Gson gson;

    public SavedGameParser() {
        this(false);
    }

    public SavedGameParser(boolean pretty) {
        GsonBuilder builder = MessageParser.createGsonBuilder();

         builder
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .setExclusionStrategies(new SavedGameExclStrat());

         if (pretty) {
            builder.setPrettyPrinting();
         }

         gson = builder.create();
    }

    public String toJson(SavedGame src) {
        return gson.toJson(src);
    }

    public void toJson(SavedGame src, Writer writer) {
        gson.toJson(src, writer);
    }

    public SavedGame fromJson(JsonReader reader) {
        SavedGame sg = gson.fromJson(reader, SavedGame.class);
        sg.getReplay().forEach(msg -> msg.setGameId(sg.getGameId()));
        return sg;
    }

    public class SavedGameExclStrat implements ExclusionStrategy {

        @Override
        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
           String name = f.getName();
           if (WsReplayableMessage.class.isAssignableFrom(f.getDeclaringClass()) && (name.equals("gameId") || name.equals("messageId"))) {
               return true;
           }
           if (AbstractWsMessage.class.isAssignableFrom(f.getDeclaringClass()) && name.equals("sequenceNumber")) {
               return true;
           }
           return false;
        }

    }

}
