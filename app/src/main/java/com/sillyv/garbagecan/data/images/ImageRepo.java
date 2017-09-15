package com.sillyv.garbagecan.data.images;


import com.sillyv.garbagecan.data.RepositoryContract;
import com.sillyv.garbagecan.screen.camera.CameraEventModel;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.File;
import java.io.IOException;

import io.reactivex.Single;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class ImageRepo implements RepositoryContract.Image {
    public Single<String> uploadPhotoRx(CameraEventModel cameraEventModel) {
        return Single.fromCallable(() -> uploadPhoto(cameraEventModel));
    }

    private String uploadPhoto(CameraEventModel cameraEventModel) throws IOException {
            final SSHClient ssh = new SSHClient();
            ssh.loadKnownHosts();
            ssh.connect("localhost");
            try {
                ssh.authPublickey(System.getProperty("user.name"));
                final String src = System.getProperty("user.home") + File.separator + "test_file";
                final SFTPClient sftp = ssh.newSFTPClient();
                // TODO: 9/15/2017 Fix try catch implementation, mayou use automatic resource management
                try {
                    sftp.put(new FileSystemFile(src), "/tmp");
                } finally {
                    sftp.close();
                }

            } finally {
                ssh.disconnect();
            }

            return "kmj";


    }


}
