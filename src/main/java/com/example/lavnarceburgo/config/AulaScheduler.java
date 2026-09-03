package com.example.lavnarceburgo.config;

import com.example.lavnarceburgo.service.GeradorAulasService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AulaScheduler {

    private final GeradorAulasService geradorAulasService;

    public AulaScheduler(
            GeradorAulasService geradorAulasService
    ) {
        this.geradorAulasService = geradorAulasService;
    }

    @Scheduled(
            cron = "0 */5 * * * *",
            zone = "America/Sao_Paulo"
    )
    public void gerarAulasAutomaticamente() {
        geradorAulasService.gerarAulasDoDia();
    }
}