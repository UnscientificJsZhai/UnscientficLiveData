package com.github.unscientificjszhai.livedata

import androidx.lifecycle.ListLiveData

/**
 *
 */
class ArrayListLiveData<T>(originalList: ArrayList<T>) :
    ListLiveData<T>(originalList), MutableList<T> {

    // LiveData实现部分

    private var mArrayList = ArrayList<T>()

    override fun setValue(value: List<T>?) {
        mArrayList = if (value != null) {
            ArrayList(value)
        } else {
            arrayListOf()
        }
        super.setValue(mArrayList)
    }

    override fun postValue(value: List<T>?) {
        mArrayList = if (value != null) {
            ArrayList(value)
        } else {
            arrayListOf()
        }
        super.postValue(value)
    }

    // List实现部分

    override operator fun get(index: Int) = mArrayList[index]

    override fun add(element: T): Boolean {
        val result = mArrayList.add(element)
        value = mArrayList
        return result
    }

    override val size: Int
        get() = mArrayList.size

    override fun contains(element: T) = mArrayList.contains(element)

    override fun containsAll(elements: Collection<T>) = mArrayList.containsAll(elements)

    override fun indexOf(element: T) = mArrayList.indexOf(element)

    override fun isEmpty() = mArrayList.isEmpty()

    override fun iterator(): MutableIterator<T> = ListIterator(mArrayList, 0)

    override fun lastIndexOf(element: T) = mArrayList.lastIndexOf(element)

    override fun add(index: Int, element: T) {
        mArrayList.add(index, element)
        value = mArrayList
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val result = mArrayList.addAll(index, elements)
        value = mArrayList
        return result
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val result = mArrayList.addAll(elements)
        value = mArrayList
        return result
    }

    override fun clear() {
        mArrayList.clear()
        value = mArrayList
    }

    override fun listIterator(): MutableListIterator<T> = ListIterator(mArrayList, 0)

    override fun listIterator(index: Int): MutableListIterator<T> = ListIterator(mArrayList, index)

    override fun remove(element: T): Boolean {
        val result = mArrayList.remove(element)
        value = mArrayList
        return result
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val result = mArrayList.removeAll(elements.toSet())
        value = mArrayList
        return result
    }

    override fun removeAt(index: Int): T {
        val result = mArrayList.removeAt(index)
        value = mArrayList
        return result
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val result = mArrayList.retainAll(elements.toSet())
        value = mArrayList
        return result
    }

    override operator fun set(index: Int, element: T): T {
        val previous = mArrayList.set(index, element)
        value = mArrayList
        return previous
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        SubList(fromIndex, toIndex)

    /**
     * LiveData子列表的包装类。用来保证对子列表进行操作也能触发数据推送。
     *
     * @param fromIndex 起始索引。包括此索引。
     * @param toIndex 终止索引。不包括此索引。
     */
    private inner class SubList(private var fromIndex: Int, private var toIndex: Int) :
        MutableList<T> {

        private val mSubList = mArrayList.subList(fromIndex, toIndex)

        override val size: Int
            get() = mSubList.size

        override fun contains(element: T) = mSubList.contains(element)

        override fun containsAll(elements: Collection<T>) = mSubList.containsAll(elements)

        override operator fun get(index: Int) = mSubList[index]

        override fun indexOf(element: T) = mSubList.indexOf(element)

        override fun isEmpty() = mSubList.isEmpty()

        override fun iterator() = ListIterator(this, 0)

        override fun lastIndexOf(element: T) = mSubList.lastIndexOf(element)

        override fun add(element: T): Boolean {
            val result = mSubList.add(element)
            value = mArrayList
            return result
        }

        override fun add(index: Int, element: T) {
            mSubList.add(index, element)
            fromIndex += 1
            value = mArrayList
        }

        override fun addAll(index: Int, elements: Collection<T>): Boolean {
            val result = mSubList.addAll(index, elements)
            toIndex = fromIndex + mSubList.size
            value = mArrayList
            return result
        }

        override fun addAll(elements: Collection<T>): Boolean {
            val result = mSubList.addAll(elements)
            toIndex = fromIndex + mSubList.size
            value = mArrayList
            return result
        }

        override fun clear() {
            mSubList.clear()
            toIndex = fromIndex
            value = mArrayList
        }

        override fun listIterator(): MutableListIterator<T> = ListIterator(this, 0)

        override fun listIterator(index: Int): MutableListIterator<T> = ListIterator(this, index)

        override fun remove(element: T): Boolean {
            val result = mSubList.remove(element)
            value = mArrayList
            toIndex -= 1
            return result
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            val result = mSubList.removeAll(elements)
            value = mArrayList
            toIndex = fromIndex + mSubList.size
            return result
        }

        override fun removeAt(index: Int): T {
            val result = mSubList.removeAt(index)
            value = mArrayList
            toIndex -= 1
            return result
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            val result = mSubList.retainAll(elements)
            value = mArrayList
            toIndex = fromIndex + mSubList.size
            return result
        }

        override operator fun set(index: Int, element: T): T {
            val previous = mSubList.set(index, element)
            value = mArrayList
            return previous
        }

        override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
            return SubList(this.fromIndex + fromIndex, this.fromIndex + toIndex)
        }
    }

    /**
     * ListIterator的包装类。用来保证对可变迭代器的操作也能触发数据推送。
     *
     * @see ArrayListLiveData.listIterator
     * @see ArrayListLiveData.iterator
     * @param parent 父List。可以为SubList。
     * @param index 起始索引。
     */
    private inner class ListIterator(parent: MutableList<T>, index: Int) : MutableListIterator<T> {

        private val mIterator = parent.listIterator(index)

        override fun hasPrevious() = mIterator.hasPrevious()

        override fun nextIndex() = mIterator.nextIndex()

        override fun previous() = mIterator.previous()

        override fun previousIndex() = mIterator.previousIndex()

        override fun add(element: T) {
            mIterator.add(element)
            value = mArrayList
        }

        override fun hasNext() = mIterator.hasNext()

        override fun next() = mIterator.next()

        override fun remove() {
            mIterator.remove()
            value = mArrayList
        }

        override fun set(element: T) {
            mIterator.set(element)
            value = mArrayList
        }
    }
}