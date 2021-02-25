package ru.beloshitsky.telegrambot.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.beloshitsky.telegrambot.configuration.BotConfig;
import ru.beloshitsky.telegrambot.messages.AveragePriceMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AvgPriceMessageService {

    @Autowired
    BotConfig botConfig;

    public double calculateAvgPrice(String city, String cityInEnglish, String product, String URL)
            throws IOException, InterruptedException {

        System.out.println("CONTAINS CITY");

        Document htmlDoc;
        synchronized (AveragePriceMessage.class) {
            System.out.println("GETTING URL");
            long start = System.currentTimeMillis();
            htmlDoc = Jsoup.connect(URL).get();
            long wastedTime = System.currentTimeMillis() - start;
            Thread.sleep(wastedTime >= botConfig.getDelayBetweenConnections()
                    ? 0
                    : botConfig.getDelayBetweenConnections() - wastedTime);
        }

        Elements numOfPages = htmlDoc.select("span[data-marker~=page[(]\\d+[)]]");
        double averagePrice = calculateAvgOnAllPages(product, cityInEnglish, numOfPages);
        return averagePrice;
    }

    private double calculateAvgOnAllPages(String product, String cityInEnglish, Elements pages)
            throws InterruptedException, IOException {

        System.out.println("GET AVERAGE PRICE");

        int lastPage = pages.size() - 1;
        List<List<Double>> listOfResultsOnEveryPage = new ArrayList<>();

        if (lastPage > botConfig.getPagesLimit()) {
            lastPage = botConfig.getPagesLimit();
        }
        for (int i = 1; i <= lastPage; i++) {
            List<Double> listOfPricesOnPage = getListOfPricesOnPage(String.valueOf(i), cityInEnglish, product);
            listOfResultsOnEveryPage.add(listOfPricesOnPage);
        }

        List<Double> listOfPricesFromAllPages = listOfResultsOnEveryPage
                .stream()
                .filter(list -> list != null)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return calculateAvgPriceFromList(listOfPricesFromAllPages);
    }

    public List<Double> getListOfPricesOnPage(String page, String cityInEnglish, String product)
            throws InterruptedException, IOException {

        List<Double> listOfPrices = null;
        String URL = "https://www.avito.ru/" + cityInEnglish;
        int sizeOfPrices;
        Document htmlDoc;

        synchronized (AveragePriceMessage.class) {
            long start = System.currentTimeMillis();
            htmlDoc = Jsoup.connect(URL).data("p", page, "q", product).get();
            long wastedTime = System.currentTimeMillis() - start;
            Thread.sleep(wastedTime >= botConfig.getDelayBetweenConnections()
                    ? 0
                    : botConfig.getDelayBetweenConnections() - wastedTime);
        }
        Elements elementsInYourCity = htmlDoc.select("div[data-marker=catalog-serp]");
        Elements elementsPrices = elementsInYourCity.select("span[class~=price-text-.+]");
        sizeOfPrices = elementsPrices.size();

        // Если есть предложения именно в твоём городе
        if (elementsPrices.size() > 0) {
            // Парсим документ в Лист цен
            listOfPrices = elementsPrices
                    .stream()
                    .filter(e -> e.text().matches("\\d+.+"))
                    .map(e -> Double.parseDouble(e.text().replaceAll("\\W", "")))
                    .collect(Collectors.toList());
        }

        System.out.println(sizeOfPrices + " товаров " + Thread.currentThread().getName());
        return listOfPrices;
    }

    private double calculateAvgPriceFromList(List<Double> listOfPrices) {

        double averagePriceCommon = listOfPrices
                .stream()
                .mapToDouble(e -> e)
                .average()
                .getAsDouble();

        double deletionThreshold = (averagePriceCommon / 100) * botConfig.getPriceThreshold();
        return listOfPrices
                .stream()
                .filter(p -> (averagePriceCommon - p) < deletionThreshold)
                .mapToDouble(p -> p)
                .average()
                .getAsDouble();
    }
}
