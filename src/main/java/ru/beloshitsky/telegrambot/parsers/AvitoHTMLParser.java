package ru.beloshitsky.telegrambot.parsers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
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

  public List<Double> getListOfPricesFromURL(String URLCityPageProduct) {
    Document htmlDoc = getHTML(URLCityPageProduct);
    if (htmlDoc == null) {
      return null;
    }
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
    log.info(URL);

    try {
      long start = System.currentTimeMillis();
      htmlDoc =
          Jsoup.connect("https://www.google.com/")
              .userAgent(
                  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
              .timeout(10000)
              .get();
      long wastedTime = System.currentTimeMillis() - start;
      long sleepTime = botConfig.getDelayBetweenConnections() - wastedTime;
      Thread.sleep(sleepTime < 0 ? 0 : sleepTime);
      System.out.println(wastedTime + " " + sleepTime);
      log.info(htmlDoc.toString());
    } catch (IOException | InterruptedException e) {
      log.error("Couldn't fetch the URL");
      e.printStackTrace();
      return null;
    }

    return htmlDoc;
  }
}
