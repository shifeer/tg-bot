package io.proj3ct.MyWToMBot.config;

import io.proj3ct.MyWToMBot.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class BotInit {
    @Autowired
    TelegramBot bot;
    private static Logger log = java.util.logging.Logger.getLogger(BotInit.class.getName());
    @EventListener({ContextRefreshedEvent.class})
    public void init () throws TelegramApiException {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.log(Level.WARNING, "Error : " + e.getMessage());
        }
    }
}
