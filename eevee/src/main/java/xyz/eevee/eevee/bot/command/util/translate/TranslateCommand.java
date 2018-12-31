package xyz.eevee.eevee.bot.command.util.translate;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateException;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.collect.ImmutableList;
import common.util.RateLimiter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.configuration.GlobalConfiguration;
import xyz.eevee.eevee.exc.InvalidRuntimeEnvironmentException;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.parser.arguments.VariadicArgument;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
public class TranslateCommand extends Command {
    private static RateLimiter RATE_LIMITER = RateLimiter.builder()
                                                         .maxHits(2)
                                                         .duration(15000) // 15 seconds
                                                         .build();

    public TranslateCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public void bootstrap() {
        if (System.getenv(GlobalConfiguration.GOOGLE_API_CRED_ENV_VAR_NAME) == null) {
            throw new InvalidRuntimeEnvironmentException(
                String.format(
                    "Missing environment variable: %s. This command cannot function without it.",
                    GlobalConfiguration.GOOGLE_API_CRED_ENV_VAR_NAME
                )
            );
        }
    }

    @Override
    public String getShortLabel() {
        return "translate";
    }

    @Override
    public String getLabel() {
        return "Translate Text";
    }

    @Override
    public String getDescription() {
        return "Translates a given source text into a given language. " +
            "Translations are rate-limited to 2 uses every 15 seconds.";
    }

    @Override
    public String getExample() {
        return "ev translate en こんにちは";
    }

    @Override
    public RateLimiter getRateLimiter() {
        return RATE_LIMITER;
    }

    @Override
    public Arguments<TranslateCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("translate"),
            new StringArgument("targetLanguage"),
            new VariadicArgument<StringArgument, String>("sourceText", new StringArgument("foo"))
        ), TranslateCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        TranslateCommandArguments args = (TranslateCommandArguments) arguments;
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        String sourceText = args.getSourceText().stream().collect(Collectors.joining(" "));

        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        try {
            Translation translation = translate.translate(
                sourceText,
                TranslateOption.targetLanguage(args.getTargetLanguage())
            );

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(
                String.format(
                    "Translation (%s ⟶ %s)",
                    translation.getSourceLanguage().toUpperCase(Locale.ENGLISH),
                    args.getTargetLanguage().toUpperCase(Locale.ENGLISH)
                )
            );
            embedBuilder.addField("Source:", sourceText, false);
            embedBuilder.addField("Translation:", translation.getTranslatedText(), false);
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));
            action.sendEmbedMessage(e -> {
                log.warn("Failed to send translation as embed.", e);
            }, event.getChannelId(), embedBuilder.build());
        } catch (TranslateException e) {
            log.error("Failed to translate provided text.", e);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.appendDescription("Could not recognize target language code.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            action.sendEmbedMessage(ex -> {
                log.warn("Failed to send translation as embed.", ex);
            }, event.getChannelId(), embedBuilder.build());
        }
    }
}
