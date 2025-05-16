import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MyDiary {
    static final int MAX_ENTRIES = 50;
    private String[] dates;
    private LocalDateTime[] timestamps;
    private String[] entries;
    private int entryCount;
    private String dateFormat;
    private String currentFileName;

    public MyDiary() {
        dates = new String[MAX_ENTRIES];
        timestamps = new LocalDateTime[MAX_ENTRIES];
        entries = new String[MAX_ENTRIES];
        entryCount = 0;
        dateFormat = "dd.MM.yyyy";
        currentFileName = null;
    }

    public void chooseDateFormat(Scanner scanner) {
        System.out.println("\nОберіть формат відображення дати:");
        System.out.println("1. DD.MM.YYYY (31.12.2025)");
        System.out.println("2. MM/DD/YYYY (12/31/2025)");
        System.out.println("3. YYYY-MM-DD (2024-12-31)");
        System.out.println("4. DD MMMM YYYY (31 грудня 2025)");
        System.out.println("5. Власний формат");
        System.out.print("Ваш вибір (1-5): ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                dateFormat = "dd.MM.yyyy";
                break;
            case "2":
                dateFormat = "MM/dd/yyyy";
                break;
            case "3":
                dateFormat = "yyyy-MM-dd";
                break;
            case "4":
                dateFormat = "dd MMMM yyyy";
                break;
            case "5":
                System.out.println("Введіть власний формат дати (приклад: dd.MM.yyyy HH:mm):");
                String customFormat = scanner.nextLine();
                if (!customFormat.isEmpty()) {
                    try {
                        // Перевірка, чи валідний формат
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(customFormat);
                        formatter.format(LocalDateTime.now());
                        dateFormat = customFormat;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Невалідний формат дати. Встановлено формат за замовчуванням (dd.MM.yyyy)");
                    }
                }
                break;
            default:
                System.out.println("Невідомий вибір. Встановлено формат за замовчуванням (dd.MM.yyyy)");
        }
        
        System.out.println("Встановлено формат дати: " + dateFormat);
    }

    public void mainMenu(Scanner scanner) {
        while (true) {
            System.out.println("""
                
                Мій щоденник:
                1. Додати запис
                2. Видалити запис
                3. Переглянути всі записи
                4. Змінити формат дати
                5. Зберегти щоденник
                6. Завантажити щоденник
                7. Вийти    
                    """);
            System.out.print("Оберіть опцію (1-7): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addEntry(scanner);
                    break;
                case "2":
                    deleteEntry(scanner);
                    break;
                case "3":
                    viewAllEntries();
                    break;
                case "4":
                    chooseDateFormat(scanner);
                    break;
                case "5":
                    saveDiaryToFile(scanner);
                    break;
                case "6":
                    loadDiaryFromFile(scanner);
                    break;
                case "7":
                    confirmExit(scanner);
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private void addEntry(Scanner scanner) {
    if (entryCount >= MAX_ENTRIES) {
        System.out.println("Щоденник повний! Видаліть старі записи.");
        return;
    }

    System.out.print("Введіть дату (формат " + dateFormat + "): ");
    String dateInput = scanner.nextLine();

    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        LocalDateTime date;
        
        // Спроба розпарсити дату з часом
        if (dateFormat.contains("HH:mm")) {
            date = LocalDateTime.parse(dateInput, formatter);
        } else {
            // Якщо формат не містить час, додаємо поточний час
            date = LocalDateTime.parse(dateInput + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), 
                   DateTimeFormatter.ofPattern(dateFormat + " HH:mm"));
        }
        
        timestamps[entryCount] = date;
        dates[entryCount] = formatter.format(date);

        System.out.println("Введіть текст запису (для завершення введіть порожній рядок):");
        StringBuilder entry = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            entry.append(line).append("\n");
        }

        if (entry.length() > 0) {
            entries[entryCount] = entry.toString();
            entryCount++;
            System.out.println("Запис додано успішно!");
        }
    } catch (DateTimeParseException e) {
        System.out.println("Невірний формат дати! Будь ласка, використовуйте формат " + dateFormat);
    }
}

    private void deleteEntry(Scanner scanner) {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній!");
            return;
        }

        System.out.print("Введіть дату запису для видалення (формат " + dateFormat + "): ");
        String dateToDelete = scanner.nextLine();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            // Перевірка валідності формату
            formatter.parse(dateToDelete);
            
            boolean found = false;
            for (int i = 0; i < entryCount; i++) {
                if (dates[i].equals(dateToDelete)) {
                    for (int j = i; j < entryCount - 1; j++) {
                        dates[j] = dates[j + 1];
                        timestamps[j] = timestamps[j + 1];
                        entries[j] = entries[j + 1];
                    }
                    entryCount--;
                    System.out.println("Запис видалено успішно!");
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.out.println("Запис з такою датою не знайдено!");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Невірний формат дати! Будь ласка, використовуйте формат " + dateFormat);
        }
    }

    private void viewAllEntries() {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній!");
            return;
        }

        System.out.println("\nВсі записи:");
        for (int i = 0; i < entryCount; i++) {
            System.out.println("\nДата: " + dates[i]);
            System.out.println("Запис:");
            System.out.println(entries[i]);
        }
    }

   public void saveDiaryToFile(Scanner scanner) {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній, нічого зберігати!");
            return;
        }

        System.out.print("Введіть шлях до файлу для збереження" + 
                        (currentFileName != null ? " (Enter для використання " + currentFileName + ")" : "") + ": ");
        String filePath = scanner.nextLine();
        
        if (filePath.isEmpty() && currentFileName != null) {
            filePath = currentFileName;
        } else if (filePath.isEmpty()) {
            System.out.println("Шлях до файлу не вказано!");
            return;
        }

        boolean success = FileUtils.saveDiaryToFile(filePath, dateFormat, timestamps, entries, entryCount);
        if (success) {
            currentFileName = filePath;
        }
    }

    public void loadDiaryFromFile(Scanner scanner) {
        System.out.print("Введіть шлях до файлу для завантаження: ");
        String filePath = scanner.nextLine();
        
        if (filePath.isEmpty()) {
            System.out.println("Шлях до файлу не вказано!");
            return;
        }

        String[] dateFormatRef = new String[1];
        dateFormatRef[0] = dateFormat;
        
        int loadedEntries = FileUtils.loadDiaryFromFile(filePath, dates, timestamps, entries, MAX_ENTRIES, dateFormatRef);
        
        if (loadedEntries > 0) {
            entryCount = loadedEntries;
            currentFileName = filePath;
            dateFormat = dateFormatRef[0];
        }
    }

    private void confirmExit(Scanner scanner) {
        if (entryCount > 0) {
            System.out.print("Зберегти щоденник перед виходом? (так/ні): ");
            String response = scanner.nextLine().toLowerCase();
            
            if (response.equals("так") || response.equals("y") || response.equals("yes")) {
                saveDiaryToFile(scanner);
            }
        }
        
        System.out.println("Дякуємо за використання програми 'Мій щоденник'. До побачення!");
        // Не закриваємо scanner тут, щоб уникнути проблем з його використанням в інших частинах програми
    }
}