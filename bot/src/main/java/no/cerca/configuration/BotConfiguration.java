package no.cerca.configuration;

import no.cerca.util.BotEventHandler;
import no.cerca.util.CommandProcessor;
import no.cerca.api.service.YClientsAPIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mail.im.botapi.BotApiClient;
import ru.mail.im.botapi.BotApiClientController;

/**
 * Created by jadae on 17.04.2025
 */
@Configuration
public class BotConfiguration {
    @Value("${vk.teams.bot.token}")
    private static String botToken;

    @Bean
    public BotApiClient botApiClient() {
        return new BotApiClient(botToken);
    }

    @Bean
    public BotApiClientController botController(BotApiClient client) {
        return BotApiClientController.startBot(client);
    }

    @Bean
    public BotEventHandler botEventHandler(YClientsAPIService yClientsService,
                                           BotApiClientController botController,
                                           CommandProcessor commandProcessor) {
        return new BotEventHandler(yClientsService, botController, commandProcessor);
    }

    @Bean
    public CommandProcessor commandProcessor(YClientsAPIService yClientsService,
                                             BotApiClientController botController) {
        return new CommandProcessor(botController, yClientsService);
    }
}