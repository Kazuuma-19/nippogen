import { Stack } from "expo-router";

export default function ReportsLayout() {
  return (
    <Stack>
      <Stack.Screen
        name="index"
        options={{
          title: "日報管理",
          headerShown: false, // Use tab header instead
        }}
      />
      <Stack.Screen
        name="generate"
        options={{
          title: "日報生成",
        }}
      />
      <Stack.Screen
        name="[date]"
        options={{
          title: "日報詳細",
        }}
      />
    </Stack>
  );
}
