package ru.beloshitsky.telegrambot.parsers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.beloshitsky.telegrambot.configuration.BotConfig;
import ru.beloshitsky.telegrambot.messages.AveragePriceMessage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class AvitoHTMLParser {

    BotConfig botConfig;

    public int getNumOfPages(String URLCityAndProduct) throws InterruptedException, IOException {

        Document htmlDoc = getHTML(URLCityAndProduct);
        Elements pages = htmlDoc.select("span[data-marker~=page[(]\\d+[)]]");
        return pages.size();
    }

    public List<Double> getListOfPricesFromURL(String URLCityPageProduct) throws IOException, InterruptedException {

        Document htmlDoc = getHTML(URLCityPageProduct);
        Elements elementsInYourCity = htmlDoc.select("div[data-marker=catalog-serp]");
        Elements elementsPrices = elementsInYourCity.select("span[class~=price-text-.+]");
        List<Double> listOfPricesOnPage = null;
        if (elementsPrices.size() > 0) {
            listOfPricesOnPage = elementsPrices
                    .stream()
                    .filter(e -> e.text().matches("\\d+.+"))
                    .map(e -> Double.parseDouble(e.text().replaceAll("\\W", "")))
                    .collect(Collectors.toList());
        }
        return listOfPricesOnPage;
    }

    private Document getHTML(String URL) throws IOException, InterruptedException {
        Document htmlDoc;
        synchronized (AveragePriceMessage.class) {
            long start = System.currentTimeMillis();
            htmlDoc = Jsoup.connect(URL).get();
            long wastedTime = System.currentTimeMillis() - start;
            Thread.sleep(wastedTime >= botConfig.getDelayBetweenConnections()
                    ? 0
                    : botConfig.getDelayBetweenConnections() - wastedTime);
        }
        return htmlDoc;
    }
}
