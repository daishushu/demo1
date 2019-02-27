package com.dwz.baseutil.sharedpreferences

/**
 * 类名：sort
 * 作者：dwz
 * 功能：
 * 创建日期：2018/12/3
 * 修改记录：
 */
fun main() {
  /*  bubbleSort(intArrayOf(1, 5, 9, 3, 2, 7, 6, 10, 0, 4, 8))
    selectionSort(intArrayOf(1, 5, 9, 3, 2, 7, 6, 10, 0, 4, 8))
    insertionSort(intArrayOf(1, 5, 9, 3, 2, 7, 6, 10, 0, 4, 8))
    println("mergeSort start = ${System.currentTimeMillis()}")
    println(mergeSort(intArrayOf(1, 5, 9, 3, 2, 7, 6, 10, 0, 4, 8)).contentToString())
    println("mergeSort end = ${System.currentTimeMillis()}")*/
    val data = intArrayOf(1, 5, 9, 3, 2, 7, 6, 10, 0, 4, 8)
    quickSort_2(data,0,data.lastIndex)
    print(data.contentToString())
}

//冒泡排序Bubble Sort
fun bubbleSort(arr: IntArray) {
    println("bubbleSort start = ${System.currentTimeMillis()}")
    if (arr.isEmpty()) return
    val lastIndex = arr.lastIndex
    for (i in 0..lastIndex) {
        for (j in 0..lastIndex - 1 - i) {
            if (arr[j] > arr[j + 1]) {
               swap(arr,j,j+1)
            }
        }
    }
    println(arr.contentToString())
    println("bubbleSort end = ${System.currentTimeMillis()}")
}
//选择排序Selection Sort
fun selectionSort(arr: IntArray){
    println("selectionSort start = ${System.currentTimeMillis()}")
    if (arr.isEmpty()) return
    val lastIndex = arr.lastIndex
    for (i in 0 until lastIndex) {
        var index =i
        for(j in index..lastIndex){
            if(arr[j]<arr[index]){
                index = j
            }
        }
       swap(arr,index,i)
    }
    println(arr.contentToString())
    println("selectionSort end = ${System.currentTimeMillis()}")
}

//Insertion Sort 插入排序

fun insertionSort(arr:IntArray){
    println("insertionSort start = ${System.currentTimeMillis()}")
    if (arr.isEmpty()) return
    val lastIndex = arr.lastIndex
    var preIndex:Int
    var current:Int
    for (i in 1..lastIndex){
        preIndex = i - 1
        current = arr[i]
        while (preIndex>=0 && arr[preIndex] > current){
            arr[preIndex+1] = arr[preIndex]
            preIndex--
        }
        arr[preIndex+1] = current
    }
    println(arr.contentToString())
    println("insertionSort end = ${System.currentTimeMillis()}")
}
/**
 * Merge Sort
 */
fun mergeSort(arr:IntArray):IntArray{
    if(arr.size<2)return arr
    val length = arr.size
    val mid = length/2
    val left = arr.copyOfRange(0,mid)
    val right = arr.copyOfRange(mid,length)
    return merge(mergeSort(left), mergeSort(right))
}
fun merge(left:IntArray,right:IntArray): IntArray {
    val newArr = IntArray(left.size+right.size)
    var index = 0
    var i = 0
    var j = 0
    while (index < newArr.size) {
        when {
            i >= left.size -> newArr[index] = right[j++]
            j >= right.size -> newArr[index] = left[i++]
            left[i] > right[j] -> newArr[index] = right[j++]
            else -> newArr[index] = left[i++]
        }
        index++
    }
    return newArr
}
/*
class SPDelegate<T>:ReadWriteProperty<Any?,T>{
    private var value:T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}
val test:Int by Delegates.notNull()*/
fun swap(array: IntArray, i: Int, j: Int) {
    val temp = array[i]
    array[i] = array[j]
    array[j] = temp
}

fun quickSort_1(data: IntArray?, start: Int, end: Int) {
    if (data == null || start < 0 || end > data.size - 1) {
        throw IllegalArgumentException("Invalid Parameters")
    }
    if (start == end) return
    val index = partition(data, start, end)
    if (index > start) {
        quickSort_1(data, start, index - 1)
    }
    if (index < end) {
        quickSort_1(data, index + 1, end)
    }
}

private fun partition(data: IntArray, start: Int, end: Int): Int {
    var index = start + (Math.random() * (end - start + 1)).toInt()
    swap(data, index, end)
    var small = start - 1
    index = start
    while (index < end) {
        if (data[index] < data[end]) {
            small++
            if (small != index) {
                swap(data, index, small)
            }
        }
        index++
    }
    swap(data, small + 1, end)
    return small + 1
}

fun quickSort_2(data: IntArray?, start: Int, end: Int) {
    if (data == null || start >= end) return
    var i = start
    var j = end
    val pivotKey = data[start]
    while (i < j) {
        while (i < j && data[j] >= pivotKey) j--
        if (i < j) data[i++] = data[j]
        while (i < j && data[i] <= pivotKey) i++
        if (i < j) data[j--] = data[i]
    }
    data[i] = pivotKey
    quickSort_2(data, start, i - 1)
    quickSort_2(data, i + 1, end)
}
