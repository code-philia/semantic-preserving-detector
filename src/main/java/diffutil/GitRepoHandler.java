
// Handles operations related to Git repositories

package diffutil;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GitRepoHandler {

    private String repoUrl;
    private String localPath;

    public GitRepoHandler(String repoUrl, String localPath) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        this.repoUrl = repoUrl;
        this.localPath = localPath + "_" + timestamp;
    }

    public Repository cloneRepository() throws Exception {
        Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(Paths.get(localPath).toFile())
                .call();

        return new FileRepositoryBuilder()
                .setGitDir(new File(localPath + "/.git"))
                .build();
    }
}

