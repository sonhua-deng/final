package com.sevenshop.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by lianzhan on 16/10/8.
 * 集合相关处理Utils
 */

public final class ListUtils {

    public static <D> boolean isEmpty(Collection<D> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <D> boolean isEmpty(D[] list) {
        return list == null || list.length == 0;
    }

    public static <D, R> boolean isEmpty(Map<D, R> map) {
        return map == null || map.isEmpty();
    }

    public static <R> boolean isEmpty(SparseArray<R> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    public static <T extends Object> T getData(@NonNull SparseArray sparseArray, int key) {
        return (T) sparseArray.get(key);
    }

    //删除重复数据 并保证顺序
    @Nullable
    public static <T> List<T> removeDuplicateWithOrder(@Nullable List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        Set<T> set = new HashSet<>();
        List<T> newList = new ArrayList<>();
        for (T element : list) {
            if (set.add(element)) {
                newList.add(element);
            }
        }
        set.clear();
        list.clear();
        return newList;
    }

}
