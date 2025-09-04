import { Alert } from 'react-native';

/**
 * 統一されたユーザー通知機能
 */

/**
 * 成功メッセージを表示
 */
export const showSuccess = (message: string): void => {
  Alert.alert('成功', message);
};

/**
 * エラーメッセージを表示
 */
export const showError = (message: string): void => {
  Alert.alert('エラー', message);
};