/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#fef7ee',
          100: '#fdedd3',
          200: '#fad7a5',
          300: '#f6ba6d',
          400: '#f19432',
          500: '#ee7712',
          600: '#df5c08',
          700: '#b94409',
          800: '#93360f',
          900: '#772e10',
        },
        background: '#fafafa',
        foreground: '#171717',
        muted: '#737373',
        border: '#e5e5e5',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
