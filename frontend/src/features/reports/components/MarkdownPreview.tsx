import { View, Text, ScrollView } from "react-native";

interface MarkdownPreviewProps {
  content: string;
}

// Simple markdown parsing for React Native
const parseMarkdown = (content: string) => {
  const lines = content.split('\n');
  const elements: React.ReactNode[] = [];
  
  lines.forEach((line, index) => {
    const key = `line-${index}`;
    
    // Headers
    if (line.startsWith('# ')) {
      elements.push(
        <Text key={key} className="text-2xl font-bold text-gray-800 mt-6 mb-3">
          {line.substring(2)}
        </Text>
      );
    } else if (line.startsWith('## ')) {
      elements.push(
        <Text key={key} className="text-xl font-bold text-gray-800 mt-5 mb-2">
          {line.substring(3)}
        </Text>
      );
    } else if (line.startsWith('### ')) {
      elements.push(
        <Text key={key} className="text-lg font-bold text-gray-800 mt-4 mb-2">
          {line.substring(4)}
        </Text>
      );
    }
    // List items
    else if (line.startsWith('- ') || line.startsWith('* ')) {
      elements.push(
        <View key={key} className="flex-row ml-4 mb-1">
          <Text className="text-gray-700 mr-2">•</Text>
          <Text className="text-gray-700 flex-1">{line.substring(2)}</Text>
        </View>
      );
    }
    // Numbered list items
    else if (/^\d+\. /.test(line)) {
      const match = line.match(/^(\d+)\. (.*)$/);
      if (match) {
        elements.push(
          <View key={key} className="flex-row ml-4 mb-1">
            <Text className="text-gray-700 mr-2">{match[1]}.</Text>
            <Text className="text-gray-700 flex-1">{match[2]}</Text>
          </View>
        );
      }
    }
    // Code blocks
    else if (line.startsWith('```')) {
      // Simple code block handling - in a full implementation, you'd track state
      elements.push(
        <View key={key} className="bg-gray-100 p-3 rounded my-2">
          <Text className="font-mono text-sm text-gray-800">コードブロック</Text>
        </View>
      );
    }
    // Empty lines
    else if (line.trim() === '') {
      elements.push(<View key={key} className="h-3" />);
    }
    // Regular paragraphs
    else {
      // Handle inline markdown (bold, italic, inline code)
      let processedLine = line;
      
      // Bold text **text**
      processedLine = processedLine.replace(/\*\*(.*?)\*\*/g, '$1');
      
      // Inline code `code` - could be enhanced in future
      
      elements.push(
        <Text key={key} className="text-gray-700 mb-2 leading-6">
          {processedLine}
        </Text>
      );
    }
  });
  
  return elements;
};

export default function MarkdownPreview({ content }: MarkdownPreviewProps) {
  if (!content.trim()) {
    return (
      <View className="flex-1 justify-center items-center p-6">
        <Text className="text-gray-500 text-lg">内容がありません</Text>
        <Text className="text-gray-400 text-sm mt-2">
          編集モードで日報を作成してください
        </Text>
      </View>
    );
  }

  const parsedContent = parseMarkdown(content);

  return (
    <ScrollView className="flex-1 bg-white">
      <View className="p-6">
        {/* Preview Header */}
        <View className="border-b border-gray-200 pb-4 mb-6">
          <Text className="text-lg font-semibold text-gray-800">プレビュー</Text>
          <Text className="text-sm text-gray-600 mt-1">
            Markdown記法で整形された日報
          </Text>
        </View>

        {/* Markdown Content */}
        <View className="min-h-[300px]">
          {parsedContent}
        </View>

        {/* Footer */}
        <View className="mt-8 pt-6 border-t border-gray-200">
          <Text className="text-xs text-gray-500 text-center">
            編集モードに切り替えて内容を変更できます
          </Text>
        </View>
      </View>
    </ScrollView>
  );
}