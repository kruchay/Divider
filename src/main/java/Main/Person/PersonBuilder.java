package Main.Person;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PersonBuilder {

    private PersonBuilder()
    {
    }

    // Генерация списка людей
    public static List<Person> generate(int totalNumber)
    {
        List<Person> personList = new ArrayList<>(totalNumber + 1);
        if (totalNumber != 0) {
            for (int i = 0; i < totalNumber; i++) {
                int age = (int) (Math.random() * 100) + 1; // 1..100
                personList.add(new Person(age));
            }
        }
        return personList;
    }

    // Загрузка из файла списка людей
    public static List<Person> loadFromFile(File filePath) throws FileNotFoundException
    {
        List<Person> personList = new ArrayList<>(128);
        try (Scanner scanner = new Scanner(filePath)) {
             while (scanner.hasNextInt()) {
                 int age = scanner.nextInt(10);
                 personList.add(new Person(age));
             }
         }
        return personList;
    }

}
