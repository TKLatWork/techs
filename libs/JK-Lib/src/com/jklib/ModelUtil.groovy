package com.jklib

class ModelUtil implements Serializable {

    static Object populate(Object target, Map source, Map<String, Map> spec) {
        spec.each { field, config ->
            def value
            if (source.containsKey(field)) {
                value = source[field] as config.type
                if (value == null && config.containsKey('default')) {
                    value = config.default
                }
            } else if (config.containsKey('default')) {
                value = config.default
            }
            target[field] = value
        }
        return target
    }

    static Map toMap(Object source, List<String> fields) {
        def result = [:]
        fields.each { field ->
            result[field] = source[field]
        }
        return result
    }

    static Map toBody(Object source, List<String> fields) {
        def body = [:]
        fields.each { field ->
            def value = source[field]
            if (value == null) return
            if (value instanceof Collection && value.isEmpty()) return
            if (value instanceof Map && value.isEmpty()) return
            body[field] = value
        }
        return body
    }

    static Map toFilters(Object source, List<String> fields) {
        def filters = [:]
        fields.each { field ->
            def value = source[field]
            if (value) filters[field] = value
        }
        return filters
    }
}
