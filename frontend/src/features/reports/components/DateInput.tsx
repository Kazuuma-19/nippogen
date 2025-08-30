import { Platform } from "react-native";
import DateTimePicker from "@react-native-community/datetimepicker";

interface DateInputProps {
  value?: string;
  onChange: (dateString?: string) => void;
}

export default function DateInput({ value, onChange }: DateInputProps) {
  const handleWebDateChange = (e: any) => {
    const dateString = e.target.value;
    onChange(dateString || undefined);
  };

  const handleWebDateClick = (e: any) => {
    try {
      if (e.isTrusted) {
        e.target.showPicker?.();
      }
    } catch (error) {
      console.log("showPicker not available, using browser default");
    }
  };

  const handleNativeDateChange = (event: any, selectedDate?: Date) => {
    if (event.type === "set" && selectedDate) {
      const dateString = selectedDate.toISOString().split("T")[0];
      onChange(dateString);
    }
  };

  if (Platform.OS === "web") {
    return (
      <input
        type="date"
        value={value || ""}
        onChange={handleWebDateChange}
        onClick={handleWebDateClick}
        className="w-full border-none outline-none text-base text-gray-800 bg-transparent cursor-pointer"
        placeholder="選択してください"
      />
    );
  }

  return (
    <DateTimePicker
      value={value ? new Date(value) : new Date()}
      mode="date"
      onChange={handleNativeDateChange}
    />
  );
}