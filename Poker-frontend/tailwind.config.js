export default {
    content: [
        "./app.vue",
        "./components/**/*.{vue,js,ts,jsx,tsx}",
        "./layouts/**/*.{vue,js,ts,jsx,tsx}",
        "./pages/**/*.{vue,js,ts,jsx,tsx}",
        "./plugins/**/*.{js,ts}",
        "./nuxt.config.{js,ts}",
    ],
    theme: {
        extend: {
            colors: {
                customPrimary: {
                    50: '#8EC4A9',
                    100: '#81BD9F',
                    200: '#66AF8A',
                    300: '#519B76',
                    400: '#438061',
                    500: '#35654D',
                    600: '#224031',
                    700: '#0E1B15',
                    800: '#000000',
                    900: '#000000',
                    950: '#000000',
                },
            }
        },
    },
    plugins: [],
};
