///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package sort;
//
///**
// *
// * @author Dominik
// */
//public class Sortingtest {
//
//    public static void main(String[] args) {
//        Comparator<Integer> intComp = new Comparator<Integer>() {
//            @Override
//            public boolean compare(Integer i1, Integer i2) {
//                return i1 > i2;
//            }
//        };
//        Comparator<String> strComp = new Comparator<String>() {
//
//            @Override
//            public boolean compare(String s1, String s2) {
//                return s1.compareTo(s2) > 0;
//            }
//        };
////        String[] strings = new String[]{"c", "b", "a", "d", "f", "e"};
////        BubbleSort<String> bubble = new BubbleSort<>(strComp);
////        SelectionSort<String> sel = new SelectionSort<>(strComp);
////        InsertionSort<Integer> ins = new InsertionSort<>(intComp);
////        Quicksort<String> quick = new Quicksort<>(strComp);
////        Heapsort<String> heap = new Heapsort<>(strComp);
//        BubbleSort<Integer> bubble = new BubbleSort<>(intComp);
//        SelectionSort<Integer> sel = new SelectionSort<>(intComp);
//        InsertionSort<Integer> ins = new InsertionSort<>(intComp);
//        Quicksort<Integer> quick = new Quicksort<>(intComp);
//        Heapsort<Integer> heap = new Heapsort<>(intComp);
//        SortingAlgorithm<Integer>[] sorts = new SortingAlgorithm[]{quick, heap, sel, bubble};
//        int n = 1; // runs
//        int N = 10; // elements
//        long[][] times = new long[sorts.length][n];
//        System.out.println("Testing " + sorts.length + " Sortalgorithms with " + N + " elements in " + n + " runs.");
//        for (int m = 0; m < n; m++) {
//            System.out.println((m + 1) + ". Run");
//            int[] ints = SortingAlgorithm.fillIntArray(N, 1, false);
//            Integer[] values = new Integer[ints.length];
//            for (int i = 0; i < ints.length; i++) {
//                values[i] = ints[i];
//            }
//            for (int i = 0; i < sorts.length; i++) {
//                SortingAlgorithm<Integer> sort = sorts[i];
//                //sort.setValues(strings);
//                sort.setValues(values);
//                System.out.println(sort + ":");
//                sort.printValues();
//                System.out.println("");
//                long start = System.nanoTime();
//                sort.sort();
//                long diff = (System.nanoTime() - start) / 1000;
//                times[i][m] = diff;
//                if (sort.check()) {
//                    sort.printValues();
//                    System.out.println("");
////                    System.out.println("Runtime: " + diff + "ms "
////                            + "Comparisons: " + sort.getComparisons()
////                            + " Permutations: " + sort.getSwitches()
////                            + " Read Operations: " + sort.getReadOperations()
////                            + " Write Operations: " + sort.getWriteOperations()
////                    );
//                } else {
//                    System.err.println("The array is not correctly sorted!");
//                }
//            }
//        }
//        System.out.println("Test finished. Results after " + n + " runs:");
//        for (int i = 0; i < sorts.length; i++) {
//            long avgTime = 0;
//            long longestTime = 0;
//            for (int j = 0; j < times[i].length; j++) {
//                avgTime += times[i][j];
//                if (times[i][j] > longestTime) {
//                    longestTime = times[i][j];
//                }
//            }
//            avgTime /= n;
//            System.out.println(sorts[i] + ":");
//            System.out.println("Average Runtime: " + (float) avgTime / 1000 + "ms "
//                    + "Longest Runtime: " + (float) longestTime / 1000 + "ms "
//                    + "Average Speed: " + 1000 / ((float) avgTime / 1000) + "1000/ms "
//                    + "Least Speed: " + 1000 / ((float) longestTime / 1000) + "1000/ms "
//                    + "Average Comparisons: " + sorts[i].getComparisons() / n
//                    + " Average Permutations: " + sorts[i].getSwitches() / n
//                    + " Average Read Operations: " + sorts[i].getReadOperations() / n
//                    + " Average Write Operations: " + sorts[i].getWriteOperations() / n
//            );
//        }
//    }
//}
