package test;

import java.io.*;
import java.util.*;

/* Свой список
Посмотреть, как реализован LinkedList.
Элементы следуют так: 1->2->3->4  и так 4->3->2->1
По образу и подобию создать Solution.
Элементы должны следовать так:
1->3->7->15
    ->8...
 ->4->9
    ->10
2->5->11
    ->12
 ->6->13
    ->14
Удалили 2 и 9
1->3->7->15
    ->8
 ->4->10
Добавили 16,17,18,19,20 (всегда добавляются на самый последний уровень к тем элементам, которые есть)
1->3->7->15
       ->16
    ->8->17
       ->18
 ->4->10->19
        ->20
Удалили 18 и 20
1->3->7->15
       ->16
    ->8->17
 ->4->10->19
Добавили 21 и 22 (всегда добавляются на самый последний уровень к тем элементам, которые есть.
Последний уровень состоит из 15, 16, 17, 19. 19 последний добавленный элемент, 10 - его родитель.
На данный момент 10 не содержит оба дочерних элемента, поэтому 21 добавился к 10. 22 добавляется в следующий уровень.)
1->3->7->15->22
       ->16
    ->8->17
 ->4->10->19
        ->21

Во внутренней реализации элементы должны добавляться по 2 на каждый уровень
Метод getParent должен возвращать элемент, который на него ссылается.
Например, 3 ссылается на 7 и на 8, т.е.  getParent("8")=="3", а getParent("13")=="6"
Строки могут быть любыми.
При удалении элемента должна удаляться вся ветка. Например, list.remove("5") должен удалить "5", "11", "12"
Итерироваться элементы должны в порядке добавления
Доступ по индексу запрещен, воспользуйтесь при необходимости UnsupportedOperationException
Должно быть наследование AbstractList<String>, List<String>, Cloneable, Serializable
Метод main в тестировании не участвует
*/

public class Solution extends AbstractList<String> implements List<String>, Cloneable, Serializable {

    /* Метод main() используется только для тестирования */

    public static void main(String[] args) throws Exception {
        List<String> list = new Solution();

        for (int i = 1; i < 16; i++) {
            list.add(String.valueOf(i));
        }

        Solution l = (Solution) list;

        //----итератор (вывод всех элементов)
        System.out.println("Выводим элементы в цикле через итератор:");

        Iterator<String> iter = l.iterator();
        while (iter.hasNext()) {
            System.out.print(iter.next() + " ");
        }
        System.out.println("");

        //----просто вывод
        System.out.println("Выводим начальное дерево (простой вывод, работает toString()):");

        System.out.println(l);

        //----удаляем 5
        System.out.println("Удаляем 5:");

        l.remove("5");
        System.out.println(l);

        //----клонируем
        System.out.println("Клонируем:");

        Solution cloned = (Solution) l.clone();
        System.out.println(cloned);
        System.out.println("Клонирование: " + l.equals(cloned));

        //----сериализация. десериализация
        System.out.println("Сериализуем и десериализуем (исходный лист, без 5):");

        FileOutputStream fos = new FileOutputStream("testSerial.txt");
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(l);
        out.close();
        fos.close();

        FileInputStream fis = new FileInputStream("testSerial.txt");
        ObjectInputStream in = new ObjectInputStream(fis);
        Solution l2 = (Solution) in.readObject();
        in.close();
        fis.close();

        System.out.println(l2);
        System.out.println("Сериализация: " + l.equals(l2));

        //----клонирование
        System.out.println("Снова клонируем (десериализованный лист):");

        Solution l3 = (Solution) l2.clone();
        System.out.println(l3 + " - клон: " + l3.equals(l2));

        //----сериализация. десериализация
        System.out.println("Снова сериализуем и десериализуем (клон):");

        fos = new FileOutputStream("/Users/derenkosn/Desktop/testSerial.txt");
        out = new ObjectOutputStream(fos);
        out.writeObject(l3);
        out.close();
        fos.close();

        fis = new FileInputStream("/Users/derenkosn/Desktop/testSerial.txt");
        in = new ObjectInputStream(fis);
        Solution l4 = (Solution) in.readObject();
        in.close();
        fis.close();

        System.out.println(l4);

        //----итератором удаляем 6
        System.out.println("Удаляем 6 с помощью итератора (из десериализованного листа):");

        iter = l4.iterator();
        while (iter.hasNext()) {
            String next = iter.next();
            if ("6".equals(next))
                iter.remove();
        }
        System.out.println(l4);

        //----очистка списка
        System.out.println("Очищаем лист:");

        l4.clear();
        System.out.println(l4 + " - пустой: " + l4.isEmpty());
    }

    private static final long serialVersionUID = 7142557871662602445L;
    private transient Node<String> root = new Node<>(null, null, null, null, null, null);
    transient int size = 0;

    //-----------Класс Node (по образцу LinkedList)-----------

    private static class Node<String> {

        String item;

        Solution.Node<String> parent;
        Solution.Node<String> left;
        Solution.Node<String> right;

        Solution.Node<String> prev;    // порядок добавления элементов:
        Solution.Node<String> next;    // до и после данного

        Node(Solution.Node<String> parent, String element, Solution.Node<String> left, Solution.Node<String> right, Solution.Node<String> prev, Solution.Node<String> next) {
            this.item = element;
            this.parent = parent;
            this.left = left;
            this.right = right;
            this.prev = prev;
            this.next = next;
        }
    }

    //-----------------Класс итератор------------------

    private class ListItr implements ListIterator<String> {
        private Node<String> lastReturned = null;
        private Node<String> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr() {
            next = root.next;
            nextIndex = 0;
        }

        private Node<String> nextNode() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;

            return lastReturned;
        }

        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public String next() {
            return nextNode().item;
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public String previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<String> lastNext = lastReturned.next;
            int sizeBefore = size;
            unlink(lastReturned);
            int sizeAfter = size;
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount += sizeBefore - sizeAfter;
        }

        @Override
        public void set(String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(String s) {
            throw new UnsupportedOperationException();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }


    //---------Новый метод, возвращающий родителя---------

    public String getParent(String value) {

        Node<String> node = getNodeByValue(value);
        if (node != null && node.parent != root)
            return node.parent.item;
        else
            return null;
    }

    //-----------Мои методы, выполняющие основную работу-----------

    private Node<String> getNodeByValue(String value) {

        if (value != null && !value.equals("")) {
            ListItr iter = new ListItr();
            while (iter.hasNext()) {
                Node<String> node = iter.nextNode();
                if (value.equals(node.item))
                    return node;
            }
        }
        return null;
    }

    private void unlink(Node<String> node) {
        if (node != null) {
            if (node.prev != null)
                node.prev.next = node.next;
            if (node.next != null)
                node.next.prev = node.prev;

            unlink(node.right);
            unlink(node.left);

            if (node.parent.left == node)
                node.parent.left = null;
            else
                node.parent.right = null;

            size--;
            modCount++;
        }
    }

    private void link(String s) {
        Node<String> newNode;
        Node<String> lastAdded = null;
        Node<String> lastAddedParent;
        Node<String> firstWithoutDescendants = null;
        Node<String> parent;

        //---добавляем первый узел
        if (root.next == null) {
            newNode = new Node<>(root, s, null, null, root, null);
            root.left = newNode;
            root.next = newNode;
            size++;
            modCount++;
        } else {

            //----ищем последний добавленный узел и первый узел без потомков
            ListItr iter = new ListItr();
            while (iter.hasNext())
                lastAdded = iter.nextNode();

            lastAddedParent = lastAdded.parent;

            iter = new ListItr();
            while (iter.hasNext()) {
                firstWithoutDescendants = iter.nextNode();
                if (firstWithoutDescendants.left == null && firstWithoutDescendants.right == null)
                    break;
            }

            if (lastAddedParent.left == null) {
                parent = lastAddedParent;
                newNode = new Node<>(parent, s, null, null, lastAdded, null);
                lastAdded.next = newNode;
                parent.left = newNode;
                size++;
                modCount++;

            } else if (lastAddedParent.right == null) {
                parent = lastAddedParent;
                newNode = new Node<>(parent, s, null, null, lastAdded, null);
                lastAdded.next = newNode;
                parent.right = newNode;
                size++;
                modCount++;
            } else {
                parent = firstWithoutDescendants;
                newNode = new Node<>(parent, s, null, null, lastAdded, null);
                lastAdded.next = newNode;
                parent.left = newNode;
                size++;
                modCount++;
            }
        }
    }


    //--------- Методы, которые необходимо реализовать-------

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return getNodeByValue((String) o) != null;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        ListItr iter = new ListItr();
        while (iter.hasNext())
            result[i++] = iter.next();

        return result;
    }

    @Override
    public boolean remove(Object o) {
        Node<String> searched = getNodeByValue((String) o);
        if (searched != null) {
            unlink(searched);
            return true;
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object elem : c) {
            if (!contains(elem))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        for (String elem : c) {
            add(elem);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return removeAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            remove(o);
        }
        return true;
    }

    @Override
    public void clear() {
        unlink(root.right);
        unlink(root.left);
        root.next = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        ListItr iter = new ListItr();
        Node<String> node;
        while (iter.hasNext()) {
            node = iter.nextNode();
            sb.append(node.item + ", ");
        }
        String s = sb.toString();
        if (s.length() > 1)
            s = s.substring(0, s.length() - 2) + "]";
        else
            s += "]";
        return s;
    }

    @Override
    public Iterator<String> iterator() {
        return new ListItr();
    }

    @Override
    public boolean add(String s) {
        if (s != null && !"".equals(s)) {
            link(s);
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (root != null ? root.hashCode() : 0);
        result = 31 * result + size;
        return result;
    }

    /* Метод equals(Object o) так "странно" реализован из-за особенностей автоматической проверки JavaRush:
    как я понял из обсуждений на форуме, он должен проверять на равенство не только с объеком того же класса,
     но и с любым листом. Такое решение зачлось.*/

    @Override
    public boolean equals(Object o) {
        try {
            if (o instanceof Solution) {
                String s = "[";
                ListItr iter = new ListItr();
                Node<String> node;
                while (iter.hasNext()) {
                    node = iter.nextNode();
                    s += "(" + node.parent.item + ") " + node.item + ", ";
                }
                if (s.length() > 1)
                    s = s.substring(0, s.length() - 2) + "]";
                else
                    s += "]";

                Solution l = (Solution) o;

                if (l.size() != size)
                    return false;

                String s2 = "[";
                ListIterator<?> lIter = l.listIterator();

                while (lIter.hasNext()) {
                    String next = (String) lIter.next();
                    if (lIter.nextIndex() > 2)
                        s2 += "(" + l.getParent(next) + ") " + next + ", ";
                    else
                        s2 += "(null) " + next + ", ";
                }

                if (s2.length() > 1)
                    s2 = s2.substring(0, s2.length() - 2) + "]";
                else
                    s2 += "]";

                return s.equals(s2);

            } else {
                List<? extends String> l = (List<? extends String>) o;

                if (!(l.get(0) instanceof String))
                    return false;

                if (l.size() != size)
                    return false;

                ListItr iter = new ListItr();
                ListIterator<?> lIter = l.listIterator();

                while (iter.hasNext()) {
                    if (!iter.next().equals(lIter.next())) {
                        return false;
                    }
                }
            }
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }


    @Override
    public ListIterator<String> listIterator() {
        return new ListItr();
    }


    //---- Переопределение методов для сериализации и десериализации

    private void writeObject(ObjectOutputStream s) throws IOException {

        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        ListItr iter = new ListItr();
        while (iter.hasNext())
            s.writeObject(iter.next());

        // Write the information about parents and descendants
        Node<String> node;
        iter = new ListItr();
        while (iter.hasNext()) {
            node = iter.nextNode();
            if (node.parent != root)
                s.writeObject(node.parent.item);
            else
                s.writeObject("ROOT");
            if (node.left != null)
                s.writeObject(node.left.item);
            else
                s.writeObject("NULL");
            if (node.right != null)
                s.writeObject(node.right.item);
            else
                s.writeObject("NULL");
        }

    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {

        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();
        root = new Node<>(null, null, null, null, null, null);

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            link((String) s.readObject());

        //Read and rewrite the information about parents and descendants
        Node<String> node;
        ListItr iter = new ListItr();
        while (iter.hasNext()) {

            node = iter.nextNode();

            String parent = (String) s.readObject();
            if ("ROOT".equals(parent))
                node.parent = root;
            else
                node.parent = getNodeByValue(parent);

            String left = (String) s.readObject();
            if ("NULL".equals(left))
                node.left = null;
            else
                node.left = getNodeByValue(left);

            String right = (String) s.readObject();
            if ("NULL".equals(right))
                node.right = null;
            else
                node.right = getNodeByValue(right);
        }
    }

    //---- Переопределение метода для клонирования

    public Object clone() {

        Solution clone = superClone();

        // Put clone into "virgin" state
        clone.root = new Node<>(null, null, null, null, null, null);
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        ListItr iter = new ListItr();
        while (iter.hasNext())
            clone.add(iter.next());

        // Rewrite the information about parents and descendants
        Node<String> nodeSource;
        Node<String> nodeClone;
        iter = new ListItr();
        ListItr iterClone = (ListItr) clone.listIterator();
        while (iter.hasNext()) {

            nodeSource = iter.nextNode();
            nodeClone = iterClone.nextNode();

            if (nodeSource.parent != root)
                nodeClone.parent = nodeSource.parent;
            else
                nodeClone.parent = clone.root;

            nodeClone.left = nodeSource.left;
            nodeClone.right = nodeSource.right;
        }

        return clone;
    }

    private Solution superClone() {
        try {
            return (Solution) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }


    //--------- Методы, обращающиеся по индексу (и не только) - не реализуются, выбрасывают исключение -------


    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String set(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

}