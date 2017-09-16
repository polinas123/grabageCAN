package com.sillyv.garbagecan.data.images;


import com.sillyv.garbagecan.data.RepositoryContract;
import com.sillyv.garbagecan.data.credentials.CredentialsRepo;
import com.sillyv.garbagecan.screen.camera.FileUploadEvent;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

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
                .get(CredentialsRepo.HOST_KEY_KEY));
        ssh.connect(fileUploadEvent.getCredentialsMap().get(CredentialsRepo.SERVER_KEY));
        try {
            ssh.authPassword(fileUploadEvent.getCredentialsMap().get(CredentialsRepo.USERNAME_KEY),
                    fileUploadEvent.getCredentialsMap().get(CredentialsRepo.PASSWORD_KEY));
            final String src = fileUploadEvent.getFile().getPath();
            try (SFTPClient sftp = ssh.newSFTPClient()) {
                sftp.put(new FileSystemFile(src),
                        fileUploadEvent.getCredentialsMap()
                                .get(CredentialsRepo.SERVER_INTERNAL_PATH_KEY));
            }
        } finally {
            ssh.disconnect();
        }
        return "kmj";
    }


}
