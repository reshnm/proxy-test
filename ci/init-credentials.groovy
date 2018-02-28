@Grab('org.yaml:snakeyaml:1.17')

import jenkins.model.*
import hudson.security.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret
import org.yaml.snakeyaml.Yaml

void runCmd(String cmd) {
    def process = new ProcessBuilder(cmd.split(" ")).redirectErrorStream(true).start()
    process.inputStream.eachLine { println it }
}

println "--> importing credentials"

def domain = Domain.global()
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
Yaml parser = new Yaml()
def credentials = parser.load(("/tmp/credentials.yml" as File).text)

for (def secretText : credentials["secret_text"]) {
    def secretTextCred = new StringCredentialsImpl(
        CredentialsScope.GLOBAL,
        secretText["id"],
        secretText["description"],
        Secret.fromString(secretText["text"]))
    store.addCredentials(domain, secretTextCred)
}

for (def userPassword : credentials["user_password"]) {
    def userPasswordCred = new UsernamePasswordCredentialsImpl(
        CredentialsScope.GLOBAL,
        userPassword["id"],
        userPassword["description"],
        userPassword["user"],
        userPassword["password"])
    store.addCredentials(domain, userPasswordCred)
}

runCmd("rm -rf /tmp/credentials.yml")
println "--> importing credentials ... done"