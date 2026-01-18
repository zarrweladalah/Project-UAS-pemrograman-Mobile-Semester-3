package com.example.todolist.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GreetingHelper {
    
    private static final Map<String, String[]> greetings = new HashMap<>();
    
    static {
        // Format: [Good Morning, Good Afternoon, Good Evening, Welcome, Continue]
        greetings.put("ID", new String[]{"Selamat Pagi", "Selamat Siang", "Selamat Sore", "Selamat Datang", "Masuk ke Aplikasi"});
        greetings.put("US", new String[]{"Good Morning", "Good Afternoon", "Good Evening", "Welcome", "Enter App"});
        greetings.put("GB", new String[]{"Good Morning", "Good Afternoon", "Good Evening", "Welcome", "Enter App"});
        greetings.put("AU", new String[]{"Good Morning", "Good Afternoon", "Good Evening", "Welcome", "Enter App"});
        greetings.put("JP", new String[]{"おはようございます", "こんにちは", "こんばんは", "ようこそ", "アプリに入る"});
        greetings.put("CN", new String[]{"早上好", "下午好", "晚上好", "欢迎", "进入应用"});
        greetings.put("KR", new String[]{"좋은 아침입니다", "좋은 오후입니다", "좋은 저녁입니다", "환영합니다", "앱 들어가기"});
        greetings.put("DE", new String[]{"Guten Morgen", "Guten Tag", "Guten Abend", "Willkommen", "App betreten"});
        greetings.put("FR", new String[]{"Bonjour", "Bon après-midi", "Bonsoir", "Bienvenue", "Entrer dans l'app"});
        greetings.put("ES", new String[]{"Buenos días", "Buenas tardes", "Buenas noches", "Bienvenido", "Entrar a la app"});
        greetings.put("IT", new String[]{"Buongiorno", "Buon pomeriggio", "Buonasera", "Benvenuto", "Entra nell'app"});
        greetings.put("PT", new String[]{"Bom dia", "Boa tarde", "Boa noite", "Bem-vindo", "Entrar no app"});
        greetings.put("BR", new String[]{"Bom dia", "Boa tarde", "Boa noite", "Bem-vindo", "Entrar no app"});
        greetings.put("NL", new String[]{"Goedemorgen", "Goedemiddag", "Goedenavond", "Welkom", "Ga naar app"});
        greetings.put("RU", new String[]{"Доброе утро", "Добрый день", "Добрый вечер", "Добро пожаловать", "Войти в приложение"});
        greetings.put("TH", new String[]{"สวัสดีตอนเช้า", "สวัสดีตอนบ่าย", "สวัสดีตอนเย็น", "ยินดีต้อนรับ", "เข้าสู่แอป"});
        greetings.put("VN", new String[]{"Chào buổi sáng", "Chào buổi chiều", "Chào buổi tối", "Chào mừng", "Vào ứng dụng"});
        greetings.put("MY", new String[]{"Selamat Pagi", "Selamat Petang", "Selamat Malam", "Selamat Datang", "Masuk Aplikasi"});
        greetings.put("SG", new String[]{"Good Morning", "Good Afternoon", "Good Evening", "Welcome", "Enter App"});
        greetings.put("PH", new String[]{"Magandang umaga", "Magandang hapon", "Magandang gabi", "Maligayang pagdating", "Pumasok sa app"});
        greetings.put("IN", new String[]{"Good Morning", "Good Afternoon", "Good Evening", "Welcome", "Enter App"});
        greetings.put("SA", new String[]{"صباح الخير", "مساء الخير", "مساء الخير", "أهلا بك", "أدخل التطبيق"});
        greetings.put("AE", new String[]{"صباح الخير", "مساء الخير", "مساء الخير", "أهلا بك", "أدخل التطبيق"});
        greetings.put("TR", new String[]{"Günaydın", "İyi günler", "İyi akşamlar", "Hoş geldiniz", "Uygulamaya gir"});
        // Default English
        greetings.put("DEFAULT", new String[]{"Good Morning", "Good Afternoon", "Good Evening", "Welcome", "Enter App"});
    }

    public static String getTimeBasedGreeting(String countryCode) {
        String[] countryGreetings = greetings.getOrDefault(countryCode, greetings.get("DEFAULT"));
        
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (hour >= 5 && hour < 12) {
            return countryGreetings[0]; // Morning
        } else if (hour >= 12 && hour < 17) {
            return countryGreetings[1]; // Afternoon
        } else {
            return countryGreetings[2]; // Evening
        }
    }

    public static String getWelcomeText(String countryCode) {
        String[] countryGreetings = greetings.getOrDefault(countryCode, greetings.get("DEFAULT"));
        return countryGreetings[3];
    }

    public static String getContinueButtonText(String countryCode) {
        String[] countryGreetings = greetings.getOrDefault(countryCode, greetings.get("DEFAULT"));
        return countryGreetings[4];
    }

    public static String getCountryName(String countryCode) {
        Map<String, String> countries = new HashMap<>();
        countries.put("ID", "Indonesia");
        countries.put("US", "United States");
        countries.put("GB", "United Kingdom");
        countries.put("AU", "Australia");
        countries.put("JP", "Japan");
        countries.put("CN", "China");
        countries.put("KR", "South Korea");
        countries.put("DE", "Germany");
        countries.put("FR", "France");
        countries.put("ES", "Spain");
        countries.put("IT", "Italy");
        countries.put("PT", "Portugal");
        countries.put("BR", "Brazil");
        countries.put("NL", "Netherlands");
        countries.put("RU", "Russia");
        countries.put("TH", "Thailand");
        countries.put("VN", "Vietnam");
        countries.put("MY", "Malaysia");
        countries.put("SG", "Singapore");
        countries.put("PH", "Philippines");
        countries.put("IN", "India");
        countries.put("SA", "Saudi Arabia");
        countries.put("AE", "United Arab Emirates");
        countries.put("TR", "Turkey");
        
        return countries.getOrDefault(countryCode, "Unknown");
    }
}

