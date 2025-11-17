package andrey.dev.springtelegramtest;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public UpdateConsumer() {
        this.telegramClient = new OkHttpTelegramClient("8430104704:AAG7fJC5o_SDiL8lXeYnmGScotkUqcRY_bg");
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            String massageTest = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (massageTest.equals("/start")) {
                sendMainMenu(chatId);
            } else {
                SendMessage message = SendMessage.builder().
                        text("я вас не понимаю. Вы можете  ввести команду /start ")
                        .chatId(update.getMessage().getChatId())
                        .build();
                telegramClient.execute(message);
            }
        }


    }

    private void sendMainMenu(Long chatId) {
//затычка привет
    }
}
