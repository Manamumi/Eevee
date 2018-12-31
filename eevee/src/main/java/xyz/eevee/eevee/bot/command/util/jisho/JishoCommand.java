package xyz.eevee.eevee.bot.command.util.jisho;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.provider.JishoSearchProvider;
import xyz.eevee.eevee.provider.model.JishoSearchResult;
import xyz.eevee.eevee.provider.model.jisho.Japanese;
import xyz.eevee.eevee.provider.model.jisho.ResultData;
import xyz.eevee.eevee.provider.model.jisho.Sense;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public class JishoCommand extends Command {
    public JishoCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "jisho";
    }

    @Override
    public String getLabel() {
        return "Search Jisho.org";
    }

    @Override
    public String getDescription() {
        return "Search Jisho.org for definitions and translations of various words.";
    }

    @Override
    public Arguments<JishoCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("jisho"),
            new StringArgument("searchQuery")
        ), JishoCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev jisho 食べ物";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        JishoCommandArguments args = (JishoCommandArguments) arguments;
        Optional<JishoSearchResult> searchResultOptional = JishoSearchProvider.getSearchResult(args.getSearchQuery());
        EmbedBuilder embedBuilder = new EmbedBuilder();

        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        if (!searchResultOptional.isPresent()) {
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.setDescription("An unexpected error occurred while searching Jisho. Please try again later.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            action.sendEmbedMessage(e -> {
                log.warn("Failed to send Jisho info as embed.", e);
            }, event.getChannelId(), embedBuilder.build());
            return;
        }

        JishoSearchResult searchResult = searchResultOptional.get();

        embedBuilder.setTitle(String.format("Jisho Search Results: %s", args.getSearchQuery()));
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        if (searchResult.getData().isEmpty()) {
            embedBuilder.setDescription(String.format("No results found for query: %s.", args.getSearchQuery()));
            action.sendEmbedMessage(e -> {
                log.warn("Failed to send Jisho info as embed.", e);
            }, event.getChannelId(), embedBuilder.build());
            return;
        }

        ResultData topResult = searchResult.getData().get(0);

        embedBuilder.addField("Word", formatJapanese(topResult.getJapanese()), false);

        int currentIndex = 0;

        for (Sense sense : topResult.getSenses()) {
            String partsOfSpeech = String.join("; ", sense.getPartsOfSpeech());

            if (!partsOfSpeech.isEmpty()) {
                embedBuilder.addField("Parts of Speech", partsOfSpeech, false);
                currentIndex = 1;
            }

            String fieldName = String.format("%s. %s", currentIndex, String.join("; ", sense.getEnglishDefinitions()));

            embedBuilder.addField(fieldName, formatNotes(sense), false);

            currentIndex++;
        }

        action.sendEmbedMessage(e -> {
            log.warn("Failed to send Jisho info as embed.", e);
        }, event.getChannelId(), embedBuilder.build());
    }

    private String formatJapanese(@NonNull List<Japanese> japaneseList) {
        return japaneseList.stream()
                           .map(japanese -> {
                               if (japanese.getWord() == null) {
                                   return japanese.getReading();
                               } else if (japanese.getReading() == null) {
                                   return japanese.getWord();
                               }
                               return String.format("%s (%s)", japanese.getWord(), japanese.getReading());
                           })
                           .collect(Collectors.joining(", "));
    }

    private String formatNotes(@NonNull Sense sense) {
        String notes = String.join("; ", sense.getTags());

        if (!sense.getInfo().isEmpty()) {
            notes = String.format("%s%n*%s*", notes, String.join("; ", sense.getInfo()));
        }

        return notes;
    }
}
