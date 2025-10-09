package com.simple.taxi.auth.validation;

import java.util.Objects;

public class Main {


    public static void main(String[] args) {
        Animal animal = new Animal("asd");
        Animal animal2 = new Animal("asd");

        String a = "hello".intern();
        String b = new String("hello");
        String c = "hello";

        System.out.println(a == b);
        System.out.println(a == c);

        System.out.println(a.equals(b));

        System.out.println(animal.equals(animal2));
    }

    public static final class Animal {
        private final String name;

        public Animal(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

    }
}
