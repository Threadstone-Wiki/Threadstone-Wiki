//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

public class Long2ObjectOpenHashMap<V> extends AbstractLong2ObjectMap<V> implements Serializable, Cloneable, Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient V[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Long2ObjectMap.FastEntrySet<V> entries;
    protected transient LongSet keys;
    protected transient ObjectCollection<V> values;

    public Long2ObjectOpenHashMap(int expected, float f) {
        if (!(f <= 0.0F) && !(f > 1.0F)) {
            if (expected < 0) {
                throw new IllegalArgumentException("The expected number of elements must be nonnegative");
            } else {
                this.f = f;
                this.n = HashCommon.arraySize(expected, f);
                this.mask = this.n - 1;
                this.maxFill = HashCommon.maxFill(this.n, f);
                this.key = new long[this.n + 1];
                this.value = (Object[])(new Object[this.n + 1]);
            }
        } else {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
    }

    public Long2ObjectOpenHashMap(int expected) {
        this(expected, 0.75F);
    }

    public Long2ObjectOpenHashMap() {
        this(16, 0.75F);
    }

    public Long2ObjectOpenHashMap(Map<? extends Long, ? extends V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Long2ObjectOpenHashMap(Map<? extends Long, ? extends V> m) {
        this(m, 0.75F);
    }

    public Long2ObjectOpenHashMap(Long2ObjectMap<V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Long2ObjectOpenHashMap(Long2ObjectMap<V> m) {
        this(m, 0.75F);
    }

    public Long2ObjectOpenHashMap(long[] k, V[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        } else {
            for(int i = 0; i < k.length; ++i) {
                this.put(k[i], v[i]);
            }

        }
    }

    public Long2ObjectOpenHashMap(long[] k, V[] v) {
        this(k, v, 0.75F);
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
    }

    private void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }

    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(1073741824L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((double)((float)capacity / this.f)))));
        if (needed > this.n) {
            this.rehash(needed);
        }

    }

    private V removeEntry(int pos) {
        V oldValue = this.value[pos];
        this.value[pos] = null;
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }

        return oldValue;
    }

    private V removeNullEntry() {
        this.containsNullKey = false;
        V oldValue = this.value[this.n];
        this.value[this.n] = null;
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }

        return oldValue;
    }

    public void putAll(Map<? extends Long, ? extends V> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity((long)(this.size() + m.size()));
        }

        super.putAll(m);
    }

    private int insert(long k, V v) {
        int pos;
        if (k == 0L) {
            if (this.containsNullKey) {
                return this.n;
            }

            this.containsNullKey = true;
            pos = this.n;
        } else {
            long[] key = this.key;
            long curr;
            if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) != 0L) {
                if (curr == k) {
                    return pos;
                }

                while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (curr == k) {
                        return pos;
                    }
                }
            }
        }

        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }

        return -1;
    }

    public V put(long k, V v) {
        int pos = this.insert(k, v);
        if (pos < 0) {
            return this.defRetValue;
        } else {
            V oldValue = this.value[pos];
            this.value[pos] = v;
            return oldValue;
        }
    }

    /** @deprecated */
    @Deprecated
    public V put(Long ok, V ov) {
        int pos = this.insert(ok, ov);
        if (pos < 0) {
            return this.defRetValue;
        } else {
            V oldValue = this.value[pos];
            this.value[pos] = ov;
            return oldValue;
        }
    }

    protected final void shiftKeys(int pos) {
        long[] key = this.key;

        while(true) {
            int last = pos;
            pos = pos + 1 & this.mask;

            long curr;
            while(true) {
                if ((curr = key[pos]) == 0L) {
                    key[last] = 0L;
                    this.value[last] = null;
                    return;
                }

                int slot = (int)HashCommon.mix(curr) & this.mask;
                if (last <= pos) {
                    if (last >= slot || slot > pos) {
                        break;
                    }
                } else if (last >= slot && slot > pos) {
                    break;
                }

                pos = pos + 1 & this.mask;
            }

            key[last] = curr;
            this.value[last] = this.value[pos];
        }
    }

    public V remove(long k) {
        if (k == 0L) {
            return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
        } else {
            long[] key = this.key;
            long curr;
            int pos;
            if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
                return this.defRetValue;
            } else if (k == curr) {
                return this.removeEntry(pos);
            } else {
                while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (k == curr) {
                        return this.removeEntry(pos);
                    }
                }

                return this.defRetValue;
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public V remove(Object ok) {
        long k = (Long)((Long)ok);
        if (k == 0L) {
            return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
        } else {
            long[] key = this.key;
            long curr;
            int pos;
            if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
                return this.defRetValue;
            } else if (curr == k) {
                return this.removeEntry(pos);
            } else {
                while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (curr == k) {
                        return this.removeEntry(pos);
                    }
                }

                return this.defRetValue;
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public V get(Long ok) {
        if (ok == null) {
            return null;
        } else {
            long k = ok;
            if (k == 0L) {
                return this.containsNullKey ? this.value[this.n] : this.defRetValue;
            } else {
                long[] key = this.key;
                long curr;
                int pos;
                if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
                    return this.defRetValue;
                } else if (k == curr) {
                    return this.value[pos];
                } else {
                    while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                        if (k == curr) {
                            return this.value[pos];
                        }
                    }

                    return this.defRetValue;
                }
            }
        }
    }

    public V get(long k) {
        if (k == 0L) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        } else {
            long[] key = this.key;
            long curr;
            int pos;
            if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
                return this.defRetValue;
            } else if (k == curr) {
                return this.value[pos];
            } else {
                while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (k == curr) {
                        return this.value[pos];
                    }
                }

                return this.defRetValue;
            }
        }
    }

    public boolean containsKey(long k) {
        if (k == 0L) {
            return this.containsNullKey;
        } else {
            long[] key = this.key;
            long curr;
            int pos;
            if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
                return false;
            } else if (k == curr) {
                return true;
            } else {
                while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (k == curr) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public boolean containsValue(Object v) {
        V[] value = this.value;
        long[] key = this.key;
        if (this.containsNullKey) {
            label44: {
                if (value[this.n] == null) {
                    if (v != null) {
                        break label44;
                    }
                } else if (!value[this.n].equals(v)) {
                    break label44;
                }

                return true;
            }
        }

        int i = this.n;

        while(true) {
            do {
                if (i-- == 0) {
                    return false;
                }
            } while(key[i] == 0L);

            if (value[i] == null) {
                if (v == null) {
                    break;
                }
            } else if (value[i].equals(v)) {
                break;
            }
        }

        return true;
    }

    public void clear() {
        if (this.size != 0) {
            this.size = 0;
            this.containsNullKey = false;
            Arrays.fill(this.key, 0L);
            Arrays.fill(this.value, (Object)null);
        }
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    /** @deprecated */
    @Deprecated
    public void growthFactor(int growthFactor) {
    }

    /** @deprecated */
    @Deprecated
    public int growthFactor() {
        return 16;
    }

    public Long2ObjectMap.FastEntrySet<V> long2ObjectEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }

        return this.entries;
    }

    public LongSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }

        return this.keys;
    }

    public ObjectCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractObjectCollection<V>() {
                public ObjectIterator<V> iterator() {
                    return Long2ObjectOpenHashMap.this.new ValueIterator();
                }

                public int size() {
                    return Long2ObjectOpenHashMap.this.size;
                }

                public boolean contains(Object v) {
                    return Long2ObjectOpenHashMap.this.containsValue(v);
                }

                public void clear() {
                    Long2ObjectOpenHashMap.this.clear();
                }
            };
        }

        return this.values;
    }

    /** @deprecated */
    @Deprecated
    public boolean rehash() {
        return true;
    }

    public boolean trim() {
        int l = HashCommon.arraySize(this.size, this.f);
        if (l < this.n && this.size <= HashCommon.maxFill(l, this.f)) {
            try {
                this.rehash(l);
                return true;
            } catch (OutOfMemoryError var3) {
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean trim(int n) {
        int l = HashCommon.nextPowerOfTwo((int)Math.ceil((double)((float)n / this.f)));
        if (l < n && this.size <= HashCommon.maxFill(l, this.f)) {
            try {
                this.rehash(l);
                return true;
            } catch (OutOfMemoryError var4) {
                return false;
            }
        } else {
            return true;
        }
    }

    protected void rehash(int newN) {
        long[] key = this.key;
        V[] value = this.value;
        int mask = newN - 1;
        long[] newKey = new long[newN + 1];
        V[] newValue = (Object[])(new Object[newN + 1]);
        int i = this.n;

        int pos;
        for(int j = this.realSize(); j-- != 0; newValue[pos] = value[i]) {
            do {
                --i;
            } while(key[i] == 0L);

            if (newKey[pos = (int)HashCommon.mix(key[i]) & mask] != 0L) {
                while(newKey[pos = pos + 1 & mask] != 0L) {
                }
            }

            newKey[pos] = key[i];
        }

        newValue[newN] = value[this.n];
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Long2ObjectOpenHashMap<V> clone() {
        Long2ObjectOpenHashMap c;
        try {
            c = (Long2ObjectOpenHashMap)super.clone();
        } catch (CloneNotSupportedException var3) {
            throw new InternalError();
        }

        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (long[])this.key.clone();
        c.value = (Object[])this.value.clone();
        return c;
    }

    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;

        for(int t = false; j-- != 0; ++i) {
            while(this.key[i] == 0L) {
                ++i;
            }

            int t = HashCommon.long2int(this.key[i]);
            if (this != this.value[i]) {
                t ^= this.value[i] == null ? 0 : this.value[i].hashCode();
            }

            h += t;
        }

        if (this.containsNullKey) {
            h += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
        }

        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        long[] key = this.key;
        V[] value = this.value;
        Long2ObjectOpenHashMap<V>.MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;

        while(j-- != 0) {
            int e = i.nextEntry();
            s.writeLong(key[e]);
            s.writeObject(value[e]);
        }

    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        long[] key = this.key = new long[this.n + 1];
        V[] value = this.value = (Object[])(new Object[this.n + 1]);

        Object v;
        int pos;
        for(int i = this.size; i-- != 0; value[pos] = v) {
            long k = s.readLong();
            v = s.readObject();
            if (k == 0L) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                for(pos = (int)HashCommon.mix(k) & this.mask; key[pos] != 0L; pos = pos + 1 & this.mask) {
                }
            }

            key[pos] = k;
        }

    }

    private void checkTable() {
    }

    private final class ValueIterator extends Long2ObjectOpenHashMap<V>.MapIterator implements ObjectIterator<V> {
        public ValueIterator() {
            super(null);
        }

        public V next() {
            return Long2ObjectOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet extends AbstractLongSet {
        private KeySet() {
        }

        public LongIterator iterator() {
            return Long2ObjectOpenHashMap.this.new KeyIterator();
        }

        public int size() {
            return Long2ObjectOpenHashMap.this.size;
        }

        public boolean contains(long k) {
            return Long2ObjectOpenHashMap.this.containsKey(k);
        }

        public boolean rem(long k) {
            int oldSize = Long2ObjectOpenHashMap.this.size;
            Long2ObjectOpenHashMap.this.remove(k);
            return Long2ObjectOpenHashMap.this.size != oldSize;
        }

        public void clear() {
            Long2ObjectOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator extends Long2ObjectOpenHashMap<V>.MapIterator implements LongIterator {
        public KeyIterator() {
            super(null);
        }

        public long nextLong() {
            return Long2ObjectOpenHashMap.this.key[this.nextEntry()];
        }

        public Long next() {
            return Long2ObjectOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet extends AbstractObjectSet<Long2ObjectMap.Entry<V>> implements Long2ObjectMap.FastEntrySet<V> {
        private MapEntrySet() {
        }

        public ObjectIterator<Long2ObjectMap.Entry<V>> iterator() {
            return Long2ObjectOpenHashMap.this.new EntryIterator();
        }

        public ObjectIterator<Long2ObjectMap.Entry<V>> fastIterator() {
            return Long2ObjectOpenHashMap.this.new FastEntryIterator();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            } else {
                Map.Entry<?, ?> e = (Map.Entry)o;
                if (e.getKey() != null && e.getKey() instanceof Long) {
                    long k = (Long)((Long)e.getKey());
                    V v = e.getValue();
                    if (k == 0L) {
                        boolean var10000;
                        label86: {
                            if (Long2ObjectOpenHashMap.this.containsNullKey) {
                                if (Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n] == null) {
                                    if (v == null) {
                                        break label86;
                                    }
                                } else if (Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n].equals(v)) {
                                    break label86;
                                }
                            }

                            var10000 = false;
                            return var10000;
                        }

                        var10000 = true;
                        return var10000;
                    } else {
                        long[] key = Long2ObjectOpenHashMap.this.key;
                        long curr;
                        int pos;
                        if ((curr = key[pos = (int)HashCommon.mix(k) & Long2ObjectOpenHashMap.this.mask]) == 0L) {
                            return false;
                        } else if (k == curr) {
                            return Long2ObjectOpenHashMap.this.value[pos] == null ? v == null : Long2ObjectOpenHashMap.this.value[pos].equals(v);
                        } else {
                            while((curr = key[pos = pos + 1 & Long2ObjectOpenHashMap.this.mask]) != 0L) {
                                if (k == curr) {
                                    return Long2ObjectOpenHashMap.this.value[pos] == null ? v == null : Long2ObjectOpenHashMap.this.value[pos].equals(v);
                                }
                            }

                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        public boolean rem(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            } else {
                Map.Entry<?, ?> e = (Map.Entry)o;
                if (e.getKey() != null && e.getKey() instanceof Long) {
                    long k = (Long)((Long)e.getKey());
                    V v = e.getValue();
                    if (k == 0L) {
                        label43: {
                            if (Long2ObjectOpenHashMap.this.containsNullKey) {
                                if (Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n] == null) {
                                    if (v == null) {
                                        break label43;
                                    }
                                } else if (Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n].equals(v)) {
                                    break label43;
                                }
                            }

                            return false;
                        }

                        Long2ObjectOpenHashMap.this.removeNullEntry();
                        return true;
                    } else {
                        long[] key = Long2ObjectOpenHashMap.this.key;
                        long curr;
                        int pos;
                        if ((curr = key[pos = (int)HashCommon.mix(k) & Long2ObjectOpenHashMap.this.mask]) == 0L) {
                            return false;
                        } else if (curr == k) {
                            label50: {
                                if (Long2ObjectOpenHashMap.this.value[pos] == null) {
                                    if (v == null) {
                                        break label50;
                                    }
                                } else if (Long2ObjectOpenHashMap.this.value[pos].equals(v)) {
                                    break label50;
                                }

                                return false;
                            }

                            Long2ObjectOpenHashMap.this.removeEntry(pos);
                            return true;
                        } else {
                            while(true) {
                                do {
                                    if ((curr = key[pos = pos + 1 & Long2ObjectOpenHashMap.this.mask]) == 0L) {
                                        return false;
                                    }
                                } while(curr != k);

                                if (Long2ObjectOpenHashMap.this.value[pos] == null) {
                                    if (v == null) {
                                        break;
                                    }
                                } else if (Long2ObjectOpenHashMap.this.value[pos].equals(v)) {
                                    break;
                                }
                            }

                            Long2ObjectOpenHashMap.this.removeEntry(pos);
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        public int size() {
            return Long2ObjectOpenHashMap.this.size;
        }

        public void clear() {
            Long2ObjectOpenHashMap.this.clear();
        }
    }

    private class FastEntryIterator extends Long2ObjectOpenHashMap<V>.MapIterator implements ObjectIterator<Long2ObjectMap.Entry<V>> {
        private final Long2ObjectOpenHashMap<V>.MapEntry entry;

        private FastEntryIterator() {
            super(null);
            this.entry = Long2ObjectOpenHashMap.this.new MapEntry();
        }

        public Long2ObjectOpenHashMap<V>.MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator extends Long2ObjectOpenHashMap<V>.MapIterator implements ObjectIterator<Long2ObjectMap.Entry<V>> {
        private Long2ObjectOpenHashMap<V>.MapEntry entry;

        private EntryIterator() {
            super(null);
        }

        public Long2ObjectMap.Entry<V> next() {
            return this.entry = Long2ObjectOpenHashMap.this.new MapEntry(this.nextEntry());
        }

        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private class MapIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        LongArrayList wrapped;

        private MapIterator() {
            this.pos = Long2ObjectOpenHashMap.this.n;
            this.last = -1;
            this.c = Long2ObjectOpenHashMap.this.size;
            this.mustReturnNullKey = Long2ObjectOpenHashMap.this.containsNullKey;
        }

        public boolean hasNext() {
            return this.c != 0;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                --this.c;
                if (this.mustReturnNullKey) {
                    this.mustReturnNullKey = false;
                    return this.last = Long2ObjectOpenHashMap.this.n;
                } else {
                    long[] key = Long2ObjectOpenHashMap.this.key;

                    while(--this.pos >= 0) {
                        if (key[this.pos] != 0L) {
                            return this.last = this.pos;
                        }
                    }

                    this.last = Integer.MIN_VALUE;
                    long k = this.wrapped.getLong(-this.pos - 1);

                    int p;
                    for(p = (int)HashCommon.mix(k) & Long2ObjectOpenHashMap.this.mask; k != key[p]; p = p + 1 & Long2ObjectOpenHashMap.this.mask) {
                    }

                    return p;
                }
            }
        }

        private final void shiftKeys(int pos) {
            long[] key = Long2ObjectOpenHashMap.this.key;

            while(true) {
                int last = pos;
                pos = pos + 1 & Long2ObjectOpenHashMap.this.mask;

                long curr;
                while(true) {
                    if ((curr = key[pos]) == 0L) {
                        key[last] = 0L;
                        Long2ObjectOpenHashMap.this.value[last] = null;
                        return;
                    }

                    int slot = (int)HashCommon.mix(curr) & Long2ObjectOpenHashMap.this.mask;
                    if (last <= pos) {
                        if (last >= slot || slot > pos) {
                            break;
                        }
                    } else if (last >= slot && slot > pos) {
                        break;
                    }

                    pos = pos + 1 & Long2ObjectOpenHashMap.this.mask;
                }

                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new LongArrayList(2);
                    }

                    this.wrapped.add(key[pos]);
                }

                key[last] = curr;
                Long2ObjectOpenHashMap.this.value[last] = Long2ObjectOpenHashMap.this.value[pos];
            }
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            } else {
                if (this.last == Long2ObjectOpenHashMap.this.n) {
                    Long2ObjectOpenHashMap.this.containsNullKey = false;
                    Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n] = null;
                } else {
                    if (this.pos < 0) {
                        Long2ObjectOpenHashMap.this.remove(this.wrapped.getLong(-this.pos - 1));
                        this.last = -1;
                        return;
                    }

                    this.shiftKeys(this.last);
                }

                --Long2ObjectOpenHashMap.this.size;
                this.last = -1;
            }
        }

        public int skip(int n) {
            int i = n;

            while(i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }

            return n - i - 1;
        }
    }

    final class MapEntry implements Long2ObjectMap.Entry<V>, Map.Entry<Long, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        /** @deprecated */
        @Deprecated
        public Long getKey() {
            return Long2ObjectOpenHashMap.this.key[this.index];
        }

        public long getLongKey() {
            return Long2ObjectOpenHashMap.this.key[this.index];
        }

        public V getValue() {
            return Long2ObjectOpenHashMap.this.value[this.index];
        }

        public V setValue(V v) {
            V oldValue = Long2ObjectOpenHashMap.this.value[this.index];
            Long2ObjectOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            } else {
                boolean var10000;
                label31: {
                    Map.Entry<Long, V> e = (Map.Entry)o;
                    if (Long2ObjectOpenHashMap.this.key[this.index] == (Long)e.getKey()) {
                        if (Long2ObjectOpenHashMap.this.value[this.index] == null) {
                            if (e.getValue() == null) {
                                break label31;
                            }
                        } else if (Long2ObjectOpenHashMap.this.value[this.index].equals(e.getValue())) {
                            break label31;
                        }
                    }

                    var10000 = false;
                    return var10000;
                }

                var10000 = true;
                return var10000;
            }
        }

        public int hashCode() {
            return HashCommon.long2int(Long2ObjectOpenHashMap.this.key[this.index]) ^ (Long2ObjectOpenHashMap.this.value[this.index] == null ? 0 : Long2ObjectOpenHashMap.this.value[this.index].hashCode());
        }

        public String toString() {
            return Long2ObjectOpenHashMap.this.key[this.index] + "=>" + Long2ObjectOpenHashMap.this.value[this.index];
        }
    }
}
