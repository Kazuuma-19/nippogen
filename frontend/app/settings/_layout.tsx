import { Stack } from "expo-router";

export default function SettingsLayout() {
  return (
    <Stack>
      <Stack.Screen
        name="index"
        options={{
          title: "設定",
          headerShown: false,
        }}
      />
      <Stack.Screen
        name="api-credentials"
        options={{
          title: "API認証情報",
          headerStyle: {
            backgroundColor: "#267D00",
          },
          headerTintColor: "#fff",
          headerTitleStyle: {
            fontWeight: "bold",
          },
        }}
      />
    </Stack>
  );
}
