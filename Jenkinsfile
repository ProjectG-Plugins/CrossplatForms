pipeline {
    agent any
    
    tools {
        jdk 'Jdk21'
        gradle 'gradle'
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh './gradlew clean build'
            }
            post {
                success {
                    archiveArtifacts 'spigot/build/libs/CrossplatForms-Spigot.jar'
                    archiveArtifacts 'bungeecord/build/libs/CrossplatForms-BungeeCord.jar'
                    archiveArtifacts 'velocity/build/libs/CrossplatForms-Velocity.jar'
                }
            }
        }
    }

    post {
        always {
            deleteDir()
        }

        success {
            script {
                def changeLogSets = currentBuild.changeSets
                def message = "**Changes:**"

                if (changeLogSets.size() == 0) {
                    message += "\n*No changes.*"
                } else {
                    def repositoryUrl = scm.userRemoteConfigs[0].url.replace(".git", "")
                    def count = 0;
                    def extra = 0;
                    for (int i = 0; i < changeLogSets.size(); i++) {
                        def entries = changeLogSets[i].items
                        for (int j = 0; j < entries.length; j++) {
                            if (count <= 10) {
                                def entry = entries[j]
                                def commitId = entry.commitId.substring(0, 6)
                                message += "\n   - [`${commitId}`](${repositoryUrl}/commit/${entry.commitId}) ${entry.msg}"
                                count++
                            } else {
                                extra++;
                            }
                        }
                    }

                    if (extra != 0) {
                        message += "\n   - ${extra} more commits"
                    }
                }
                env.changes = message
            }

            discordSend description: "**Build:** [${currentBuild.id}](${env.BUILD_URL})\n**Status:** [${currentBuild.currentResult}](${env.BUILD_URL})\n${changes}\n\n[**Artifacts on Jenkins**](https://ci.kejonamc.dev/job/CrossplatForms/)", footer: 'KejonaMC', link: env.BUILD_URL, successful: currentBuild.resultIsBetterOrEqualTo('SUCCESS'), result: currentBuild.currentResult, title: "${env.JOB_NAME}", webhookURL: "${env.DISCORD_WEBHOOK}"
        }
    }
}