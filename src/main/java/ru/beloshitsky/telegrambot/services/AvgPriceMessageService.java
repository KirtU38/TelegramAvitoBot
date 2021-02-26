package ru.beloshitsky.telegrambot.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.beloshitsky.telegrambot.configuration.BotConfig;
import ru.beloshitsky.telegrambot.parsers.AvitoHTMLParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AvgPriceMessageService {

    BotConfig botConfig;
    AvitoHTMLParser avitoHTMLParser;

    public double calculateAvgPrice(String cityInEnglish, String product)
            throws IOException, InterruptedException {

        String URLCityAndProduct = botConfig.getRootURL() + cityInEnglish + "?q=" + product;
        int numOfPages = avitoHTMLParser.getNumOfPages(URLCityAndProduct);
        return getAvgOnAllPages(product, cityInEnglish, numOfPages);
    }

    private double getAvgOnAllPages(String product, String cityInEnglish, int numOfPages)
            throws InterruptedException, IOException {

        if (numOfPages >= botConfig.getPagesLimit()) {
            numOfPages = botConfig.getPagesLimit();
        }
        List<List<Double>> listOfPricesOnEveryPage = new ArrayList<>();
        for (int page = 1; page < numOfPages; page++) {
            String URLCityPageProduct = botConfig.getRootURL() + cityInEnglish + "?p=" + page + "&q=" + product;
            List<Double> listOfPricesOnPage = avitoHTMLParser.getListOfPricesFromURL(URLCityPageProduct);
            listOfPricesOnEveryPage.add(listOfPricesOnPage);
        }
        return calculateAvgPriceFromAllPages(listOfPricesOnEveryPage);
    }

    private double calculateAvgPriceFromAllPages(List<List<Double>> listOfResultsOnEveryPage) {

        List<Double> listOfPricesFromAllPages = listOfResultsOnEveryPage
                .stream()
                .filter(list -> list != null)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        double averagePriceCommon = listOfPricesFromAllPages
                .stream()
                .mapToDouble(e -> e)
                .average()
                .getAsDouble();

        double deletionThreshold = (averagePriceCommon / 100) * botConfig.getPriceThreshold();

        return listOfPricesFromAllPages
                .stream()
                .filter(p -> (averagePriceCommon - p) < deletionThreshold)
                .mapToDouble(p -> p)
                .average()
                .getAsDouble();
    }
}
