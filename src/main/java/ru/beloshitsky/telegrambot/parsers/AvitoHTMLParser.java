package ru.beloshitsky.telegrambot.parsers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.beloshitsky.telegrambot.advices.annotations.LogArgs;
import ru.beloshitsky.telegrambot.configuration.BotConfig;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class AvitoHTMLParser {

  BotConfig botConfig;
  AvitoTagsParser tagsParser;

  @LogArgs
  public List<Double> getListOfPricesFromURL(String URLCityPageProduct) {
    Document htmlDoc = getHTML(URLCityPageProduct);
    if (htmlDoc == null) {
      return null;
    }
    return parseHTMLToListOfPrices(htmlDoc);
  }

  public List<Double> parseHTMLToListOfPrices(Document htmlDoc) {
    Elements elementsPrices = tagsParser.selectPrices(htmlDoc);
    List<Double> listOfPricesOnPage = null;

    if (elementsPrices.size() > 0) {
      listOfPricesOnPage =
          elementsPrices.stream()
              .filter(e -> e.text().matches("\\d+.+"))
              .map(e -> Double.parseDouble(e.text().replaceAll("\\W", "")))
              .collect(Collectors.toList());
    }
    return listOfPricesOnPage;
  }

  private Document getHTML(String URL) {
    Document htmlDoc;
    try {
      htmlDoc =
          Jsoup.connect(URL)
              .userAgent(
                  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                      + "(KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
              .referrer("https://www.google.com")
              .get();
    } catch (IOException e) {
      log.error("Couldn't fetch the URL");
      e.printStackTrace();
      return null;
    }
    return htmlDoc;
  }
}
