package com.example.dictionarybot.service;

import com.example.dictionarybot.config.BotConfig;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (message) {
                case "/start":
                    try {
                        sendAnswer(chatId, "Введи слово, а я тебе выдам синонимы!");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    try {
                        try {
                            sendAnswer(chatId, parseSynonyms(message));
                        } catch (TelegramApiException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (RuntimeException e) {
                        try {
                            sendAnswer(chatId, "Я не знаю такого слова, или оно введено не верно...");
                        } catch (TelegramApiException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
            }
        }
    }

    private void sendAnswer(long chatId, String response) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response);

        execute(message);
    }

    private String parseSynonyms(String word) throws IOException {
        StringBuilder sb = new StringBuilder();

        Document document = Jsoup.connect(String.format("https://sinonim.org/s/%s", word))
                .userAgent("Chrome/107.0.0.0")
                .referrer("http://www.google.com")
                .get();

        Elements listOfWord = document.select("div.outtable").select("table");


        for (Element element : listOfWord.select("a")) {
            if (element.text().equals("https://sinonim.org/")) {
                continue;
            }
            sb.append(element.text() + System.lineSeparator());
        }

        return sb.toString();
    }
}
