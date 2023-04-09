/**
 * 手写一个简单的HashMap
 * 关于HashMap的实现，还有几点我们没有解决：
 *
 * 1. 扩容问题: 在HashMap中，当存储的元素数量超过阈值（threshold = capacity * loadFactor）时，HashMap就会发生扩容（resize）,然后将内部的所有元素进行rehash，使hash冲突尽可能减少。
 *      在我们的MyHashMap中，虽然定义了加载因子，但是并没有使用它，capacity是固定的，虽然由于链表的存在，仍然可以一直存入数据，但是数据量增大时，查询效率将急剧下降。
 * 2. 树化问题（treeify）: 我们之前讲过，链表节点数量超过8时，为了更高的查询效率，链表将转化为红黑树。但是我们的代码中并没有实现这个功能。
 * 3. null值的判断: HashMap中是允许存null值的key的，key为null时，HashMap中的hash()方法会固定返回0，即key为null的值固定存在table[0]处。这个实现起来很简单，不实现的情况下MyHashMap中如果存入null值会直接报NullPointerException异常。
 * 4. 一些其他问题。
 */
public class MyHashMap<K, V> implements MyMap<K, V>{
    //参照HashMap设置一个默认的容量capacity和默认的加载因子loadFactor
    final static int DEFAULT_CAPACITY = 16;
    final static float DEFAULT_LOAD_FACTOR = 0.75f;
    private final Entry[] table;

    int capacity;
    float loadFactor;
    int size = 0;

    public MyHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int capacity, float loadFactor) {
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.table = new Entry[capacity];
    }

    /**
     * 用于获取大于capacity的最小的2次幂
     *     再HashMap中，开发者用更精妙的位运算方式完成了这个功能，效率更高
     *     为什么一定要2次幂？————>这是为了方便HashMap中的数组扩容时，已存在元素的重新哈希（rehash）考虑的
     * @param n
     * @return
     */
    private static int upperMinPowerOf2 (int n) {
        int power = 1;
        while (power <= n){
            power *= 2;
        }
        return power;
    }


    /**
     *  通过传入的K-V值构建一个Entry对象，然后判断它应该被放在数组的那个位置。
     *     根据“想要提高HashMap的效率，最重要的就是尽量避免生成链表，或者说尽量减少链表的长度”
     *     我们需要Entry对象尽可能均匀地散布在数组table中，且index不能超过table的长度，
     *     很明显，取模运算很符合我们的需求int index = k.hashCode() % table.length。
     *      而再HashMap源码中，HashMap中也使用了一种效率更高的方法——通过&运算完成key的散列
     *      插入链表有两种方法：
     *       如果使用尾插法，我们需要遍历这个链表，将新节点插入末尾；
     *       如果使用头插法，我们只需要将table[index]的引用指向新节点，然后将新节点的next引用指向原来table[index]位置的节点即可，这是jdk1.7的做法，而在jdk1.8中，使用的是“尾插法”。
     * @param k
     * @param v
     * @return
     */
    @Override
    public V put(K k, V v) {
        //通过hashcode散列
        int index = k.hashCode() % table.length;
        Entry<K, V> current = table[index];

        //判断table[index]是否已存在元素
        if (current != null) {
            //遍历链表是否有相等的key，有的话替换并且返回旧值
            while (current != null) {
                if (current.k == k) { //TODO HashMap中使用 equals方法？
                    V oldValue = current.v;
                    current.v = v;
                    return oldValue;
                }
                current = current.next;
            }
            //不存在的话就用头插法
            table[index] = new Entry<K, V>(k, v, table[index]);
            size++;
            return null;
        }
        // table[index]为空，直接赋值
        table[index] = new Entry<K, V>(k, v, table[index]);
        size++;
        return null;
    }


    /**
     * 根据key的hashcode计算它对应的index，然后直接去table中的对应位置查找即可，如果有链表就遍历。
     * @param k
     * @return
     */
    @Override
    public V get(K k) {
        int index = k.hashCode() % table.length;
        Entry<K, V> current = table[index];
        //遍历链表
        while (current != null) {
            if (current.k == k) {
                return current.v;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * 返回HashMap的大小（元素的个数）
     * @return
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * TODO 扩容方法
     */
    public void reSize(){

    }

    /**
     * TODO 对当前桶数组的元素重新进行散列
     * @param newTable
     */
    public void reHash(Entry<K, V>[] newTable) {

    }

    /**
     *  移除某个节点时，如果该key对应的index处没有形成链表，那么直接置为null。
     *      如果存在链表，我们需要将目标节点的前驱节点的next引用指向目标节点的后继节点。
     * @param k
     * @return
     */
    @Override
    public V remove(K k) {
        int index = k.hashCode() % table.length;
        Entry<K, V> current = table[index];
        //如果直接匹配到第一个节点
        if (current.k == k) {
            table[index] = null;
            size--;
            return current.v;
        }
        //在链表中删除节点
        while (current.next != null) {
            if (current.next.k == k) {
                V oldValue = current.next.v;;
                current.next = current.next.next;
                size--;
                return oldValue;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * 判断是否为空
     * @return
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * TODO 清空HashMap
     */
    @Override
    public void clear() {
        size = 0;

    }
}
