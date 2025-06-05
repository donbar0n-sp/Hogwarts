package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port}")
    private String port;

    @GetMapping("/port")
    public String getPort(){
        return "Application is running on port: " + port;
    }

    @GetMapping("/stream/sum")
    public long getParallelStreamSum() {
        long start = System.currentTimeMillis();

        int sum = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .parallel()
                .mapToInt(Integer::intValue)
                .sum();

        long duration = System.currentTimeMillis() - sum;
        logger.info("Parallel stream sum computed in {} ms", duration);

        return sum;
    }
}
