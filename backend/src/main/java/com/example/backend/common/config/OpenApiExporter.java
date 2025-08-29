package com.example.backend.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * アプリケーション起動後にOpenAPI仕様を取得してファイルに保存するコンポーネント
 */
@Component
@RequiredArgsConstructor
public class OpenApiExporter {

    private final WebClient.Builder webClientBuilder;

    // アプリケーション起動後にOpenAPI仕様を取得してファイルに保存
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        webClientBuilder.build()
            .get()
            .uri("http://localhost:8080/api-docs")
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(
                this::writeOpenApiFile,
                error -> { /* エラー処理省略 */ }
            );
    }
    private void writeOpenApiFile(String openApiJson) {
        try {
            if (openApiJson != null && !openApiJson.isEmpty()) {
                Path outputPath = Paths.get("/app/generated/openapi.json");
                Files.write(outputPath, openApiJson.getBytes());
            }
        } catch (Exception e) {
            // エラー処理省略
        }
    }
}
