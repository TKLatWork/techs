import groovy.json.JsonOutput

def call(int code, String message, Map data = [:], Map files = [:]) {
    def result = [
        code: code,
        message: message,
        data: data
    ]
    if (files) {
        result.files = files
    }
    
    def json = JsonOutput.toJson(result)
    writeFile file: 'Return.json', text: json
    archiveArtifacts artifacts: 'Return.json', allowEmptyArchive: true
}
