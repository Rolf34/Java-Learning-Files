import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Вітаємо в додатку 'Мій щоденник'!");
            System.out.println("1. Створити новий щоденник");
            System.out.println("2. Відкрити існуючий щоденник");
            System.out.print("Ваш вибір (1-2): ");
            
            String initialChoice = scanner.nextLine();
            
            MyDiary diary = new MyDiary();
            
            if ("2".equals(initialChoice)) {
                diary.loadDiaryFromFile(scanner);
            } else if (!"1".equals(initialChoice)) {
                System.out.println("Невідомий вибір. Буде створено новий щоденник.");
            }
            
            diary.chooseDateFormat(scanner);
            diary.mainMenu(scanner);
        } finally {
            scanner.close();
        }
    }
}