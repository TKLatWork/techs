package com.jklib.g3

class G3JobSupport implements Serializable {

    static final Map FUNCTION_MAP = buildDispatchMap()

    private static Map buildDispatchMap() {
        def classMap = [
            createReleasePackage: [G3Model.CreateReleasePackageInput, G3Model.CreateReleasePackageOutput],
            getReleasePackage: [null, G3Model.GetReleasePackageOutput],
            listReleasePackages: [G3Model.ListReleasePackagesInput, G3Model.ListReleasePackagesOutput],
            executeRelease: [G3Model.ExecuteReleaseInput, G3Model.ExecuteReleaseOutput],
            getExecutionStatus: [null, G3Model.GetExecutionStatusOutput],
            rollbackRelease: [G3Model.RollbackReleaseInput, G3Model.RollbackReleaseOutput],
        ]
        def map = [:]
        G3API.ENDPOINTS.each { name, _ ->
            def (inputCls, outputCls) = classMap[name]
            map[name] = { Map m, G3API g3 ->
                def input = inputCls ? inputCls.fromMap(m) : m
                outputCls.fromApiResult(g3.callApi(name, input))
            }
        }
        return map
    }
}
