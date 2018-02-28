import jenkins.model.*
import hudson.security.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import org.yaml.snakeyaml.Yaml

jenkins_home = System.getenv("JENKINS_HOME")
instance = Jenkins.getInstance()

void runCmd(String cmd) {
    def process = new ProcessBuilder(cmd.split(" ")).redirectErrorStream(true).start()
    process.inputStream.eachLine { println it }
}

boolean isAlreadyInitialized() {
    File initFile = new File("${jenkins_home}/sapmachineInitialized")

    if (!initFile.exists()) {
        initFile.write("1")
        return false
    } else {
        return true
    }
}

if (!isAlreadyInitialized()) {
    println "--> creating local user 'SapMachine'"
    File passwordFile = new File("${jenkins_home}/secrets/sapmachinePassword")
    String password = UUID.randomUUID().toString().replace("-", "")
    passwordFile.write(password)

    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount('SapMachine', password)
    instance.setSecurityRealm(hudsonRealm)

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    instance.setAuthorizationStrategy(strategy)
    instance.save()

    println "********************************************************"
    println "* SapMachine Password: ${password}"
    println "********************************************************"
    println "--> creating local user 'SapMachine' ... done"

    println "--> importing keys"
    runCmd("gpg --import-ownertrust /var/pkg/deb/keys/sapmachine.ownertrust")
    runCmd("gpg --import /var/pkg/deb/keys/sapmachine.secret.key")
    runCmd("rm -rf  /var/pkg/deb/keys/sapmachine.ownertrust")
    runCmd("rm -rf /var/pkg/deb/keys/sapmachine.secret.key")
    println "--> importing keys ... done"


    println "--> importing credentials"
    def domain = Domain.global()
    def store = instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
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


    Thread.start {
        sleep 20000
        println "--> applying Jenkins configuration"
        runCmd("git clone https://github.com/sap/SapMachine-infrastructure /tmp/SapMachine-infrastructure")
        runCmd("python /tmp/SapMachine-infrastructure/lib/jenkins_restore.py -s /tmp/SapMachine-infrastructure -t ${jenkins_home}")
        runCmd("rm -rf /tmp/SapMachine-infrastructure")
        println "--> applying Jenkins configuration ... done"
    }
}
