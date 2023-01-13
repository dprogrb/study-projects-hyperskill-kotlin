import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

val separator = File.separator
val path = System.getProperty("user.dir")

fun createFolderVCS() {
    val dir = File("${path}${separator}vcs")
    if (!dir.exists()) dir.mkdir()
}

/*
fun addRecord(fileName: String, record: String) {
    File(fileName).appendText("$record\n")
}
*/

fun copyFile(src: String, dest: String) {
    val file = File(src)
    if (file.exists()) {
        val sourcePath = Paths.get(src)
        val targetPath = Paths.get(dest)
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
    }
}

fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun commandConfig(args: Array<String>) {
    val configFile = File("${path}${separator}vcs${separator}config.txt")
    when (args.size) {
        1 -> {
            when (args[0]) {
                "config" -> {
                    if (configFile.exists()) {
                        println("The username is ${configFile.readText()}.")
                    } else {
                        println("Please, tell me who you are.")
                    }
                }
            }
        }

        2 -> {
            when (args[0]) {
                "config" -> {
                    if (args[1] != "") {
                        configFile.writeText(args[1])
                        println("The username is ${configFile.readText()}.")
                    }
                }
            }
        }
    }
}

fun commandAdd(args: Array<String>) {
    val indexFile = File("${path}\\vcs\\index.txt")
    val hashFile = File("${path}\\vcs\\hash.txt")
    when (args.size) {
        1 -> {
            when (args[0]) {
                "add" -> {
                    if (indexFile.exists()) {
                        println("Tracked files:")
                        indexFile.forEachLine { if (it.contains(".txt")) println(it) }
                    } else {
                        println("Add a file to the index.")
                    }
                }
            }
        }

        2 -> {
            when (args[0]) {
                "add" -> {
                    if (args[1] != "") {
                        val addedFileName = "${path}${separator}${args[1]}"
                        val addedFile = File(addedFileName)
                        if (addedFile.exists()) {
                            if (indexFile.exists()) {
                                indexFile.appendText("${args[1]}\n")
                                indexFile.appendText("${addedFile.readText().toMD5()}\n")
                            } else {
                                indexFile.writeText("${args[1]}\n")
                                indexFile.appendText("${addedFile.readText().toMD5()}\n")
                            }
                            println("The file '${args[1]}' is tracked.")
                        } else {
                            println("Can't find '${args[1]}'.")
                        }
                    }
                }
            }
        }
    }
}

fun commandCommit(args: Array<String>) {
    val configFile = File("${path}${separator}vcs${separator}config.txt")
    val indexFile = File("${path}\\vcs\\index.txt")
    val indexPath = "${path}\\vcs\\index.txt"
    val hashPath = "${path}\\vcs\\hash.txt"
    if (!configFile.exists()) {
        configFile.createNewFile()
    }
    when (args.size) {
        1 -> {
            when (args[0]) {
                "commit" -> {
                    println("Message was not passed.")
                }
            }
        }

        2 -> {
            when (args[0]) {
                "commit" -> {
                    val hashCommitFile = File("${path}\\vcs\\hash.txt")
                    var isFirstCommit = false
                    val commitsFolder = File("${path}\\vcs\\commits")
                    if (!commitsFolder.exists()) {
                        commitsFolder.mkdirs()
                        isFirstCommit = true
                    }
                    val fromFile = File("${path}")
                    if (hashCommitFile.exists()) hashCommitFile.writeText("")
                    fromFile.walkTopDown().maxDepth(1).forEach {
                        if ((it.isFile) && (it.extension == "txt")) {

                            var indexFileName = it.name
                            var textFileFrom = ""
                            textFileFrom = it.readText()
                            indexFile.forEachLine {
                                if (indexFileName == it) {
                                    if (hashCommitFile.exists()) {
                                        hashCommitFile.appendText("${indexFileName}\n${textFileFrom.toMD5()}\n")
                                    } else {
                                        hashCommitFile.createNewFile()
                                        hashCommitFile.writeText("${indexFileName}\n${textFileFrom.toMD5()}\n")
                                    }
                                }
                            }
                        }
                    }
                    val hashCommitFolderName = hashCommitFile.readText().toMD5()
                    File("${path}\\vcs\\commits\\$hashCommitFolderName").mkdirs()

                    if ((hashCommitFile.readText().toMD5() == indexFile.readText().toMD5()) && !isFirstCommit) {
                        println("Nothing to commit.")
                    } else {
                        fromFile.walkTopDown().maxDepth(1).forEach {// create files in folder commits
                            if ((it.isFile) && (it.extension == "txt")) {
                                val fileThat = File("${path}\\vcs\\commits\\$hashCommitFolderName\\${it.name}")
                                var indexFileName = it.name
                                var textFileFrom = ""
                                textFileFrom = it.readText()
                                indexFile.forEachLine {
                                    if (indexFileName == it) {
                                        if (fileThat.exists()) {
                                            fileThat.writeText(textFileFrom)
                                        } else {
                                            fileThat.createNewFile()
                                            fileThat.writeText(textFileFrom)
                                        }
                                    }
                                }
                            }
                        }
                        val logFile = File("${path}\\vcs\\log.txt")
                        val logFileCopy = File("${path}\\vcs\\log_copy.txt")
                        if (logFile.exists()) {
                            logFileCopy.writeText("${logFile.readText()}\n")
                            logFile.writeText("commit ${hashCommitFolderName}\nAuthor: ${configFile.readText()}\n${if (args.size > 1) "${args[1]}\n" else ""}")
                            logFile.appendText("${logFileCopy.readText()}\n")
                        } else {
                            logFile.createNewFile()
                            logFile.writeText("commit ${hashCommitFolderName}\nAuthor: ${configFile.readText()}\n${if (args.size > 1) "${args[1]}\n" else ""}")
                        }
                        logFileCopy.delete()
                        println("Changes are committed.")
                        copyFile(hashPath, indexPath)
                    }
                }
            }
        }
    }
}

fun commandLog(args: Array<String>) {
    val logFile = File("${path}\\vcs\\log.txt")
    if (args.isNotEmpty()) {
        when (args[0]) {
            "log" -> {
                if (logFile.exists()) {
                    println("${logFile.readText()}")
                } else {
                    println("No commits yet.")
                }
            }
        }
    }
}

fun commandCheckout(args: Array<String>) {

    when (args.size) {
        1 -> {
            when (args[0]) {
                "checkout" -> {
                    println("Commit id was not passed.")
                }
            }
        }

        2 -> {
            when (args[0]) {
                "checkout" -> {
                    val dirPath = "${path}"
                    var isExistsCommitsID = false
                    val folderOfCommits = File("${path}\\vcs\\commits")
                    var choosingCommitFile = File("")
                    if (args[1] != "") {
                        folderOfCommits.walkTopDown().maxDepth(1).forEach {
                            if (args[1] == it.name) {
                                choosingCommitFile = it
                                isExistsCommitsID = true
                            }
                        }
                        if(isExistsCommitsID) {
                            choosingCommitFile.walkTopDown().maxDepth(1).forEach {
                                if ((it.isFile) && (it.extension == "txt")) {
                                    it.copyTo(File("${path}\\${it.name}"), overwrite = true)
                                }
                            }
                            println("Switched to commit ${choosingCommitFile.name}.")
                        } else {
                            println("Commit does not exist.")
                        }
                    } else {
                        println("Commit id was not passed.")
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    //var args: Array<String> = arrayOf("checkout", "d0c88294e63748ff9ef0e3561397bdf7")
    //var args: Array<String> = arrayOf("add", "file3.txt")
    //var args: Array<String> = arrayOf("commit", "message")
    createFolderVCS()
    val map = mapOf(
        "--help" to "These are SVCS commands:\n" +
                "config     Get and set a username.\n" +
                "add        Add a file to the index.\n" +
                "log        Show commit logs.\n" +
                "commit     Save changes.\n" +
                "checkout   Restore a file.",
        "" to "These are SVCS commands:\n" +
                "config     Get and set a username.\n" +
                "add        Add a file to the index.\n" +
                "log        Show commit logs.\n" +
                "commit     Save changes.\n" +
                "checkout   Restore a file.",
        "config" to "Get and set a username.",
        "add" to "Add a file to the index.",
        "log" to "Show commit logs.",
        "commit" to "Save changes.",
        "checkout" to "Restore a file."
    )
    if (args.isNotEmpty()) {
        if (map.containsKey(args[0])) {
            when (args[0]) {
                "config" -> {
                    commandConfig(args)
                }

                "add" -> {
                    commandAdd(args)
                }

                "commit" -> {
                    commandCommit(args)
                }

                "log" -> {
                    commandLog(args)
                }
                "checkout" -> {
                    commandCheckout(args)
                }
                "--help", "" -> {
                    println(map[args[0]])
                }
            }
        } else println("'${args[0]}' is not a SVCS command.")
    } else {
        println(
            """
            These are SVCS commands:
            config     Get and set a username.
            add        Add a file to the index.
            log        Show commit logs.
            commit     Save changes.
            checkout   Restore a file.""".trimIndent()
        )
    }

}
