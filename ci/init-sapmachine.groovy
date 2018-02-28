import jenkins.model.*
import hudson.security.*

void runCmd(String cmd) {
    def process = new ProcessBuilder(cmd.split(" ")).redirectErrorStream(true).start()
    process.inputStream.eachLine { println it }
}

def jenkins_home = System.getenv("JENKINS_HOME")
def instance = Jenkins.getInstance()
File passwordFile = new File("${jenkins_home}/secrets/sapmachinePassword")

if (!passwordFile.exists()) {
    println "--> creating local user 'SapMachine'"

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
    println "--> importing keys ... done"

    Thread.start {
        sleep 20000
        println "--> applying Jenkins configuration"
        runCmd("git clone https://github.com/sap/SapMachine-infrastructure /tmp/SapMachine-infrastructure")
        runCmd("python /tmp/SapMachine-infrastructure/lib/jenkins_restore.py -s /tmp/SapMachine-infrastructure -t ${jenkins_home}")
        runCmd("rm -rf /tmp/SapMachine-infrastructure")
        println "--> applying Jenkins configuration ... done"
    }
}
