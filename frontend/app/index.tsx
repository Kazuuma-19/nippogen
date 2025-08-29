import { useEffect } from "react";
import { router } from "expo-router";

export default function Index() {
  useEffect(() => {
    // Automatically redirect to tabs home screen
    router.replace("/(tabs)");
  }, []);

  return null; // This component won't be visible due to immediate redirect
}
