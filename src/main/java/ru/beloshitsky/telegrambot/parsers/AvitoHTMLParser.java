package ru.beloshitsky.telegrambot.parsers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.beloshitsky.telegrambot.configuration.BotConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    Document htmlDoc = null;
    log.info(URL);

    try {
      long start = System.currentTimeMillis();
      Connection connection = Jsoup.connect(URL).headers(getHeaders()).timeout(30000);
      log.info("Connection: " + connection.toString());
      Connection.Response response = connection.execute();
      log.info("Respone body" + response.body());
      int status = response.statusCode();
      log.info("status code" + String.valueOf(response));
      if (status == 200) {
        htmlDoc = connection.get();
        long wastedTime = System.currentTimeMillis() - start;
        long sleepTime = botConfig.getDelayBetweenConnections() - wastedTime;
        Thread.sleep(sleepTime < 0 ? 0 : sleepTime);
      }
    } catch (IOException | InterruptedException e) {
      log.error("Couldn't fetch the URL");
      e.printStackTrace();
      return null;
    }
    return htmlDoc;
  }

  private Map<String, String> getHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put(":authority", "www.avito.ru");
    headers.put(":scheme", "https");
    headers.put(
        "accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
    headers.put("accept-encoding", "gzip, deflate, br");
    headers.put("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7,tg;q=0.6");
    headers.put("cache-control", "no-cache");
    headers.put("pragma", "no-cache");
    headers.put(
        "sec-ch-ua",
        "\"Google Chrome\";v=\"89\", \"Chromium\";v=\"89\", \";Not A Brand\";v=\"99\"");
    headers.put("sec-ch-ua-mobile", "?0");
    headers.put("sec-fetch-dest", "document");
    headers.put("sec-fetch-mode", "navigate");
    headers.put("sec-fetch-site", "same-origin");
    headers.put("sec-fetch-user", "?1");
    headers.put("upgrade-insecure-requests", "1");
    headers.put(
        "user-agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36");

    return headers;
  }
}
