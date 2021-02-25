package ru.beloshitsky.telegrambot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {

        List<String> strings = Files.readAllLines(Paths.get("src/main/resources/citiesMap.txt"));
        HashMap<String,String> map = new HashMap<>();
        strings.forEach(e->{
            String[] tokens = e.split("=", 2);
            map.put(tokens[0], tokens[1]);
        });




        // List<String> strings = Files.readAllLines(Paths.get("src/main/resources/citiesMap1.json"));
        // PrintWriter writer = new PrintWriter("src/main/resources/citiesMap3.json");
        // strings.forEach(e->{
        //     String s = "{" + e.trim().replaceAll(",", "") + "},\n";
        //     writer.write(s);
        // });
        // writer.flush();
        // writer.close();


    }
}
