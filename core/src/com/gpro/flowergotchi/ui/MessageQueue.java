package com.gpro.flowergotchi.ui;

import com.gpro.flowergotchi.FlowergotchiGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12.01.2016.
 */
class MessageQueue {
    private final FlowergotchiGame game;
    private final GameUI ui;
    private final List<Message> Messages;
    private boolean messageActive = false;

    public MessageQueue(FlowergotchiGame game, GameUI ui) {
        this.game = game;
        this.ui = ui;
        Messages = new ArrayList<Message>();
    }

    public boolean isMessageActive() {
        return messageActive;
    }

    public void setMessageActive(boolean messageActive) {
        this.messageActive = messageActive;
    }

    public void update() {
        if (messageActive || Messages.isEmpty() || ui.isWallpaperMode()) {
            return;
        }
        final Message e = Messages.get(0);

        game.utility.fullMessage(game, ui.getStage(), game.utility.getDefaultSkin(), ui.getClick(), game.utility.getSmallFont(), e.mes, false, new Runnable() {
            @Override
            public void run() {
                Messages.remove(e);
                setMessageActive(false);
            }
        });
        messageActive = true;
    }

    public void addMessage(final String id, final String mes, Runnable run) {
        if (game.getPreferences().checkMessageShown(id)) {
            return;
        }
        Message message = new Message(mes, run);
        game.getPreferences().setMessageShown(id);

        Messages.add(message);
    }

    public static class Message {
        public final Runnable run;
        public final String mes;

        public Message(String mes, Runnable run) {
            this.mes = mes;
            this.run = run;
        }
    }
}
