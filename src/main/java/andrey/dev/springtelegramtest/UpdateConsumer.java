package andrey.dev.springtelegramtest;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;


    public UpdateConsumer(@Value("${Telegram.Bot.Token}") String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            String massageTest = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (massageTest.equals("/start")) {
                sendMainMenu(chatId);
            } else if (massageTest.equals("/menu")) {
                sendMenu(chatId);
            } else if (massageTest.equals("рандомное")) {
                sendRandom(chatId);
            } else if (massageTest.equals("имя")) {
                sendMyName(chatId, update.getCallbackQuery().getFrom());
            } else {
                sendMessage(chatId, "я вас не понимаю. Вы можете  ввести команду /start или /menu ");
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }


    }

    @SneakyThrows
    private void sendMenu(Long chatId) {
        SendMessage message = SendMessage.builder().text("меню").chatId(chatId).build();
        List<KeyboardRow> row = List.of(new KeyboardRow("рандомное", "имя"));
        ReplyKeyboardMarkup murkUp = new ReplyKeyboardMarkup(row);
        message.setReplyMarkup(murkUp);
        telegramClient.execute(message);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getFrom().getId();
        var user = callbackQuery.getFrom();
        switch (data) {
            case "my_name" -> sendMyName(chatId, user);
            case "random" -> sendRandom(chatId);
            case "long_procces" -> sendLongProcces(chatId);
            default -> sendMessage(chatId, "нет такой команды  ");
        }
    }

    @SneakyThrows
    private void sendMessage(Long chatId, String mainMessage) {
        SendMessage message = SendMessage.builder().text(mainMessage).chatId(chatId).build();
        telegramClient.execute(message);
    }

    private void sendLongProcces(Long chatId) {
        sendMessage(chatId, "Запустили загрузку картинки ");
        new Thread(() -> {
            String Url = "https://http.cat/images/200.jpg";
            try {
                URL url = new URL(Url);
                var inputStream = url.openStream();
                SendPhoto sendPhoto = SendPhoto.builder().chatId(chatId).photo(new InputFile(inputStream, "tomas.jpg")).caption("ваша картинка").build();
                telegramClient.execute(sendPhoto);
            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void sendRandom(Long chatId) {
        var randomInt = ThreadLocalRandom.current().nextInt(10);
        sendMessage(chatId, String.format("число : %s", randomInt));
    }

    private void sendMyName(Long chatId, User user) {
        String text = String.format("\n вас зовут : %s  ваш ник: %s", user.getFirstName(), user.getUserName());
        sendMessage(chatId, text);
    }

    private void sendMainMenu(Long chatId) throws TelegramApiException {
        SendMessage massage = SendMessage.builder().text("Добро пожаловать  !! выберите действие : ").chatId(chatId).build();
        var button1 = InlineKeyboardButton.builder().text("Как меня зовут ?").callbackData("my_name").build();
        var button2 = InlineKeyboardButton.builder().text("случайное число ").callbackData("random").build();
        var button3 = InlineKeyboardButton.builder().text(" Долгий процесс").callbackData("long_procces").build();
        List<InlineKeyboardRow> keybordrows = List.of(new InlineKeyboardRow(button1), new InlineKeyboardRow(button2), new InlineKeyboardRow(button3));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keybordrows);
        massage.setReplyMarkup(markup);
        telegramClient.execute(massage);
    }
}
