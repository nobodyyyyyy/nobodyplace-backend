package com.nobody.nobodyplace.trash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;

public class Solution {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }


    public int[] arr = new int[] {5,2,7,2,7,9,1,2,4,6,0,2,12,7};

    public void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public int[] insertSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            int pre = i - 1;
            int curVal = arr[i];
            while (pre >= 0 && curVal < arr[pre]) {
                arr[pre + 1] = arr[pre];
                --pre;
            }
            arr[pre + 1] = curVal;
        }
        return arr;
    }

//    private int[] tmp;
//
//    public int[] _mergeSort(int[] arr) {
//        tmp = new int[arr.length];
//        mergeSort(arr, 0, arr.length - 1);
//        return arr;
//    }
//
//    public void mergeSort(int[] arr, int left, int right) {
//        if (left < right) {
//            int mid = left + (right - left) / 2;
//            mergeSort(arr, left, mid);
//            mergeSort(arr, mid + 1, right);
//            merge(arr, left, right);
//        }
//    }
//
//    public void merge(int[] arr, int left, int right) {
//        int mid = left + (right - left) / 2;
//        int pos1 = left, pos2 = mid + 1;
//        int tmpPos = left;
//        while (pos1 <= mid && pos2 <= right) {
//            if (arr[pos1] <= arr[pos2]) {
//                tmp[tmpPos++] = arr[pos1++];
//            } else {
//                tmp[tmpPos++] = arr[pos2++];
//            }
//        }
//        while (pos1 <= mid) {
//            tmp[tmpPos++] = arr[pos1++];
//        }
//        while (pos2 <= right) {
//            tmp[tmpPos++] = arr[pos2++];
//        }
//        for (int i = left; i <= right; ++i) {
//            arr[i] = tmp[i];
//        }
//    }

    public int[] sortArray(int[] nums) {
        int len = nums.length;
        // 将数组整理成堆
        heapify(nums);
        // 现在是大根堆
        for (int i = len - 1; i >= 1; ) {
            // 把堆顶元素（当前最大）交换到数组末尾
            swap(nums, 0, i);
            i--;
            shiftDown(nums, 0, i);
        }
        return nums;
    }

    private void heapify(int[] nums) {
        int len = nums.length;
        // 只需要从 i = (len - 1) / 2 这个位置开始逐层下移【从最后一个非叶节点】【当然这个计算的是第一个叶子节点，然后往上，也差不多】
        for (int i = (len - 1) / 2; i >= 0; i--) {
            shiftDown(nums, i, len - 1);
        }
    }

    private void shiftDown(int[] nums, int k, int end) {
        while (2 * k + 1 <= end) {
            int j = 2 * k + 1;  // 左边的小孩
            if (j + 1 <= end && nums[j + 1] > nums[j]) {
                j++;  // 右边的小孩更大，拿大的那个比
            }
            if (nums[j] > nums[k]) {  // 如果大的小孩比你大，你就下去
                swap(nums, j, k);
            } else {
                break;
            }
            k = j; // 继续往下看下面的小孩
        }
    }


    public static void main(String[] args) {
        Solution s = new Solution();
//        System.out.println(Arrays.toString(s.heap_sort(s.arr, s.arr.length - 1)));
    }

}
