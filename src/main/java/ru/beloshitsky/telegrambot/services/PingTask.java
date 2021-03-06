package ru.beloshitsky.telegrambot.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PingTask {
  String pingURL;

  public PingTask(@Value("${pingtask.pingURL}") String pingURL) {
    this.pingURL = pingURL;
  }

  // @Scheduled(fixedRateString = "${pingtask.pingRate}")
  // public void pingGoogle() {
  //   try {
  //     URL url = new URL(pingURL);
  //     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
  //     connection.connect();
  //     log.info("Ping {}, OK: response code {}", url.getHost(), connection.getResponseCode());
  //     connection.disconnect();
  //   } catch (IOException e) {
  //     log.error("Ping FAILED");
  //     e.printStackTrace();
  //   }
  // }
}
