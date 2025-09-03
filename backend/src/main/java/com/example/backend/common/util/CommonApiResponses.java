package com.example.backend.common.util;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 共通のOpenAPI レスポンス定義
 * 重複するエラーレスポンス定義を統一化
 */
public class CommonApiResponses {
    
    /**
     * 標準的なエラーレスポンス（400, 500）
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正", content = @Content),
        @ApiResponse(responseCode = "500", description = "サーバー内部エラー", content = @Content)
    })
    public @interface StandardErrorResponses {}
    
    /**
     * 404エラーを含むエラーレスポンス（400, 404, 500）
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正", content = @Content),
        @ApiResponse(responseCode = "404", description = "リソースが見つからない", content = @Content),
        @ApiResponse(responseCode = "500", description = "サーバー内部エラー", content = @Content)
    })
    public @interface WithNotFound {}
    
    /**
     * 認証情報系APIの標準エラーレスポンス
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "リクエストデータが不正", content = @Content),
        @ApiResponse(responseCode = "500", description = "サーバーエラー", content = @Content)
    })
    public @interface CredentialErrorResponses {}
    
    /**
     * 認証情報削除用エラーレスポンス（404含む）
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "認証情報が見つからない", content = @Content),
        @ApiResponse(responseCode = "500", description = "サーバーエラー", content = @Content)
    })
    public @interface CredentialDeleteErrorResponses {}
}