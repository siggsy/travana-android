package com.VegaSolutions.lpptransit.testing;

public class Test<T, E> {

    E a;
    T t;

    public static void main(String[] args) {

        Test[] a = new Test[] {
                new Test<String, Boolean>(),
                new Test()
        };

    }

}
