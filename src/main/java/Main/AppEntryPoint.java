package Main;

import Main.Person.Person;
import Main.Person.PersonBuilder;
import Main.Utils.Divider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

public class AppEntryPoint {

    public static void main(String [] args) {
        runApp();
    }

    private static void runApp() {
        List<Person> objects;

        // Получение списка людей
        if (!AppConfig.getProperty("generate", true)) {
            String filePath = AppConfig.getProperty("file_path", "");
            try {
                objects = PersonBuilder.loadFromFile(new File(filePath));
            }
            catch (FileNotFoundException ex) {
                System.out.println();
                System.out.println("Error! Designated file \"" + filePath + "\" was not found");
                System.out.println("Please check settings in Divider.properties file");
                return;
            }
        } else {
            int totalNumber = AppConfig.getProperty("total_number", 25);
            objects = PersonBuilder.generate(totalNumber);
        }

        if (objects.size() < 2) {
            System.out.println();
            System.out.println("Error! Person list must have at least 2 elements");
            return;
        }

        // Разделение людей на две группы
        List<Person> group1 = new ArrayList<>(objects.size() / 2);
        List<Person> group2 = new ArrayList<>(objects.size() / 2);
        long timeStampBefore = System.currentTimeMillis();
        ToIntFunction<Person> weight = p -> (p != null) ? p.getAge() : 0;
        Divider.divideObjectsByWeight(objects, weight, group1, group2);
        long timeStampAfter = System.currentTimeMillis();

        // Результаты работы
        long[] totalWeight = new long[] {0, 0, 0};
        long[] numberOfObjects = new long[] {0, 0, 0};
        System.out.println();
        System.out.print("All people: ");
        objects.stream().filter(Objects::nonNull).forEach(p->System.out.printf("%d ", p.getAge()));
        System.out.println();
        System.out.print("FIRST group: ");
        group1.forEach(p->System.out.printf("%d ", p.getAge()));
        System.out.println();
        System.out.print("SECOND group: ");
        group2.forEach(p->System.out.printf("%d ", p.getAge()));
        System.out.println();
        System.out.println("==========================================================");
        numberOfObjects[0] = objects.stream().filter(Objects::nonNull).peek(p->totalWeight[0]+=p.getAge()).count();
        System.out.printf("Total weight of all people: %d. Members: %d", totalWeight[0], numberOfObjects[0]);
        System.out.println();
        numberOfObjects[1] = group1.stream().filter(Objects::nonNull).peek(p->totalWeight[1]+=p.getAge()).count(); // тут нулевых объектов быть не может, но фильтр добавляю из-за того что в Java9 и выше из-за оптимизации один лишь peek с count работать не будет!
        System.out.printf("Total weight of FIRST group: %d. Members: %d", totalWeight[1], numberOfObjects[1]);
        System.out.println();
        numberOfObjects[2] = group2.stream().filter(Objects::nonNull).peek(p->totalWeight[2]+=p.getAge()).count(); // тут нулевых объектов быть не может, но фильтр добавляю из-за того что в Java9 и выше из-за оптимизации один лишь peek с count работать не будет!
        System.out.printf("Total weight of SECOND group: %d. Members: %d", totalWeight[2], numberOfObjects[2]);
        System.out.println();
        System.out.printf("Difference in total weight of groups: %d", Math.abs(totalWeight[1] - totalWeight[2]));
        System.out.println();
        System.out.printf("Executing time: %dms", timeStampAfter - timeStampBefore);
        System.out.println();
        System.out.println();
    }

}
