package andrey.dev.springtelegramtest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
@RequiredArgsConstructor
public class MyTelegramBot implements SpringLongPollingBot {
    private final UpdateConsumer updateConsumer;

    @Override
    public String getBotToken() {
        return "8430104704:AAG7fJC5o_SDiL8lXeYnmGScotkUqcRY_bg";
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
