import { Stack } from "expo-router";

export default function ReportsLayout() {
  return (
    <Stack
      screenOptions={{
        headerStyle: {
          backgroundColor: "#267D00",
        },
        headerTintColor: "#fff",
        headerTitleStyle: {
          fontWeight: "bold",
        },
      }}
    >
      <Stack.Screen
        name="index"
        options={{
          title: "日報管理",
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