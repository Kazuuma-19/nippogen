/** @type {import('tailwindcss').Config} */
module.exports = {
  // NOTE: Update this to include the paths to all files that contain Nativewind classes.
  content: ["./app/**/*.{js,jsx,ts,tsx}", "./components/**/*.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#267D00',
          50: '#A3E087',
          100: '#96DC7A',
          200: '#7DD360',
          300: '#63CA46',
          400: '#4AB733',
          500: '#267D00',
          600: '#1F6400',
          700: '#184C00',
          800: '#103300',
          900: '#091B00',
          950: '#040D00'
        },
      },
    },
  },
  plugins: [],
};
