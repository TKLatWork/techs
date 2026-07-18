package com.jklib

class Utils implements Serializable {
    static String slugify(String input) {
        return input.toLowerCase().replaceAll(/[^a-z0-9]+/, '-').replaceAll(/^-|-$/, '')
    }
}
