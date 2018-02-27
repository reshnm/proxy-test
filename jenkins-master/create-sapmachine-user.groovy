import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()
File passwordFile = new File('/var/jenkins_home/secrets/sapmachinePassword')

if (!passwordFile.exists()) {
    println "--> applying Jenkins configuration"

    def git_process = new ProcessBuilder("git clone https://reshnm:h5Uu9v6p@github.com/sap/SapMachine-infrastructure /tmp/SapMachine-infrastructure".split(" ")).redirectErrorStream(true).start()
    git_process.inputStream.eachLine {println it}

    def restore_process = new ProcessBuilder("python /tmp/jenkins_restore.py -s /tmp/SapMachine-infrastructure -t /var/jenkins_home".split(" ")).redirectErrorStream(true).start()
    restore_process.inputStream.eachLine {println it}

    println "--> applying Jenkins configuration ... done"
    println "--> creating local user 'SapMachine'"

    String password = UUID.randomUUID().toString()

    passwordFile.write(password)

    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount('SapMachine', password)
    instance.setSecurityRealm(hudsonRealm)

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    instance.setAuthorizationStrategy(strategy)
    instance.save()

    println "--> creating local user 'SapMachine' ... done"

    //instance.doReload()
}
