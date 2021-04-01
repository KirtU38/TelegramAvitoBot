package ru.beloshitsky.telegrambot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.beloshitsky.telegrambot.configuration.BotConfig;
import ru.beloshitsky.telegrambot.util.AvgPriceCalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AvgPriceCalculatorTest {

  BotConfig botConfig;
  AvgPriceCalculator avgPriceCalculator;

  @Before
  public void init() {
    botConfig = Mockito.mock(BotConfig.class);
    avgPriceCalculator = new AvgPriceCalculator(botConfig);
  }

  @Test
  public void avgPriceFromAllPagesTest() throws IOException {
    List<Double> pricesFromPage1 =
        new ArrayList<Double>() {
          {
            add(10000D);
            add(27000D);
            add(500D);
            add(15000D);
            add(17000D);
            add(6D);
            add(20000D);
          }
        };
    List<Double> pricesFromPage2 =
        new ArrayList<Double>() {
          {
            add(13000D);
            add(1D);
            add(900D);
            add(19000D);
            add(21700D);
            add(12D);
            add(23400D);
          }
        };
    List<List<Double>> listOfAllPrices = new ArrayList<>();

    listOfAllPrices.add(pricesFromPage1);
    listOfAllPrices.add(pricesFromPage2);

    double expected = 19512.5;
    double result = avgPriceCalculator.calculateAvgPrice(listOfAllPrices);
    assertThat(result).isEqualTo(expected);
  }
}
