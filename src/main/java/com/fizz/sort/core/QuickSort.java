package com.fizz.sort.core;

public class QuickSort {

    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, 0, arr.length-1, 16);
    }

    /**
     * @param arr   待排序的数组
     * @param left  左闭
     * @param right 右闭
     * @param length    当快排递归到子问题的规模 <= k 时，采用插入排序优化
     * @param <T>   泛型，待排序可比较类型
     */
    public static <T extends Comparable<? super T>> void sort(T[] arr, int left, int right, int length) {
        if (left >= right) return;
        /*if (length>0 && (right-left)<length) {
            insertionSort(arr, left, right);
            return;
        }*/
        int p = partition(arr, left, right);
        sort(arr, left, p - 1, length);
        sort(arr, p + 1, right, length);
    }

    public static <T extends Comparable<? super T>> int partition(T[] arr, int left, int right) {
        //排序前，先让基准值和随机的一个数进行交换。这样，基准值就有随机性。
        //就不至于在数组相对有序时，导致左右两边的递归规模不一致，产生最坏时间复杂度
        swap(arr,left,(int)(Math.random()*(right - left + 1)+left));

        T base = arr[left];
        int i= left;
        int j = right;
        while (i < j) {
            //从右到左扫描，扫描出第一个比base小的元素，然后j停在那里。
            while (j > i && arr[j].compareTo(base) > 0)
                j--;
            arr[i] = arr[j];

            //从左到右扫描，扫描出第一个比base大的元素，然后i停在那里。
            while (i < j && arr[i].compareTo(base) < 0)
                i++;
            arr[j] = arr[i];
        }
        /*for (; i<= right; i++) {
            if (base.compareTo(arr[i]) > 0) {
                j++;
                swap(arr, j, i);
            }
        }
        swap(arr, left, j);*/
        arr[j] = base;
        return j;//返回一躺排序后基准值的下角标
    }

    /**
     * 插入排序
     * @param arr
     * @param l
     * @param r
     * @param <T>
     */
    public static <T extends Comparable<? super T>> void insertionSort(T[] arr, int l, int r) {
        for (int i = l + 1; i <= r; i++) {
            T cur = arr[i];
            int j = i - 1;
            for (; j >= 0 && cur.compareTo(arr[j]) < 0; j--) {
                arr[j + 1] = arr[j];
            }
            arr[j + 1] = cur;
        }
    }

    public static void swap(Object[] arr, int i, int j) {
        if (i != j) {
            Object temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    private static void printArr(Object[] arr) {
        for (Object o : arr) {
            System.out.print(o + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Integer[] arr = {3, 5, 1, 7, 2, 9, 8, 0, 4, 6};
        sort(arr);
        printArr(arr);
    }

}
