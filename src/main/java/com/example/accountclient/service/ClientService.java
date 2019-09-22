package com.example.accountclient.service;

import com.example.accountclient.config.ClientConfiguration;
import com.example.accountclient.feign.CustomFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final CustomFeignClient feignClient;
    private final ClientConfiguration configuration;
    private static ExecutorService executorService;
    private static Random random = new Random();
    private static AtomicLong counter = new AtomicLong(); //for understanding how many tasks are performed for precise moment

    public void startProcess() {

        switch (configuration.getWayToDo()) {
            case "executor":
                startByExecutor();
                break;
            case "manually":
                startManually();
                break;
        }
    }

    private void startByExecutor() {

        counter.set(0);

        if (executorService == null) initExecutor();

        List<Integer> listOfIds = configuration.getListOfIds();

        List<Callable<Long>> tasks = new ArrayList<>();

        for (int i = 0; i < configuration.getAmountOfRequestsForGettingProcess(); i++) {

            tasks.add(() -> {
                Integer userId = listOfIds.get(random.nextInt(listOfIds.size()));
                Long result = feignClient.getValue(userId);
                log.info("Getting amount by executor for id=" + userId + " ; amount is: " + result
                        + "; counter is: " + counter.incrementAndGet());
                return null;
            });
        }

        for (int i = 0; i < configuration.getAmountOfRequestsForPuttingProcess(); i++) {

            tasks.add(() -> {
                Integer userId = listOfIds.get(random.nextInt(listOfIds.size()));
                Long amount = random.nextLong();
                feignClient.putValue(userId, amount);
                log.info("Putting amount by executor for id=" + userId + " ; amount is: " + amount
                        + "; counter is: " + counter.incrementAndGet());
                return null;
            });
        }

        Collections.shuffle(tasks);

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

    private void initExecutor() {

        int amountOfThreads = configuration.getAmountOfThreads() == 0 ?
                Runtime.getRuntime().availableProcessors() : configuration.getAmountOfThreads();

        executorService =
                Executors.newFixedThreadPool(amountOfThreads);

//        executorService =
//                Executors.newWorkStealingPool(amountOfThreads);
    }

    private void startManually() {

        counter.set(0);

        int amountOfUsersForGettingProcess = configuration.getAmountOfRequestsForGettingProcess();
        int amountOfUsersForPuttingProcess = configuration.getAmountOfRequestsForPuttingProcess();
        List<Integer> listOfIds = configuration.getListOfIds();

        final CyclicBarrier gate = new CyclicBarrier(amountOfUsersForGettingProcess
                + amountOfUsersForPuttingProcess);

        for (int i = 0; i < amountOfUsersForGettingProcess; i++) {
            new Thread(() -> {
                try {
                    gate.await();
                    Integer userId = listOfIds.get(random.nextInt(listOfIds.size()));
                    Long result = feignClient.getValue(userId);
                    log.info("Getting amount by manual thread execution for id=" + userId + " ; amount is: " + result
                            + "; counter is: " + counter.incrementAndGet());
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        for (int i = 0; i < amountOfUsersForPuttingProcess; i++) {
            new Thread(() -> {
                try {
                    gate.await();
                    Integer userId = listOfIds.get(random.nextInt(listOfIds.size()));
                    Long amount = random.nextLong();
                    feignClient.putValue(userId, amount);
                    log.info("Putting amount by manual thread execution for id=" + userId + " ; amount is: " + amount
                            + "; counter is: " + counter.incrementAndGet());
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
