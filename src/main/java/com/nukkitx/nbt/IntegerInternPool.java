/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.nukkitx.nbt;

import javax.annotation.Nonnull;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * A pool for interning {@link Integer} values, with support for very high concurrency.
 *
 * @author DaPorkchop_
 */
public final class IntegerInternPool {
    private static final Map<Object, WeakInteger> CACHE = new ConcurrentSkipListMap<>((a, b) -> {
        if (a instanceof Integer) { //this is a retrieval, unbox the temporary boxed int before comparison
            return Integer.compare((Integer) a, ((WeakInteger) b).value);
        } else { //this is an insertion/removal, compare the values
            return Integer.compare(((WeakInteger) a).value, ((WeakInteger) b).value);
        }
    });

    private static final ReferenceQueue<?>[] REFERENCE_QUEUES = IntStream.range(0, Runtime.getRuntime().availableProcessors())
            .mapToObj(i -> new ReferenceQueue<>())
            .toArray(ReferenceQueue[]::new);

    private static void expungeStaleEntries(@Nonnull ReferenceQueue<?> referenceQueue) {
        WeakInteger entry;
        while ((entry = (WeakInteger) referenceQueue.poll()) != null) {
            CACHE.remove(entry, entry);
        }
    }

    public static Integer intern(int value) {
        if ((int) (byte) value == value) { //value is small enough to be cached by Java itself, so we don't have to do anything
            return Integer.valueOf(value);
        }

        Integer boxed = value;
        Integer interned;

        //check to see if value is already cached
        WeakInteger entry = CACHE.get(boxed);
        if (entry != null) {
            expungeStaleEntries(entry.referenceQueue);

            if ((interned = entry.get()) != null) { //cached value hasn't been GC'd
                return interned; //re-use existing cached value
            }
        }

        //try to insert a new element
        WeakInteger newEntry = new WeakInteger(boxed, REFERENCE_QUEUES[ThreadLocalRandom.current().nextInt(REFERENCE_QUEUES.length)]);
        do {
            entry = CACHE.putIfAbsent(newEntry, newEntry);
            if (entry != null) { //another thread inserted their entry before us, let's try to re-use that
                expungeStaleEntries(entry.referenceQueue);
                interned = entry.get();
            } else { //we won the race to insert a new entry
                interned = boxed;
            }
        } while (interned == null);

        return interned;
    }

    private static final class WeakInteger extends WeakReference<Integer> {
        private final int value;
        private final ReferenceQueue<?> referenceQueue;

        @SuppressWarnings("unchecked")
        public WeakInteger(@Nonnull Integer value, @Nonnull ReferenceQueue referenceQueue) {
            super(value, referenceQueue);

            this.value = value;
            this.referenceQueue = referenceQueue;
        }
    }
}
