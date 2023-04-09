/**
 * Entry 类保存了“K—V”数据，
 * next字段表明它可能是一个链表的节点
 * @param <K>
 * @param <V>
 */
public class Entry <K, V>{
    K k;
    V v;
    Entry<K, V> next;

    public Entry(K k, V v, Entry<K, V> next) {
        this.k = k;
        this.v = v;
        this.next = next;
    }
}
