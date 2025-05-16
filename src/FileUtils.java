import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FileUtils {
    
    public static boolean saveDiaryToFile(String filePath, String dateFormat, 
                                      LocalDateTime[] timestamps, String[] entries, int entryCount) {
        try {
            FileWriter writer = new FileWriter(filePath);
            
            writer.write("FORMAT:" + dateFormat + "\n");
            
            for (int i = 0; i < entryCount; i++) {
                if (timestamps[i] != null) {
                    writer.write("DATE:" + timestamps[i].toString() + "\n");
                    writer.write("ENTRY:\n" + entries[i]);
                    writer.write("END_ENTRY\n\n");
                }
            }
            
            writer.close();
            System.out.println("Щоденник успішно збережено у файл: " + filePath);
            return true;
            
        } catch (IOException e) {
            System.out.println("Помилка при збереженні файлу: " + e.getMessage());
            return false;
        }
    }

    public static int loadDiaryFromFile(String filePath, String[] dates, LocalDateTime[] timestamps, 
                                   String[] entries, int maxEntries, String[] dateFormatRef) {
        if (filePath.isEmpty()) {
            System.out.println("Шлях до файлу не вказано!");
            return 0;
        }
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            System.out.println("Файл не знайдено або це не файл!");
            return 0;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            
            int entryCount = 0;
            String line = reader.readLine();
            
            // Зчитування формату дати
            if (line != null && line.startsWith("FORMAT:")) {
                String savedFormat = line.substring(7);
                try {
                    // Перевіряємо валідність формату
                    DateTimeFormatter.ofPattern(savedFormat);
                    dateFormatRef[0] = savedFormat;
                    System.out.println("Завантажено формат дати: " + savedFormat);
                } catch (IllegalArgumentException e) {
                    System.out.println("Невалідний формат дати у файлі. Використовується формат за замовчуванням.");
                }
                
                line = reader.readLine();
            }
            
            String currentEntry = "";
            LocalDateTime currentTimestamp = null;
            
            while (line != null) {
                if (line.startsWith("DATE:")) {
                    try {
                        currentTimestamp = LocalDateTime.parse(line.substring(5));
                    } catch (DateTimeParseException e) {
                        System.out.println("Помилка при зчитуванні дати із файлу. Пропускаємо запис.");
                        // Перейти до наступного запису
                        while (line != null && !line.equals("END_ENTRY")) {
                            line = reader.readLine();
                        }
                        line = reader.readLine(); // пропускаємо порожній рядок
                        continue;
                    }
                } else if (line.equals("ENTRY:")) {
                    currentEntry = "";
                    line = reader.readLine();
                    
                    while (line != null && !line.equals("END_ENTRY")) {
                        currentEntry += line + "\n";
                        line = reader.readLine();
                    }
                    
                    if (entryCount < maxEntries) {
                        timestamps[entryCount] = currentTimestamp;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatRef[0]);
                        dates[entryCount] = currentTimestamp.format(formatter);
                        entries[entryCount] = currentEntry;
                        entryCount++;
                    } else {
                        System.out.println("Досягнуто максимальну кількість записів. Деякі записи не завантажено.");
                        break;
                    }
                }
                
                line = reader.readLine();
            }
            
            reader.close();
            System.out.println("Щоденник успішно завантажено з файлу: " + filePath);
            System.out.println("Завантажено " + entryCount + " записів.");
            
            return entryCount;
            
        } catch (IOException e) {
            System.out.println("Помилка при зчитуванні файлу: " + e.getMessage());
            return 0;
        }
    }
    
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}