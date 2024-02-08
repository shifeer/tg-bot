package io.proj3ct.MyWToMBot.service;

import io.proj3ct.MyWToMBot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private enum BotStatus {
        ENTER_ORIG, ENTER_REP, ERROR
    }

    private BotStatus botStatus = BotStatus.ERROR;
    private final long chatIdShop = 828674953;
    final BotConfig config;
    private final static Logger log = java.util.logging.Logger.getLogger(TelegramBot.class.getName());
    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getUserName();
        if(update.getMessage().getChatId().equals(chatIdShop) && update.hasMessage()) {
            sendMessage(chatIdShop, "добро пожаловать на прием заказов");
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            switch (messageText) {
                case "/start":
                    log.log(Level.INFO, userName + " user login");
                    sendStartMenu(chatId);
                    break;
                case "Как правильно сделать заказ":
                    sendMessageHowToOrder(chatId);
                    break;
                case "Сделать заказ":
                    sendMakeOrderMenu(chatId);
                    break;
                case "Назад":
                    sendStartMenu(chatId);
                    break;
                case "Оригинал", "Реплика":
                    if(messageText.equals("Оригинал")) {
                        botStatus = BotStatus.ENTER_ORIG;
                        sendMessageWrite(chatId);
                    } else {
                        botStatus = BotStatus.ENTER_REP;
                        sendMessageWrite(chatId);
                    }
                    break;
                case "Отменить":
                    sendMessage(chatId, "Операция отменена");
                    sendStartMenu(chatId);
                    break;
                default:
                    examinationBotStatus(chatId, messageText, userName);
            }
        } else {
            sendMessage(chatId, "Я не могу обработать это!");
        }
    }
    private void examinationBotStatus(long chatId, String text, String userName) {

        switch(botStatus) {
            case ENTER_ORIG:
                sendMessageToMyTG(chatId, chatIdShop,"Оригинал", text, userName);
                break;
            case ENTER_REP:
                sendMessageToMyTG(chatId, chatIdShop,"Реплика", text, userName);
                break;
            case ERROR:
                sendMessage(chatId, "Нет такой операции");
                break;
        }
    }
    private void sendMessageWrite(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Напишите размер, модель кроссовок и цвет(размер, модель, цвет):");

        ReplyKeyboardMarkup km = new ReplyKeyboardMarkup();
        km.setOneTimeKeyboard(true);
        km.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Отменить"));
        keyboard.add(row);
        km.setKeyboard(keyboard);
        message.setReplyMarkup(km);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.log(Level.WARNING, "Error : " + e.getMessage());
        }
    }
    private void sendMessage(long chatId, String text) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.log(Level.WARNING, "Error : " + e.getMessage());
        }

    }
    private void sendMessageHowToOrder(long chatId) {

        String textToSend = "1) Выберите какой тип кроссовок: оригинал или реплика.\n2) Напишите как в примере (размер, модель кроссовок, цвет) и отправьте сообщение, размер желательно отправлять длину стопы в мм.\nЗатем с вами свяжутся для уточнения стоимости.";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup km = new ReplyKeyboardMarkup();
        km.setOneTimeKeyboard(true);
        km.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Назад"));
        keyboard.add(row);
        km.setKeyboard(keyboard);
        message.setReplyMarkup(km);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.log(Level.WARNING, "Error : " + e.getMessage());
        }
    }
    private void sendStartMenu(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите действие:");

        ReplyKeyboardMarkup km = new ReplyKeyboardMarkup();
        km.setOneTimeKeyboard(true);
        km.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Сделать заказ"));
        row.add(new KeyboardButton("Как правильно сделать заказ"));
        keyboard.add(row);
        km.setKeyboard(keyboard);
        message.setReplyMarkup(km);


        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.log(Level.WARNING, "Error : " + e.getMessage());
        }
    }
    private void sendMakeOrderMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите:");

        ReplyKeyboardMarkup km = new ReplyKeyboardMarkup();
        km.setOneTimeKeyboard(true);
        km.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Оригинал"));
        row.add(new KeyboardButton("Реплика"));
        keyboard.add(row);
        km.setKeyboard(keyboard);
        message.setReplyMarkup(km);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.log(Level.WARNING, "Error : " + e.getMessage());
        }
    }
    private void sendMessageToMyTG(long chatId, long chatIdShop, String type, String userInput, String userName) {
        if(complianceCheck(userInput)) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatIdShop));
            message.setText("Новый заказ!\n" + "@" + userName+ "\n" + type + ". " + userInput);
            sendMessage(chatId, "Спасибо за заказ!\nВам скоро ответят.");
            botStatus = BotStatus.ERROR;
            log.log(Level.INFO, userName + " made an order");
            sendStartMenu(chatId);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.log(Level.WARNING, "Error : " + e.getMessage());
            }
        } else {
            sendMessage(chatId, "Неверно указаны данные");
            sendMessageWrite(chatId);
        }
    }
    private boolean complianceCheck(String text) {
        Pattern p = Pattern.compile("^\\d+,(\\s|)[\\wА-Яа-я\\s]+,(\\s|)[\\wА-Яа-я]+$");
        Matcher m = p.matcher(text);
        return m.matches();
    }
}