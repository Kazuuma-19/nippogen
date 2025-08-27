package com.example.backend.domain.repositories;

import com.example.backend.domain.entities.NotionPage;
import com.example.backend.domain.entities.NotionTableRow;

import java.util.List;

/**
 * Notion統合のためのリポジトリインターフェース
 * インフラストラクチャ層で実装される
 */
public interface INotionRepository {
    
    /**
     * Notion接続をテストする
     * 
     * @return 接続成功時true
     */
    boolean testConnection();
    
    /**
     * 設定済みページの内容を取得
     * バックエンドで指定されたページIDを使用
     * 
     * @return ページ情報
     */
    NotionPage getPageContent();
    
    /**
     * 設定済みテーブルの内容を取得
     * バックエンドで指定されたテーブルIDを使用
     * 
     * @return テーブル行データのリスト
     */
    List<NotionTableRow> getTableContent();
}