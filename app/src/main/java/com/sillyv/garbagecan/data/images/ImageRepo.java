package com.sillyv.garbagecan.data.images;


import com.sillyv.garbagecan.data.RepositoryContract;
import com.sillyv.garbagecan.data.credentials.CredentialsManager;
import com.sillyv.garbagecan.screen.camera.FileUploadEvent;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;

import io.reactivex.Single;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class ImageRepo
        implements RepositoryContract.Image {


    public Single<String> uploadPhotoRx(FileUploadEvent fileUploadEvent) {
        return Single.fromCallable(() -> uploadPhoto(fileUploadEvent));
    }


    private String uploadPhoto(FileUploadEvent fileUploadEvent) throws IOException {

        final SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(fileUploadEvent.getCredentialsMap()
                .get(CredentialsManager.HOST_KEY_KEY));
        ssh.connect(fileUploadEvent.getCredentialsMap().get(CredentialsManager.SERVER_KEY),22);
        try {
            ssh.authPassword(fileUploadEvent.getCredentialsMap().get(CredentialsManager.USERNAME_KEY),
                    fileUploadEvent.getCredentialsMap().get(CredentialsManager.PASSWORD_KEY));
            final String src = fileUploadEvent.getFile().getPath();
            try (SFTPClient sftp = ssh.newSFTPClient()) {
                sftp.put(new FileSystemFile(src),
                        fileUploadEvent.getCredentialsMap()
                                .get(CredentialsManager.SERVER_INTERNAL_PATH_KEY));
            }
        } finally {
            ssh.disconnect();
        }
        return "test";
    }


}
