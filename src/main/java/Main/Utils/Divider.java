package Main.Utils;

import java.util.List;
import java.util.function.ToIntFunction;

//  Алгоритм разделения списка объектов по группам
//  0.  Есть список объектов с разными весами
//  1.  Пока не пройдены все объекты в списке, найти два с максимальным весом
//  2.  Распределение найденных объектов по группам:
//        ЕСЛИ вес группы с минимальным суммарным весом + вес обоих найденных объектов
//        МЕНЬШЕ, чем вес группы с максимальным суммарным весом + вес найденного объекта с минимальным весом,
//        ТО,  добавляем оба объекта в группу с минимальным суммарным весом
//        ИНАЧЕ в группу с минимальным суммарным весом добавляем найденный объект с максимальным весом,
//        а в группу с максимальным суммарным весом добавляем найденный объект с минимальным весом
//  3. Отметить найденные объекты, как пройденные и вернуться на шаг 1

public class Divider {
    // Разделяет список объектов на две группы по весовому коэффициенту таким образом,
    // чтоб сумарный коэффициент каждой группы был примерно одинаковым!
    // Функция возвращает true, если разделение выполнено успешно
    // Параметры:
    //  [objects] - список объектов
    //  [weight] - функция получения весового коэффициента из объекта
    //  [group1], [group2] - списки объектов 1-й и 2-й группы, которые будут наполняться функцией
    public static <T> boolean divideObjectsByWeight(List<T> objects, ToIntFunction<T> weight, List<T> group1, List<T> group2) {
        if (objects == null || weight == null || group1 == null || group2 == null)
            return false;
        // Если все корректно, можно начинать работу
        int weightGroup1 = 0;
        int weightGroup2 = 0;
        Object[] arrObjects = objects.toArray();
        // Цикл перебора всех объектов массива. В каждой итерации получаем по два объекта с максимальнымми
        // весовымми коэффициентами и распределяем их по группам. После каждой итерации сужаем диапазон поиска в
        // массиве на количество найденных объектов, а сами найденные объекты исключаются из дальнейшего поиска.
        int seek = arrObjects.length-1;
        int[] result = new int[2];
        while (findTwoMaxObjects(arrObjects, 0, seek, weight, result)) {
           int index1 = result[0];
           int index2 = result[1];
           Object obj1 = null;
           Object obj2 = null;
           // Найдено два объекта
           if (index1 != -1 && index2 != -1) {
               obj1 = arrObjects[index1];
               obj2 = arrObjects[index2];
               // Сужение диапазона поиска
               if (seek != index1 && seek != index2) { // объект под индексом seek не отбирался как максимальный
                   if (index1 < seek-1) arrObjects[index1] = arrObjects[seek]; // ищем ему свободное место и
                   else arrObjects[index2] = arrObjects[seek]; // перемещаем его туда
               }
               if (seek-1 != index1 && seek-1 != index2) { // объект под индексом seek-1 не отбирался как максимальный
                   if (index2 < seek-1) arrObjects[index2] = arrObjects[seek-1]; // ищем ему свободное место и
                   else arrObjects[index1] = arrObjects[seek-1]; // перемещаем его туда
               }
              seek = seek - 2;
           }
           // Найден один объект
           else if (index1 != -1) {
               obj1 = arrObjects[index1];
               obj2 = null;
               // Сужение диапазона поиска
               if (seek != index1) {  // объект под индексом seek не отбирался как максимальный
                   arrObjects[index1] = arrObjects[seek]; // ищем ему свободное место и перемещаем его туда
               }
               seek = seek - 1;
           }
           // Определяем группу для найднных объектов
           int weightObj1 = (obj1 != null) ? weight.applyAsInt((T)obj1) : 0;
           int weightObj2 = (obj2 != null) ? weight.applyAsInt((T)obj2) : 0;
           defineGroupForObjects(weightObj1, weightObj2, weightGroup1, weightGroup2, result);
           int groupForObj1 = result[0];
           int groupForObj2 = result[1];
           if (obj1 != null && groupForObj1 == 1) {group1.add((T)obj1); weightGroup1 += weightObj1;}
           if (obj1 != null && groupForObj1 == 2) {group2.add((T)obj1); weightGroup2 += weightObj1;}
           if (obj2 != null && groupForObj2 == 1) {group1.add((T)obj2); weightGroup1 += weightObj2;}
           if (obj2 != null && groupForObj2 == 2) {group2.add((T)obj2); weightGroup2 += weightObj2;}
       }
       return true;
    }

    // Поиск в указанном диапазоне массива двух объектов с максимальными весовыми коэффициентами
    // Функция возвращает true, если найден хотябы один объект
    // Параметры:
    //  [arrObjects] - массив объектов
    //  [from], [to] - диапазон поиска
    //  [weight] - функция получения весового коэффициента из объекта
    //  [result] - массив на два элемента, куда функция будет возвращать индексы соответственно 1-го и 2-го найденного объекта
    //             с максимальными весовыми коэффициентами; если индекс -1 соответствующий объект не найден
    private static <T> boolean findTwoMaxObjects(Object[] arrObjects, int from, int to, ToIntFunction<T> weight, int[] result) {
        int index1 = -1;
        int index2 = -1;
        if (to <= arrObjects.length-1) {
            int max1 = Integer.MIN_VALUE;
            int max2 = Integer.MIN_VALUE;
            for (int i = from; i <= to; i++) {
                if (arrObjects[i] == null) continue;
                int cur = weight.applyAsInt((T)arrObjects[i]);
                if (cur > max1) {
                    max2 = max1;
                    index2 = index1;
                    max1 = cur;
                    index1 = i;
                } else if (cur > max2) {
                    max2 = cur;
                    index2 = i;
                }
            }
        }
        result[0] = index1;
        result[1] = index2;
        return index1 != -1; // Поиск успешный, если нашли хотябы один объект
    }

    // Определение группы назначения для двух объектов, согласно их весовых коэффициентов и суммарного веса каждой группы
    // Параметры:
    //  [weightObj1] - весовой коэффициент 1-го объекта
    //  [weightObj2] - весовой коэффициент 2-го объекта
    //  [weightGroup1] - суммарный вес 1-й группы
    //  [weightGroup2] - суммарный вес 2-й группы
    //  [result] - массив на два элемента, куда функция будет возвращать номер группы соответственно для 1-го и 2-го объекта
    private static void defineGroupForObjects(int weightObj1, int weightObj2, int weightGroup1, int weightGroup2, int[] result)
    {
        int groupForObj1 = 0;
        int groupForObj2 = 0;
        //  1-я группа имеет больший вес
        if (weightGroup1 >= weightGroup2)
        {
            if (weightGroup2 + weightObj1 + weightObj2 < weightGroup1 + Math.min(weightObj1, weightObj2)) {
                groupForObj1 = 2;
                groupForObj2 = 2;
            } else if (weightObj1 < weightObj2) {
                groupForObj1 = 1;
                groupForObj2 = 2;
            } else {
                groupForObj1 = 2;
                groupForObj2 = 1;
            }
        }
        //  2-я группа имеет больший вес
        if (weightGroup1 < weightGroup2)
        {
            if (weightGroup1 + weightObj1 + weightObj2 < weightGroup2 + Math.min(weightObj1, weightObj2)) {
                groupForObj1 = 1;
                groupForObj2 = 1;
            } else if (weightObj1 < weightObj2) {
                groupForObj1 = 2;
                groupForObj2 = 1;
            } else {
                groupForObj1 = 1;
                groupForObj2 = 2;
            }
        }
        result[0] = groupForObj1;
        result[1] = groupForObj2;
    }
}
